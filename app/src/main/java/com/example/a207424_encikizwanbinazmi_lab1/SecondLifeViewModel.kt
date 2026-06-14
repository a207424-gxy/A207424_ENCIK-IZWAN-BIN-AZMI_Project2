package com.example.a207424_encikizwanbinazmi_lab1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Product(
    val title: String,
    val price: String,
    val imageRes: Int,
    val description: String = "This pre-loved item is in great condition. Choosing secondhand helps reduce waste and supports SDG 12!"
)

data class SecondLifeUiState(
    val sellCount: Int = 0,
    val productList: List<Product> = emptyList()
)

class SecondLifeViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val seedProducts = listOf(
        Product("Vintage Camera", "RM 120", R.drawable.camera1),
        Product("Used Textbook", "RM 25", R.drawable.textbook1),
        Product("Retro Watch", "RM 85", R.drawable.watch1),
        Product("Coffee Mug", "RM 15", R.drawable.mug1),
        Product("Desk Lamp", "RM 40", R.drawable.lamp1),
        Product("Gaming Mouse", "RM 90", R.drawable.mouse1)
    )

    val uiState: StateFlow<SecondLifeUiState> =
        combine(repository.allProducts, repository.productCount) { roomProducts, count ->
            SecondLifeUiState(
                sellCount = count,
                productList = seedProducts + roomProducts
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SecondLifeUiState(productList = seedProducts)
        )

    fun addProduct(name: String, price: String) {
        val newProduct = Product(
            title = name,
            price = "RM $price",
            imageRes = R.drawable.nophoto,
            description = "A quality $name for a sustainable lifestyle. Every purchase contributes to responsible consumption in Malaysia."
        )
        viewModelScope.launch {
            repository.insert(newProduct)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }

    fun getProduct(index: Int): Product? {
        return uiState.value.productList.getOrNull(index)
    }
}

class SecondLifeViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecondLifeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SecondLifeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}