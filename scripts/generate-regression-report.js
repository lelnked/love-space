#!/usr/bin/env node
/**
 * 回归测试 HTML 报告生成器（regression-test skill §5）。
 *
 * 视觉真源是同目录 report-template.html（tokens/样式/脚本/静态骨架全在模板里），
 * 本脚本只做数据解析并填充 6 个 {{占位符}}：DATE / COMMIT_CHIP / BANNER /
 * STATS / UT_SECTION / DOMAINS。改样式改模板，改数据口径改这里。
 *
 * 数据源（全部落盘、可复现）：
 * - test-evidence/regression/<domain>/TC-*    存证目录（域 = 子目录名）
 * - tests/<domain>/{it,web,app}.md            用例标题、优先级（**优先级**: P0/P1/P2；按存在文件通配，端插拔零改动）
 * - test-evidence/regression/ut-summary.json  可选，UT 汇总（runner 跑完 UT 后写入）
 *
 * 用法：node scripts/generate-regression-report.js
 * 输出：test-evidence/regression/report.html
 */
const fs = require('node:fs');
const path = require('node:path');
const { execSync } = require('node:child_process');

const ROOT = path.join(__dirname, '..');
const REG = path.join(ROOT, 'test-evidence', 'regression');
const OUT = path.join(REG, 'report.html');
const TPL = fs.readFileSync(path.join(__dirname, 'report-template.html'), 'utf8');

const esc = (s) => String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
const today = new Date().toISOString().slice(0, 10);
let commitChip = '';
try {
  const commit = execSync('git rev-parse --short HEAD', { cwd: ROOT }).toString().trim();
  commitChip = `<span class="chip">commit <code>${commit}</code></span>`;
} catch { /* 非 git 环境省略该 chip */ }

const CHEV = '<svg class="chev" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 4l4 4-4 4"/></svg>';

// 域 = regression 下的子目录
const domains = fs.readdirSync(REG, { withFileTypes: true }).filter((d) => d.isDirectory()).map((d) => d.name).sort();

