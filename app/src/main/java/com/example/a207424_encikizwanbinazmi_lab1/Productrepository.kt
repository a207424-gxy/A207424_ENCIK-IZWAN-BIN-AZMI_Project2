package com.example.a207424_encikizwanbinazmi_lab1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(private val productDao: ProductDao) {

    // All products from Room, converted to UI model
    val allProducts: Flow<List<Product>> = productDao.getAll().map { entities ->
        entities.map { it.toProduct() }
    }

    // Count of user-added products in Room
    val productCount: Flow<Int> = productDao.getCount()

    suspend fun insert(product: Product) {
        productDao.insert(product.toEntity())
    }

    suspend fun delete(product: Product) {
        productDao.delete(product.toEntity())
    }
}