package harmony.command

import it.unimi.dsi.fastutil.objects.*
import org.bukkit.*
import org.bukkit.command.*
import org.bukkit.entity.*

open class Instructor(name: String) : Command(name.trim().lowercase()), Instructable {
  
  /** Simple holder class for help commands */
  companion object {
    /** All sintaxes of valid help commands. */
    val HELP_SINTAXES = setOf("help", "ajuda", "?")
  }
  
  /**
   * The current performer action of this instructable object.
   *
   * This is a lambda function that will be executed when the instructable command is performed.
   * It is invoked on an [Context] object.
   */
  override lateinit var executor: Executor
  
  /**
   * The current completer of this instructable object.
   *
   * This is a lambda function that will be executed when the instructable command tab complete is performed.
   * It is invoked on an [Context] object.
   */
  override var completer: Completer = SmartCompleter
  
  /**
   * Returns the type of [Sender] that can perform this instructable.
   *
   * This property defines which sender type (e.g., player, console, or both) is allowed to execute
   * the instructable command.
   */
  override var sender = Sender.ALL
  
  /**
   * The list of alternate subcommands of this instructable.
   *
   * A mutable list that holds any subcommands (children) that belong to this instructable. Each
   * child command is represented by a [ChildrenInstructor].
   */
  override var childrens: MutableList<ChildrenInstructor> = ObjectArrayList(4)
  
  /**
   * The lookup table of subcommands of this instructable.
   *
   * A mutable map that holds any subcommands (children) that belong to this instructable. Each
   * child command is represented by a [ChildrenInstructor].
   *
   * This map is used to quickly access subcommands by their names or alias when searching
   * for them while executing the command.
   */
  override var childrensLookup: MutableMap<String, ChildrenInstructor> = Object2ObjectOpenHashMap(8)
  
  /**
   * The maximum number of arguments that can be passed to this instructable.
   *
   * Defines the maximum number of arguments that the instructable command can accept.
   * A negative value (e.g., -1) indicates no limit on the number of arguments.
   *
   * @return The maximum number of arguments that can be passed to this instructable.
   */
  override var maxArgs = -1
  
  /**
   * Gets the cached fully qualified name of this instructable.
   *
   * This function returns the fully qualified name of this instructable, which is a combination of
   * the name of the command and the names of any subcommands that belong to it.
   *
   * @return The fully qualified name of this instructable.
   */
  override var fullyName = ""
  
  /**
   * Gets the usage arguments of this instructable.
   *
   * This function returns the usage arguments of this instructable, which is a string that
   * describes the arguments that the instructable command accepts.
   *
   * @return The usage arguments of this instructable.
   */
  override var usageArguments = ""
  
  /**
   * Gets the required permission to access the help usage of this instructable.
   *
   * If the sender does not have the required permission to access the help usage of this
   * instructable, the help usage of this instructable will not be displayed.
   *
   * @return The required permission, or null/blank if no permission is required.
   */
  override var helpPermission: String? = null
  
  /**
   * Creates a new [Instructor] with the given names.
   *
   * The first name is the name, the rest of the names are the aliases of the new [Instructor].
   *
   * @param names The names of the new [Instructor].
   */
  constructor(names: List<String>) : this(names.first()) {
    aliases = names.drop(1)
  }
  
  /** The message that will be sent to the sender when permission is denied. */
  init {
    permissionMessage = "§cEste comando não foi encontrado."
  }
  
