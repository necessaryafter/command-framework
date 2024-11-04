package harmony.command

import harmony.command.misc.*
import org.bukkit.*

/**
 * The interface for defining command completion logic.
 */
fun interface Completer {
  
  /**
   * Suggests possible completions based on the given [context].
   *
   * @param context The context of the command that is being completed.
   * @param lastWord The last word of the command that is being completed.
   * @return A list of possible completions.
   */
  fun suggest(context: Context, lastWord: String): List<String>
  
  /**
   * Merges this completer with the specified [completer].
   *
   * @param completer The completer to merge with this one.
   * @return The merged completer.
   */
  infix fun merge(completer: Completer): Completer {
    return Completer { context, lastWord ->
      this.suggest(context, lastWord) + completer.suggest(context, lastWord)
    }
  }
}

/**
 * A completer that returns an empty list.
 *
 * This is only useful for clearing the list of suggestions.
 */
object EmptyCompleter : Completer {
  override fun suggest(context: Context, lastWord: String): List<String> {
    return emptyList()
  }
}

/**
 * A completer that suggests players name's based on the last word argument.
 */
object PlayerCompleter : Completer {
  override fun suggest(context: Context, lastWord: String): List<String> {
    return tabComplete(lastWord, Bukkit.getOnlinePlayers()) { it.name }
  }
}

/**
 * A completer that suggests subcommands based on the last word argument.
 */
object ChildrenCompleter : Completer {
  override fun suggest(context: Context, lastWord: String): List<String> {
    val childrens = context.instructor.childrens
    if (childrens.isEmpty()) {
      return emptyList()
    }
    
    return tabComplete(lastWord, childrens) { it.name }
  }
}

/**
 * A completer that suggests subcommands and players based on the last word argument.
 *
 * ### Behavior:
 * 1. Suggests subcommands based on the last word argument.
 * 2. Suggests players based on the last word argument if none subcommands are found.
 */
object SmartCompleter : Completer {
  override fun suggest(context: Context, lastWord: String): List<String> {
    val childrens = context.instructor.childrens
    if (childrens.isEmpty()) {
      return PlayerCompleter.suggest(context, lastWord)
    }
    
    val childrensSuggestions = tabComplete(lastWord, childrens) { it.name }
    if (childrensSuggestions.isEmpty()) {
      return PlayerCompleter.suggest(context, lastWord)
    }
    
    return childrensSuggestions
  }
}
