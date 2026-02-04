package com.example.androidbootcampiwatepref.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch


/**
 * ClothDetailScreen
 * -------------------------
 * 1. ClothViewModel から目的の cloth を取得
 * 2. ハート（お気に入り）は ViewModel + DataStore と同期
 * 3. カゴに追加ボタンは Toast を表示
 */

@Composable
fun ClothDetailScreen(
    navController: NavController,
    clothId: String?,
    viewModel: ClothViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cartMessage by viewModel.cartMessage.collectAsState()
    val clothList by viewModel.clothList.collectAsState()

    val cartCount = clothList.count { it.isInCart.value }

    // Toastを出すイベント監視はComposableの先頭で行う
    LaunchedEffect(cartMessage) {
        cartMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearCartMessage()
        }
    }





    val id = clothId?.toIntOrNull()


    val cloth = clothList.find{ it.id == id }

    if(cloth == null){
        Text("商品が見つかりません")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //戻るボタン
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "戻る"
                )
            }

            IconButton(onClick = { navController.navigate("cart") }) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            androidx.compose.material3.Badge {
                                Text("$cartCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "カゴを見る"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = cloth.imageRes),
            contentDescription = cloth.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer (modifier = Modifier.height(16.dp))

        Text (text = cloth.name, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = cloth.description,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.weight(1f))



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

            IconButton(
                onClick = {

                    scope.launch{
                        viewModel.onFavoriteClick(cloth.id)
                    }
                },
                modifier = Modifier.size(60.dp)
            ){
                Icon(
                    imageVector =
                        if(cloth.isFavorite.value)Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                    contentDescription = "お気に入り",
                    tint = if(cloth.isFavorite.value) Color.Red else Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
               onClick = { viewModel.onCartClick(cloth.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (cloth.isInCart.value) Color.White else Color.Black,
                    contentColor =
                        if (cloth.isInCart.value) Color.Black else Color.White
                ),
                border = if(cloth.isInCart.value)
                    BorderStroke(1.dp, Color.Black)
                else null,
                modifier = Modifier
                    .weight(1f)//横に広げる（中央寄せ効果）
                    .height(55.dp)//ボタンを少し大きく
                    .padding(start = 20.dp)//ハートとの距離
            ){
                Text(if(cloth.isInCart.value) "追加を取り消す" else "カゴに追加")

            }
        }
    }
}