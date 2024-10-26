package harmony.command

import org.bukkit.command.*
import org.bukkit.entity.*

/**
 * Represents a sender entity for commands, which can be either a player or the console.
 *
 * This class serves as a base for specific senders like `PlayerSender` and `ConsoleSender`.
 * Each sender has a message and display name associated with it, and can be used to check
 * whether a specific `CommandSender` is valid to perform a command.
 *
 * @property message The message that will be shown to the sender if they cannot execute the command.
 * @property display The display name of the sender, used for visual representation.
 */
open class Sender(val message: String, val display: String) {
  
  companion object {
    /** Predefined instance for player-specific command senders. */
    val PLAYER get() = PlayerSender
    
    /** Predefined instance for console-specific command senders. */
    val CONSOLE get() = ConsoleSender
    
    /** Instance for both players and console senders, with a generic message. */
    val ALL = Sender("", "Jogadores e Console")
  }
  
  /**
   * Checks if the given sender is allowed to execute a command.
   *
   * This method can be overridden by specific implementations to apply more detailed checks.
   * By default, this method allows any sender to pass.
   *
   * @param sender The sender executing the command.
   * @return `true` if the sender is allowed to execute the command, otherwise `false`.
   */
  open fun check(sender: CommandSender): Boolean {
    return true
  }
  
  /**
   * Creates a executor that handles the execution of a command.
   *
   * This method is responsible for creating an `Argumentable` performer which executes
   * the command based on the sender, the instructor, and any arguments passed.
   *
   * @param sender The sender executing the command.
   * @param instructor The instructor managing the command execution process.
   * @param args The arguments provided with the command.
   * @return An instance of `Argumentable` responsible for handling the command.
   */
  open fun createExecutor(sender: CommandSender, instructor: Instructor, args: Array<out String>): Argumentable {
    return DefaultExecutor(sender, instructor, args)
  }
}

/**
 * A specific sender implementation for players.
 *
 * This class only allows players to execute commands and will return an appropriate
 * message if the command is executed by a non-player entity.
 */
object PlayerSender : Sender("§cApenas jogadores podem executar este comando.", "Jogadores") {
  
  /**
   * Checks if the given sender is a player.
   *
   * @param sender The sender executing the command.
   * @return `true` if the sender is a player, otherwise `false`.
   */
  override fun check(sender: CommandSender): Boolean {
    return sender is Player
  }
  
  /**
   * Creates a executor specific to player commands.
   *
   * @param sender The player executing the command.
   * @param instructor The instructor managing the command execution process.
   * @param args The arguments provided with the command.
   * @return A `PlayerPerformer` responsible for handling the command execution.
   */
  override fun createExecutor(sender: CommandSender, instructor: Instructor, args: Array<out String>): Argumentable {
    return PlayerExecutor(sender, instructor, args)
  }
}

/**
 * A specific sender implementation for the console.
 *
 * This class only allows the console to execute commands and will return an appropriate
 * message if the command is executed by a non-console entity.
 */
object ConsoleSender : Sender("§cApenas o Console pode executar este comando.", "Console") {
  
  /**
   * Checks if the given sender is the console.
   *
   * @param sender The sender executing the command.
   * @return `true` if the sender is the console, otherwise `false`.
   */
  override fun check(sender: CommandSender): Boolean {
    return sender is ConsoleCommandSender
  }
  
  /**
   * Creates a executor specific to console commands.
   *
   * @param sender The console executing the command.
   * @param instructor The instructor managing the command execution process.
   * @param args The arguments provided with the command.
   * @return A `ConsolePerformer` responsible for handling the command execution.
   */
  override fun createExecutor(sender: CommandSender, instructor: Instructor, args: Array<out String>): Argumentable {
    return ConsoleExecutor(sender, instructor, args)
  }
}
