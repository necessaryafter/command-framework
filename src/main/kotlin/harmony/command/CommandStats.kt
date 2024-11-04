package harmony.command

import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Represents some statistics about the execution of a command.
 *
 * @property executedCount The number of times the command has been executed.
 * @property failedCount The number of times the command has failed.
 * @property successCount The number of times the command has succeeded.
 * @property lastExecution The timestamp of the last time the command was executed.
 * @property lastFailure The timestamp of the last time the command failed.
 * @property lastSuccess The timestamp of the last time the command succeeded.
 */
data class CommandStats(
  var executedCount: Int = 0,
  var failedCount: Int = 0,
  var successCount: Int = 0,
  var lastExecution: Long = 0,
  var lastFailure: Long = 0,
  var lastSuccess: Long = 0
) {
  
  /**
   * Calculates the success rate of the command executions as a percentage.
   *
   * @return The success rate, or 0.0 if no commands were executed.
   */
  val successRate: Double get() = if (executedCount > 0) (successCount / executedCount.toDouble()) * 100 else 0.0
  
  
  /**
   * Calculates the failure rate of the command executions as a percentage.
   *
   * @return The failure rate, or 0.0 if no commands were executed.
   */
  val failureRate: Double get() = if (executedCount > 0) (failedCount / executedCount.toDouble()) * 100 else 0.0
  
  /**
   * Determines the failure-to-success ratio of the command executions.
   *
   * @return The ratio of failures to successes, or null if there are no successes.
   */
  val failureToSuccessRatio: Double? get() = if (successCount > 0) failedCount / successCount.toDouble() else null
  
  /**
   * Calculates the time since the last execution of the command.
   *
   * @return Duration since the last execution, or null if the command has not been executed.
   */
  val timeSinceLastExecution: Duration
    get() = if (lastExecution > 0) (System.currentTimeMillis() - lastExecution).milliseconds else Duration.ZERO
  
  
  /**
   * Calculates the time since the last successful execution of the command.
   *
   * @return Duration since the last success, or null if there has been no success.
   */
  val timeSinceLastSuccess: Duration
    get() = if (lastSuccess > 0) (System.currentTimeMillis() - lastSuccess).milliseconds else Duration.ZERO
  
  /**
   * Calculates the time since the last failed execution of the command.
   *
   * @return Duration since the last failure, or null if there has been no failure.
   */
  val timeSinceLastFailure: Duration
    get() = if (lastFailure > 0) (System.currentTimeMillis() - lastFailure).milliseconds else Duration.ZERO
  
  /**
   * Calculates the average interval between command executions.
   *
   * @param firstExecution The timestamp of the first execution.
   * @return The average interval as a Duration, or null if executed less than twice.
   */
  fun averageExecutionInterval(firstExecution: Long): Duration {
    return if (executedCount > 1) {
      val totalDuration = (lastExecution - firstExecution).milliseconds
      totalDuration / (executedCount - 1)
    } else {
      Duration.ZERO
    }
  }
  
  /**
   * Calculates the frequency of command execution over a given period.
   *
   * @param period The period in seconds over which to calculate the frequency.
   * @return The frequency as executions per second, or 0.0 if the period is 0 or undefined.
   */
  fun executionFrequency(period: Long): Double {
    return if (period > 0) executedCount / period.toDouble() else 0.0
  }
  
  /**
   * Resets all counters and timestamps to their default values.
   */
  fun resetStats() {
    executedCount = 0
    failedCount = 0
    successCount = 0
    lastExecution = 0
    lastFailure = 0
    lastSuccess = 0
  }
}

