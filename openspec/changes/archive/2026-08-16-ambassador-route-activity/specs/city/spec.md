# city delta（ambassador-route-activity）

## ADDED Requirements

### Requirement: 地图下架对路线与活动级联生效
城市（地图）下架后，该城市下的路线与活动 SHALL 在 app 端不可见（列表过滤、详情 404）；web 端下架确认提示 SHALL 在既有「商户、Banner、推荐清单」口径上补充「路线、活动」。

#### Scenario: 下架城市后 app 端路线与活动不可见
- **GIVEN** 一个城市下有可见路线与上线活动，随后该城市被下架
- **WHEN** app 端按该城市查路线列表、活动列表及各自详情
- **THEN** 列表为空；详情返回 404

#### Scenario: web 下架确认提示包含路线与活动
- **GIVEN** 地图管理页存在一个上架城市
- **WHEN** 点击下架该城市
- **THEN** 确认弹窗文案包含商户、Banner、推荐清单、路线、活动的级联下架说明
