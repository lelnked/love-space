# Contract: OSS Bucket Lifecycle 与前缀策略

## 前缀划分

| 前缀 | 含义 | 写入者 | 生命周期 |
|---|---|---|---|
| `images/<uuid>.<ext>` | 直传落地区；尚未绑定任何业务实体 | 浏览器（凭 STS） | 24 小时后自动删除 |
| `bound/<uuid>.<ext>` | 已绑定业务实体；持久保存 | admin 服务端（绑定时 copy 进来） | 永久保留 |

## Lifecycle 规则（OSS 控制台 / Terraform）

```json
{
  "Rules": [
    {
      "ID": "expire-unbound-images",
      "Prefix": "images/",
      "Status": "Enabled",
      "Expiration": { "Days": 1 }
    }
  ]
}
```

- 规则**只覆盖 `images/` 前缀**；`bound/` 永远不在 lifecycle 删除范围内。
- 1 天为 OSS lifecycle 最小粒度（按 UTC 自然日计算，实际寿命 24–48h，可接受）。
- 服务端 bind 时**不删除** `images/<uuid>.<ext>`（只 copy 到 `bound/`）；`images/` 原对象一律由该 lifecycle 在 24h 后回收。这样 bind 不产生不可回滚副作用，业务事务回滚后原图仍在、可重试。

## 服务端绑定时行为

- `ObjectKeyValidator.validateAndBind`：
  - 输入 `images/<uuid>.<ext>` → `copyObject` 到 `bound/<uuid>.<ext>`（**保留 `images/` 原对象，不 delete**）→ 返回 `bound/<uuid>.<ext>`。`images/` 原对象交给 lifecycle 24h 回收。
  - 输入 `bound/<uuid>.<ext>` → head 校验存在 → 直接返回
- **bound/ 孤儿**：业务请求若在绑定后回滚（如多图中某张校验失败），先前已 `copyObject` 到 `bound/` 的对象会暂时无业务引用。因 `boundKey` 由源 `uuid` 唯一决定，用同一 objectKey 重试成功时会复用该对象（不产生新孤儿）；仅彻底放弃提交才残留。`bound/` 不在 lifecycle 范围，如需清理可另配长周期规则或定期巡检（非本特性强约束）。

## RAM Role 权限

- STS Role 仅授权 `oss:PutObject` 到 `acs:oss:*:*:<bucket>/images/*`（资源前缀限定）。
- 服务端主账号 AK 拥有 `oss:GetObjectMeta`、`oss:GetObject`（用于签名）、`oss:CopyObject`，作用于整个 bucket。（绑定不再调用 `DeleteObject`；`images/` 回收交给 lifecycle，主 AK 不需要删权限。）

## 监控建议（非本特性强约束）

- 监控 `images/` 前缀对象总量，异常增长可能是孤儿；正常应保持低水位（仅 24h 内未绑定的对象）。
- 监控 RAM Role AssumeRole QPS 异常。

## 控制台操作步骤（一次性）

1. OSS 控制台 → 选定 bucket → "基础设置" → "生命周期" → 新建规则：
   - 策略名：`expire-unbound-images`
   - 应用范围：前缀 `images/`
   - 文件过期：1 天 → 删除
2. RAM 控制台 → 创建角色 `LoveSpaceOssUploader`，信任策略为主账号；附加自定义策略 `LoveSpaceOssPutImagesOnly`：
   ```json
   {
     "Version": "1",
     "Statement": [{
       "Effect": "Allow",
       "Action": ["oss:PutObject"],
       "Resource": ["acs:oss:*:*:<bucket>/images/*"]
     }]
   }
   ```
3. 记录 `roleArn` 填入 `app.storage.sts.role-arn`。
