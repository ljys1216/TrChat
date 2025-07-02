# Product Context

This file provides a high-level overview of the project and the expected product that will be created. Initially it is based upon projectBrief.md (if provided) and all other available project-related information in the working directory. This file is intended to be updated as the project evolves, and should be used to inform all other modes of the project's goals and context.
2025-07-02 19:21:56 - Log of updates made will be appended as footnotes to the end of this file.

*

## Project Goal

*   扩展 TrChat 的颜色处理能力，使其兼容 MiniMessage 格式。

## Key Features

*   添加一个可配置的选项，用于在 MiniMessage 和 TrChat/Taboolib 的原生颜色代码之间切换。
*   实现一个解析器，将 MiniMessage 格式的字符串转换为 Minecraft 的 `Component` 对象。
*   确保新功能与现有的颜色处理逻辑（渐变、RGB 等）无缝集成或提供明确的替代方案。

## Overall Architecture

*   