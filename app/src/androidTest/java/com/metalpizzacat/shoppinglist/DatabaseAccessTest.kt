package com.metalpizzacat.shoppinglist;

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.metalpizzacat.shoppinglist.data.Product
import com.metalpizzacat.shoppinglist.data.ProductDao
import com.metalpizzacat.shoppinglist.data.ProductDatabase
import com.metalpizzacat.shoppinglist.data.ProductState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class DatabaseAccessTest {
    private lateinit var productDao: ProductDao
    private lateinit var db: ProductDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ProductDatabase::class.java
        ).build()
        productDao = db.getProductDao()

        suspend {
            for (p in listOf("Apple", "Orange", "Pear", "Kiwi")) {
                productDao.insert(
                    Product(
                        name = p,
                        day = 28,
                        month = 2,
                        year = 2016,
                        state = ProductState.BOUGHT
                    )
                )
            }
            for (p in listOf("Potatoes", "Tomato", "Pasta", "Malina")) {
                productDao.insert(
                    Product(
                        name = p,
                        day = 1,
                        month = 2,
                        year = 2016,
                        state = ProductState.BOUGHT
                    )
                )
            }

            for (p in listOf("Evil", "Good")) {
                productDao.insert(Product(name = p))
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testProperCount() = runTest {
        assertEquals(6, productDao.getAllForState(ProductState.BOUGHT).toList().size)
    }


//        val user: User = TestUtil.createUser(3).apply {
//            setName("george")
//        }
//        userDao.insert(user)
//        val byName = userDao.findUsersByName("george")
//        assertThat(byName.get(0), equalTo(user))
}
