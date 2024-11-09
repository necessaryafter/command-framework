package harmony.command

import harmony.command.misc.*
import net.md_5.bungee.api.chat.*
import org.bukkit.*
import org.bukkit.block.data.BlockData
import org.bukkit.command.*
import org.bukkit.enchantments.*
import org.bukkit.entity.*
import kotlin.reflect.*

/**
 * Represents an object that holds an array of [String] arguments.
 *
 * This interface is used to manage and manipulate command arguments passed to a command.
 * It provides several utility functions for accessing and verifying arguments, as well as
 * obtaining information about the sender who executed the command.
 */
interface Context : Iterable<String> {
  
  /**
   * The name of the command that holds all arguments.
   *
   * This property represents the base command that is being executed, and the
   * arguments associated with it are stored in [arguments].
   */
  val command: String
  
  /**
   * The instructor associated with this argumentable.
   *
   * The instructor is responsible for organizing and handling the command execution process.
   */
  val instructor: Instructor
  
  /**
   * The current index of the argument sequence.
   *
   * This variable keeps track of the position of the current argument being processed
   * in the [arguments] array. It is updated as arguments are retrieved or processed.
   */
  var currentIndex: Int
  
  /**
   * The arguments of this argumentable object.
   *
   * This array contains all the arguments passed to the command. The arguments are stored
   * as an array of [String] and can be accessed or manipulated using various utility functions.
   */
  val arguments: Array<out String>
  
  /**
   * The sender who executed this command.
   *
   * This property represents the [CommandSender] who performed the command, which could be a
   * player, console, or other type of sender in the Minecraft server environment.
   */
  val sender: CommandSender
  
  /**
   * Verifies if the sender is the console.
   *
   * This property returns `true` if the sender is an instance of [ConsoleCommandSender],
   * indicating that the command was executed from the console.
   */
  val isConsole: Boolean
  
  /**
   * Verifies if the sender is a player.
   *
   * This property returns `true` if the sender is an instance of [Player], indicating
   * that the command was executed by a player.
   */
  val isPlayer: Boolean
  
  /**
   * Returns the sender as a console command sender.
   *
   * If the sender is not a console, an error will be thrown. This property allows access
   * to the sender as a [ConsoleCommandSender].
   *
   * @throws InstructorError if the sender is not a console.
   */
  val console: ConsoleCommandSender
  
  /**
   * Returns the sender as a player.
   *
   * If the sender is not a player, an error will be thrown. This property allows access
   * to the sender as a [Player].
   *
   * @throws InstructorError if the sender is not a player.
   */
  val player: Player
  
  /**
   * Retrieves an optional string argument at the specified [index].
   *
   * If the argument exists at the given index, it will be returned; otherwise, `null` is returned.
   * Additionally, if a [permission] is specified, the sender must have that permission to access
   * the argument, or an error will be thrown.
   *
   * @param index The index of the argument to retrieve (default is [currentIndex]).
   * @param permission The permission required to retrieve the argument, if any.
   * @return The argument at the specified index, or `null` if it doesn't exist.
   * @throws InstructorError if the sender lacks the required permission.
   */
  fun optionalString(index: Int = currentIndex, permission: String? = null): String? {
    val arg = arguments.getOrNull(index)
    if (arg != null && permission != null && !sender.hasPermission(permission)) {
      fail("§cVocê não tem permissão para executar este comando.")
    }
    currentIndex++
    return arg
  }
  
  /**
   * Retrieves a string argument at the specified [index], or throws an error if it doesn't exist.
   *
   * This function attempts to retrieve the argument at the given [index]. If the argument does
   * not exist, an [InstructorError] will be thrown with the specified [message].
   * Additionally, if a [permission] is specified, the sender must have that permission to access
   * the argument, or an error will be thrown.
   *
   * @param index The index of the argument to retrieve (default is [currentIndex]).
   * @param message The message to display if the argument doesn't exist (default is [usage]).
   * @param permission The permission required to retrieve the argument, if any.
   * @return The argument at the specified index.
   * @throws InstructorError if the argument doesn't exist or the sender lacks the required permission.
   */
  fun string(
    index: Int = currentIndex,
    message: String = usage,
    permission: String? = null,
  ): String = optionalString(index, permission) ?: fail(message)
  
  /**
   * Returns this value or a default value if this is null.
   *
   * This function provides a convenient shorthand for returning `this` value if it is non-null,
   * or the provided [value] if it is null.
   *
   * Equivalent to `this ?: value`.
   *
   * @param value The value to return if this is null.
   * @return `this` if it is non-null, otherwise [value].
   */
  infix fun <T> T?.or(value: T): T = this ?: value
  
  /**
   * The size of the argument array.
   *
   * This property returns the number of arguments in the [arguments] array.
   */
  val size get() = arguments.size
  
  /**
   * Verifies if the argument array is empty.
   *
   * This function checks whether the [arguments] array contains any elements.
   *
   * @return `true` if the argument array is empty, `false` otherwise.
   */
  fun isEmpty() = arguments.isEmpty()
  
  /**
   * Returns an iterator over the arguments in this argumentable object.
   *
   * This function allows the object to be used in `for` loops, iterating over the [arguments].
   *
   * @return An iterator over the arguments.
   */
  override fun iterator(): Iterator<String> = arguments.iterator()
}


/**
 * Retrieves the usage message of this instructor.
 *
 * This property provides the usage description defined in the instructor,
 * often used to display help information or as feedback when required
 * arguments are missing.
 */
inline val Context.usage: String get() = instructor.usage

/**
 * Returns the last index of all arguments in this argumentable object.
 *
 * Useful for getting the final index in the argument array, typically
 * for reverse iteration or checking elements at the end of the argument list.
 */
inline val Context.lastIndex get() = arguments.lastIndex

/**
 * Checks if this argumentable object contains an argument
 * at the specified index.
 *
 * @param index The index to check in the argument list.
 * @return `true` if the argument exists at the given index, otherwise `false`.
 */
operator fun Context.contains(index: Int): Boolean = index < size

