package harmony.command

/**
 * Represents an instructable object that can instruct a command to be executed.
 *
 * This interface provides the necessary properties and functions to define and configure a command
 * that can be executed by a command sender (player or console). It also allows for the customization
 * of command execution logic.
 */
interface Instructable {
  
  /**
   * The current performer action of this instructable object.
   *
   * This is a lambda function that will be executed when the instructable command is performed.
   * It is invoked on an [Argumentable] object.
   */
  var executor: Argumentable.() -> Unit
  
  /**
   * Sets the new performer action of this instructable object.
   *
   * This function allows you to define the action to be performed by setting a new [executor] lambda.
   *
   * @param action The lambda defining the behavior of this instructable when executed.
   */
  fun performs(action: Argumentable.() -> Unit) {
    executor = action
  }
  
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
   * If this instructable is asynchronous.
   *
   * This property indicates whether the instructable command is asynchronous or synchronous.
   * Defaults to false.
   *
   * @return `true` if the instructable command is asynchronous, `false` otherwise.
   */
  var isAsync: Boolean
  
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
}
