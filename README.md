# Yet Another Pico 8 Builder

YAP8B is Pico 8 builder written in Java. 
I made it because other builders, frankly, sucked.

## How to use

### Installation

For windows users, download release `yap8b.exe` file. 
You will need Java 8 or later to run the tool. 
Add the tool to your path variable: https://java.com/en/download/help/path.html

### Linux

Download `yap8b.jar` and either use `java -jar yap8b.jar` or create shell script
and add it to path.

## Building from source

Download java 8 jdk, maven and run `maven package`.

## Modules

### Init

Creates initial descriptor file. You can copy or create file manually, but 
this tool will create it for you. Simply run `yap8b init` and fill the necessary
info. It will create JSON descriptor file `project.p8j`, such as:

```bash
$ yap8b init
Name of the project: Test
Author: Me
Version: 1.0.1
Description: 
Project file /tmp/example/project.p8j generated!
```

Generated descriptor file:
```json
{
  "name": "Test",
  "author": "Me",
  "version": "1.0.1",
  "description": "",
  "main": null,
  "dependencies": {
    "__gfx__": null,
    "__gff__": null,
    "__label__": null,
    "__map__": null,
    "__sfx__": null,
    "__music__": null
  }
}
```

### Descriptor parts

`main`: path to a lua file. This file gets loaded and preprocessed. Must be 
filled.

`__gfx__`, `__label__`: path to either `.p8` file from which section is copied or
a `.png` image file (must be 128x128px or less).

`__gff__`, `__map__`, `__sfx__`, `__music__`: path to `.p8` file from which section
is copied.

## Build module

Invoked with `yap8b build`. Creates folder build and generates `"name".p8` file 
based on descriptor. 

## Lua preprocessing

Lua is preprocessed with simple comment macros. 
Comment macro must start on start of the line. 

### Macros

#### include

Use example:

```lua
-- include some/path/to/luafile.lua
```

Includes that file in the result lua source code. Every include is resolved only
once.

