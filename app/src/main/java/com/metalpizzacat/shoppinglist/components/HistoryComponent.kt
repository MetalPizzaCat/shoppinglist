package com.metalpizzacat.shoppinglist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.ProductDatabase
import com.metalpizzacat.shoppinglist.view.ShoppingViewModel
import com.metalpizzacat.shoppinglist.view.ShoppingViewModelFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@Composable
fun PreviousPurchasesList(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModelFactory(
            ProductDatabase.getDatabase(LocalContext.current).getProductDao()
        )
    ),
) {
    var productToPickDateFor by remember { mutableStateOf<Product?>(null) }

    val products by viewModel.allPreviousPurchases.collectAsState(initial = emptyMap())
    LazyColumn(
        modifier
    ) {
        items(products.keys.toList()) { date ->
            if (date.year != null && date.month != null && date.day != null) {
                ElevatedCard(Modifier.padding(5.dp)) {
                    Column {
                        Text(
                            SimpleDateFormat(
                                "EEE dd-MM-yyyy",
                                Locale.getDefault()
                            ).format(
                                Date.from(
                                    LocalDate.of(date.year, date.month + 1, date.day + 1)
                                        .atStartOfDay(
                                            ZoneId.systemDefault()
                                        ).toInstant()
                                )
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        products[date]?.forEachIndexed { i, product ->
                            Column(
                                modifier = Modifier.background(
                                    if (i % 2 == 0) {
                                        MaterialTheme.colorScheme.inversePrimary
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    }
                                )
                            ) {
                                ProductDisplay(
                                    product,
                                    onProductUpdated = { viewModel.updateProduct(it) },
                                    onMovedToShoppingCart = {},
                                    onDeleted = { },
                                    purchaseIcon = {},
                                    cancelPurchaseIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete item from list"
                                        )
                                    }
                                )
                                Text(
                                    SimpleDateFormat(
                                        "EEE dd-MM-yyyy",
                                        Locale.getDefault()
                                    ).format(
                                        Date.from(
                                            LocalDate.of(date.year, date.month + 1, date.day + 1)
                                                .atStartOfDay(
                                                    ZoneId.systemDefault()
                                                ).toInstant()
                                        )
                                    )
                                )

                            }
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(visible = productToPickDateFor != null) {
        DatePickerModal(onDismiss = { productToPickDateFor = null }, onDateSelected = {
            it?.let {
                val date = LocalDate.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                productToPickDateFor?.let { p ->
                    viewModel.updateProduct(
                        p.copy(
                            day = date.dayOfMonth,
                            month = date.monthValue,
                            year = date.year
                        )
                    )
                }
                productToPickDateFor = null
            }
        })
    }
}