// 用例标题 + 优先级，从 living 文件解析：### TC-xxx: 标题 ... **优先级**: P0
function loadMeta(domain) {
  const meta = {};
  for (const layer of ['it', 'web', 'app']) {
    const p = path.join(ROOT, 'tests', domain, `${layer}.md`);
    if (!fs.existsSync(p)) continue;
    const src = fs.readFileSync(p, 'utf8');
    for (const m of src.matchAll(/### (TC-[\w-]+-\d+):\s*(.+)([\s\S]*?)(?=\n### |$)/g)) {
      const prio = (m[3].match(/\*\*优先级\*\*[:：]\s*(P\d)/) || [])[1] || '';
      meta[m[1]] = { title: m[2].trim(), prio };
    }
  }
  return meta;
}

// exchange.md 是 runner 产出的受控格式，轻量渲染：## step → 小标题，围栏 → 代码块(bash 带复制按钮)
function renderExchange(md) {
  const out = [];
  const lines = md.split('\n');
  for (let i = 0; i < lines.length; i++) {
    const l = lines[i];
    if (l.startsWith('```')) {
      const lang = l.slice(3).trim();
      const buf = [];
      while (++i < lines.length && !lines[i].startsWith('```')) buf.push(lines[i]);
      const copyBtn = lang === 'bash' ? '<button class="copy" onclick="copyCode(this)">复制</button>' : '';
      out.push(`<div class="codeblock">${copyBtn}<pre>${esc(buf.join('\n'))}</pre></div>`);
    } else if (l.startsWith('## ')) out.push(`<h4>${esc(l.slice(3))}</h4>`);
    else if (!l.startsWith('# ') && l.trim()) out.push(`<p class="xline">${esc(l).replace(/`([^`]+)`/g, '<code>$1</code>')}</p>`);
  }
  return out.join('\n');
}

function caseHtml(domain, tc, meta) {
  const dir = path.join(REG, domain, tc);
  if (!fs.existsSync(dir)) return ''; // missing evidence -> skip entirely
  const assertionsPath = path.join(dir, 'assertions.md');
  if (!fs.existsSync(assertionsPath)) return ''; // no assertions -> skip
  const lines = fs.readFileSync(assertionsPath, 'utf8').trim().split('\n');
  const failed = lines.some((l) => l.startsWith('- [ ]'));
  const asserts = lines.map((l) => `<li class="${l.startsWith('- [x]') ? 'ok' : 'bad'}">${esc(l.slice(6))}</li>`).join('\n');
  const files = fs.readdirSync(dir).sort();
  const shots = files.filter((f) => f.endsWith('.png'))
    .map((f) => `<a href="${domain}/${tc}/${f}" target="_blank"><img src="${domain}/${tc}/${f}" alt="${f}" loading="lazy" onerror="this.classList.add('missing')"><span>${f}</span></a>`).join('\n');
  // 非图片存证内容直接内嵌（assertions.md 已渲染为断言列表，跳过）
  const others = files.filter((f) => !f.endsWith('.png') && f !== 'assertions.md')
    .map((f) => {
      const raw = fs.readFileSync(path.join(dir, f), 'utf8');
      const body = f === 'exchange.md' ? renderExchange(raw) : `<div class="codeblock"><pre>${esc(raw)}</pre></div>`;
      return `<details class="file"><summary><code>${f}</code></summary>${body}</details>`;
    }).join('\n');
  const info = meta[tc] || {};
  const prio = (info.prio || '').toLowerCase();
  const badge = failed ? '<span class="fail">失败</span>' : '<span class="pass">通过</span>';
  // 失败用例默认展开
  return `<details class="case" data-priority="${prio}"${failed ? ' open' : ''}>
<summary><span class="tc-id">${tc}</span>${prio ? `<span class="prio ${prio}">${info.prio}</span>` : ''}<span class="tc-title">${esc(info.title ?? '')}</span>${badge}</summary>
<div class="case-body">
<ul class="asserts">
${asserts}
</ul>
${shots ? `<div class="shots">\n${shots}\n</div>` : ''}
${others ? `<div class="files">存证：\n${others}\n</div>` : ''}
</div>
</details>`;
}

const passBadge = (passed, total, suffix = '') =>
  passed === total ? `<span class="pass">${passed}/${total}${suffix}</span>` : `<span class="fail">${passed}/${total}${suffix}，${total - passed} 失败</span>`;

let total = 0, totalFail = 0;
const layerStat = { IT: [0, 0], WEB: [0, 0], APP: [0, 0] }; // [passed, total]
const prioStat = {}; // P0/P1/P2 → [passed, total]（无优先级的用例归入 '—'）
const domainsHtml = domains.map((domain) => {
  const meta = loadMeta(domain);
  const cases = fs.readdirSync(path.join(REG, domain), { withFileTypes: true })
    .filter((d) => {
      const full = path.join(REG, domain, d.name);
      // Follow symlinks and include directories/symlinks-to-directories starting with TC-
      if (!d.name.startsWith('TC-')) return false;
      try { return fs.statSync(full).isDirectory(); } catch { return false; }
    }).map((d) => d.name).sort();
  const isFailed = (tc) => {
    const a = path.join(REG, domain, tc, 'assertions.md');
    if (!fs.existsSync(a)) return false; // missing evidence -> not failed
    return fs.readFileSync(a, 'utf8').includes('- [ ]');
  };
  const fails = cases.filter(isFailed);
  total += cases.length; totalFail += fails.length;
  for (const tc of cases) {
    const p = (meta[tc] || {}).prio || '—';
    prioStat[p] ??= [0, 0];
    prioStat[p][1]++; if (!fails.includes(tc)) prioStat[p][0]++;
  }

  const groups = [
    ['接口测试 · IT', 'IT', cases.filter((t) => t.includes('-IT-'))],
    ['Web 端到端 · WEB', 'WEB', cases.filter((t) => t.includes('-WEB-'))],
    ['App 端到端 · APP', 'APP', cases.filter((t) => t.includes('-APP-'))],
  ].filter(([, , list]) => list.length).map(([name, key, list]) => {
    const gFails = list.filter(isFailed).length;
    layerStat[key][0] += list.length - gFails; layerStat[key][1] += list.length;
    return `<details class="tc-group" open>
<summary>${CHEV}<h3 class="group">${name} · ${list.length} 条</h3>${passBadge(list.length - gFails, list.length)}</summary>
${list.map((tc) => caseHtml(domain, tc, meta)).join('\n')}
</details>`;
  }).join('\n');

  return `<details class="domain" open>
<summary>${CHEV}<span class="tag">域</span><h2>${esc(domain)}</h2>${passBadge(cases.length - fails.length, cases.length, ' 通过')}</summary>
${groups}
</details>`;
}).join('\n');

// UT 全局一节（跨域整套运行，锚点归属见追溯矩阵）
const utPath = path.join(REG, 'ut-summary.json');
let utSection = '', utPassed = null, utTotal = null;
if (fs.existsSync(utPath)) {
  const ut = JSON.parse(fs.readFileSync(utPath, 'utf8'));
  utTotal = ut.suites.reduce((a, s) => a + s.total, 0);
  utPassed = ut.suites.reduce((a, s) => a + s.passed, 0);
  const rows = ut.suites.map((s) =>
    `<tr><td><code>${esc(s.package)}</code></td><td>${esc(s.files)}</td><td>${esc(s.framework)}</td><td class="num">${passBadge(s.passed, s.total)}</td></tr>`).join('\n');
  utSection = `<section>
<div class="section-head"><span class="tag">UT</span><h2>单元测试（全局 ${utTotal} 条）</h2></div>
<p class="meta section-note">UT 按包整套运行、不分域；用例与 Scenario 的对应关系见 <code>traceability-matrix.md</code> 的 @scenario 锚点列。</p>
<table>
<tr><th>包</th><th>测试文件</th><th>框架</th><th>结果</th></tr>
${rows}
</table>
</section>`;
}

// verdict banner（右侧：总通过率 + 按 P0/P1/P2 分列的通过率，口径均为端上用例合计）
const pctOf = (p, t) => (t ? Math.round((p / t) * 100) + '%' : '—');
const rates = [['总通过率', total - totalFail, total],
  ...['P0', 'P1', 'P2', '—'].filter((p) => prioStat[p]).map((p) => [p, ...prioStat[p]])]
  .map(([label, p, t]) => `<span class="rate${t && p === t ? ' ok' : ''}"><b>${pctOf(p, t)}</b><span>${label} ${p}/${t}</span></span>`).join('\n');
const ratesHtml = `<span class="rates">\n${rates}\n</span>`;
const utBrief = utTotal !== null ? `UT ${utPassed}/${utTotal} · ` : '';
// 端标签只列实际有用例的端（未启用端不出现）
const endsLabel = Object.entries(layerStat).filter(([, v]) => v[1]).map(([k]) => k).join('+') || 'IT';
const allGood = totalFail === 0 && (utPassed === null || utPassed === utTotal);
const banner = allGood
  ? `<p class="banner good">
<svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M8 12.5l2.5 2.5L16 9.5"/></svg>
<span><span class="verdict">结论：全部通过</span><br><span class="verdict-sub">${utBrief}${endsLabel} ${total}/${total} ✅（${domains.length} 个域）</span></span>
${ratesHtml}
</p>`
  : `<p class="banner bad">
<svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M15 9l-6 6M9 9l6 6"/></svg>
<span><span class="verdict">结论：${totalFail} 条失败</span><br><span class="verdict-sub">${utBrief}${endsLabel} ${total - totalFail}/${total}（${domains.length} 个域），失败用例已展开</span></span>
${ratesHtml}
</p>`;

// KPI 统计卡（UT 缺 ut-summary.json 时显示 —）
const statCard = (label, passed, tot) => {
  if (tot === null) return `<div class="stat"><div class="label">${label}</div><div class="value">—</div><div class="meter"><i style="width:0"></i></div></div>`;
  const pct = tot ? Math.round((passed / tot) * 100) : 100;
  return `<div class="stat"><div class="label">${label}</div><div class="value">${passed} <span class="of">/ ${tot}</span></div><div class="meter"><i${passed < tot ? ' class="bad"' : ''} style="width:${pct}%"></i></div></div>`;
};
const stats = [
  statCard('单元测试 · UT', utPassed, utTotal),
  statCard('接口测试 · IT', layerStat.IT[0], layerStat.IT[1] || null),
  // 插件端：有用例才出卡（未启用的端不渲染）
  ...(layerStat.WEB[1] ? [statCard('Web 端到端 · WEB', layerStat.WEB[0], layerStat.WEB[1])] : []),
  ...(layerStat.APP[1] ? [statCard('App 端到端 · APP', layerStat.APP[0], layerStat.APP[1])] : []),
  `<div class="stat"><div class="label">覆盖域</div><div class="value">${domains.length} <span class="of">个</span></div><div class="meter"><i style="width:100%"></i></div></div>`,
].join('\n');

// 填模板（split/join 避免 replace 的 $ 转义坑，curl 存证里有 $TOKEN）
const fill = (tpl, key, val) => tpl.split(`{{${key}}}`).join(val);
let page = TPL;
for (const [k, v] of Object.entries({
  DATE: today, COMMIT_CHIP: commitChip, BANNER: banner,
  STATS: stats, UT_SECTION: utSection, DOMAINS: domainsHtml,
})) page = fill(page, k, v);

fs.writeFileSync(OUT, page);
console.log(`✅ 回归报告已生成：${path.relative(ROOT, OUT)}（${domains.length} 个域，${endsLabel} ${total} 条，失败 ${totalFail}）`);
