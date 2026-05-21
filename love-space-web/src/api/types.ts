/**
 * 与后端 PageResponseMapper.PageResponse 对齐的分页结构。
 * page 为 1 基。
 */
export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/** 生理周期枚举（与后端 Period 一致）。 */
export type Period = "MENSTRUAL" | "FOLLICULAR" | "OVULATION" | "LUTEAL";

export const PERIOD_VALUES: Period[] = [
  "MENSTRUAL",
  "FOLLICULAR",
  "OVULATION",
  "LUTEAL",
];

export const PERIOD_LABEL: Record<Period, string> = {
  MENSTRUAL: "经期",
  FOLLICULAR: "卵泡期",
  OVULATION: "排卵期",
  LUTEAL: "黄体期",
};
