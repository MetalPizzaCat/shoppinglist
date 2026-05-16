package com.metalpizzacat.shoppinglist.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.ProductDao
import com.metalpizzacat.shoppinglist.data.ProductState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShoppingViewModel(private val dao: ProductDao) : ViewModel() {
    val allProductsToPurchase: Flow<List<Product>> = dao.getAllForState(ProductState.TODO)


    /**
     * Add new product to the to do list section of the shopping list
     * @param name Name of the product to add
     * @param amount Amount of product to buy
     */
    fun addNewProductToBuy(name: String, amount: Float) {
        viewModelScope.launch {
            dao.insert(Product(id = 0, name = name, amount = amount))
        }
    }

    /**
     * Update product value in the data base with new info using id in the  product object
     * @param product Product to update
     */
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            dao.update(product)
        }
    }

    /**
     * Delete given product from the database
     * @param product Product to delete
     */
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            dao.delete(product)
        }
    }
}