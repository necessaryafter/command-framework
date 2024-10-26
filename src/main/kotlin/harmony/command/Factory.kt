package harmony.command

import harmony.command.misc.*

/**
 * Creates an instance of [Instructor] with the specified properties.
 *
 * @param name The name of the command, which can contain multiple aliases separated by '|'.
 * @param sender The type of sender that can execute the command (either [Sender.PLAYER] or [Sender.ALL]).
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command. If specified, a formatted usage message will be generated.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param action A lambda to define the behavior of the [Instructor] after creation.
 *
 * @return The created [Instructor] instance with all the provided properties applied.
 */
internal fun internalBuildInstructor(
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  action: Instructor.() -> Unit,
) = Instructor(name.split('|')).apply {
  this.sender = sender
  this.maxArgs = max
  if (permission != null) this.permission = permission
  fullyName = name
  if (usage != null) {
    usageArguments = usage
    this.usage = "§cUse: /${this.name} $usage."
  }
  action(this)
}

/**
 * Overloaded version of [internalBuildInstructor] that allows specifying whether only players can execute the command.
 *
 * @param name The name of the command, which can contain multiple aliases separated by '|'.
 * @param onlyPlayers Whether only players can execute the command. If true, the sender is restricted to [Sender.PLAYER].
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command. If specified, a formatted usage message will be generated.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param action A lambda to define the behavior of the [Instructor] after creation.
 *
 * @return The created [Instructor] instance with all the provided properties applied.
 */
internal fun internalBuildInstructor(
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  action: Instructor.() -> Unit,
) = internalBuildInstructor(
  name,
  sender = if (onlyPlayers) Sender.PLAYER else Sender.ALL,
  permission = permission,
  usage = usage,
  max = max,
  action = action
)

/**
 * Creates an instance of [ChildrenInstructor] as a child of the specified [Instructor].
 *
 * @param parent The parent [Instructor] to which this child will be attached.
 * @param name The name of the child command, which can contain multiple aliases separated by '|'.
 * @param sender The type of sender that can execute the child command.
 * @param permission Optional permission required to execute the child command. Defaults to the parent's permission.
 * @param usage Optional usage instructions for the child command.
 * @param max The maximum number of arguments allowed for the child command. Defaults to -1 (unlimited).
 * @param showHelp Whether this child command should appear in help menus. Defaults to true.
 * @param extraInfo Whether to show extra information about this child command.
 * @param action A lambda to define the behavior of the child [Instructor].
 *
 * @return The created [ChildrenInstructor] instance.
 */
internal fun internalBuildChildrenInstructor(
  parent: Instructor,
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Instructor.() -> Unit,
) = ChildrenInstructor(parent, name.split('|')).apply {
  this.sender = sender
  this.permission = permission ?: parent.permission
  fullyName = fetchFullyName()
  showInHelp = showHelp
  this.extraInfo = extraInfo
  if (usage != null) {
    this.usageArguments = usage
    this.usage = usage.let { "§cUse: /$fullyName $it." }
  } else {
    this.usage = parent.usage
  }
  this.maxArgs = max
  action(this)
}

/**
 * Overloaded version of [internalBuildChildrenInstructor] allowing the restriction of the sender to only players.
 *
 * @param parent The parent [Instructor] to which this child will be attached.
 * @param name The name of the child command, which can contain multiple aliases separated by '|'.
 * @param onlyPlayers Whether only players can execute the child command. If true, the sender is restricted to [Sender.PLAYER].
 * @param permission Optional permission required to execute the child command. Defaults to the parent's permission.
 * @param usage Optional usage instructions for the child command.
 * @param max The maximum number of arguments allowed for the child command. Defaults to -1 (unlimited).
 * @param showHelp Whether this child command should appear in help menus. Defaults to true.
 * @param extraInfo Whether to show extra information about this child command.
 * @param action A lambda to define the behavior of the child [Instructor].
 *
 * @return The created [ChildrenInstructor] instance.
 */