  /**
   * Executes the instructable command.
   *
   * This function is responsible for executing the instructable command based on the sender,
   * the name of the command, and any arguments passed.
   *
   * ## Behavior:
   * 1. Searches for a subcommand with the given name.
   *
   * 2. If a subcommand is found, the subcommand is executed and back to the previous behavior `1`.
   * Otherwise, this command is executed and followed to the next behavior `3`.
   *
   * 3. Checks the nature of [sender] based on [Instructor.sender].
   *
   * 4. Checks if the sender has the required permission if any is defined.
   *
   * 5. Checks if the command has a max number of arguments and the number of arguments passed is less
   * than the max number of arguments.
   *
   * 6. Checks if [executor] has defined or the first argument is a help command.
   *
   * 7. Executes the [executor] lambda function.
   *
   * @param sender The sender executing the command.
   * @param name The name of the command.
   * @param args The arguments passed with the command.
   * @return `true` if the command was executed successfully, `false` otherwise.
   */
  override fun execute(sender: CommandSender, name: String, args: Array<out String>): Boolean {
    val firstArg = args.getOrNull(0)
    
    if (childrens.isNotEmpty() && firstArg != null) {
      val child = findChildren(firstArg)
      if (child != null) {
        return child.execute(sender, "$name $firstArg", args.copyOfRange(1, args.size))
      }
    }
    
    if (!this.sender.check(sender)) {
      sender.sendMessage(this.sender.message)
      return false
    }
    
    if (!testPermissionSilent(sender)) {
      sender.sendMessage(permissionMessage)
      return false
    }
    
    if (maxArgs >= 0 && args.size > maxArgs) {
      sender.sendMessage(usage)
      return false
    }
    
    if (!this::executor.isInitialized || isHelpArg(firstArg) && childrens.isNotEmpty()) {
      if (helpPermission.isNullOrBlank() || sender.hasPermission(helpPermission)) {
        if (sender is Player) {
          sendHelpToPlayer(sender)
        } else {
          sendHelpToConsole(sender)
        }
      } else {
        sender.sendMessage(permissionMessage)
      }
      return false
    }
    
    try {
      executor.execute(this.sender.createContext(sender, this, args))
      return true
    } catch (e: InstructorStop) {
      return false
    } catch (e: InstructorError) {
      sender.sendMessage(e.message)
      return false
    } catch (e: Exception) {
      if (sender.isOp /*&& sender !is ConsoleCommandSender*/) {
        sender.sendMessage("§cUm erro inesperado ocorreu: '${e.message}'")
        // TODO: add ability to toggle optional strack tracing sended to players to track errors directly
        //sender.sendMessage("§8${e.stackTraceToString()}")
      } else {
        sender.sendMessage("§cUm erro inesperado ocorreu. Contate um Administrador.")
      }
      Bukkit.getConsoleSender().sendMessage("§cErro ao executar o comando '$fullyName'. Executado por ${sender.name}")
      e.printStackTrace()
      return false
    }
  }
  
  /**
   * Tab completes the instructable command.
   *
   * This function is responsible for tab-completing the instructable command based on the sender,
   * the name of the command, and any arguments passed.
   *
   * @param sender The sender executing the command.
   * @param alias The name of the command.
   * @param args The arguments passed with the command.
   * @return A mutable list of sorted, case-insensitive suggestions that match the last word.
   */
  override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String> {
    if (args.isEmpty()) {
      return emptyList()
    }
    
    if (childrens.isNotEmpty()) {
      val firstArg = args.get(0)
      val child = findChildren(firstArg)
      if (child != null) {
        return child.tabComplete(sender, "$name $firstArg", args.copyOfRange(1, args.size))
      }
    }
    
    return completer.suggest(this.sender.createContext(sender, this, args), args.last())
  }
  
  /**
   * Verifies if the provided [arg] is a help argument.
   *
   * This function checks if the provided [arg] is a help argument. A help argument is a
   * special argument that is used to display help information for a command.
   */
  override fun isHelpArg(arg: String?): Boolean {
    return arg != null && arg.lowercase() in HELP_SINTAXES
  }
  
  /**
   * Fetchs the fully qualified name of this instructable.
   *
   * This function returns the fully qualified name of this instructable, which is a combination of
   * the name of the command and the names of any subcommands that belong to it.
   *
   * @return The fully qualified name of this instructable.
   */
  override fun fetchFullyName(): String {
    return name
  }
  
  /**
   * Tests if the provided [target] has the required permission.
   *
   * @param target The target to test the permission for.
   * @return `true` if the target has the required permission, `false` otherwise.
   */
  override fun testPermissionSilent(target: CommandSender): Boolean {
    return if (permission.isNullOrBlank()) true else target.hasPermission(permission)
  }
  
  /**
   * Tests if the provided [target] has the required permission.
   *
   * @param target The target to test the permission for.
   * @return `true` if the target has the required permission, `false` otherwise.
   */
  override fun testPermission(target: CommandSender): Boolean {
    return testPermissionSilent(target)
  }
  
  /**
   * Sends the help message to [player].
   *
   * @param player The player to send the help message to.
   * @see sendHelpToConsole
   */
  fun sendHelpToPlayer(player: Player) {
    player.sendMessage("")
    player.sendMessage(" §b§l${name.uppercase()} COMANDOS")
    childrens.forEachIndexed { index, child ->
      if (child.showInHelp) {
        if (child.extraInfo) {
          player.spigot().sendMessage(child.getInformationalHelp(index == childrens.size - 1))
        } else {
          player.sendMessage(" §b§l${if (index == childrens.size - 1) "┗" else "┃"} §f/${child.fullUsage}")
        }
      }
    }
    player.sendMessage("")
  }
  
  /**
   * Sends the help message to the console.
   *
   * @param sender The sender of the help message.
   * @see sendHelpToPlayer
   */
  fun sendHelpToConsole(sender: CommandSender) {
    sender.sendMessage("")
    sender.sendMessage(" §b§l${name.uppercase()} COMANDOS")
    childrens.forEachIndexed { index, child ->
      if (child.showInHelp) {
        sender.sendMessage(" §b§l${if (index == childrens.size - 1) "┗" else "┃"} §f/${child.fullUsage}")
      }
    }
    sender.sendMessage("")
  }
}
