# city delta（article-and-featured-feed）

## ADDED Requirements

### Requirement: 地图下架对精选推荐级联生效
城市（地图）下架后，关联该城市的精选推荐 SHALL 在 app 端信息流中不可见；web 端下架确认提示 SHALL 在既有「商户、Banner、推荐清单、路线、活动」口径上补充「精选推荐」。

#### Scenario: 下架城市后 app 端精选推荐不可见
- **GIVEN** 某上架城市关联有上线的精选推荐，随后该城市被下架
- **WHEN** app 端查精选推荐信息流列表
- **THEN** 列表不含该城市的推荐条目

#### Scenario: web 下架确认提示包含精选推荐
- **GIVEN** 地图管理页存在一个上架城市
- **WHEN** 点击下架该城市
- **THEN** 确认弹窗文案包含商户、Banner、推荐清单、路线、活动、精选推荐的级联下架说明
