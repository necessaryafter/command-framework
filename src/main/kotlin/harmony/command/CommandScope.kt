package harmony.command

import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * A coroutine scope for command execution.
 *
 * Its a simple coroutine scope with a [SupervisorJob] to allow for cancellation of child coroutines
 * without interrupting others coroutines.
 */
object CommandScope : CoroutineScope {
  override val coroutineContext: CoroutineContext = SupervisorJob()
}
