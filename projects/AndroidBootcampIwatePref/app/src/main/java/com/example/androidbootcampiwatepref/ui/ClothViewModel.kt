package com.example.androidbootcampiwatepref.ui
import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidbootcampiwatepref.datastore.FavoriteDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.androidbootcampiwatepref.R
import com.example.androidbootcampiwatepref.datastore.CartDataStore

/**
 * ▼ アプリ全体を管理する ViewModel
 *
 * 役割：
 * ・Cloth 一覧を持つ
 * ・DataStore からお気に入りIDを読み込む
 * ・ハートボタン押した時の状態変更
 * ・変更を DataStore に保存
 */

data class Cloth(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int,
    val description: String,
    var isFavorite: MutableState<Boolean> = mutableStateOf(false),
    var isInCart: MutableState<Boolean> = mutableStateOf(false),
    var quantity: MutableState<Int> = mutableStateOf(1)
)
class ClothViewModel(application: Application) : AndroidViewModel(application) {

    // ① アプリ全体で使う cloth リスト
    private val _clothList = MutableStateFlow(
        listOf(
            Cloth(
                1, "シンプルパーカー", 3500, R.drawable.simple_parka,
                "男女問わず多様なコーデが可能"
            ),
            Cloth(
                2, "フラワーTシャツ", 1500, R.drawable.flower_tshirt,
                "黄色の花がほどよいアクセントに"
            ),
            Cloth(
                3, "ロゴTシャツ", 1500, R.drawable.logo_tshirt,
                "デニムやスウェットと相性抜群！"
            ),
            Cloth(
                4, "ノースリーブTシャツ", 1000, R.drawable.sleeveless_shirt,
                "シンプルなコーデにおすすめ"
            ),
            Cloth(
                5, "トレーナー", 1500, R.drawable.sweat_shirt,
                "裏起毛で冬も快適"
            )
        )
    )
    val clothList: StateFlow<List<Cloth>> = _clothList

    // ② アプリ起動時：DataStoreからお気に入りIDを読み込んで反映
    init {
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            FavoriteDataStore.loadFavoriteIds(context).collect { favSet ->
                _clothList.update { list ->
                    list.map { cloth ->
                        cloth.apply {
                            isFavorite.value = id in favSet
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            CartDataStore.loadCart(context).collect { cartSet ->
                _clothList.update { list ->
                    list.map { cloth ->
                        cloth.apply {
                            isInCart.value = id in cartSet
                        }
                    }
                }
            }
        }
    }

    /**
     * ③ ハートマークが押されたときの処理
     */
    fun onFavoriteClick(id: Int) {
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            // DataStore で ON / OFF 切り替え
            FavoriteDataStore.toggleFavorite(context, id)
        }
    }

    private val _cartMessage = MutableStateFlow<String?>(null)
    val cartMessage: StateFlow<String?> = _cartMessage
    fun onCartClick(id: Int) {
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            CartDataStore.toggleCart(context, id)
        }

        _clothList.update { list ->
            list.map { cloth ->
                if (cloth.id == id) {
                    val added = !cloth.isInCart.value
                    cloth.apply {
                        isInCart.value = added
                        if (isInCart.value && quantity.value == 0) {
                            quantity.value = 1
                        }
                    }

                    _cartMessage.value =
                        if(added) "「${cloth.name}」をカゴに追加しました！"
                        else "「${cloth.name}」をカゴから削除しました"

                    cloth
                }
                else cloth
            }
        }
    }

    fun increaseQuantity(id: Int){
        _clothList.update{ list ->
            list.map{ cloth ->
                if(cloth.id == id){
                    cloth.apply {
                        quantity.value++
                    }
                }
                else cloth
            }
        }
    }

    fun decreaseQuantity(id: Int) {
        _clothList.update { list ->
            list.map { cloth ->
                if (cloth.id == id && cloth.quantity.value > 1) {
                    cloth.apply {
                        quantity.value--
                    }
                }
                else cloth
            }
        }
    }

    fun clearCartMessage(){
        _cartMessage.value = null
    }

}
