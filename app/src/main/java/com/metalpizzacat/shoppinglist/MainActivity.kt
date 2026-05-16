package com.metalpizzacat.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.ProductDatabase
import com.metalpizzacat.shoppinglist.ui.theme.ShoppinglistTheme
import com.metalpizzacat.shoppinglist.view.ShoppingViewModel
import com.metalpizzacat.shoppinglist.view.ShoppingViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metalpizzacat.shoppinglist.data.ProductState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppinglistTheme {
                ShoppingListApplication()
            }
        }
    }
}

@Composable
fun ShoppingListApplication(
    viewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModelFactory(
            ProductDatabase.getDatabase(LocalContext.current).getProductDao()
        )
    )
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            ShoppingList()
            ShoppingCart(emptyList())
        }
    }
}

@Composable
fun ShoppingListItemEdit(
    productName: String,
    productAmount: Float,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (Float) -> Unit,

    onAccepted: () -> Unit,
    onCanceled: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        OutlinedTextField(
            productName,
            onValueChange = onNameChanged,
            label = { Text("Name") }, modifier = Modifier.weight(0.6f)
        )
        OutlinedTextField(
            productAmount.toString(),
            onValueChange = {
                it.toFloatOrNull()?.let { amount ->
                    onAmountChanged(amount)
                }
            },
            label = { Text("Amount") },
            modifier = Modifier.weight(0.3f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal // Opens numeric keyboard with decimal point
            ),
        )
        IconButton(onClick = {
            onAccepted()

        }, modifier = Modifier.weight(0.1f), enabled = productName.isNotBlank()) {
            Icon(
                Icons.Default.Done,
                contentDescription = "Finish editing"
            )
        }
        onCanceled?.let {
            IconButton(onClick = {
                onCanceled()
            }, modifier = Modifier.weight(0.1f), enabled = productName.isNotBlank()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel editing"
                )
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    product: Product,
    onProductUpdated: (Product) -> Unit,
    onMovedToShoppingCart: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEdited by remember { mutableStateOf(false) }
    var productName by remember { mutableStateOf(product.name) }
    var productAmount by remember { mutableFloatStateOf(product.amount) }
    Row(
        modifier
            .fillMaxWidth()
            .padding(5.dp)
            .combinedClickable(onLongClick = { isEdited = !isEdited }, onClick = {})
    ) {
        AnimatedContent(isEdited) { isBeingEdited ->
            Row {
                if (isBeingEdited) {
                    ShoppingListItemEdit(
                        productName, productAmount, onNameChanged = {
                            productName = it
                        },
                        onAmountChanged = { productAmount = it },
                        onAccepted = {
                            onProductUpdated(
                                product.copy(
                                    name = productName,
                                    amount = productAmount
                                )
                            )
                            isEdited = false
                        },
                        onCanceled = {
                            productName = product.name
                            productAmount = product.amount
                            isEdited = false
                        })
                } else {
                    Text(productName, Modifier.weight(0.6f))
                    Text(productAmount.toString(), Modifier.weight(0.1f))
                    IconButton(onClick = { onMovedToShoppingCart() }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Mark as being in cart"
                        )
                    }
                    IconButton(onClick = { onDeleted() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete item from list"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItem(product: Product, modifier: Modifier = Modifier) {

}

@Composable
fun PurchasedItem(product: Product) {

}

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
    ElevatedCard(modifier.fillMaxWidth()) {
        Row {
            Icon(Icons.Default.Create, contentDescription = "Shopping list icon")
            Text("Shopping list", fontSize = 14.sp)
        }
        ShoppingListItemEdit(
            newProductName,
            newProductAmount,
            onNameChanged = { newProductName = it },
            onAmountChanged = { newProductAmount = it },
            onAccepted = {
                viewModel.addNewProductToBuy(newProductName, newProductAmount)
                newProductName = ""
                newProductAmount = 1f
            },
            onCanceled = null,
            modifier = Modifier.padding(3.dp)
        )
        LazyColumn() {
            itemsIndexed(products, key = { i, p -> p.id }) { i, product ->
                ShoppingListItem(
                    product,
                    onProductUpdated = { viewModel.updateProduct(it) },
                    onMovedToShoppingCart = { viewModel.updateProduct(product.copy(state = ProductState.IN_CART)) },
                    onDeleted = { viewModel.deleteProduct(product) },
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
fun ShoppingCart(products: List<Product>, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
    ) {
        Row {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping cart icon")
            Text("Card", fontSize = 14.sp)
        }
        LazyColumn() {
            itemsIndexed(products, key = { i, p -> p.id }) { i, product ->
                CartItem(
                    product, Modifier.background(
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