/**
 * Gets a nullable optional character at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified, but no character is found, returns `null`.
 *
 * @param index The index to retrieve the character from.
 * @param permission Optional permission required to access the argument.
 * @return The character at the specified index or `null` if not found.
 */
fun Context.nullableChar(
  index: Int = currentIndex,
  permission: String? = null,
): Char? {
  return optionalString(index, permission)?.firstOrNull()
}

/**
 * Gets an optional character at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but no character is found, invokes [fail] with the [invalid] message.
 *
 * @param index The index to retrieve the character from.
 * @param invalid The error message if a character is not found.
 * @param permission Optional permission required to access the argument.
 * @return The character at the specified index or `null` if not found.
 */
fun Context.optionalChar(
  index: Int = currentIndex,
  invalid: String = "§cCaractere não encontrado.",
  permission: String? = null,
): Char? {
  val string = optionalString(index, permission) ?: return null
  return string.firstOrNull() ?: fail(invalid)
}

/**
 * Gets a required character at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, invokes [fail] with the [empty] message.
 * 2. If the argument is specified but no character is found, invokes [fail] with the [invalid] message.
 *
 * @param index The index to retrieve the character from.
 * @param empty The error message if the argument is missing.
 * @param invalid The error message if no character is found.
 * @param permission Optional permission required to access the argument.
 * @return The character at the specified index.
 */
fun Context.char(
  index: Int = currentIndex,
  empty: String = usage,
  invalid: String = "§cCaractere não encontrado.",
  permission: String? = null,
): Char {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.firstOrNull() ?: fail(invalid)
}

/**
 * Gets an optional character array at the specified index.
 *
 * If no argument is provided, returns `null`.
 *
 * @param index The index to retrieve the character array from.
 * @param permission Optional permission required to access the argument.
 * @return The character array at the specified index or `null`.
 */
fun Context.optionalCharArray(
  index: Int = currentIndex,
  permission: String? = null,
): CharArray? {
  return optionalString(index, permission)?.toCharArray()
}

/**
 * Gets a required character array at the specified index.
 *
 * If no argument is provided, invokes [fail] with [message].
 *
 * @param index The index to retrieve the character array from.
 * @param message The error message if no argument is found.
 * @param permission Optional permission required to access the argument.
 * @return The character array at the specified index.
 */
fun Context.charArray(
  index: Int = currentIndex,
  message: String = usage,
  permission: String? = null,
): CharArray {
  return optionalCharArray(index, permission) ?: fail(message)
}

/**
 * Gets a nullable optional boolean value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but not a valid boolean representation, returns `null`.
 *
 * @param index The index to retrieve the boolean value from.
 * @param permission Optional permission required to access the argument.
 * @return The boolean value at the specified index or `null`.
 */
fun Context.nullableBoolean(
  index: Int = currentIndex,
  permission: String? = null,
): Boolean? {
  return optionalString(index, permission)?.toBooleanStrictOrNull()
}

/**
 * Gets an optional boolean value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but not a valid boolean, invokes [fail] with [invalid] as message.
 *
 * @param index The index to retrieve the boolean value from.
 * @param invalid The error message if a valid boolean is not found.
 * @param permission Optional permission required to access the argument.
 * @return The boolean value at the specified index or `null`.
 */
fun Context.optionalBoolean(
  index: Int = currentIndex,
  invalid: String = "§cValor true/false inválido.",
  permission: String? = null,
): Boolean? {
  val string = optionalString(index, permission) ?: return null
  return string.toBooleanStrictOrNull() ?: fail(invalid)
}

/**
 * Gets a required boolean value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, invokes [fail] with the [empty] message.
 * 2. If the argument is specified but not a valid boolean, invokes [fail] with [invalid] as message.
 *
 * @param index The index to retrieve the boolean value from.
 * @param empty The error message if the argument is missing.
 * @param invalid The error message if a valid boolean is not found.
 * @param permission Optional permission required to access the argument.
 * @return The boolean value at the specified index.
 */
fun Context.boolean(
  index: Int = currentIndex,
  empty: String = usage,
  invalid: String = "§cValor true/false inválido.",
  permission: String? = null,
): Boolean {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toBooleanStrictOrNull() ?: fail(invalid)
}

/**
 * Gets a nullable optional byte value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but not a valid byte, returns `null`.
 *
 * @param index The index to retrieve the byte value from.
 * @param permission Optional permission required to access the argument.
 * @return The byte value at the specified index or `null`.
 */
fun Context.nullableByte(
  index: Int = currentIndex,
  permission: String? = null,
): Byte? {
  return optionalString(index, permission)?.toByteOrNull()
}

/**
 * Gets an optional byte value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but not a valid byte, invokes [fail] with [invalid] as message.
 *
 * @param index The index to retrieve the byte value from.
 * @param invalid The error message if a valid byte is not found.
 * @param permission Optional permission required to access the argument.
 * @return The byte value at the specified index or `null`.
 */
fun Context.optionalByte(
  index: Int = currentIndex,
  invalid: String = "§cValor numérico Byte não encontrado.",
  permission: String? = null,
): Byte? {
  val string = optionalString(index, permission) ?: return null
  return string.toByteOrNull() ?: fail(invalid)
}

/**
 * Gets a required byte value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, invokes [fail] with the [empty] message.
 * 2. If the argument is specified but not a valid byte, invokes [fail] with [invalid] as message.
 *
 * @param index The index to retrieve the byte value from.
 * @param empty The error message if the argument is missing.
 * @param invalid The error message if a valid byte is not found.
 * @param permission Optional permission required to access the argument.
 * @return The byte value at the specified index.
 */
fun Context.byte(
  index: Int = currentIndex,
  empty: String = usage,
  invalid: String = "§cValor numérico Byte não encontrado.",
  permission: String? = null,
): Byte {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toByteOrNull() ?: fail(invalid)
}

/**
 * Gets a nullable optional short value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but not a valid short, returns `null`.
 *
 * @param index The index to retrieve the short value from.
 * @param permission Optional permission required to access the argument.
 * @return The short value at the specified index or `null`.
 */
