package harmony.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.md_5.bungee.api.chat.*

/**
 * Represents a child command that extends the functionality of a parent [Instructor] command.
 *
 * A child command is part of a command hierarchy and inherits properties and behavior from its parent.
 * It can have its own specific functionality, usage, and permissions.
 *
 * @property parent The parent [Instructor] command to which this child belongs.
 * @property showInHelp Boolean flag indicating whether this child command should appear in the help menu.
 * @property extraInfo Boolean flag indicating whether extra information should be shown for this command.
 *
 * @constructor Creates a [ChildrenInstructor] with a specified [name] and a [parent] command.
 *
 * @param parent The parent command of this child.
 * @param name The name of this child command.
 */
open class ChildrenInstructor(val parent: Instructor, name: String) : Instructor(name) {
  
  /**
   * Whether this child command should be displayed in help listings.
   * Defaults to `true`.
   */
  var showInHelp = true
  
  /**
   * Whether to show extra information for this child command.
   * Defaults to `true`.
   */
  var extraInfo = true
  
  /**
   * Secondary constructor that allows the child command to have multiple aliases.
   *
   * @param parent The parent command.
   * @param names A list of names, where the first name is the main name and the others are aliases.
   */
  constructor(parent: Instructor, names: List<String>) : this(parent, names.first()) {
    aliases = names.drop(1)
  }
  
  /**
   * Constructs the full name of the command by traversing the parent-child hierarchy.
   *
   * This method builds the complete command name by recursively traversing through
   * the parent commands, appending each name in reverse order to form the full command string.
   *
   * @return A [String] representing the full name of the command, including its parent commands.
   */
  override fun fetchFullyName(): String {
    var cmd = this
    return buildString {
      while (true) {
        insert(0, "${cmd.name} ")
        val parent = cmd.parent
        if (parent is ChildrenInstructor) {
          cmd = parent
        } else {
          break
        }
      }
      insert(0, "${parent.name} ")
    }.trim()
  }
  
  /**
   * Gets the [TextComponent] representation of this command's help information.
   *
   * @param isEnd Whether this command is the last command in the command hierarchy.
   * @return The [TextComponent] representation of the command's help information.
   */
  open fun getInformationalHelp(isEnd: Boolean): TextComponent {
    val text = if (isEnd) "┗" else "┃"

    return Component.text(" §b§l$text §f${fullUsage}")
      .clickEvent(ClickEvent.suggestCommand("/${fullyName}"))
      .hoverEvent(HoverEvent.showText(
        Component.text(
          """
                §fAliases: §7${if (aliases.isEmpty()) "Nenhum" else aliases.joinToString(", ", limit = 5)}
                §fPermissão: §7${permission ?: "Nenhuma"}
                §fAplicado para: §7${sender.display}
                §fUso: §7${usageArguments}
                §fMáximo de argumentos: §7${if (maxArgs < 0) "∞" else maxArgs}
                §fSub-comandos: §7${childrens.size}
                
                §bClique para sugerir.
                """.trimIndent()
        )
      ))
  }
}
