package harmony.command

import it.unimi.dsi.fastutil.ints.*
import org.bukkit.entity.*
import kotlin.time.*

/**
 * Manages cooldowns for commands, enforcing a delay between consecutive executions by the same sender.
 *
 * @property cooldown The duration of the cooldown period between command executions for each sender.
 * @property message The message displayed when the sender is on cooldown.
 * @property resetThreshold The threshold of command executions before a reset for inactives entries is performed.
 */
open class CommandCooldown(
  var cooldown: Duration,
  var message: String = "§cAguarde para usar o comando novamente.",
  var resetThreshold: Int = 200
) {
  
  /**
   * A map storing the last execution timestamp for each sender, indexed by the sender's hashed uuid.
   */
  val map = Int2LongOpenHashMap(8, 0.85f)
  
  /**
   * Computes a unique hash code based on the sender's uuid, used as a key in the cooldown map.
   *
   * @param sender The command sender whose hash code is to be computed.
   * @return An integer hash code representing the sender.
   */
  private fun hash(sender: Player): Int {
    return sender.uniqueId.hashCode()
  }
  
  /**
   * Checks if the sender is eligible to execute the command based on the cooldown period.
   *
   * If the cooldown has elapsed since the last execution by the sender, the timestamp is updated
   * to the current time, and the method returns `true`. Otherwise, it returns `false`, indicating
   * that the sender must wait until the cooldown period expires.
   *
   * @param sender The command sender attempting to execute the command.
   * @return `true` if the sender can execute the command, or `false` if they must wait.
   */
  fun isReady(sender: Player): Boolean {
    val now = System.currentTimeMillis()
    val hash = hash(sender)
    val last = map.get(hash)
    if (last < now - cooldown.inWholeMilliseconds) {
      map[hash] = now
      return true
    }
    return false
  }
  
  /**
   * Resets the cooldown for a specific sender, allowing immediate command execution.
   *
   * @param sender The command sender for whom the cooldown should be reset.
   */
  fun reset(sender: Player) {
    map.remove(hash(sender))
  }
  
  /**
   * Resets cooldowns for all senders, allowing all to execute the command immediately.
   */
  fun resetAll() {
    map.clear()
  }
  
  /**
   * Resets cooldowns for inactive senders, that is, those that have not executed the command recently.
   */
  fun resetInactives() {
    val now = System.currentTimeMillis()
    val iterator = map.int2LongEntrySet().fastIterator()
    synchronized(iterator) {
      while (iterator.hasNext()) {
        val entry = iterator.next()
        if (now - entry.longValue > cooldown.inWholeMilliseconds) {
          iterator.remove()
        }
      }
    }
  }
}