fun Context.nullableShort(
  index: Int = currentIndex,
  permission: String? = null,
): Short? {
  return optionalString(index, permission)?.toShortOrNull()
}

/**
 * Gets an optional short value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, returns `null`.
 * 2. If the argument is specified but not a valid short, invokes [fail] with [invalid] as message.
 *
 * @param index The index to retrieve the short value from.
 * @param invalid The error message if a valid short is not found.
 * @param permission Optional permission required to access the argument.
 * @return The short value at the specified index or `null`.
 */
fun Context.optionalShort(
  index: Int = currentIndex,
  invalid: String = "§cValor numérico Short não encontrado.",
  permission: String? = null,
): Short? {
  val string = optionalString(index, permission) ?: return null
  return string.toShortOrNull() ?: fail(invalid)
}

/**
 * Gets a required short value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, invokes [fail] with the [empty] message.
 * 2. If the argument is specified but not a valid short, invokes [fail] with [invalid] as message.
 *
 * @param index The index to retrieve the short value from.
 * @param empty The error message if the argument is missing.
 * @param invalid The error message if a valid short is not found.
 * @param permission Optional permission required to access the argument.
 * @return The short value at the specified index.
 */
fun Context.short(
  index: Int = currentIndex,
  empty: String = usage,
  invalid: String = "§cValor numérico Short não encontrado.",
  permission: String? = null,
): Short {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toShortOrNull() ?: fail(invalid)
}

/**
 * Gets a nullable optional integer value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid integer representation, return null.
 *
 * @param index The index to retrieve the integer value from.
 * @param permission Optional permission required to access the argument.
 * @return The nullable integer value at the specified index, or null if not found.
 */
fun Context.nullableInt(
  index: Int = currentIndex,
  permission: String? = null,
): Int? {
  return optionalString(index, permission)?.toIntOrNull()
}

/**
 * Gets an optional integer value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid integer representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the integer value from.
 * @param found The error message if a valid integer is not found.
 * @param permission Optional permission required to access the argument.
 * @return The optional integer value at the specified index, or null if not found.
 */
fun Context.optionalInt(
  index: Int = currentIndex,
  found: String = "§cValor numérico Int não encontrado.",
  permission: String? = null,
): Int? {
  val string = optionalString(index, permission) ?: return null
  return string.toIntOrNull() ?: fail(found)
}

/**
 * Gets a required integer value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return [fail] with [empty] as message.
 * 2. If the argument is specified, but the value is not a valid integer representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the integer value from.
 * @param empty The error message if the argument is missing.
 * @param found The error message if a valid integer is not found.
 * @param permission Optional permission required to access the argument.
 * @return The integer value at the specified index.
 */
fun Context.int(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cValor numérico Int não encontrado.",
  permission: String? = null,
): Int {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toIntOrNull() ?: fail(found)
}

/**
 * Gets a nullable optional long value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid long representation, return null.
 *
 * @param index The index to retrieve the long value from.
 * @param permission Optional permission required to access the argument.
 * @return The nullable long value at the specified index, or null if not found.
 */
fun Context.nullableLong(
  index: Int = currentIndex,
  permission: String? = null,
): Long? {
  return optionalString(index, permission)?.toLongOrNull()
}

/**
 * Gets an optional long value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid long representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the long value from.
 * @param found The error message if a valid long is not found.
 * @param permission Optional permission required to access the argument.
 * @return The optional long value at the specified index, or null if not found.
 */
fun Context.optionalLong(
  index: Int = currentIndex,
  found: String = "§cValor numérico Long não encontrado.",
  permission: String? = null,
): Long? {
  val string = optionalString(index, permission) ?: return null
  return string.toLongOrNull() ?: fail(found)
}

/**
 * Gets a required long value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return [fail] with [empty] as message.
 * 2. If the argument is specified, but the value is not a valid long representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the long value from.
 * @param empty The error message if the argument is missing.
 * @param found The error message if a valid long is not found.
 * @param permission Optional permission required to access the argument.
 * @return The long value at the specified index.
 */
fun Context.long(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cValor numérico Long não encontrado.",
  permission: String? = null,
): Long {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toLongOrNull() ?: fail(found)
}

/**
 * Gets a nullable optional float value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid float representation, return null.
 *
 * @param index The index to retrieve the float value from.
 * @param permission Optional permission required to access the argument.
 * @return The nullable float value at the specified index, or null if not found.
 */
fun Context.nullableFloat(
  index: Int = currentIndex,
  permission: String? = null,
): Float? {
  return optionalString(index, permission)?.toFloatOrNull()
}

/**
 * Gets an optional float value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid float representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the float value from.
 * @param found The error message if a valid float is not found.
 * @param permission Optional permission required to access the argument.
 * @return The optional float value at the specified index, or null if not found.
 */
fun Context.optionalFloat(
  index: Int = currentIndex,
  found: String = "§cValor numérico Float não encontrado.",
  permission: String? = null,
): Float? {
  val string = optionalString(index, permission) ?: return null
  return string.toFloatOrNull() ?: fail(found)
}

/**
 * Gets a required float value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return [fail] with [empty] as message.
 * 2. If the argument is specified, but the value is not a valid float representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the float value from.
 * @param empty The error message if the argument is missing.
 * @param found The error message if a valid float is not found.
 * @param permission Optional permission required to access the argument.
 * @return The float value at the specified index.
 */
fun Context.float(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cValor numérico Float não encontrado.",
  permission: String? = null,
): Float {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toFloatOrNull() ?: fail(found)
}

/**
 * Gets a nullable optional double value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid double representation, return null.
 *
 * @param index The index to retrieve the double value from.
 * @param permission Optional permission required to access the argument.
 * @return The nullable double value at the specified index, or null if not found.
 */
fun Context.nullableDouble(
  index: Int = currentIndex,
  permission: String? = null,
): Double? {
  return optionalString(index, permission)?.toDoubleOrNull()
}

