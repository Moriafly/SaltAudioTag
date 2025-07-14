# Salt Audio Tag

[](https://search.maven.org/search?q=g:io.github.moriafly)
[](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html)

## 📖 About

**Salt Audio Tag** aims to be a cross-platform audio tag editor for Android, iOS, Windows, Linux, and macOS.

This project is currently in the **early stages of development**.

## 🏗️ Project Structure

The project is built using **Compose Multiplatform** for the user interface and **kotlinx-io** for handling I/O operations.

- `composeApp`: Contains the application's UI code.
- `core`: The core module for audio tagging logic.

## 🎶 Format Support

| Format | Streaminfo |  Metadata  | Pictures |
|:-------|:----------:|:----------:|:--------:|
| FLAC   |    Read    | Read/Write |   Read   |
| CDA    |    Read    |    Read    |          |

## 🚀 Usage (Core Library)

To use the core library in your project, add the following dependency.

**Gradle (Kotlin DSL)**

```kotlin
implementation("io.github.moriafly:salt-audiotag:LATEST_VERSION")
```

*Please replace `LATEST_VERSION` with the version number from the Maven Central badge above.*

## 📜 License

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