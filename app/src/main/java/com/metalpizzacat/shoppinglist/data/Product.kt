package com.metalpizzacat.shoppinglist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Product(
    @PrimaryKey
    val id: Int,
    val name: String,
    val amount: Float = 1f,
    val price: Float? = null,
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = 0,
    val state: ProductState = ProductState.TODO,
) {
    val purchaseDate: LocalDateTime?
        get() = if (day != null && month != null && year != null) {
            LocalDateTime.of(year, month, day, 0, 0)
        } else {
            null
        }

}