/**
 * Gets an optional double value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return null.
 * 2. If the argument is specified, but the value is not a valid double representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the double value from.
 * @param found The error message if a valid double is not found.
 * @param permission Optional permission required to access the argument.
 * @return The optional double value at the specified index, or null if not found.
 */
fun Context.optionalDouble(
  index: Int = currentIndex,
  found: String = "§cValor numérico Double não encontrado.",
  permission: String? = null,
): Double? {
  val string = optionalString(index, permission) ?: return null
  return string.toDoubleOrNull() ?: fail(found)
}

/**
 * Gets a required double value at the specified index.
 *
 * ### Behavior:
 * 1. If the argument is not specified, return [fail] with [empty] as message.
 * 2. If the argument is specified, but the value is not a valid double representation, return [fail] with [found] as message.
 *
 * @param index The index to retrieve the double value from.
 * @param empty The error message if the argument is missing.
 * @param found The error message if a valid double is not found.
 * @param permission Optional permission required to access the argument.
 * @return The double value at the specified index.
 */
fun Context.double(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cValor numérico Double não encontrado.",
  permission: String? = null,
): Double {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toDoubleOrNull() ?: fail(found)
}

/**
 * Retrieves a nullable `Player` object by the provided index.
 * If the argument is absent or the player cannot be found, it returns `null`.
 *
 * This function first attempts to retrieve an optional string from the arguments at the specified index.
 * If a valid player name is provided, it uses the Bukkit API to get the corresponding player.
 * If the player does not exist or is offline, it returns `null`.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, the function returns `null`.
 * - If the string is not a valid player name, the function returns `null`.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The `Player` object associated with the provided name or `null` if not found.
 */
fun Context.nullablePlayer(
  index: Int = currentIndex,
  permission: String? = null,
): Player? {
  return optionalString(index, permission)?.let { Bukkit.getPlayer(it) }
}

/**
 * Retrieves an optional `Player` object by the provided index.
 * If the argument is missing, it returns `null`.
 * If the player cannot be found, it fails with the specified message.
 *
 * This function checks for an optional string argument and tries to resolve it to a `Player` using the
 * Bukkit API. If the player cannot be found, it invokes the `fail` function with the specified message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, the function returns `null`.
 * - If the string is not a valid player name, it fails with the provided message.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param found The error message if the player is not found. Default is "§cJogador não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The `Player` object associated with the provided name or `null` if not found.
 */
fun Context.optionalPlayer(
  index: Int = currentIndex,
  found: String = "§cJogador não encontrado.",
  permission: String? = null,
): Player? {
  val name = optionalString(index, permission) ?: return null
  return Bukkit.getPlayer(name) ?: fail(found)
}

/**
 * Retrieves a `Player` object by the provided index.
 * If the argument is missing, it fails with the specified message.
 * If the player cannot be found, it also fails with the provided message.
 *
 * This function first attempts to retrieve a string from the arguments at the specified index and checks
 * if the player exists using the Bukkit API. It fails with the appropriate error message if the player
 * does not exist.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string is not a valid player name, it fails with the provided found message.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the player is not found. Default is "§cJogador não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The `Player` object associated with the provided name.
 */
fun Context.player(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cJogador não encontrado.",
  permission: String? = null,
): Player {
  val name = optionalString(index, permission) ?: fail(empty)
  return Bukkit.getPlayer(name) ?: fail(found)
}

/**
 * Retrieves a `Player` object by the provided index.
 * If the sender is the console, it retrieves a player.
 * If the argument is missing, it fails with the specified message.
 * If the player cannot be found, it also fails with the provided message.
 *
 * This function checks if the sender is a console or a player. If the sender is a console, it retrieves a
 * player by calling the `player` function. If it's a player, it tries to get the player with
 * `optionalPlayer` function and falls back to retrieving the sender if no player is found.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string is not a valid player name, it fails with the provided found message.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the player is not found. Default is "§cJogador não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The `Player` object associated with the provided name or the sender if the sender is a player.
 */
fun Context.playerOrSender(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cJogador não encontrado.",
  permission: String? = null,
): Player {
  return if (isConsole) player(index, empty, found, permission) else optionalPlayer(index, found, permission) ?: player
}

/**
 * Retrieves a nullable `OfflinePlayer` object by the provided index.
 * If the argument is absent, it returns `null`.
 * If the player does not exist, it also returns `null`.
 *
 * This function first attempts to retrieve an optional string from the arguments at the specified index.
 * If a valid player name is provided, it uses the Bukkit API to get the corresponding offline player.
 * If the player name is null, it returns `null`.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, the function returns `null`.
 * - If the string is not a valid offline player name, the function returns `null`.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The `OfflinePlayer` object associated with the provided name or `null` if not found.
 */
fun Context.nullableOfflinePlayer(
  index: Int = currentIndex,
  permission: String? = null,
): OfflinePlayer? {
  val name = optionalString(index, permission) ?: return null
  val player = Bukkit.getOfflinePlayer(name)
  return if (player.name == null) null else player
}

/**
 * Retrieves an optional `OfflinePlayer` object by the provided index.
 * If the argument is missing, it returns `null`.
 * If the player cannot be found, it fails with the specified message.
 *
 * This function checks for an optional string argument and tries to resolve it to an `OfflinePlayer` using the
 * Bukkit API. If the player cannot be found, it invokes the `fail` function with the specified message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, the function returns `null`.
 * - If the string is not a valid offline player name, it fails with the provided message.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param found The error message if the player is not found. Default is "§cJogador não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The `OfflinePlayer` object associated with the provided name or `null` if not found.
 */
fun Context.optionalOfflinePlayer(
  index: Int = currentIndex,
  found: String = "§cJogador não encontrado.",
  permission: String? = null,
): OfflinePlayer? {
  val name = optionalString(index, permission) ?: return null
  val player = Bukkit.getOfflinePlayer(name)
  return if (player.name == null) fail(found) else player
}

