# System Patterns *Optional*

This file documents recurring patterns and standards used in the project.
It is optional, but recommended to be updated as the project evolves.
2025-07-02 19:22:35 - Log of updates made.

*

## Coding Patterns

*   

---
### Lazy-Evaluation Logging
[2025-07-02 20:52:49] - For performance-critical logging, especially debug messages, use a function that accepts a lambda (`() -> Any?`) instead of a direct string. This ensures the log message string is only constructed if the logging condition (e.g., debug mode being enabled) is met, avoiding unnecessary object creation and string concatenation in production environments.

**Example:**
```kotlin
// Recommended
fun debug(message: () -> Any?) {
    if (Settings.options.debug) {
        info("[Debug] " + message.invoke().toString())
    }
}

// Usage
TrChat.debug { "Processing message for player ${player.name}" }
```

## Architectural Patterns

*   

---
### 最终渲染模式 (Finalization Rendering Pattern)
[2025-07-02 21:52:00] - 对于需要根据上下文（如用户权限）进行最终渲染（如颜色处理）的数据，应将渲染逻辑收敛到数据模型的最终输出点。这意味着在数据对象（如 `Text.kt`, `Style.kt`）将其内容提供给渲染引擎（如 Component 构建器）之前的最后一刻，才应用这些渲染逻辑。

**Rationale:**
*   **一致性**: 确保所有数据都经过同样、唯一的渲染流程，避免因在数据流的不同阶段进行处理而导致的不一致性。
*   **可维护性**: 将渲染逻辑集中在一个地方，使得修改和调试变得简单直观。
*   **职责清晰**: 数据模型的核心职责是持有和提供数据。渲染逻辑被明确地放在其生命周期的末端，符合单一职责原则。

**Example (Colorization):**
```kotlin
// Data class responsible for providing text content
data class Text(val content: String, ...) {
    fun content(sender: Player): String {
        // ... other processing like variable replacement
        // The VERY LAST step before returning the string is to apply color
        return processedContent.colorize(sender) 
    }
}

// Extension function that encapsulates the rendering logic
fun String.colorize(sender: Player): String {
    return MessageColors.replaceWithPermission(sender, this)
}
```
## Testing Patterns

*

---
### Unified Logger Pattern
[2025-07-02 21:13:43] - For modular projects, a centralized, globally accessible logger (`TrChatLogger`) provides a consistent and maintainable logging strategy. It decouples logging implementation from individual components, allowing for global control (e.g., via a single debug switch) and standardized log formats. Using lazy evaluation (lambda-based messages) is crucial to prevent performance degradation in production environments.

**Example:**
```kotlin
// In: project/common/src/main/kotlin/me/arasple/mc/trchat/util/TrChatLogger.kt
object TrChatLogger {
    fun debug(message: () -> Any?) {
        if (Settings.options.debug) {
            // Implementation to print to console
        }
    }
    // ... other methods like info, warn
}

// Usage in any module:
TrChatLogger.debug { "Database connection established for ${user.name}" }
```