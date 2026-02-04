package com.example.androidbootcampiwatepref.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ClothViewModel = viewModel(),
    onBack: () -> Unit
){
    val clothList by viewModel.clothList.collectAsState()
    val cartItems = clothList.filter {it.isInCart.value}

    val totalPrice = cartItems.sumOf{ it.price * it.quantity.value}



    //画面の基本レイアウト
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("カゴの中身") },
                navigationIcon = {
                    IconButton(onClick = onBack){
                        Icon(Icons.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            LazyColumn(
                modifier
                = Modifier.weight(1f)
            ) {
                items(cartItems) { cloth ->
                    CartItemRow(cloth, viewModel)
                }
            }
            //区切り線
            Divider()


            Text(
                text = "合計：￥$totalPrice",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun CartItemRow(
    cloth:Cloth,
    viewModel:ClothViewModel
){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Row{
            Image(
                painter = painterResource(id = cloth.imageRes),
                contentDescription = cloth.name,
                modifier = Modifier.size(80.dp),
                //ContentScale.Crop 空いているスペースがなくなるように画像を切り抜いて中央に配置
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column{
                Text(cloth.name, style = MaterialTheme.typography.titleMedium)
                Text("￥${cloth.price}")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick = { viewModel.decreaseQuantity(cloth.id)}){
                Text("ー",fontSize = 20.sp)
            }

            Text("${cloth.quantity.value}",modifier = Modifier.padding(8.dp))

            IconButton(onClick = { viewModel.increaseQuantity(cloth.id)}){
                Text("＋",fontSize = 20.sp)
            }
        }
        IconButton(
            onClick = { viewModel.onCartClick(cloth.id)}
        ){
            Text("削除")
        }
    }
}