/**
 * Retrieves an `OfflinePlayer` object by the provided index.
 * If the argument is missing, it fails with the specified message.
 * If the player cannot be found, it also fails with the provided message.
 *
 * This function first attempts to retrieve a string from the arguments at the specified index and checks
 * if the offline player exists using the Bukkit API. It fails with the appropriate error message if the player
 * does not exist.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string is not a valid offline player name, it fails with the provided found message.
 *
 * @param index The index to retrieve the player name from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the player is not found. Default is "§cJogador não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The `OfflinePlayer` object associated with the provided name.
 */
fun Context.offlinePlayer(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cJogador não encontrado.",
  permission: String? = null,
): OfflinePlayer {
  val name = optionalString(index, permission) ?: fail(empty)
  val player = Bukkit.getOfflinePlayer(name)
  return if (player.name == null) fail(found) else player
}

/**
 * Retrieves a nullable `GameMode` object by the provided index.
 * If the argument is missing or invalid, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `GameMode`. If the string does not correspond to a valid game mode,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string is not a valid game mode, it also returns null.
 *
 * @param index The index to retrieve the game mode string from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `GameMode` object if found, or null if the index is invalid or the game mode does not exist.
 */
fun Context.nullableGamemode(
  index: Int = currentIndex,
  permission: String? = null,
): GameMode? {
  return optionalString(index, permission)?.toGamemode()
}

/**
 * Retrieves a nullable `GameMode` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `GameMode`. If the string does not correspond to a valid game mode,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string is not a valid game mode, it also returns null.
 *
 * @param index The index to retrieve the game mode string from. Default is `currentIndex`.
 * @param found The error message if the game mode is not found. Default is "§cMode de jogo não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `GameMode` object if found, or null if the index is invalid or the game mode does not exist.
 */
fun Context.optionalGamemode(
  index: Int = currentIndex,
  found: String = "§cMode de jogo não encontrado.",
  permission: String? = null,
): GameMode? {
  val string = optionalString(index, permission) ?: return null
  return string.toGamemode() ?: fail(found)
}

/**
 * Retrieves a `GameMode` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `GameMode`. If the string does not correspond to a valid game mode,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string is not a valid game mode, it fails with the provided found message.
 *
 * @param index The index to retrieve the game mode string from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the game mode is not found. Default is "§cMode de jogo não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `GameMode` object associated with the provided name.
 */
fun Context.gamemode(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cMode de jogo não encontrado.",
  permission: String? = null,
): GameMode {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toGamemode() ?: fail(found)
}

/**
 * Retrieves a nullable `Enchantment` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to an `Enchantment`. If the string does not correspond to a valid enchantment,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string is not a valid enchantment, it also returns null.
 *
 * @param index The index to retrieve the enchantment string from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Enchantment` object if found, or null if the index is invalid or the enchantment does not exist.
 */
fun Context.nullableEnchantment(
  index: Int = currentIndex,
  permission: String? = null,
): Enchantment? {
  return optionalString(index, permission)?.toEnchantment()
}

/**
 * Retrieves a nullable `Enchantment` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to an `Enchantment`. If the string does not correspond to a valid enchantment,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string is not a valid enchantment, it fails with the provided found message.
 *
 * @param index The index to retrieve the enchantment string from. Default is `currentIndex`.
 * @param found The error message if the enchantment is not found. Default is "§cEncantamento não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Enchantment` object if found, or null if the index is invalid or the enchantment does not exist.
 */
fun Context.optionalEnchantment(
  index: Int = currentIndex,
  found: String = "§cEncantamento não encontrado.",
  permission: String? = null,
): Enchantment? {
  val string = optionalString(index, permission) ?: return null
  return string.toEnchantment() ?: fail(found)
}

/**
 * Retrieves an `Enchantment` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to an `Enchantment`. If the string does not correspond to a valid enchantment,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string is not a valid enchantment, it fails with the provided found message.
 *
 * @param index The index to retrieve the enchantment string from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the enchantment is not found. Default is "§cEncantamento não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Enchantment` object associated with the provided name.
 */
fun Context.enchantment(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cEncantamento não encontrado.",
  permission: String? = null,
): Enchantment {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toEnchantment() ?: fail(found)
}

/**
 * Retrieves a nullable `World` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and looks up the corresponding `World` object using the Bukkit API. If the string does not
 * correspond to a valid world name, null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not match any world, it also returns null.
 *
 * @param index The index to retrieve the world name from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `World` object if found, or null if the index is invalid or the world does not exist.
 */
fun Context.nullableWorld(
  index: Int = currentIndex,
  permission: String? = null,
): World? {
  return optionalString(index, permission)?.let { Bukkit.getWorld(it) }
}

/**
 * Retrieves a nullable `World` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and looks up the corresponding `World` object using the Bukkit API. If the string does not
 * correspond to a valid world, it fails with the specified error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not match any world, it fails with the provided found message.
 *
 * @param index The index to retrieve the world name from. Default is `currentIndex`.
 * @param found The error message if the world is not found. Default is "§cMundo não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `World` object if found, or null if the index is invalid or the world does not exist.
 */
fun Context.optionalWorld(
  index: Int = currentIndex,
  found: String = "§cMundo não encontrado.",
  permission: String? = null,
): World? {
  val string = optionalString(index, permission) ?: return null
  return Bukkit.getWorld(string) ?: fail(found)
}

/**
 * Retrieves a `World` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and looks up the corresponding `World` object using the Bukkit API. If the string does not
 * correspond to a valid world, it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string does not match any world, it fails with the provided found message.
 *
 * @param index The index to retrieve the world name from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the world is not found. Default is "§cMundo não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `World` object associated with the provided name.
 */
fun Context.world(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cMundo não encontrado.",
  permission: String? = null,
): World {
  val string = optionalString(index, permission) ?: fail(empty)
  return Bukkit.getWorld(string) ?: fail(found)
}

/**
 * Retrieves a nullable `Materials` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `Materials` object. If the string does not correspond to a valid material,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid material, it also returns null.
 *
 * @param index The index to retrieve the material string from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Materials` object if found, or null if the index is invalid or the material does not exist.
 */
