package com.metalpizzacat.shoppinglist.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.metalpizzacat.shoppinglist.data.ProductDao

class ShoppingViewModelFactory(private val dao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}