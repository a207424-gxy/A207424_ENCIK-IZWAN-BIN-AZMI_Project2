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
    val description: String = "Sustainable and affordable pre-loved item."
)

// 2.包装所有需要跨屏幕共享的数据
data class SecondLifeUiState(
    val sellCount: Int = 0,
    val productList: List<Product> = listOf(
        // 初始默认数据
        Product("Vintage Camera", "RM 120", R.drawable.camera1),
        Product("Used Textbook", "RM 25", R.drawable.textbook1),
        Product("Retro Watch", "RM 85", R.drawable.watch1),
        Product("Coffee Mug", "RM 15", R.drawable.mug1),
        Product("Desk Lamp", "RM 40", R.drawable.lamp1),
        Product("Gaming Mouse", "RM 90", R.drawable.mouse1)
    )
)

// 3.管理数据和业务逻辑
// 创建 ViewModel 类，继承 ViewModel，对应4要做的内容
class SecondLifeViewModel : ViewModel() {

    // 当手机旋转时，ViewModel 不会被销毁，因此数据得以保留
    var uiState by mutableStateOf(SecondLifeUiState())
        private set//外部不能修改

    // 业务逻辑：添加新商品
    fun addProduct(name: String, price: String) {
        val newProduct = Product(
            title = name,
            price = "RM $price",
            imageRes = R.drawable.nophoto // 暂时使用固定图片
        )

        // 更新 UI 状态：增加计数并添加列表项
        uiState = uiState.copy(
            sellCount = uiState.sellCount + 1,
            productList = uiState.productList + newProduct
        )
    }
}