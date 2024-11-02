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
  implementation("com.github.networkharmony:command-framework:1.0.6")
}
```

## Understanding the framework
Here below, will follow the main entry of the framework design:

### The two types of command
On this framework, we can separate commands by two types: `complex` and `simple`:

1. Simple commands is a more directive to their execution.
2. Complex allows more customization on the command, such sub-commands and more.

### Simple Command example:
Here a simple example, where we automatically register a command to be used in the server with name ``"example"`` and a alias of ``"examples"`` ``(aliases is splitted by '|')``.
Here, we allow only players to execute the command by `sender`, with a permission of ``"harmony.example"``, a simple usage and a max arguments possible by `1`.

```kt
Command("example|examples", sender = Sender.PLAYER, permission = "harmony.example", usage = "<some-arg>", max = 1, async = true) {
  msg("§aHello, ${player.name}!")
}
```

#### The `usage` parameter is only for arguments legibility, not a descriptive reason of what the command do.
#### The `async` parameter is for executing the command in asynchronous instead of the main thread. All commands builders support this

### Complex command example:
Complex command follows the same structure as simple command:

```kt
ComplexCommand("example|examples", sender = Sender.PLAYER, permission = "harmony.example", usage = "<some-arg>", max = 1) {
  // the execution of the command
  performs {
    msg("§aHello, ${player.name}!")
  }
    
  // allows more customization
  permissionMessage = "§cYou do not have permission to execute this command."
}
```

### Complex commands support sub-commamnds by:

```kt
ComplexCommand("example|examples", sender = Sender.PLAYER, permission = "harmony.example", usage = "<some-arg>", max = 1) {
  // the execution of the command
  performs {
    msg("§aHello, ${player.name}!")
  }

  // sub-commands:
    
  // `sub` is a simple command
  sub("test", permission = "harmony.example.test", showHelp = true, extraInfo = true) {
    msg("§aThis is a test comand: /example test")
  }
    
  // `complex` is an complex command
  complex("another|other|others|anothers", sender = Sender.CONSOLE, showHelp = false) {
    performs {
      msg("§aThis is a test comand: /example another")
    }
      
    sub("another-test|more-example") {
      msg("§aThis is another test command: /example another another-test")
    }
  }  
}
```

#### Undestanding `showHelp` and `extraInfo` parameters on sub-commands:
When creating a complex command, automatically will generate a help command with all their childrens ``(sub-commands)`` with the name of the command and their `usage`.
The example above, generate a help command like this:

![image](https://github.com/user-attachments/assets/46a91570-8eb0-46ea-bc70-02e1eef5e4d9)

The parameter `showHelp` in sub-commands is to enable/disable if the command will show in the help command. Defaults go to `true`.
The parameter `extraInfo` in sub-commands is to enable/disable if the command show extra information of the command. Defaults go to `true`. A example of `extraInfo` generated:

![image](https://github.com/user-attachments/assets/291ff77a-dcd9-4901-bd8c-9bf8f8599099)

If `extraInfo` is false, just a simple message will be generated.

### Showing the help command:
The help command is automatically created, and can be viewed by these 3 arguments:

1. `/example help`
2. `/example ajuda`
3. `/example ?`

#### All complex commands have a help command.

You can also show the example command calling the specified function:
```kt
// to players
sendHelpToPlayer(player: Player)

// to console (or players if no need to implement the extraInfo)
sendHelpToConsole(sender: CommandSender)
```

## Arguments
A lot of arguments is supported in command-framework. Before we show how to use the arguments, we must know the 3 types of argument predefined and their behavior:

### Nullable arguments
Nullable arguments is a argument that returns null if not specified and if is not a valid representation.

### Optional arguments
Optional arguments is a argument that returns null if not specified, but stops the command execution sending a message for the player if is not a valid representation.

### Required arguments
Required arguments is a argument that `never` returns null. They must be specified and must be a valid representation. Stopping the command execution sending a message for the player
if they not pass in the check.

### Arguments are sequencial and indexed
This means that if you not specify the `index` parameter at the argument function, the selected index will be the internal cursor at the arguments array.

### Using arguments:
Here a simple example using all the 3 types of arguments for getting an Int argument:

```kt
Command("example") {
  val requiredArg: Int = int(empty = "Specify an integer", found = "Invalid integer", permission = "example.arg1") // argument at index 0
  val optionalArg: Int? = optionalInt() // argument at index 1
  val nullableArg: Int? = nullableInt() // argument at index 2
}
```

All arguments can specify a permission to access the argument, if the player don't have the permission, they cannot use the argument.
`empty` parameter is the message to send to the player if they not specify the argument.
`found` parameter is the message to send to the player if they not specified a valid `Int`.

## All predefined arguments:
Here a list of all predefined arguments:

- String
- Char
- CharArray
- Boolean
- Byte
- Short
- Int
- Long
- Float
- Double
- Player
- OfflinePlayer
- Gamemode
- Enchantment
- World
- Material
- MaterialData
- EntityType
- Sound

### Extra argument functions
Also we have an extra utilities for arguments such:

- Join (joining all passed arguments into a unique string, great for messages)
- Slicing array
- Slicing list
- Validating a boolean (stops the command with a message if the boolean returns false)

## Exceptions
We have 2 specifics exceptions for handling errors in commands:

`InstructorError`: A exception that stops the command execution and send a message to the player.
`InstructorStop`: A exception that just stops the command execution without sending not to the player.

### Using:

```kt
// just a simple example of a registry
val registry = HashMap<String, String>()
  
Command("example") {
  val key = string()
  // will stop execution if the key doesn't exist
  val value = registry[key] ?: fail("The registry doesn't contain the key '$key'.")
  // only called if the key exists
  msg("The current value is '$value'.")
}
```

### Not handled exceptions:
Exceptions that doens't is `InstructorStop` or `InstructorError` will notify the player with a message:

#### If player is OP:
![image](https://github.com/user-attachments/assets/cde183c9-4e3c-4455-8632-29a2ab68de82)

#### If player is NOT an OP
![image](https://github.com/user-attachments/assets/ef968ed2-409d-4969-be07-2ed12c017be3)

#### Console message:
![image](https://github.com/user-attachments/assets/cca49357-029e-42fa-a304-ccff6f83fe5c)

## Commons examples:
After we know almost everything about the framework, here some commons examples:

### Fly command
```kt
Command("voar|fly|voo", sender = Sender.PLAYER, permission = "harmony.fly", usage = "[jogador]", max = 1) {
  // a optional target if player have the permission "harmony.admin" to select who will fly
  // if not specified, select the sender (itself)
  val target = playerOrSender(permission = "harmony.admin")
    
  target.allowFlight = !target.allowFlight
  target.isFlying = false
    
  target.sendMessage("§aVoo: ${if (target.allowFlight) "ativado" else "desativado"}")
}
```

### Broadcast command
```kt
Command("broadcast", sender = Sender.ALL, permission = "harmony.broadcast") {
  // join all arguments into a single string. If arguments are empty, fail with the given message.
  Bukkit.broadcastMessage(joinNotEmpty("Specify the message to broadcast"))
}
```

## Current limitations:
A unique limitation of this framework, is that is not much customizable for messages. This will be implemented later in future versions.
