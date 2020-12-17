@file:Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")

package de.maaxgr.ktor.globalcalldata

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class GlobalCallData(configuration: Configuration) {

    class Configuration

    private fun interceptBeforeReceive(context: PipelineContext<Unit, ApplicationCall>) {
        callCache.create(context.coroutineContext)
        callCache.set(context.coroutineContext, Call, context.call)
    }

    private fun interceptAfterSend(context: PipelineContext<Any, ApplicationCall>) {
        callCache.remove(context.coroutineContext)
    }

    /**
     * Installable feature for [GlobalCallData].
     */
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, GlobalCallData> {
        override val key = AttributeKey<GlobalCallData>("CustomHeader")
        val callCache = CallCache()
        var enabled = false

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): GlobalCallData {
            val configuration = Configuration().apply(configure)

            return GlobalCallData(configuration).also { callDataFeature ->
                pipeline.intercept(ApplicationCallPipeline.Call) {
                    callDataFeature.interceptBeforeReceive(this)
                }

                pipeline.sendPipeline.intercept(ApplicationSendPipeline.Before) {
                    callDataFeature.interceptAfterSend(this)
                }

                enabled = true
            }
        }
    }
}

