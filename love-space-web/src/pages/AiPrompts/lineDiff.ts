// ponytail: 行级对比——某行不在其他任一段中出现即标为差异；需要字符级 diff 时再引库
/** 输入 N 段文本，返回每段每行 {text, changed}。 */
export function lineDiff(texts: string[]): { text: string; changed: boolean }[][] {
  const lineSets = texts.map((t) => new Set(t.split("\n")));
  return texts.map((t, i) =>
    t.split("\n").map((line) => ({
      text: line,
      changed: lineSets.some((set, j) => j !== i && !set.has(line)),
    })),
  );
}
