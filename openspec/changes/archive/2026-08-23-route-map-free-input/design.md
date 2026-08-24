# design
表单新增 `mapName` 状态；提交时若 cityId 为空则取 `mapName.trim()`。编辑态回显历史值，但不可修改。后端只保留 `@NotNull`，去掉 `cityRepository.existsById(...)`。
