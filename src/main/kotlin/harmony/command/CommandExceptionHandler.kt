package harmony.command

/**
 * Functional interface representing a handler for exceptions that occur during command execution.
 *
 * Implementations of this interface define custom behavior for handling exceptions
 * that may arise when executing commands within a given context. This allows for
 * flexible control over how exceptions are logged, displayed, or suppressed, depending on
 * specific requirements.
 */
fun interface CommandExceptionHandler {
  
  /**
   * Handles an exception thrown during the execution of a command.
   *
   * @param exception The exception that was thrown during command execution.
   * @param command The command instance associated with the execution.
   * @param context The context in which the command was executed.
   */
  fun handle(exception: Exception, command: Instructor, context: Context)
  
  /**
   * Merges this exception handler with another handler.
   *
   * @param other The other exception handler to merge with this one.
   * @return The merged exception handler.
   */
  infix fun merge(other: CommandExceptionHandler): CommandExceptionHandler {
    return CommandExceptionHandler { exception, command, context ->
      this.handle(exception, command, context)
      other.handle(exception, command, context)
    }
  }
}

/**
 * Default exception handler for commands, logging any errors that occur during execution.
 *
 * This handler logs a warning message to the server's logger, identifying the command
 * and the user who executed it, and also prints the full stack trace for debugging purposes.
 */
object DefaultExceptionHandler : CommandExceptionHandler {
  override fun handle(exception: Exception, command: Instructor, context: Context) {
    when (exception) {
      is InstructorStop -> return
      is InstructorError -> context.sender.sendMessage(exception.message)
      else -> {
        val sender = context.sender
        if (sender.isOp) {
          sender.sendMessage("§cUm erro inesperado ocorreu: '${exception.message}'")
        } else {
          sender.sendMessage("§cUm erro inesperado ocorreu. Contate um Administrador.")
        }
        sender.server.logger.warning("§cErro ao executar o comando '${command.name}'. Executado por ${sender.name}")
        exception.printStackTrace()
      }
    }
  }
}

/**
 * Exception handler that ignores any exceptions encountered during command execution.
 *
 * This handler performs no action when an exception occurs, effectively silencing
 * any errors and allowing the command execution to continue without intervention.
 */
object IgnoreExceptionHandler : CommandExceptionHandler {
  override fun handle(exception: Exception, command: Instructor, context: Context) {
    // only handle InstructorErrors
    if (exception is InstructorError) {
      context.sender.sendMessage(exception.message)
    }
  }
}
