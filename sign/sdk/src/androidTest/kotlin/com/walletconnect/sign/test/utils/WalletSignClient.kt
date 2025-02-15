package com.walletconnect.sign.test.utils

import com.walletconnect.sign.client.Sign
import timber.log.Timber

val WalletSignClient = TestClient.Wallet.signClient

val walletClientRespondToRequest = { topic: String, response: Sign.Model.JsonRpcResponse ->
    WalletSignClient.respond(
        Sign.Params.Response(topic, jsonRpcResponse = response),
        onSuccess = { Timber.d("Wallet: respondOnSuccess") },
        onError = ::globalOnError
    )
}

val walletClientUpdateSession = { topic: String ->
    WalletSignClient.update(
        Sign.Params.Update(topic, mapOf(sessionNamespaceKey to sessionNamespace.copy(sessionChains.plus("eip155:2")))),
        onSuccess = { Timber.d("Wallet: updateOnSuccess") },
        onError = ::globalOnError
    )
}


val walletClientEmitEvent = { topic: String ->
    WalletSignClient.emit(
        Sign.Params.Emit(topic, Sign.Model.SessionEvent(sessionEvents.first(), "dummy"), sessionChains.first()),
        onSuccess = { Timber.d("Wallet: emitOnSuccess") },
        onError = ::globalOnError
    )
}

val walletClientExtendSession = { topic: String ->
    WalletSignClient.extend(
        Sign.Params.Extend(topic),
        onSuccess = { Timber.d("Wallet: extendOnSuccess") },
        onError = ::globalOnError
    )
}