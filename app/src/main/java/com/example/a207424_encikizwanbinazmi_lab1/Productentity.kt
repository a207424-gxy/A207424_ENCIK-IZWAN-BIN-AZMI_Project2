package com.example.a207424_encikizwanbinazmi_lab1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val price: String,
    val description: String,
    val imageRes: Int
)

// Convert Entity → UI Product
fun ProductEntity.toProduct(): Product = Product(
    title = this.title,
    price = this.price,
    imageRes = this.imageRes,
    description = this.description
)

// Convert UI Product → Entity
fun Product.toEntity(): ProductEntity = ProductEntity(
    title = this.title,
    price = this.price,
    imageRes = this.imageRes,
    description = this.description
)