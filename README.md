# Command framework

## Introduction

A highly productive command framework written in Kotlin. Commands in bukkit design is very robust, the main point of this framework, is to eliminate all of this complication.

## How to use
command-framework is stored in jitpack repository, so all you need is here:
```gradle
repositories {
  maven("https://jitpack.io")
}

dependencies {
  implementation("com.github.networkharmony:command-framework:1.1.0")
}
```

## 1.1.0 Features:
### News
- Enhanced asynchronous command creation
- Added support to tab completion
- Added extra permission to help command

### Break-changes
- Splitted off the command executor into a separate interface
- Renamed `Argumentable` to `Context`
- Renamed some functions to be more consistent with their functionality

### Fixes
- Fixed (possible) NPE when accessing the help command

## Wiki:
[wiki](https://github.com/networkharmony/command-framework/wiki)