fun Context.nullableMaterial(
  index: Int = currentIndex,
  permission: String? = null,
): Material? {
  return optionalString(index, permission)?.toMaterial()
}

/**
 * Retrieves a nullable `Materials` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `Materials` object. If the string does not correspond to a valid material,
 * it fails with the specified error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid material, it fails with the provided found message.
 *
 * @param index The index to retrieve the material string from. Default is `currentIndex`.
 * @param found The error message if the material is not found. Default is "§cMaterial não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Materials` object if found, or null if the index is invalid or the material does not exist.
 */
fun Context.optionalMaterial(
  index: Int = currentIndex,
  found: String = "§cMaterial não encontrado.",
  permission: String? = null,
): Material? {
  val string = optionalString(index, permission) ?: return null
  return string.toMaterial() ?: fail(found)
}

/**
 * Retrieves a `Materials` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `Materials` object. If the string does not correspond to a valid material,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string does not correspond to a valid material, it fails with the provided found message.
 *
 * @param index The index to retrieve the material string from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the material is not found. Default is "§cMaterial não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Materials` object associated with the provided name.
 */
fun Context.material(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cMaterial não encontrado.",
  permission: String? = null,
): Material {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toMaterial() ?: fail(found)
}

/**
 * Retrieves a nullable `BlockData` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `BlockData` object. If the string does not correspond to a valid block data,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid block data, it also returns null.
 *
 * @param index The index to retrieve the block data string from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `BlockData` object if found, or null if the index is invalid or the block data does not exist.
 */
fun Context.nullableBlockData(
  index: Int = currentIndex,
  permission: String? = null,
): BlockData? {
  return optionalString(index, permission)?.toBlockData()
}

/**
 * Retrieves a nullable `BlockData` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `BlockData` object. If the string does not correspond to a valid block data,
 * it fails with the specified error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid block data, it fails with the provided found message.
 *
 * @param index The index to retrieve the block data string from. Default is `currentIndex`.
 * @param found The error message if the block data is not found. Default is "§cBlock Data não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `BlockData` object if found, or null if the index is invalid or the block data does not exist.
 */
fun Context.optionalBlockData(
  index: Int = currentIndex,
  found: String = "§cBlock Data não encontrado.",
  permission: String? = null,
): BlockData? {
  val string = optionalString(index, permission) ?: return null
  return string.toBlockData() ?: fail(found)
}

/**
 * Retrieves a `BlockData` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `BlockData` object. If the string does not correspond to a valid block data,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string does not correspond to a valid block data, it fails with the provided found message.
 *
 * @param index The index to retrieve the block data string from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the block data is not found. Default is "§cBlock Data não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `BlockData` object associated with the provided name.
 */
fun Context.blockData(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cBlock Data não encontrado.",
  permission: String? = null,
): BlockData {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toBlockData() ?: fail(found)
}

/**
 * Retrieves a nullable `EntityType` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to an `EntityType` object. If the string does not correspond to a valid entity type,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid entity type, it also returns null.
 *
 * @param index The index to retrieve the entity type string from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `EntityType` object if found, or null if the index is invalid or the entity type does not exist.
 */
fun Context.nullableEntityType(
  index: Int = currentIndex,
  permission: String? = null,
): EntityType? {
  return optionalString(index, permission)?.toEntityType()
}

/**
 * Retrieves a nullable `EntityType` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to an `EntityType` object. If the string does not correspond to a valid entity type,
 * it fails with the specified error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid entity type, it fails with the provided found message.
 *
 * @param index The index to retrieve the entity type string from. Default is `currentIndex`.
 * @param found The error message if the entity type is not found. Default is "§cEntidade não encontrada."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `EntityType` object if found, or null if the index is invalid or the entity type does not exist.
 */
fun Context.optionalEntityType(
  index: Int = currentIndex,
  found: String = "§cEntidade não encontrada.",
  permission: String? = null,
): EntityType? {
  val string = optionalString(index, permission) ?: return null
  return string.toEntityType() ?: fail(found)
}

/**
 * Retrieves an `EntityType` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to an `EntityType` object. If the string does not correspond to a valid entity type,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string does not correspond to a valid entity type, it fails with the provided found message.
 *
 * @param index The index to retrieve the entity type string from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the entity type is not found. Default is "§cEntidade não encontrada."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `EntityType` object associated with the provided name.
 */
fun Context.entityType(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cEntidade não encontrada.",
  permission: String? = null,
): EntityType {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toEntityType() ?: fail(found)
}

/**
 * Retrieves a nullable `Sound` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `Sound` object. If the string does not correspond to a valid sound,
 * null is returned.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid sound, it also returns null.
 *
 * @param index The index to retrieve the sound string from. Default is `currentIndex`.
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Sound` object if found, or null if the index is invalid or the sound does not exist.
 */
fun Context.nullableSound(
  index: Int = currentIndex,
  permission: String? = null,
): Sound? {
  return optionalString(index, permission)?.toSound()
}

/**
 * Retrieves a nullable `Sound` object by the provided index.
 * If the argument is missing, it returns null.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `Sound` object. If the string does not correspond to a valid sound,
 * it fails with the specified error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it returns null.
 * - If the string does not correspond to a valid sound, it fails with the provided found message.
 *
 * @param index The index to retrieve the sound string from. Default is `currentIndex`.
 * @param found The error message if the sound is not found. Default is "§cSom não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Sound` object if found, or null if the index is invalid or the sound does not exist.
 */
fun Context.optionalSound(
  index: Int = currentIndex,
  found: String = "§cSom não encontrado.",
  permission: String? = null,
): Sound? {
  val string = optionalString(index, permission) ?: return null
  return string.toSound() ?: fail(found)
}

