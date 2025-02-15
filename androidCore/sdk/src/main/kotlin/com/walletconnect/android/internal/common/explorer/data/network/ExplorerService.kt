@file:JvmSynthetic

package com.walletconnect.android.internal.common.explorer.data.network

import com.walletconnect.android.internal.common.explorer.data.network.model.DappListingsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExplorerService {

    @GET("dapps")
    suspend fun getAllDapps(@Query("projectId") projectId: String): Response<DappListingsDTO>
}