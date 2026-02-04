package com.example.androidbootcampiwatepref.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.cartDataStore by preferencesDataStore(name = "cart")

object CartDataStore {
    private val CART_IDS = stringSetPreferencesKey("cart_ids")

    fun loadCart(context: Context): Flow<Set<Int>>{
        return context.cartDataStore.data.map{ pref ->
            pref[CART_IDS]?.map{ it.toInt() }?.toSet()?:emptySet()
        }
    }

    suspend fun toggleCart(context: Context, id: Int){
        context.cartDataStore.edit{ pref ->
            val current = pref[CART_IDS] ?: emptySet()

            pref[CART_IDS] =
                if(current.contains(id.toString()))
                    current - id.toString()
                else
                    current + id.toString()
        }
    }
}