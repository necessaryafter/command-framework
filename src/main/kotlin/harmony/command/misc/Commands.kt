package harmony.command.misc

import harmony.command.*
import it.unimi.dsi.fastutil.objects.*
import org.bukkit.*
import org.bukkit.command.*
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.util.*

val commandMap: SimpleCommandMap get() = (Bukkit.getServer() as CraftServer).commandMap

/**
 * Finds a command registered in the server by its name.
 *
 * @param name The name of the command to be searched for.
 * @return The [Command] if found, or `null` if no command with the given name exists.
 */
fun findCommand(name: String): Command? {
  return commandMap.getCommand(name)
  //return commandMap.commands.find { it.name == name }
}

/**
 * Finds an instructor command registered in the server by its name.
 *
 * This function attempts to locate a command and cast it to an [Instructor].
 *
 * @param name The name of the instructor command to be searched for.
 * @return The [Instructor] if found, or `null` if no such instructor exists.
 */
fun findInstructor(name: String): Instructor? {
  return findCommand(name) as? Instructor
}

/**
 * Registers this command on the server.
 *
 * This function registers the current command to the server's command map.
 * It associates the command with its label, making it available for use.
 */
fun Command.register() {
  commandMap.register(label, this)
}

/**
 * Unregisters this command from the server.
 *
 * This function removes the current command from the server's command map,
 * effectively making it unavailable for use.
 */
fun Command.unregister() {
  this.unregister(commandMap)
}

/**
 * Unregisters a command from the server by its name.
 *
 * This function finds a command by its name and unregisters it from the server.
 *
 * @param command The name of the command to be unregistered.
 */
fun unregisterCommand(command: String) {
  findCommand(command)?.unregister(commandMap)
}

/**
 * Unregisters multiple commands from the server.
 *
 * This function takes a variable number of command names and unregisters each one from the server.
 *
 * @param commands A list of command names to be unregistered.
 */
fun unregisterCommands(vararg commands: String) {
  val map = commandMap
  for (cmd in commands) {
    findCommand(cmd)?.unregister(map)
  }
}

/**
 * Unregisters multiple commands from the server using an iterable.
 *
 * This function takes an iterable of command names and unregisters each one from the server.
 *
 * @param commands An iterable of command names to be unregistered.
 */
fun unregisterCommands(commands: Iterable<String>) {
  val map = commandMap
  for (cmd in commands) {
    findCommand(cmd)?.unregister(map)
  }
}

/**
 * Provides tab-completion suggestions based on the last word typed.
 *
 * This function compares the given [lastWord] with a set of possible completions,
 * returning a sorted list of suggestions that start with the same characters.
 *
 * @param lastWord The last word typed by the player for which suggestions are being generated.
 * @param possibilities A list of possible completions to be filtered and sorted.
 * @param sort Whether to sort the suggestions alphabetically.
 * @return A mutable list of sorted, case-insensitive suggestions that match the last word.
 */
fun tabComplete(lastWord: String, possibilities: Iterable<String>, sort: Boolean = true): List<String> {
  val result = ObjectArrayList<String>()
  possibilities.filterTo(result) { StringUtil.startsWithIgnoreCase(it, lastWord) }
  if (sort) {
    result.sortWith(String.CASE_INSENSITIVE_ORDER)
  }
  return result
}

/**
 * Provides tab-completion suggestions based on the last word typed.
 *
 * This function compares the given [lastWord] with a set of possible completions,
 * returning a sorted list of suggestions that start with the same characters.
 *
 * @param lastWord The last word typed by the player for which suggestions are being generated.
 * @param possibilities A list of possible completions to be filtered and sorted.
 * @param sort Whether to sort the suggestions alphabetically.
 * @return A mutable list of sorted, case-insensitive suggestions that match the last word.
 */
fun tabComplete(lastWord: String, possibilities: Array<out String>, sort: Boolean = true): List<String> {
  val result = ObjectArrayList<String>()
  possibilities.filterTo(result) {it.startsWith(lastWord, ignoreCase = true) }
  if (sort) {
    result.sortWith(String.CASE_INSENSITIVE_ORDER)
  }
  return result
}


/**
 * Provides tab-completion suggestions based on the last word typed.
 *
 * This function compares the given [lastWord] with a set of possible completions,
 * returning a sorted list of suggestions that start with the same characters.
 *
 * @param lastWord The last word typed by the player for which suggestions are being generated.
 * @param entries A list of possible completions to be filtered and sorted.
 * @param transform A function that transforms each item in the list into a string for tab completion.
 * @param sort Whether to sort the suggestions alphabetically.
 * @return A mutable list of sorted, case-insensitive suggestions that match the last word.
 */
inline fun <T> tabComplete(lastWord: String, entries: Collection<T>, sort: Boolean = true, transform: (T) -> String): List<String> {
  return tabComplete(lastWord, entries.mapTo(ObjectArrayList(entries.size), transform), sort)
}