internal fun internalBuildChildrenInstructor(
  parent: Instructor,
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Instructor.() -> Unit,
) = internalBuildChildrenInstructor(
  parent,
  name,
  sender = if (onlyPlayers) Sender.PLAYER else Sender.ALL,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
  action = action
)


/**
 * Creates a child command for an existing [Instructor].
 *
 * @param name The name of the child command.
 * @param sender The type of sender that can execute the child command.
 * @param permission Optional permission required to execute the child command. Defaults to the parent's permission.
 * @param usage Optional usage instructions for the child command.
 * @param max The maximum number of arguments allowed for the child command. Defaults to -1 (unlimited).
 * @param showHelp Whether this child command should appear in help menus. Defaults to true.
 * @param extraInfo Whether to show extra information about this child command.
 * @param action A lambda to define the behavior of the child [Instructor].
 * @return The created [ChildrenInstructor] instance attached to the parent.
 */
internal fun Instructor.internalAppendChildren(
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Instructor.() -> Unit,
): ChildrenInstructor {
  val children = internalBuildChildrenInstructor(
    this,
    name,
    sender = sender,
    permission = permission,
    usage = usage,
    max = max,
    showHelp = showHelp,
    extraInfo = extraInfo,
    action = action
  )
  
  addChildren(children)
  return children
}

/**
 * Creates a child command for an existing [Instructor].
 *
 * @param name The name of the child command.
 * @param onlyPlayers Whether only players can execute the child command. Defaults to false.
 * @param permission Optional permission required to execute the child command. Defaults to the parent's permission.
 * @param usage Optional usage instructions for the child command.
 * @param max The maximum number of arguments allowed for the child command.
 * @param showHelp Whether this child command should appear in help menus.
 * @param extraInfo Whether to show extra information about the child command.
 * @param action A lambda to define the behavior of the child command.
 *
 * @return The created [ChildrenInstructor] instance attached to the parent.
 */
fun Instructor.internalAppendChildren(
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Instructor.() -> Unit,
): ChildrenInstructor {
  val children = internalBuildChildrenInstructor(
    this,
    name,
    onlyPlayers = onlyPlayers,
    permission = permission,
    usage = usage,
    max = max,
    showHelp = showHelp,
    extraInfo = extraInfo,
    action = action
  )
  addChildren(children)
  return children
}

/**
 * Registers a complex command with multiple potential configurations.
 *
 * @param name The name of the command.
 * @param sender The type of sender that can execute the command.
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun ComplexCommand(
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  action: Instructor.() -> Unit,
): Instructor {
  val command =
    internalBuildInstructor(name, sender = sender, permission = permission, usage = usage, max = max, action = action)
  command.register()
  return command
}

/**
 * Registers a complex command with multiple potential configurations.
 *
 * @param name The name of the command.
 * @param onlyPlayers Whether only players can execute the command. Defaults to false.
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun ComplexCommand(
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  action: Instructor.() -> Unit,
): Instructor {
  val command = internalBuildInstructor(
    name,
    onlyPlayers = onlyPlayers,
    permission = permission,
    usage = usage,
    max = max,
    action = action
  )
  command.register()
  return command
}

/**
 * Registers a simplified command with no extra configuration.
 *
 * @param name The name of the command.
 * @param sender The type of sender that can execute the command.
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun Command(
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  action: Argumentable.() -> Unit,
): Instructor {
  val command = internalBuildInstructor(name, sender = sender, permission = permission, usage = usage, max = max) {
    performs(action)
  }
  
  command.register()
  return command
}

/**
 * Registers a simplified command with no extra configuration.
 *
 * @param name The name of the command.
 * @param onlyPlayers Whether only players can execute the command. Defaults to false.
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun Command(
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  action: Argumentable.() -> Unit,
): Instructor {
  val command = internalBuildInstructor(name, onlyPlayers = onlyPlayers, permission = permission, usage = usage, max = max) {
    performs(action)
  }
  
  command.register()
  return command
}

/**
 * Creates an alternate instructor of this instructable.
 *
 * This function is used to define a more complex instructor, allowing configuration of various
 * properties like the name, permission, usage, maximum arguments, and whether the command is
 * restricted to players only. It also provides an action to be executed by the instructor.
 *
 * @param name The name of the alternate instructor.
 * @param onlyPlayers Whether the instructor should be restricted to players only.
 * @param permission The permission required to execute the instructor, if any.
 * @param usage The usage instructions for the instructor, if any.
 * @param max The maximum number of arguments the instructor can accept. Defaults to -1 for unlimited.
 * @param showHelp Whether help should be shown for this instructor.
 * @param extraInfo Whether extra information should be shown for this instructor when help is shown.
 * @param action The action to be performed by this instructor.
 * @return A [ChildrenInstructor] representing the created alternate instructor.
 */
