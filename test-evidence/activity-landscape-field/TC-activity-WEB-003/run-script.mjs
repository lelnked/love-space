import { chromium } from 'playwright';
import fs from 'fs';

const BASE = 'http://100.100.117.79:5173/love-space';
const EV = '/home/lanshuangping/personal/love-space/test-evidence/activity-landscape-field/TC-activity-WEB-003';
const logs = [];
const shot = (p, n) => p.screenshot({ path: `${EV}/${n}.png`, fullPage: true });

const b = await chromium.launch();
const ctx = await b.newContext({ viewport: { width: 1440, height: 1000 } });
const page = await ctx.newPage();
page.on('console', m => logs.push(`[${m.type()}] ${m.text()}`));
page.on('pageerror', e => logs.push(`[pageerror] ${e.message}`));
let createdId = null;
page.on('response', async r => {
  const u = r.url();
  if (/\/api\/admin\//.test(u)) logs.push(`[net] ${r.request().method()} ${r.status()} ${u}`);
  if (r.request().method() === 'POST' && /\/api\/admin\/activities$/.test(u) && r.status() < 300) {
    try { createdId = (await r.json()).id; } catch {}
  }
});

const fail = async (msg, name) => {
  logs.push(`FAIL: ${msg}`);
  await shot(page, name || 'failure');
  fs.writeFileSync(`${EV}/console-logs.txt`, logs.join('\n'));
  await b.close();
  console.log('RESULT=FAIL ' + msg);
  process.exit(1);
};

const landscapeInput = () => page.locator('label:text-is("景观") + div input');

async function fillForm({ title, landscape, detail }) {
  // city
  const sel = page.locator('select').first();
  await sel.waitFor();
  const opts = await sel.locator('option').all();
  let val = null;
  for (const o of opts) { const v = await o.getAttribute('value'); if (v) { val = v; break; } }
  if (!val) await fail('无可选上架城市');
  await sel.selectOption(val);
  // title
  await page.locator('label:has-text("标题") + div input').first().fill(title);
  // image
  await page.locator('input[type=file]').first().setInputFiles('/tmp/tc003.png');
  await page.locator('img[alt="预览"]').first().waitFor({ timeout: 60000 });
  // landscape
  await landscapeInput().fill(landscape);
  if (detail) {
    const pm = page.locator('.ProseMirror').first();
    await pm.click();
    await pm.pressSequentially(detail, { delay: 10 });
  }
}

// ---- login
await page.goto(`${BASE}/signin`, { waitUntil: 'networkidle' });
await page.locator('input[placeholder="username"]').fill('admin');
await page.locator('input[placeholder="请输入密码"]').fill('8@y2eoRLyStM*UVU');
await page.getByRole('button', { name: /登录|Sign in/i }).click();
await page.waitForURL(u => !/signin/.test(u.toString()), { timeout: 20000 });
await shot(page, 'step-0-logged-in');

// ---- step 1: create
const TITLE = `TC003景观用例-${Date.now()}`;
const DETAIL = 'tiptap 富文本内容 TC003';
await page.goto(`${BASE}/activities/create`, { waitUntil: 'networkidle' });
const pmCount = await page.locator('.ProseMirror').count();
logs.push(`[check] ProseMirror 节点数=${pmCount}`);
if (pmCount === 0) await fail('富文本编辑器(.ProseMirror)未渲染', 'failure-editor');
await fillForm({ title: TITLE, landscape: '海岸线景观', detail: DETAIL });
await shot(page, 'step-1-create-form-filled');
await page.getByRole('button', { name: '创建' }).click();
await page.waitForURL(/\/activities$/, { timeout: 30000 }).catch(() => {});
if (!/\/activities$/.test(page.url())) await fail('创建未成功跳转，当前URL=' + page.url(), 'failure-create');
await shot(page, 'step-1-created-list');
if (!createdId) await fail('未捕获到新建活动 id');
logs.push(`[info] createdId=${createdId}`);

// ---- step 2: edit, verify 海岸线景观, change to 火山地貌
await page.goto(`${BASE}/activities/${createdId}/edit`, { waitUntil: 'networkidle' });
await landscapeInput().waitFor({ timeout: 20000 });
const v1 = await landscapeInput().inputValue();
const d1 = await page.locator('.ProseMirror').first().innerText();
logs.push(`[check] 首次编辑回显 景观="${v1}" 富文本="${d1.trim()}"`);
await shot(page, 'step-2-edit-echo-coastline');
if (v1 !== '海岸线景观') await fail(`景观回显不符，期望"海岸线景观"，实际"${v1}"`, 'failure-echo1');
if (!d1.includes(DETAIL)) logs.push(`WARN: 富文本回显不含预期文本，实际="${d1.trim()}"`);
await landscapeInput().fill('火山地貌');
await shot(page, 'step-2-edit-changed-volcano');
await page.getByRole('button', { name: '保存' }).click();
await page.waitForURL(/\/activities$/, { timeout: 30000 }).catch(() => {});
if (!/\/activities$/.test(page.url())) await fail('编辑保存未成功跳转，当前URL=' + page.url(), 'failure-save');

// ---- step 3: reopen, verify 火山地貌
await page.goto(`${BASE}/activities/${createdId}/edit`, { waitUntil: 'networkidle' });
await landscapeInput().waitFor({ timeout: 20000 });
const v2 = await landscapeInput().inputValue();
const d2 = await page.locator('.ProseMirror').first().innerText();
logs.push(`[check] 二次编辑回显 景观="${v2}" 富文本="${d2.trim()}"`);
await shot(page, 'step-3-edit-echo-volcano');
if (v2 !== '火山地貌') await fail(`景观回显不符，期望"火山地貌"，实际"${v2}"`, 'failure-echo2');
const richOk = d2.includes(DETAIL);
logs.push(`[check] 富文本保存后不丢失=${richOk}`);

fs.writeFileSync(`${EV}/console-logs.txt`, logs.join('\n'));
await b.close();
console.log('RESULT=PASS richTextPreserved=' + richOk + ' id=' + createdId);
