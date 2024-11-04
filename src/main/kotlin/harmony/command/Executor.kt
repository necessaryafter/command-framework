package harmony.command

/**
 * The interface for defining command execution logic.
 */
fun interface Executor {
  
  /**
   * Executes the instructable command.
   */
  fun execute(context: Context)
  
  /**
   * Merges this executor with another executor.
   *
   * @param other The other executor to merge with this one.
   * @return The merged executor.
   */
  fun merge(other: Executor): Executor {
    return Executor {
      execute(it)
      other.execute(it)
    }
  }
}
