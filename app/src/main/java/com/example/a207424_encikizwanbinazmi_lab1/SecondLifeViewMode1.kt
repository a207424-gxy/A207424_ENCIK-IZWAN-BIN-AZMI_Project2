package com.example.a207424_encikizwanbinazmi_lab1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// 1.定义应用的数据模型
data class Product(
    val title: String,
    val price: String,
    val imageRes: Int,
    // 增加默认描述
    val description: String = "This pre-loved item is in great condition. Choosing secondhand helps reduce waste and supports SDG 12!"
)

// 2.包装所有需要跨屏幕共享的数据
data class SecondLifeUiState(
    val sellCount: Int = 0,
    val productList: List<Product> = listOf(
        Product("Vintage Camera", "RM 120", R.drawable.camera1),
        Product("Used Textbook", "RM 25", R.drawable.textbook1),
        Product("Retro Watch", "RM 85", R.drawable.watch1),
        Product("Coffee Mug", "RM 15", R.drawable.mug1),
        Product("Desk Lamp", "RM 40", R.drawable.lamp1),
        Product("Gaming Mouse", "RM 90", R.drawable.mouse1)
    )
)

// 3.管理数据和业务逻辑
class SecondLifeViewModel : ViewModel() {

    var uiState by mutableStateOf(SecondLifeUiState())
        private set

    // 业务逻辑：添加新商品
    fun addProduct(name: String, price: String) {
        val newProduct = Product(
            title = name,
            price = "RM $price",
            imageRes = R.drawable.nophoto,
            // 动态生成描述，增加项目深度
            description = "A quality $name for a sustainable lifestyle. Every purchase contributes to responsible consumption in Malaysia."
        )

        uiState = uiState.copy(
            sellCount = uiState.sellCount + 1,
            productList = uiState.productList + newProduct
        )
    }

    fun getProduct(index: Int): Product? {
        return uiState.productList.getOrNull(index)
    }
}