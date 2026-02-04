package com.example.androidbootcampiwatepref.datastore

import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context



// 1) Context 拡張で DataStore を一つだけ作る（ファイル名 "favorites"）
private val Context.dataStore by preferencesDataStore(name = "favorites")

object FavoriteDataStore {

    // ▼ 保存するデータのキー（お気に入りIDのセット）

    private val FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")


    /**
     * ▼ ① お気に入りIDセットを保存する
     *    clothId の on/off をアプリ側で作って渡す想定
     */
    
    //saveFavoriteIds(context, ids) すべてのお気に入りIDを一度に保存
    suspend fun saveFavoriteIds(context: Context, ids: Set<Int>){
        context.dataStore.edit{ prefs ->
            prefs[FAVORITE_IDS] = ids.map{ it.toString()}.toSet()
        }
    }
    //loadFavoriteIds 保存されたお気に入りIDのセットを読み込む
    fun loadFavoriteIds(context: Context): Flow<Set<Int>>{
        return context.dataStore.data
            .map{ prefs ->
                prefs[FAVORITE_IDS] ?.map{ it.toInt() }?.toSet() ?: emptySet()
            }
    }
    //toggleFavorite 指定したIDのON/OFFを自動で切り替える
    suspend fun toggleFavorite(context: Context, id: Int){
        context.dataStore.edit{ prefs ->
            val current = prefs[FAVORITE_IDS] ?: emptySet()
            
            prefs[FAVORITE_IDS] =
                if (current.contains(id.toString()))
                    current - id.toString()
                else
                    current + id.toString()
            
        }
    }


   
}