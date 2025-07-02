# TrChat MiniMessage 功能指南

TrChat 现已支持 [MiniMessage](https://docs.adventure.kyori.net/minimessage/format.html) 语法，为您提供更强大、更灵活的文本格式化能力。本文档将指导您如何启用和使用此功能。

## 1. 如何启用 MiniMessage

要启用 MiniMessage 功能，您需要在服务器的 `settings.yml` 文件中进行配置。

- 打开 `plugins/TrChat/settings.yml` 文件。
- 找到 `Color` 部分，并将 `MiniMessage` 的值设置为 `true`。

```yaml
Color:
  # ... 其他颜色设置
  MiniMessage: true
```

修改并保存文件后，请重启服务器或使用 `trchat reload` 命令重载插件配置，以使更改生效。

## 2. 所需权限

为了能够使用 MiniMessage 语法，用户必须拥有特定的权限。

- **权限节点:** `trchat.color.minimessage`

请确保已将此权限授予希望使用该功能的用户或用户组。

## 3. MiniMessage 语法示例

启用功能并获得权限后，您就可以在聊天消息中使用 MiniMessage 标签了。以下是一些简单的示例：

- **纯色文本:**
  ```
  <gold>这段文字将显示为金色。</gold>
  ```

- **渐变色文本:**
  ```
  <gradient:blue:red>这段文字将从蓝色渐变到红色。</gradient>
  ```

- **彩虹色文本:**
  ```
  <rainbow>这段文字将以彩虹色显示。</rainbow>
  ```

- **组合使用:**
  ```
  <i:gold>这段文字是金色的斜体。</i>
  ```

更多高级用法和标签，请参阅官方 [MiniMessage 文档](https://docs.adventure.kyori.net/minimessage/format.html)。

## 4. 与 TrChat 原生功能兼容

MiniMessage 语法可以与 TrChat 的其他原生功能（如 `@提及`）无缝协作。

**示例:**

假设您想提及一位名为 `PlayerA` 的玩家，并使用渐变色高亮您的消息：

```
<gradient:green:yellow>大家好，快看 @PlayerA 的精彩操作！</gradient>
```

TrChat 会正确解析 `@PlayerA` 并应用 MiniMessage 的渐变色效果，同时触发对该玩家的提及通知。

---

希望这份文档能帮助您更好地使用 TrChat 的 MiniMessage 功能！