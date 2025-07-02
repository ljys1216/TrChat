# Decision Log

This file records architectural and implementation decisions using a list format.
2025-07-02 19:22:27 - Log of updates made.

*
      
---
### Decision
[2025-07-02 19:27:48] - 采用基于配置的条件分支策略在 TrChat 中集成 MiniMessage 支持。

**Rationale:**
为了在不破坏现有功能的情况下引入 MiniMessage，最直接且风险最低的方法是添加一个配置开关。这使得服务器管理员可以根据自己的需求选择性地启用新功能。在核心的 `MsgComponent.kt` 中进行直接修改，可以避免过度设计，并使逻辑集中，易于理解和维护。

**Implications/Details:**
*   **配置文件**: 需要在 `settings.yml` 中添加 `use_minimessage: boolean` 选项。
*   **核心代码**: 需要修改 `MsgComponent.kt` 的 `build` 方法，根据 `use_minimessage` 的值选择 `MiniMessage.miniMessage().deserialize()` 或现有的颜色处理逻辑。
*   **依赖**: `build.gradle.kts` 文件需要添加 `net.kyori:adventure-text-minimessage` 依赖。
*   **错误处理**: MiniMessage 解析代码块必须包含 `try-catch` 机制，以处理无效输入。

---
### Decision (Debug)
[2025-07-02 20:22:20] - 修复聊天格式中 MiniMessage 不生效的问题

**Rationale:**
问题根源在于 `Text.process()` 方法过早地调用了 `.colorify()`，破坏了 MiniMessage 标签。同时，`Style.Hover.Text` 的处理逻辑也没有考虑 MiniMessage。解决方案是重构 `Text.kt`，分离出 `content()` 方法以获取原始文本，然后在 `JsonComponent.kt` 和 `Style.kt` 中根据 `Settings.colorType` 条件性地调用 MiniMessage 解析器。

**Details:**
*   **Affected components/files**:
    *   `project/runtime-bukkit/src/main/kotlin/me/arasple/mc/trchat/module/display/format/obj/Text.kt`
    *   `project/runtime-bukkit/src/main/kotlin/me/arasple/mc/trchat/module/display/format/JsonComponent.kt`
    *   `project/runtime-bukkit/src/main/kotlin/me/arasple/mc/trchat/module/display/format/obj/Style.kt`