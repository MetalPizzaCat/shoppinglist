package com.metalpizzacat.shoppinglist.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.metalpizzacat.shoppinglist.data.Product
import kotlin.text.toFloatOrNull


@Composable
fun ProductEdit(
    productName: String,
    productAmount: Float,
    price: Float?,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (Float) -> Unit,
    onPriceChanged: ((Float) -> Unit)?,
    //onDateChanged: ((Int, Int, Int) -> Unit)?,

    onAccepted: () -> Unit,
    onCanceled: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row {
            OutlinedTextField(
                productName,
                onValueChange = onNameChanged,
                label = { Text("Name") }, modifier = Modifier.weight(0.5f)
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

            onPriceChanged?.let {
                OutlinedTextField(
                    (price ?: 0f).toString(),
                    onValueChange = {
                        it.toFloatOrNull()?.let { amount ->
                            onPriceChanged(amount)
                        }
                    },
                    label = { Text("Price") },
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal // Opens numeric keyboard with decimal point
                    ),
                )
            }
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
        ElevatedCard {
        }
    }
}

@Composable
fun ProductDisplay(
    product: Product,
    onProductUpdated: (Product) -> Unit,
    onMovedToShoppingCart: () -> Unit,
    onDeleted: () -> Unit,
    purchaseIcon: @Composable (() -> Unit),
    cancelPurchaseIcon: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
) {
    var isEdited by remember { mutableStateOf(false) }
    var productName by remember { mutableStateOf(product.name) }
    var productAmount by remember { mutableFloatStateOf(product.amount) }
    var productPrice by remember { mutableStateOf(product.price) }
    Row(
        modifier
            .fillMaxWidth()
            .padding(5.dp)
            .combinedClickable(onLongClick = { isEdited = !isEdited }, onClick = {})
    ) {
        AnimatedContent(isEdited) { isBeingEdited ->
            Row {
                if (isBeingEdited) {
                    ProductEdit(
                        productName,
                        productAmount,
                        productPrice,
                        onNameChanged = {
                            productName = it
                        },
                        onAmountChanged = { productAmount = it },
                        onPriceChanged = { productPrice = it },
                        onAccepted = {
                            if (productPrice != null && productPrice == 0f) {
                                productPrice = null
                            }
                            onProductUpdated(
                                product.copy(
                                    name = productName,
                                    amount = productAmount,
                                    price = productPrice
                                )
                            )
                            isEdited = false
                        },
                        onCanceled = {
                            productName = product.name
                            productAmount = product.amount
                            productPrice = product.price
                            isEdited = false
                        })
                } else {
                    Text(productName, Modifier.weight(0.5f))
                    Text(productAmount.toString(), Modifier.weight(0.1f))
                    productPrice?.let {
                        Text(it.toString(), Modifier.weight(0.1f))
                    }
                    IconButton(onClick = { onMovedToShoppingCart() }) {
                        purchaseIcon()
                    }
                    IconButton(onClick = { onDeleted() }) {
                        cancelPurchaseIcon()

                    }
                }
            }
        }
    }
}