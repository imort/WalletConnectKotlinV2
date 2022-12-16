package com.walletconnect.android.impl.di

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.walletconnect.android.impl.core.AndroidCoreDatabase
import com.walletconnect.android.impl.storage.JsonRpcHistory
import com.walletconnect.android.impl.storage.MetadataStorageRepository
import com.walletconnect.android.impl.storage.PairingStorageRepository
import com.walletconnect.android.impl.storage.data.dao.MetaData
import com.walletconnect.android.internal.common.model.AppMetaDataType
import com.walletconnect.android.internal.common.storage.MetadataStorageRepositoryInterface
import com.walletconnect.android.internal.common.storage.PairingStorageRepositoryInterface
import com.walletconnect.android.internal.common.wcKoinApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun baseStorageModule() = module {

    fun Scope.createCoreDB(): AndroidCoreDatabase = AndroidCoreDatabase(
        get(named(AndroidCoreDITags.ANDROID_CORE_DATABASE_DRIVER)),
        MetaDataAdapter = MetaData.Adapter(
            iconsAdapter = get(named(AndroidCoreDITags.COLUMN_ADAPTER_LIST)),
            typeAdapter = get(named(AndroidCoreDITags.COLUMN_ADAPTER_APPMETADATATYPE))
        ),
    )

    if (wcKoinApp.koin.getOrNull<ColumnAdapter<List<String>, String>>(named(AndroidCoreDITags.COLUMN_ADAPTER_LIST)) == null) {
        single<ColumnAdapter<List<String>, String>>(named(AndroidCoreDITags.COLUMN_ADAPTER_LIST)) {
            object : ColumnAdapter<List<String>, String> {
                override fun decode(databaseValue: String) =
                    if (databaseValue.isBlank()) {
                        listOf()
                    } else {
                        databaseValue.split(",")
                    }

                override fun encode(value: List<String>) = value.joinToString(separator = ",")
            }
        }
    } else {
        wcKoinApp.koin.get<ColumnAdapter<List<String>, String>>(named(AndroidCoreDITags.COLUMN_ADAPTER_LIST))
    }

    if (wcKoinApp.koin.getOrNull<ColumnAdapter<AppMetaDataType, String>>(named(AndroidCoreDITags.COLUMN_ADAPTER_APPMETADATATYPE)) == null) {
        single<ColumnAdapter<AppMetaDataType, String>>(named(AndroidCoreDITags.COLUMN_ADAPTER_APPMETADATATYPE)) { EnumColumnAdapter() }
    } else {
        wcKoinApp.koin.get<ColumnAdapter<AppMetaDataType, String>>(named(AndroidCoreDITags.COLUMN_ADAPTER_APPMETADATATYPE))
    }

    if (wcKoinApp.koin.getOrNull<AndroidCoreDatabase>(named(AndroidCoreDITags.ANDROID_CORE_DATABASE)) == null) {
        single<AndroidCoreDatabase>(named(AndroidCoreDITags.ANDROID_CORE_DATABASE)) {
            try {
                createCoreDB().also { database -> database.jsonRpcHistoryQueries.selectLastInsertedRowId().executeAsOneOrNull() }
            } catch (e: Exception) {
                deleteDBs(DBNames.ANDROID_CORE_DB_NAME)
                createCoreDB()
            }
        }
    } else {
        wcKoinApp.koin.get<AndroidCoreDatabase>(named(AndroidCoreDITags.ANDROID_CORE_DATABASE))
    }

    single { get<AndroidCoreDatabase>(named(AndroidCoreDITags.ANDROID_CORE_DATABASE)).jsonRpcHistoryQueries }

    single { get<AndroidCoreDatabase>(named(AndroidCoreDITags.ANDROID_CORE_DATABASE)).pairingQueries }

    single { get<AndroidCoreDatabase>(named(AndroidCoreDITags.ANDROID_CORE_DATABASE)).metaDataQueries }

    if (wcKoinApp.koin.getOrNull<JsonRpcHistory>() == null) {
        single<MetadataStorageRepositoryInterface> { MetadataStorageRepository(get()) }
    } else {
        wcKoinApp.koin.get<JsonRpcHistory>()
    }

    single<PairingStorageRepositoryInterface> {
        PairingStorageRepository(get())
    }

    if (wcKoinApp.koin.getOrNull<JsonRpcHistory>() == null) {
        single { JsonRpcHistory(get(), get()) }
    } else {
        wcKoinApp.koin.get<JsonRpcHistory>()
    }
}

object DBNames {
    const val ANDROID_CORE_DB_NAME = "WalletConnectAndroidCore.db"

    fun getSdkDBName(storageSuffix: String) = "WalletConnectV2$storageSuffix.db"
}

fun Scope.deleteDBs(dbName: String) {
    androidContext().deleteDatabase(dbName)
}