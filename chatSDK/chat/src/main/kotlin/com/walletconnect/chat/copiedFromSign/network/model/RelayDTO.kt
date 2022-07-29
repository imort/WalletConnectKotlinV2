package com.walletconnect.chat.copiedFromSign.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.walletconnect.chat.copiedFromSign.core.adapters.SubscriptionIdAdapter
import com.walletconnect.chat.copiedFromSign.core.adapters.TopicAdapter
import com.walletconnect.chat.copiedFromSign.core.adapters.TtlAdapter
import com.walletconnect.chat.copiedFromSign.core.model.vo.SubscriptionIdVO
import com.walletconnect.chat.copiedFromSign.core.model.vo.TopicVO
import com.walletconnect.chat.copiedFromSign.core.model.vo.TtlVO

internal sealed class RelayDTO {
    abstract val id: Long
    abstract val jsonrpc: String

    internal sealed class Publish : RelayDTO() {

        @JsonClass(generateAdapter = true)
        internal data class Request(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "method")
            val method: String = IRN_PUBLISH,
            @Json(name = "params")
            val params: Params
        ) : Publish() {

            @JsonClass(generateAdapter = true)
            internal data class Params(
                @Json(name = "topic")
                @field:TopicAdapter.Qualifier
                val topic: TopicVO,
                @Json(name = "message")
                val message: String,
                @Json(name = "ttl")
                @field:TtlAdapter.Qualifier
                val ttl: TtlVO,
                @Json(name = "tag")
                val tag: Int,
                @Json(name = "prompt")
                val prompt: Boolean?,
            )
        }

        internal data class Acknowledgement(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "result")
            val result: Boolean
        ) : Publish()

        internal data class JsonRpcError(
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "error")
            val error: Error,
            @Json(name = "id")
            override val id: Long
        ) : Publish()
    }

    internal sealed class Subscribe : RelayDTO() {

        @JsonClass(generateAdapter = true)
        internal data class Request(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "method")
            val method: String = IRN_SUBSCRIBE,
            @Json(name = "params")
            val params: Params
        ) : Subscribe() {

            @JsonClass(generateAdapter = true)
            internal data class Params(
                @Json(name = "topic")
                @field:TopicAdapter.Qualifier
                val topic: TopicVO
            )
        }

        internal data class Acknowledgement(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "result")
            @field:SubscriptionIdAdapter.Qualifier
            val result: SubscriptionIdVO
        ) : Subscribe()

        internal data class JsonRpcError(
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "error")
            val error: Error,
            @Json(name = "id")
            override val id: Long
        ) : Subscribe()
    }

    internal sealed class Subscription : RelayDTO() {

        @JsonClass(generateAdapter = true)
        internal data class Request(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "method")
            val method: String = IRN_SUBSCRIPTION,
            @Json(name = "params")
            val params: Params
        ) : Subscription() {

            val subscriptionTopic: TopicVO = params.subscriptionData.topic
            val message: String = params.subscriptionData.message

            @JsonClass(generateAdapter = true)
            internal data class Params(
                @Json(name = "id")
                @field:SubscriptionIdAdapter.Qualifier
                val subscriptionId: SubscriptionIdVO,
                @Json(name = "data")
                val subscriptionData: SubscriptionData
            ) {

                @JsonClass(generateAdapter = true)
                internal data class SubscriptionData(
                    @Json(name = "topic")
                    @field:TopicAdapter.Qualifier
                    val topic: TopicVO,
                    @Json(name = "message")
                    val message: String //ack, jsonrpc error, eth_sign
                )
            }
        }

        internal data class Acknowledgement(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "result")
            val result: Boolean
        ) : Subscription()

        internal data class JsonRpcError(
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "error")
            val error: Error,
            @Json(name = "id")
            override val id: Long
        ) : Subscription()
    }

    internal sealed class Unsubscribe : RelayDTO() {

        internal data class Request(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "method")
            val method: String = IRN_UNSUBSCRIBE,
            @Json(name = "params")
            val params: Params
        ) : Unsubscribe() {

            internal data class Params(
                @Json(name = "topic")
                @field:TopicAdapter.Qualifier
                val topic: TopicVO,
                @Json(name = "id")
                @field:SubscriptionIdAdapter.Qualifier
                val subscriptionId: SubscriptionIdVO
            )
        }

        internal data class Acknowledgement(
            @Json(name = "id")
            override val id: Long,
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "result")
            val result: Boolean
        ) : Unsubscribe()

        internal data class JsonRpcError(
            @Json(name = "jsonrpc")
            override val jsonrpc: String = "2.0",
            @Json(name = "error")
            val error: Error,
            @Json(name = "id")
            override val id: Long
        ) : Unsubscribe()
    }

    internal data class Error(
        @Json(name = "code")
        val code: Long,
        @Json(name = "message")
        val message: String,
    ) {
        val errorMessage: String = "Error code: $code; Error message: $message"
    }
}