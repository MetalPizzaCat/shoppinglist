package com.metalpizzacat.shoppinglist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metalpizzacat.shoppinglist.R
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.ProductDatabase
import com.metalpizzacat.shoppinglist.data.ProductState
import com.metalpizzacat.shoppinglist.data.PurchaseDay
import com.metalpizzacat.shoppinglist.view.ShoppingViewModel
import com.metalpizzacat.shoppinglist.view.ShoppingViewModelFactory
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingList(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModelFactory(
            ProductDatabase.getDatabase(LocalContext.current).getProductDao()
        )
    ),
) {
    var newProductName by remember { mutableStateOf("") }
    var newProductAmount by remember { mutableFloatStateOf(1f) }
    val products by viewModel.allProductsToPurchase.collectAsState(initial = emptyList())

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
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.trash_icon)
                )
            },
            onDismissRequest = { currentlyDeletedItem = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(currentlyDeletedItem!!)
                    currentlyDeletedItem = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = {
                    currentlyDeletedItem = null
                }) { Text(stringResource(R.string.no)) }
            })
    }
    ElevatedCard(modifier.fillMaxWidth()) {
        Row {
            Icon(
                Icons.Default.Create,
                contentDescription = stringResource(R.string.shopping_list_icon)
            )
            Text("Shopping list", fontSize = 14.sp)
        }
        Row {
            OutlinedTextField(
                newProductName,
                onValueChange = { newProductName = it },
                label = { Text(stringResource(R.string.name)) }, modifier = Modifier.weight(0.5f)
            )
            OutlinedTextField(
                newProductAmount.toString(),
                onValueChange = {
                    it.toFloatOrNull()?.let { a ->
                        newProductAmount = a
                    }
                },
                label = { Text(stringResource(R.string.amount)) },
                modifier = Modifier.weight(0.3f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal // Opens numeric keyboard with decimal point
                ),
            )
            IconButton(onClick = {
                viewModel.addNewProductToBuy(newProductName, newProductAmount)
                newProductName = ""
                newProductAmount = 1f
            }, modifier = Modifier.weight(0.1f), enabled = newProductName.isNotBlank()) {
                Icon(
                    Icons.Default.Done,
                    contentDescription = stringResource(R.string.finish_editing)
                )
            }
        }
        LazyColumn() {
            itemsIndexed(products, key = { _, p -> p.id }) { i, product ->
                ProductDisplay(
                    product,
                    onProductUpdated = { viewModel.updateProduct(it) },
                    onMovedToShoppingCart = {
                        viewModel.updateProduct(
                            product.copy(
                                state = ProductState.IN_CART,
                                day = LocalDateTime.now().dayOfMonth,
                                month = LocalDateTime.now().monthValue,
                                year = LocalDateTime.now().year
                            )
                        )
                    },
                    onDeleted = { currentlyDeletedItem = product },
                    purchaseIcon = {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.mark_as_being_in_cart)
                        )
                    },
                    cancelPurchaseIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_item_from_list)
                        )
                    },
                    modifier = Modifier.background(
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

@Composable
fun ShoppingCart(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModelFactory(
            ProductDatabase.getDatabase(LocalContext.current).getProductDao()
        )
    ),
) {
    val products by viewModel.allProductsInCart.collectAsState(initial = emptyList())
    ElevatedCard(
        modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
    ) {
        Row {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = stringResource(R.string.shopping_cart_icon)
            )
            Text(stringResource(R.string.cart), fontSize = 14.sp)
        }
        LazyColumn() {
            itemsIndexed(products, key = { i, p -> p.id }) { i, product ->
                ProductDisplay(
                    product,
                    onProductUpdated = { viewModel.updateProduct(it) },
                    onMovedToShoppingCart = {
                        viewModel.updateProduct(
                            product.copy(
                                state = ProductState.BOUGHT
                            )
                        )
                    },
                    onDeleted = {
                        viewModel.updateProduct(product.copy(state = ProductState.TODO))
                    },
                    purchaseIcon = {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = stringResource(R.string.finalize_purchase)
                        )
                    },
                    cancelPurchaseIcon = {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.return_item_to_shopping_cart)
                        )
                    },

                    modifier = Modifier.background(
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
