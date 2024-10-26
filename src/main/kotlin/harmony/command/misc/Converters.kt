package harmony.command.misc

import org.bukkit.*
import org.bukkit.enchantments.*
import org.bukkit.entity.*
import org.bukkit.material.*

/**
 * Converts a string representation of a sound name to a Sound instance.
 *
 * @return The Sound instance associated with the given name, or null if not found.
 */
fun String.toSound(): Sound? {
  return runCatching { Sound.valueOf(uppercase()) }.getOrNull()
}

/**
 * Converts a string representation of an entity type name to an EntityType instance.
 * If the name is not found, attempts to convert the string to an ID and retrieve the entity type.
 *
 * @return The EntityType instance associated with the given name or ID, or null if not found.
 */
fun String.toEntityType(): EntityType? {
  return runCatching {
    EntityType.valueOf(uppercase())
  }.recoverCatching {
    EntityType.fromId(toInt())
  }.getOrNull()
}

/**
 * Converts a string representation of a material and optional data to a MaterialData instance.
 *
 * @return The MaterialData instance created from the string, or null if the material or data is invalid.
 */
fun String.toMaterialData(): MaterialData? {
  val split = split(':', limit = 2)
  val material = Material.matchMaterial(split[0]) ?: return null
  val data = split[1].toByteOrNull() ?: return null
  return MaterialData(material, data)
}

/**
 * Converts a string representation of a game mode name to a GameMode instance.
 * If the name is not found, attempts to convert the string to an integer and retrieve the game mode.
 *
 * @return The GameMode instance associated with the given name or ID, or null if not found.
 */
fun String.toGamemode(): GameMode? {
  return runCatching {
    GameMode.valueOf(uppercase())
  }.recoverCatching {
    GameMode.getByValue(toInt())
  }.getOrNull()
}

/**
 * Converts a string representation of an enchantment name to an Enchantment instance.
 * If the name is not found, attempts to convert the string to an integer and retrieve the enchantment by ID.
 *
 * @return The Enchantment instance associated with the given name or ID, or null if not found.
 */
fun String.toEnchantment(): Enchantment? {
  return runCatching {
    Enchantment.getByName(this)
  }.recoverCatching {
    Enchantment.getById(toInt())
  }.getOrNull()
}


/**
 * Converts a string representation of a material to a Materials instance.
 *
 * @return The Materials instance associated with the given name, or null if not found.
 */
fun String.toMaterial(): Material? {
  return Material.matchMaterial(uppercase())
}

/**
 * Converts a string representation of a material to a Materials instance.
 * If the material is not found, returns AIR as the default.
 *
 * @return The Materials instance associated with the given name, or AIR if not found.
 */
fun String.toMaterialOrAir(): Material {
  return Material.matchMaterial(uppercase()) ?: Material.AIR
}
