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
 * @param completer The completer to be used for this command.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action A lambda to define the behavior of the [Instructor] after creation.
 *
 * @return The created [Instructor] instance with all the provided properties applied.
 */
internal fun internalBuildInstructor(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: Instructor.() -> Unit,
) = Instructor(name.split('|')).apply {
  this.sender = sender
  this.maxArgs = max
  if (permission != null) {
    this.permission = permission
  }
  fullyName = name
  if (usage != null) {
    usageArguments = usage
    this.usage = "§cUse: /${this.name} $usage."
  }
  if (completer != null) {
    this.completer = completer
  }
  if (helpPermission != null) {
    this.helpPermission = helpPermission
  }
  action(this)
}

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
 * @param completer The completer to be used for this child command.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action A lambda to define the behavior of the child [Instructor].
 *
 * @return The created [ChildrenInstructor] instance.
 */
internal fun internalBuildChildrenInstructor(
  parent: Instructor,
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  completer: Completer? = null,
  helpPermission: String? = null,
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
  if (completer != null) {
    this.completer = completer
  }
  if (helpPermission != null) {
    this.helpPermission = helpPermission
  }
  this.maxArgs = max
  action(this)
}

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
 * @param completer The completer to be used for this child command.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @return The created [ChildrenInstructor] instance attached to the parent.
 */
internal fun Instructor.internalAppendChildren(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  completer: Completer? = null,
  helpPermission: String? = null,
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
    completer = completer,
    helpPermission = helpPermission,
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
 * @param completer The completer to be used for this command.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun ComplexCommand(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: Instructor.() -> Unit,
): Instructor {
  val command = internalBuildInstructor(
    name,
    sender = sender,
    permission = permission,
    usage = usage,
    max = max,
    completer = completer,
    helpPermission = helpPermission,
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
 * @param completer The completer to be used for this command.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun Command(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: Context.() -> Unit,
): Instructor {
  return internalBuildInstructor(
    name,
    sender = sender,
    permission = permission,
    usage = usage,
    completer = completer,
    helpPermission = helpPermission,
    max = max
  ) {
    performs(action)
    register()
  }
}

/**
 * Registers a simplified command with no extra configuration.
 *
 * @param name The name of the command.
 * @param sender The type of sender that can execute the command.
 * @param permission Optional permission required to execute the command. If null, no permission is required.
 * @param usage Optional usage instructions for the command.
 * @param max The maximum number of arguments allowed for the command. Defaults to -1 (unlimited).
 * @param completer The completer to be used for this command.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action A lambda to define the behavior of the command.
 *
 * @return The created and registered [Instructor] instance.
 */
fun CommandAsync(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: suspend Context.() -> Unit,
): Instructor {
  return internalBuildInstructor(
    name,
    sender = sender,
    permission = permission,
    usage = usage,
    completer = completer,
    helpPermission = helpPermission,
    max = max
  ) {
    performsAsync(action)
    register()
  }
}

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
 * @param completer The completer to be used for this instructor, if any.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action The action to be performed by this instructor.
 * @return A [ChildrenInstructor] representing the created alternate instructor.
 */
fun Instructor.complex(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: Instructor.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  sender = sender,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
  completer = completer,
  helpPermission = helpPermission,
  action = action
)

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
 * @param completer The completer to be used for this sub-instructor.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action The performer action to be executed when this subcommand is called.
 * @return A [ChildrenInstructor] representing the created sub-instructor.
 */
fun Instructor.sub(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: Context.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  sender = sender,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
  completer = completer,
  helpPermission = helpPermission
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
 * @param completer The completer to be used for this sub-instructor.
 * @param helpPermission Optional permission required to execute the help command. If null, no permission is required.
 * @param action The performer action to be executed when this subcommand is called.
 * @return A [ChildrenInstructor] representing the created sub-instructor.
 */
fun Instructor.subAsync(
  name: String,
  sender: Sender = Sender.ALL,
  permission: String? = null,
  usage: String? = null,
  max: Int = -1,
  showHelp: Boolean = true,
  extraInfo: Boolean = true,
  completer: Completer? = null,
  helpPermission: String? = null,
  action: suspend Context.() -> Unit,
): ChildrenInstructor = internalAppendChildren(
  name,
  sender = sender,
  permission = permission,
  usage = usage,
  max = max,
  showHelp = showHelp,
  extraInfo = extraInfo,
  completer = completer,
  helpPermission = helpPermission
) {
  performsAsync(action)
}
