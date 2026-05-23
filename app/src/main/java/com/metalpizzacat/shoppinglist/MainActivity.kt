package com.metalpizzacat.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.metalpizzacat.shoppinglist.components.PreviousPurchasesList
import com.metalpizzacat.shoppinglist.components.ShoppingCart
import com.metalpizzacat.shoppinglist.components.ShoppingList
import com.metalpizzacat.shoppinglist.ui.theme.ShoppinglistTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isViewingHistory by remember { mutableStateOf(false) }
            ShoppinglistTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        PrimaryTabRow(
                            if (isViewingHistory) {
                                1
                            } else {
                                0
                            }
                        ) {
                            Tab(
                                selected = !isViewingHistory,
                                onClick = { isViewingHistory = false },
                                text = {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = "Shopping list"
                                    )
                                })
                            Tab(
                                selected = isViewingHistory,
                                onClick = { isViewingHistory = true },
                                text = {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "List of purchases"
                                    )
                                })
                        }
                        AnimatedContent(isViewingHistory) {
                            if (!it) {
                                Column(Modifier) {
                                    ShoppingList()
                                    ShoppingCart()
                                }
                            } else {
                                PreviousPurchasesList()
                            }
                        }
                    }
                }
            }
        }
    }
}



