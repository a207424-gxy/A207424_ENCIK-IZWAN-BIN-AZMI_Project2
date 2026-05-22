package com.example.a207424_encikizwanbinazmi_lab1

import android.app.Application

class SecondLifeApplication : Application() {

    val database: SecondLifeDatabase by lazy {
        SecondLifeDatabase.getDatabase(this)
    }

    val repository: ProductRepository by lazy {
        ProductRepository(database.productDao())
    }
}