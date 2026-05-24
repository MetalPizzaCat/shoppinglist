package com.metalpizzacat.shoppinglist

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.metalpizzacat.shoppinglist.data.PurchaseDay

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.time.LocalDate

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.metalpizzacat.shoppinglist", appContext.packageName)
    }

    @Test
    fun purchaseDayToLocalDate() {
        assertEquals(PurchaseDay(28, 2, 2016).localDate, LocalDate.of(2016, 2, 28))
    }
}