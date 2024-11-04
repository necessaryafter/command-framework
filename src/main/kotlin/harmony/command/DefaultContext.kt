package harmony.command

import org.bukkit.*
import org.bukkit.command.*
import org.bukkit.entity.*

/**
 * A basic implementation of [Context] for handling command execution logic.
 *
 * This class represents a context that interacts with a command sender (either a player or console) and
 * executes commands using the provided instructor and arguments.
 *
 * @property sender The entity executing the command.
 * @property instructor The instructor providing command details.
 * @property arguments The arguments passed along with the command.
 */
class DefaultContext(
  override val sender: CommandSender,
  override val instructor: Instructor,
  override val arguments: Array<out String>,
) : Context {
  
  /** The name of the command being executed, derived from the instructor. */
  override val command: String get() = instructor.name
  
  /** Indicates if the sender is the console. */
  override val isConsole: Boolean = sender is ConsoleCommandSender
  
  /** Indicates if the sender is a player. */
  override val isPlayer: Boolean get() = !isConsole
  
  /** Provides the console sender instance. */
  override val console: ConsoleCommandSender get() = Bukkit.getConsoleSender()
  
  /**
   * Casts the sender to a player.
   * @throws ClassCastException if the sender is not a player.
   */
  override val player: Player get() = sender as Player
  
  /** Tracks the current index of the argument being processed. */
  override var currentIndex: Int = 0
}

/**
 * A specific context implementation for console-based command execution.
 *
 * This class handles commands executed exclusively by the console. It provides behavior for console-only
 * executions, ensuring that player-related methods are not accessible.
 *
 * @property sender The console executing the command.
 * @property instructor The instructor providing command details.
 * @property arguments The arguments passed along with the command.
 */
class ConsoleContext(
  override val sender: CommandSender,
  override val instructor: Instructor,
  override val arguments: Array<out String>,
) : Context {
  
  /** The name of the command being executed, derived from the instructor. */
  override val command: String get() = instructor.name
  
  /** Always returns true, as this performer is for console use only. */
  override val isConsole: Boolean get() = true
  
  /** Always returns false, as the console cannot be a player. */
  override val isPlayer: Boolean get() = false
  
  /** Provides the console sender instance. */
  override val console: ConsoleCommandSender = Bukkit.getConsoleSender()
  
  /**
   * Throws an exception, as this performer cannot have a player as the sender.
   * @throws IllegalStateException if accessed, as this performer is console-specific.
   */
  override val player: Player get() = fail("§cApenas o Console pode executar este comando.")
  
  /** Tracks the current index of the argument being processed. */
  override var currentIndex: Int = 0
}

/**
 * A specific context implementation for player-based command execution.
 *
 * This class handles commands executed exclusively by players. It ensures that console-related methods
 * are not accessible and provides behavior specific to player command execution.
 *
 * @property sender The player executing the command.
 * @property instructor The instructor providing command details.
 * @property arguments The arguments passed along with the command.
 */
class PlayerContext(
  override val sender: CommandSender,
  override val instructor: Instructor,
  override val arguments: Array<out String>,
) : Context {
  
  /** The name of the command being executed, derived from the instructor. */
  override val command: String get() = instructor.name
  
  /** Always returns false, as this performer is for player use only. */
  override val isConsole: Boolean get() = false
  
  /** Always returns true, as the performer is for players. */
  override val isPlayer: Boolean get() = true
  
  /**
   * Throws an exception, as this performer cannot have a console as the sender.
   * @throws IllegalStateException if accessed, as this performer is player-specific.
   */
  override val console: ConsoleCommandSender get() = fail("§cApenas jogadores podem executar este comando.")
  
  /** Casts the sender to a player. */
  override val player: Player = sender as Player
  
  /** Tracks the current index of the argument being processed. */
  override var currentIndex: Int = 0
}