/**
 * Retrieves a `Sound` object by the provided index.
 * If the argument is missing or invalid, it fails with the specified message.
 *
 * This function attempts to retrieve a string from the arguments at the specified index
 * and converts it to a `Sound` object. If the string does not correspond to a valid sound,
 * it fails with the appropriate error message.
 *
 * ## Behavior
 * - If the specified index does not correspond to a valid string, it fails with the provided empty message.
 * - If the string does not correspond to a valid sound, it fails with the provided found message.
 *
 * @param index The index to retrieve the sound string from. Default is `currentIndex`.
 * @param empty The error message if the argument is missing. Default is `usage`.
 * @param found The error message if the sound is not found. Default is "§cSom não encontrado."
 * @param permission Optional permission required to access the argument.
 * @return The corresponding `Sound` object associated with the provided name.
 */
fun Context.sound(
  index: Int = currentIndex,
  empty: String = usage,
  found: String = "§cSom não encontrado.",
  permission: String? = null,
): Sound {
  val string = optionalString(index, permission) ?: fail(empty)
  return string.toSound() ?: fail(found)
}

/**
 * Retrieves a nullable array of strings from the arguments, based on the provided index range.
 * If the specified range is invalid or the array is empty, it returns null.
 *
 * This function uses Kotlin's `sliceArray` to attempt to extract a portion of the arguments
 * array from the specified index to the final index. If an exception occurs during this
 * process or if the resulting array is empty, null is returned.
 *
 * ## Behavior
 * - If the range specified by the indices is invalid, it returns null.
 * - If the resulting array is empty, it also returns null.
 *
 * @param index The starting index to retrieve the array from. Default is `currentIndex`.
 * @param finalIndex The ending index for the array slice. Default is `lastIndex`.
 * @return An array of strings if found, or null if the range is invalid or the array is empty.
 */
fun Context.optionalArray(index: Int = currentIndex, finalIndex: Int = lastIndex): Array<out String>? {
  val array = runCatching { arguments.sliceArray(index..finalIndex) }.getOrNull()
  return when {
    array.isNullOrEmpty() -> null
    else -> array
  }
}

/**
 * Retrieves an array of strings from the arguments, based on the provided index range.
 * If the specified range is invalid or the array is empty, it fails with the specified message.
 *
 * This function utilizes the `optionalArray` function to attempt to extract a portion of the arguments
 * array from the specified index to the final index. If no valid array is returned, it fails with the provided message.
 *
 * ## Behavior
 * - If the range specified by the indices is invalid, it fails with the provided message.
 * - If the resulting array is empty, it also fails with the provided message.
 *
 * @param index The starting index to retrieve the array from. Default is `currentIndex`.
 * @param finalIndex The ending index for the array slice. Default is `lastIndex`.
 * @param message The error message if the array is empty or invalid. Default is `usage`.
 * @return An array of strings associated with the specified range.
 */
fun Context.array(
  index: Int = currentIndex,
  finalIndex: Int = lastIndex,
  message: String = usage,
): Array<out String> = optionalArray(index, finalIndex) ?: fail(message)

/**
 * Retrieves a nullable list of strings from the arguments, based on the provided index range.
 * If the specified range is invalid or the list is empty, it returns null.
 *
 * This function uses Kotlin's `slice` to attempt to extract a portion of the arguments
 * list from the specified index to the final index. If an exception occurs during this
 * process or if the resulting list is empty, null is returned.
 *
 * ## Behavior
 * - If the range specified by the indices is invalid, it returns null.
 * - If the resulting list is empty, it also returns null.
 *
 * @param index The starting index to retrieve the list from. Default is `currentIndex`.
 * @param finalIndex The ending index for the list slice. Default is `lastIndex`.
 * @return A list of strings if found, or null if the range is invalid or the list is empty.
 */
fun Context.optionalList(index: Int = currentIndex, finalIndex: Int = lastIndex): List<String>? {
  val list = runCatching { arguments.slice(index..finalIndex) }.getOrNull()
  return when {
    list.isNullOrEmpty() -> null
    else -> list
  }
}

/**
 * Retrieves a list of strings from the arguments, based on the provided index range.
 * If the specified range is invalid or the list is empty, it fails with the specified message.
 *
 * This function utilizes the `optionalList` function to attempt to extract a portion of the arguments
 * list from the specified index to the final index. If no valid list is returned, it fails with the provided message.
 *
 * ## Behavior
 * - If the range specified by the indices is invalid, it fails with the provided message.
 * - If the resulting list is empty, it also fails with the provided message.
 *
 * @param index The starting index to retrieve the list from. Default is `currentIndex`.
 * @param finalIndex The ending index for the list slice. Default is `lastIndex`.
 * @param message The error message if the list is empty or invalid. Default is `usage`.
 * @return A list of strings associated with the specified range.
 */
fun Context.list(
  index: Int = currentIndex,
  finalIndex: Int = lastIndex,
  message: String = usage,
): List<String> = optionalList(index, finalIndex) ?: fail(message)

/**
 * Validates a boolean condition and fails with the specified message if the condition is false.
 *
 * This function checks the provided `valide` parameter. If it is true, the function returns true.
 * If it is false, it fails with the provided message.
 *
 * ## Parameters
 * - `valide`: The boolean condition to validate.
 * - `message`: The message to display if the validation fails. Default is `usage`.
 *
 * @return True if the validation succeeds; otherwise, fails with the provided message.
 */
fun Context.validate(valide: Boolean, message: String = usage): Boolean = if (valide) true else fail(message)

/**
 * Validates a boolean condition and fails with the specified message if the condition is true.
 *
 * This function checks the provided `valide` parameter. If it is false, the function returns true.
 * If it is true, it fails with the provided message.
 *
 * ## Parameters
 * - `valide`: The boolean condition to validate.
 * - `message`: The message to display if the validation fails. Default is `usage`.
 *
 * @return True if the validation succeeds; otherwise, fails with the provided message.
 */
fun Context.validateNot(valide: Boolean, message: String = usage): Boolean = if (!valide) true else fail(message)

/**
 * Joins all arguments into a single string, separated by spaces.
 *
 * This function uses Kotlin's `joinToString` to concatenate all arguments in the `arguments` array
 * into a single string with a space as the separator.
 *
 * @return A single string representing all arguments joined together.
 */
