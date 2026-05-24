package com.metalpizzacat.shoppinglist.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.metalpizzacat.shoppinglist.R
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.PurchaseDay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.text.toFloatOrNull


/**
 * Composable component that represents editor for the product
 * @param productName Name of the product
 * @param productAmount Amount of product
 * @param price Price or null if no price specified
 * @param purchaseDate Date when purchased or null if none specified
 * @param onNameChanged
 * @param onAmountChanged
 * @param onPriceChanged Callback for change in price through editor. If null the editor is hidden
 * @param onDateChanged Callback for change in the purchase date. If null the date picked button is hidden
 *
 * @param onAccepted Callback for user agreeing to changes
 * @param onCanceled Callback for user cancelling changes
 *
 */
@Composable
fun ProductEdit(
    productName: String,
    productAmount: Float,
    price: Float?,
    purchaseDate: PurchaseDay?,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (Float) -> Unit,
    onPriceChanged: ((Float) -> Unit)?,
    onDateChanged: ((PurchaseDay) -> Unit)?,

    onAccepted: () -> Unit,
    onCanceled: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var isPickingDate by remember { mutableStateOf(false) }
    Column(modifier) {
        Row {
            OutlinedTextField(
                productName,
                onValueChange = onNameChanged,
                label = { Text(stringResource(R.string.name)) }, modifier = Modifier.weight(0.5f)
            )
            OutlinedTextField(
                productAmount.toString(),
                onValueChange = {
                    it.toFloatOrNull()?.let { amount ->
                        onAmountChanged(amount)
                    }
                },
                label = { Text(stringResource(R.string.amount)) },
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
                    label = { Text(stringResource(R.string.price)) },
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
                    contentDescription = stringResource(R.string.finish_editing)
                )
            }
            onCanceled?.let {
                IconButton(onClick = {
                    onCanceled()
                }, modifier = Modifier.weight(0.1f), enabled = productName.isNotBlank()) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.cancel_editing)
                    )
                }
            }
        }
        purchaseDate?.toFormatedString()?.let {
            ElevatedCard(
                Modifier
                    .padding(5.dp)
                    .clickable(onClick = { isPickingDate = true })
            ) {
                Text(it)
            }
        }

        AnimatedVisibility(visible = isPickingDate) {
            DatePickerModal(onDismiss = { isPickingDate = false }, onDateSelected = {
                it?.let {
                    val date = LocalDate.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                    onDateChanged?.invoke(PurchaseDay(date.dayOfMonth, date.monthValue, date.year))
                    isPickingDate = false
                }
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
    var productPurchaseDay by remember {
        mutableStateOf(
            PurchaseDay(
                product.day,
                product.month,
                product.year
            )
        )
    }
    Row(
        modifier
            .fillMaxWidth()
            .padding(5.dp)
            .combinedClickable(
                enabled = true,
                onLongClick = { isEdited = !isEdited }, onClick = {}, onLongClickLabel = "Edit"
            )
    ) {
        AnimatedContent(isEdited) { isBeingEdited ->
            Row {
                if (isBeingEdited) {
                    ProductEdit(
                        productName,
                        productAmount,
                        productPrice,

                        productPurchaseDay,
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
                                    price = productPrice,
                                    day = productPurchaseDay.day,
                                    month = productPurchaseDay.month,
                                    year = productPurchaseDay.year
                                )
                            )
                            isEdited = false
                        },
                        onDateChanged = if (product.year == null) {
                            null
                        } else {
                            {
                                productPurchaseDay = it
                            }
                        },
                        onCanceled = {
                            productName = product.name
                            productAmount = product.amount
                            productPrice = product.price
                            isEdited = false
                        })
                } else {
                    Text(productName, Modifier.weight(0.3f))
                    Text(productAmount.toString(), Modifier.weight(0.2f))
                    productPrice?.let {
                        Text(it.toString(), Modifier.weight(0.2f))
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