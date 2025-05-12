# 椒盐音频标签 Salt Audio Tag

## 这是什么？

开发目标：成为一个跨平台（Android、Windows、Linux 和 macOS）音频标签编辑器。

开发状态：早期开发中。

## 项目结构

基于 Compose Multiplatform 开发，IO 操作使用 kotlinx-io 库。

- composeApp：App UI
- core：标签

## 格式支持

| 格式   | 流信息 | 文本元数据 | 图片 |
|------|-----|-------|----|
| FLAC | 读   | 读/写   | 读  |

## 核心库使用

[![Maven Central](https://img.shields.io/maven-central/v/io.github.moriafly/salt-audiotag)](https://search.maven.org/search?q=g:io.github.moriafly)

## 开源协议

```
Salt Audio Tag
Copyright (C) 2025 Moriafly

This library is free software; you can redistribute it and/or modify it under the terms of the
GNU Lesser General Public License as published by the Free Software Foundation; either version
2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library;
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA
```