fun Context.join(): String = arguments.joinToString(" ")

/**
 * Joins all arguments into a single string, separated by spaces, and fails with the specified message if the arguments are empty.
 *
 * This function checks if there are any arguments present. If the argument list is empty,
 * it fails with the provided message; otherwise, it calls the `join` method.
 *
 * @param message The message to display if the arguments are empty.
 * @return A single string representing all arguments joined together.
 */
fun Context.joinNotEmpty(message: String): String {
  return if (isEmpty()) fail(message) else join()
}

/**
 * Joins a subset of arguments from the specified index range into a single string, separated by spaces.
 *
 * This function attempts to retrieve a list of arguments within the specified range and
 * joins them into a single string. If no arguments are found, it returns an empty string.
 *
 * @param index The starting index for joining arguments. Default is `currentIndex`.
 * @param finalIndex The ending index for joining arguments. Default is `lastIndex`.
 * @return A single string representing the arguments joined together, or an empty string if none are found.
 */
fun Context.joinInRange(index: Int = currentIndex, finalIndex: Int = lastIndex): String {
  val list = optionalList(index, finalIndex) ?: return ""
  return list.joinToString(" ")
}

/**
 * Joins a subset of arguments from the specified range into a single string and fails with usage message if the result is empty.
 *
 * This function retrieves and joins arguments from the specified range. If the resulting string is empty,
 * it fails with a usage message.
 *
 * @param start The starting index for joining arguments. Default is `0`.
 * @param end The ending index for joining arguments. Default is `lastIndex`.
 * @return A single string representing the joined arguments.
 */
fun Context.joinNotEmpty(start: Int = 0, end: Int = lastIndex): String {
  val joined = joinInRange(start, end)
  return joined.ifEmpty { failUsage() }
}

/**
 * Synchronizes a property with the appropriate value based on its type, setting it on the specified object.
 *
 * This function checks the type of the provided property and retrieves the appropriate value
 * from the command arguments. If the property's type is unsupported, it fails with an error message.
 *
 * @param value The object containing the property to synchronize.
 * @param prop The property to synchronize.
 * @return The data that was set on the property.
 * @throws IllegalArgumentException if the property type is not serializable by command.
 */
fun <T> Context.sync(value: T, prop: KMutableProperty1<T, Any>): Any {
  val data = when (prop.returnType) {
    typeOf<String>() -> joinNotEmpty()
    typeOf<Int>() -> int()
    typeOf<Double>() -> double()
    typeOf<Boolean>() -> boolean()
    typeOf<Long>() -> long()
    typeOf<Float>() -> float()
    typeOf<Byte>() -> byte()
    typeOf<Short>() -> short()
    else -> fail(
      "§cNão foi possível sincronizar a propriedade §f${prop.name}§c, " +
        "pois o seu tipo (§f${prop.returnType}§c) não é serializável por comando."
    )
  }
  
  prop.set(value, data)
  return data
}

/**
 * Checks if the sender has the specified permission.
 *
 * This function checks the permissions of the sender of the command against the provided permission string.
 *
 * @param permission The permission string to check against.
 * @return True if the sender has the permission; otherwise, false.
 */
fun Context.hasPermission(permission: String): Boolean {
  return sender.hasPermission(permission)
}

/**
 * Validates that the command is being executed by the console and fails with the specified message if it is not.
 *
 * This function checks if the command is being executed from the console. If it is not,
 * it fails with the provided message.
 *
 * @param message The message to display if the validation fails. Default is "§cApenas o Console pode executar este comando."
 * @return True if the validation succeeds; otherwise, fails with the provided message.
 */
fun Context.validateConsole(
  message: String = "§cApenas o Console pode executar este comando.",
) = validate(isConsole, message)

/**
 * Validates that the command is being executed by a player and fails with the specified message if it is not.
 *
 * This function checks if the command is being executed by a player. If it is not,
 * it fails with the provided message.
 *
 * @param message The message to display if the validation fails. Default is "§cApenas jogadores podem executar este comando."
 * @return True if the validation succeeds; otherwise, fails with the provided message.
 */
fun Context.validatePlayer(
  message: String = "§cApenas jogadores podem executar este comando.",
) = validate(isPlayer, message)

/**
 * Validates that the sender has the specified permission and fails with the specified message if they do not.
 *
 * This function checks if the sender has the provided permission. If they do not,
 * it fails with the provided message.
 *
 * @param permission The permission to check.
 * @param message The message to display if the validation fails. Default is "§cVocê não tem permissão para executar esse comando."
 * @return True if the validation succeeds; otherwise, fails with the provided message.
 */
fun Context.validatePermission(
  permission: String,
  message: String = "§cVocê não tem permissão para executar esse comando.",
) = validate(hasPermission(permission), message)

/**
 * Sends the command usage message to the sender.
 *
 * This function retrieves the usage information from the instructor and sends it as a message to the sender.
 */
fun Context.msgUsage() = msg(instructor.usage)

/**
 * Sends a message to the command sender.
 *
 * This function sends a text message to the command sender, utilizing the `msg` method.
 *
 * @param message The message to send.
 */
fun Context.msg(message: String) {
  sender.sendMessage(message)
}

/**
 * Sends a message as a `TextComponent` to the command sender.
 *
 * This function sends a text message formatted as a `TextComponent` to the player
 * if the command sender is a player.
 *
 * @param message The message to send as a `TextComponent`.
 */
fun Context.msg(message: TextComponent) {
  if (isPlayer) player.spigot().sendMessage(message)
}

/**
 * Plays a sound for the player with the specified volume and pitch.
 *
 * This function plays a sound effect for the player if the command sender is a player.
 *
 * @param sound The sound to play.
 * @param volume The volume of the sound. Default is `1f`.
 * @param pitch The pitch of the sound. Default is `1f`.
 */
fun Context.playSound(sound: Sound, volume: Float = 1f, pitch: Float = 1f) {
  if (isPlayer) player.playSound(player.location, sound, volume, pitch)
}
