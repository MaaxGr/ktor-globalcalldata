package de.maaxgr.ktor.globalcalldata

import io.ktor.application.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

interface Key<E : Any>
object Call : Key<ApplicationCall>

open class CallData(private val context: CoroutineContext) {
    protected val delegate = CallDataDelegate(context)
    val call: ApplicationCall by delegate.propNotNull(Call)
}

suspend fun callData(): CallData {
    if (!GlobalCallData.enabled) {
        throw IllegalAccessException("GlobalCallData Feature is not enabled!")
    }

    return CallData(coroutineContext)
}