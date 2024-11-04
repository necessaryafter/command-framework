package harmony.command

import harmony.command.misc.*
import kotlinx.coroutines.*

/**
 * Represents an instructable object that can instruct a command to be executed.
 *
 * This interface provides the necessary properties and functions to define and configure a command
 * that can be executed by a command sender (player or console). It also allows for the customization
 * of command execution logic.
 */
interface Instructable {
  
  /**
   * The executor of this instructable.
   *
   * An [Executor] object that defines the behavior of the instructable command when executed.
   */
  var executor: Executor
  
  /**
   * The completer of this instructable.
   *
   * An [Completer] object that defines the behavior of the instructable command completion suggestions.
   */
  var completer: Completer
  
  /**
   * Returns the type of [Sender] that can perform this instructable.
   *
   * This property defines which sender type (e.g., player, console, or both) is allowed to execute
   * the instructable command.
   */
  var sender: Sender
  
  /**
   * The list of alternate subcommands of this instructable.
   *
   * A mutable list that holds any subcommands (children) that belong to this instructable. Each
   * child command is represented by a [ChildrenInstructor].
   */
  var childrens: MutableList<ChildrenInstructor>
  
  /**
   * The lookup table of subcommands of this instructable.
   *
   * A mutable map that holds any subcommands (children) that belong to this instructable. Each
   * child command is represented by a [ChildrenInstructor].
   *
   * This map is used to quickly access subcommands by their names or alias when searching
   * for them while executing the command.
   */
  var childrensLookup: MutableMap<String, ChildrenInstructor>
  
  /**
   * The maximum number of arguments that can be passed to this instructable.
   *
   * Defines the maximum number of arguments that the instructable command can accept.
   * A negative value (e.g., -1) indicates no limit on the number of arguments.
   *
   * @return The maximum number of arguments that can be passed to this instructable.
   */
  var maxArgs: Int
  
  /**
   * Gets the cached fully qualified name of this instructable.
   *
   * This function returns the fully qualified name of this instructable, which is a combination of
   * the name of the command and the names of any subcommands that belong to it.
   *
   * @return The fully qualified name of this instructable.
   */
  var fullyName: String
  
  /**
   * Gets the usage arguments of this instructable.
   *
   * This function returns the usage arguments of this instructable, which is a string that
   * describes the arguments that the instructable command accepts.
   *
   * @return The usage arguments of this instructable.
   */
  var usageArguments: String
  
  /**
   * Gets the required permission to access the help usage of this instructable.
   *
   * If the sender does not have the required permission to access the help usage of this
   * instructable, the help usage of this instructable will not be displayed.
   *
   * @return The required permission, or null/blank if no permission is required.
   */
  var helpPermission: String?
  
  /**
   * Gets the full usage of this instructable.
   *
   * This function returns the full usage of this instructable, which is a combination of
   * the fully qualified name and the usage arguments.
   *
   * @return The full usage of this instructable.
   */
  val fullUsage get() = "/$fullyName $usageArguments"
  
  /**
   * Verifies if the provided [arg] is a help argument.
   *
   * This function checks if the provided [arg] is a help argument. A help argument is a
   * special argument that is used to display help information for a command.
   */
  fun isHelpArg(arg: String?): Boolean
  
  /**
   * Finds a subcommand in the instructable.
   *
   * @param name The name of the subcommand to find.
   * @return The subcommand if found, or null if not found.
   */
  fun findChildren(name: String): ChildrenInstructor? {
    return childrensLookup[name.lowercase()]
  }
  
  /**
   * Adds a subcommand to the instructable.
   *
   * The subcommand is added to the [childrens] list and the [childrensLookup] map.
   *
   * @param children The subcommand to add.
   */
  fun addChildren(children: ChildrenInstructor) {
    childrens.add(children)
    childrensLookup[children.name.lowercase()] = children
    for (alias in children.aliases) {
      childrensLookup[alias.lowercase()] = children
    }
  }
  
  /**
   * Fetchs the fully qualified name of this instructable.
   *
   * This function returns the fully qualified name of this instructable, which is a combination of
   * the name of the command and the names of any subcommands that belong to it.
   *
   * @return The fully qualified name of this instructable.
   */
  fun fetchFullyName(): String
  
  /**
   * Sets the new performer action of this instructable object.
   *
   * This is for synchronous execution.
   *
   * @param action The lambda defining the behavior of this instructable when executed.
   */
  fun performs(action: Context.() -> Unit) {
    executor = Executor {
      action.invoke(it)
    }
  }
  
  /**
   * Sets the new performer action of this instructable object.
   *
   * This is for asynchronous execution.
   *
   * @param action The lambda defining the behavior of this instructable when executed.
   */
  fun performsAsync(action: suspend Context.() -> Unit) {
    executor = Executor {
      CommandScope.launch {
        action.invoke(it)
      }
    }
  }
  
  /**
   * Sets the new completer of this instructable object.
   *
   * @param suggestions The suggestions function for the completer.
   */
  fun suggests(suggestions: Completer) {
    completer = suggestions
  }
  
  /**
   * Sets the new completer of this instructable object.
   *
   * ### Note
   * The returned list is suposed to be created with [tabComplete] function.
   *
   * @param suggestions The suggestions function for the completer.
   */
  fun suggests(suggestions: Context.(lastWord: String) -> List<String>) {
    completer = Completer { context, lastWord ->
      suggestions.invoke(context, lastWord)
    }
  }
  
  /**
   * Sets the new completer of this instructable object.
   *
   * @param suggestions The suggestions function for the completer.
   */
  fun suggests(vararg suggestions: String, sort: Boolean = true) {
    suggests { _, lastWord ->
      tabComplete(lastWord, suggestions, sort)
    }
  }
  
  /**
   * Sets the new completer of this instructable object.
   *
   * @param suggestions The suggestions function for the completer.
   */
  fun suggests(suggestions: List<String>, sort: Boolean = true) {
    suggests { _, lastWord ->
      tabComplete(lastWord, suggestions, sort)
    }
  }
  
  /**
   * Sets the new completer of this instructable object.
   *
   * @param entries The suggestions entries values for the completer.
   * @param suggestions The mapper function for the completer transforming the entries into strings.
   */
  fun <T> suggests(entries: Collection<T>, sort: Boolean = true, suggestions: (T) -> String) {
    suggests { _, lastWord ->
      tabComplete(lastWord, entries, sort, suggestions)
    }
  }
}
