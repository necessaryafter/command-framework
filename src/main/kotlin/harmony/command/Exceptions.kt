@file:Suppress("NOTHING_TO_INLINE")

package harmony.command

/**
 * Represents an error that occurs during the execution of an instructor command.
 *
 * This error can be triggered when a required argument is missing, when a validation fails,
 * or when the `error` function from the [Context] interface is invoked.
 *
 * @constructor Initializes the error with a custom message.
 * @param message The detailed error message explaining the cause of the error.
 */
class InstructorError(message: String) : Exception(message)

/**
 * Represents an exception that can be used to stop the execution of a command.
 *
 * This exception can be thrown to signal that the command should be immediately halted,
 * without necessarily indicating an error condition.
 */
object InstructorStop : Exception()

/**
 * Throws an [InstructorError] with the specified [message].
 *
 * This function is intended to signal an error condition in the command execution,
 * providing a message that explains the cause of the failure.
 *
 * @param message The message explaining the reason for the error.
 * @throws InstructorError Always throws an exception with the given message.
 */
inline fun Context.fail(message: String): Nothing = throw InstructorError(message)

/**
 * Throws an [InstructorError] with the usage message of the current command.
 *
 * This is typically used when the command's usage format has not been followed correctly.
 * The error message will include the expected usage information from the [Context.instructor] object.
 *
 * @throws InstructorError Always throws an exception with the usage message of the command.
 */
inline fun Context.failUsage(): Nothing = throw InstructorError(instructor.usage)

/**
 * Stops the execution of a command by throwing an [InstructorStop] exception.
 *
 * This function is used when you want to halt command execution without signaling an error.
 * It immediately stops further processing of the command.
 *
 * @throws InstructorStop Always throws an exception to halt execution.
 */
inline fun Context.stop(): Nothing = throw InstructorStop
