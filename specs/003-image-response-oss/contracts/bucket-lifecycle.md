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
- 服务端 bind 时 `delete images/<uuid>.<ext>` 是 best-effort；删除失败由该 lifecycle 兜底。

## 服务端绑定时行为

- `ObjectKeyValidator.validateAndBind`：
  - 输入 `images/<uuid>.<ext>` → `copyObject` 到 `bound/<uuid>.<ext>` → `deleteObject(images/<uuid>.<ext>)` → 返回 `bound/<uuid>.<ext>`
  - 输入 `bound/<uuid>.<ext>` → head 校验存在 → 直接返回

## RAM Role 权限

- STS Role 仅授权 `oss:PutObject` 到 `acs:oss:*:*:<bucket>/images/*`（资源前缀限定）。
- 服务端主账号 AK 拥有 `oss:GetObjectMeta`、`oss:GetObject`（用于签名）、`oss:CopyObject`、`oss:DeleteObject`，作用于整个 bucket。

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
