package com.metalpizzacat.shoppinglist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metalpizzacat.shoppinglist.R
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.ProductDatabase
import com.metalpizzacat.shoppinglist.data.ProductState
import com.metalpizzacat.shoppinglist.data.PurchaseDay
import com.metalpizzacat.shoppinglist.view.ShoppingViewModel
import com.metalpizzacat.shoppinglist.view.ShoppingViewModelFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale


@Composable
fun PreviousPurchasesList(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModelFactory(
            ProductDatabase.getDatabase(LocalContext.current).getProductDao()
        )
    ),
) {

    val products by viewModel.allPreviousPurchases.collectAsState(initial = emptyList())
    var currentlyDeletedItem by remember { mutableStateOf<Product?>(null) }
    AnimatedVisibility(currentlyDeletedItem != null) {
        AlertDialog(
            title = {
                Text(
                    stringResource(
                        R.string.do_you_want_to_remove,
                        currentlyDeletedItem?.name ?: "OOPS!"
                    )
                )
            },
            icon = { Icon(Icons.Default.Delete, contentDescription = "Trash icon") },
            onDismissRequest = { currentlyDeletedItem = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(currentlyDeletedItem!!)
                    currentlyDeletedItem = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { currentlyDeletedItem = null }) { Text("No") }
            })
    }
    LazyColumn(
        modifier
    ) {
        val products = products.groupBy { PurchaseDay(it.day, it.month, it.year) }

        items(products.keys.toList(), key = { it.toString() }) { date ->
            if (date.year != null && date.month != null && date.day != null) {
                ElevatedCard(Modifier.padding(5.dp)) {
                    Column {
                        Row(Modifier.padding(5.dp)) {
                            Text(
                                date.toFormatedString() ?: "MISSING DATE SOMEHOW",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.3f)
                            )
                            Text("Total: ${products[date]?.sumOf { it.price?.toDouble() ?: 0.0 } ?: 0.0}",
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(0.7f))
                        }
                        products[date]?.forEachIndexed { i, product ->

                            ProductDisplay(
                                product,
                                onProductUpdated = { viewModel.updateProduct(it) },
                                onMovedToShoppingCart = { viewModel.updateProduct(product.copy(state = ProductState.IN_CART)) },
                                onDeleted = { currentlyDeletedItem = product },
                                purchaseIcon = {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = "Move back to shopping cart"
                                    )
                                },
                                cancelPurchaseIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete item from list"
                                    )
                                }, modifier = Modifier.background(
                                    if (i % 2 == 0) {
                                        MaterialTheme.colorScheme.inversePrimary
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }


}

