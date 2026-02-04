@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.androidbootcampiwatepref.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
//ClothApp アプリの「服一覧画面」を描画する
//navController: NavController 画面遷移をする
fun ClothApp(
    navController: NavController,
    viewModel: ClothViewModel = viewModel()
) {
    val clothList by viewModel.clothList.collectAsState()
    val cartMessage by viewModel.cartMessage.collectAsState()

    val context = LocalContext.current

    val cartCount = clothList.count { it.isInCart.value }


    LaunchedEffect(cartMessage) {
        cartMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearCartMessage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("商品一覧") },

                actions = {
                    IconButton(
                        onClick = { navController.navigate("cart") }
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge { Text("$cartCount") }  //カゴの件数
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "カゴを見る"

                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->


        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(clothList) { cloth ->
                    ClothItem(
                        cloth = cloth,
                        onClick = { navController.navigate("detail/${cloth.id}") },
                        onFavoriteClick = { viewModel.onFavoriteClick(cloth.id) },
                        onCartClick = { viewModel.onCartClick(cloth.id) }
                    )

                }
            }
        }
    }
}

    @Composable
    fun ClothItem(
        cloth: Cloth,
        onClick: () -> Unit,
        onFavoriteClick: () -> Unit,
        onCartClick: () -> Unit
    ) {
        //コルーチン（プログラムの実行を一時停止し再開するためのサブルーチン）を使うためのスコープ
        val scope = rememberCoroutineScope()


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() }, //行全体をタップ可能に
            verticalAlignment = Alignment.CenterVertically
        ) {  //画像表示
            Image(
                //painterResourceで画像のIDを指定
                //contentDescription　画像についての詳細
                //contentScale 画像のサイズなどを調節
                painter = painterResource(id = cloth.imageRes),
                contentDescription = cloth.name,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(cloth.name)
                Text("￥${cloth.price}")

                IconButton(
                    onClick = {
                        scope.launch {
                            onFavoriteClick()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (cloth.isFavorite.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "お気に入り",
                        tint = if (cloth.isFavorite.value) Color.Red else Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))


                //カゴボタン（追加/削除ver.)
                Button(
                    onClick = { onCartClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(cloth.isInCart.value)Color.White else Color.Black,
                        contentColor = if(cloth.isInCart.value)Color.Black else Color.White
                    ),
                    border = if(cloth.isInCart.value)
                        BorderStroke(1.dp, Color.Black)
                        else null

                ){
                   Text(if (cloth.isInCart.value)"追加を取り消す" else "カゴに追加")

                }

            }
        }
    }












