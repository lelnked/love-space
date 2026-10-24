import type { AiPromptVariable } from "../../api/ai";

const inputClass =
  "border border-gray-300 dark:border-gray-700 rounded px-2 py-1.5 text-sm w-full bg-transparent dark:text-white/90 disabled:bg-gray-100 dark:disabled:bg-gray-800";

interface Props {
  variables: AiPromptVariable[];
  errors: Record<number, string>;
  readOnly: boolean;
  onChange: (variables: AiPromptVariable[]) => void;
}

/** 模板变量表：变量名 / 必填 / 说明 / 删除。 */
export default function VariableTable({ variables, errors, readOnly, onChange }: Props) {
  const update = (index: number, patch: Partial<AiPromptVariable>) =>
    onChange(variables.map((v, i) => (i === index ? { ...v, ...patch } : v)));

  return (
    <div>
      <div className="flex items-center justify-between mb-2">
        <span className="text-sm font-semibold text-gray-800 dark:text-white/90">变量</span>
        {!readOnly && (
          <button
            type="button"
            className="text-sm text-brand-500 hover:text-brand-600"
            onClick={() => onChange([...variables, { name: "", required: false, description: "" }])}
          >
            + 添加
          </button>
        )}
      </div>
      {variables.length === 0 ? (
        <div className="text-xs text-gray-400">暂无变量，提示词里用 {"{{变量名}}"} 引用</div>
      ) : (
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-xs text-gray-500">
              <th className="py-1 font-normal">变量名</th>
              <th className="py-1 font-normal w-12 text-center">必填</th>
              <th className="py-1 font-normal">说明</th>
              {!readOnly && <th className="py-1 font-normal w-10 text-center">删除</th>}
            </tr>
          </thead>
          <tbody>
            {variables.map((v, i) => (
              <tr key={i} className="align-top">
                <td className="py-1 pr-2">
                  <input
                    className={inputClass}
                    value={v.name}
                    disabled={readOnly}
                    aria-label={`变量名 ${i + 1}`}
                    onChange={(e) => update(i, { name: e.target.value })}
                  />
                  {errors[i] && <div className="text-error-500 text-xs mt-1">{errors[i]}</div>}
                </td>
                <td className="py-1 text-center">
                  <input
                    type="checkbox"
                    checked={v.required}
                    disabled={readOnly}
                    aria-label={`必填 ${i + 1}`}
                    onChange={(e) => update(i, { required: e.target.checked })}
                  />
                </td>
                <td className="py-1 pr-2">
                  <input
                    className={inputClass}
                    value={v.description ?? ""}
                    disabled={readOnly}
                    aria-label={`说明 ${i + 1}`}
                    onChange={(e) => update(i, { description: e.target.value })}
                  />
                </td>
                {!readOnly && (
                  <td className="py-1 text-center">
                    <button
                      type="button"
                      className="text-gray-400 hover:text-error-500"
                      aria-label={`删除变量 ${i + 1}`}
                      onClick={() => onChange(variables.filter((_, j) => j !== i))}
                    >
                      ✕
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