fun Instructor.complex(
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Instructor.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  onlyPlayers = onlyPlayers,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
  action = action
)

/**
 * Creates an alternate instructor with a specific sender type.
 *
 * This version of the `complex` function allows specifying a custom [Sender], instead of using the
 * `onlyPlayers` parameter. This makes it more flexible for defining who can execute the command
 * (e.g., players, console, or both).
 *
 * @param name The name of the alternate instructor.
 * @param sender The type of sender allowed to execute the instructor.
 * @param permission The permission required to execute the instructor, if any.
 * @param usage The usage instructions for the instructor, if any.
 * @param max The maximum number of arguments the instructor can accept. Defaults to -1 for unlimited.
 * @param showHelp Whether help should be shown for this instructor.
 * @param extraInfo Whether extra information should be shown for this instructor when help is shown.
 * @param action The action to be performed by this instructor.
 * @return A [ChildrenInstructor] representing the created alternate instructor.
 */
fun Instructor.complex(
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Instructor.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  sender = sender,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
  action = action
)

/**
 * Creates a simple sub-instructor with a specified performer action.
 *
 * This function creates a subcommand (child) for the current instructor. The [action] provided will
 * be used as the performer action, defining what happens when the subcommand is executed.
 *
 * @param name The name of the sub-instructor.
 * @param onlyPlayers Whether the sub-instructor should be restricted to players only.
 * @param permission The permission required to execute the sub-instructor, if any.
 * @param usage The usage instructions for the sub-instructor, if any.
 * @param max The maximum number of arguments the sub-instructor can accept. Defaults to -1 for unlimited.
 * @param showHelp Whether help should be shown for this sub-instructor.
 * @param extraInfo Whether extra information should be shown for this instructor when help is shown.
 * @param action The performer action to be executed when this subcommand is called.
 * @return A [ChildrenInstructor] representing the created sub-instructor.
 */
fun Instructor.sub(
  name: String,
  onlyPlayers: Boolean = false,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Argumentable.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  onlyPlayers = onlyPlayers,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo
) {
  performs(action)
}

/**
 * Creates a simple sub-instructor with a specified sender type and performer action.
 *
 * This version of the `sub` function allows specifying a custom [Sender], making it more flexible
 * for defining who can execute the subcommand (e.g., players, console, or both). The [action] provided
 * will be used as the performer action.
 *
 * @param name The name of the sub-instructor.
 * @param sender The type of sender allowed to execute the sub-instructor.
 * @param permission The permission required to execute the sub-instructor, if any.
 * @param usage The usage instructions for the sub-instructor, if any.
 * @param max The maximum number of arguments the sub-instructor can accept. Defaults to -1 for unlimited.
 * @param showHelp Whether help should be shown for this sub-instructor.
 * @param extraInfo Whether extra information should be shown for this instructor when help is shown.
 * @param action The performer action to be executed when this subcommand is called.
 * @return A [ChildrenInstructor] representing the created sub-instructor.
 */
fun Instructor.sub(
  name: String,
  sender: Sender,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  action: Argumentable.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  sender = sender,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
) {
  performs(action)
}
