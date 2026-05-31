package com.brianteng.rentacar

import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.brianteng.rentacar.data.CarRepository
import com.brianteng.rentacar.ui.MainActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test

class MainActivityTest {

    @Before
    fun setUp() {
        // Disable animations for stable Espresso tests
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("set", String::class.java, String::class.java)
            method.invoke(null, "debug.hwui.renderer", "skiagl")
        } catch (_: Exception) { }
        // Reset repository state
        CarRepository.resetForTest()
    }

    @After
    fun tearDown() {
        ActivityScenario.launch(MainActivity::class.java).close()
    }

    @Test
    fun testInitialBalanceDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.tvBalance))
            .check(matches(withText("Credit: 500")))
    }

    @Test
    fun testFirstCarDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.tvNameModel))
            .check(matches(withText("Toyota Camry")))
    }

    @Test
    fun testNextButtonChangesCar() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnNext)).perform(click())
        onView(withId(R.id.tvNameModel)).check(matches(withText("Honda Civic")))
    }

    @Test
    fun testPreviousButtonChangesCar() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnNext)).perform(click())
        onView(withId(R.id.btnPrevious)).perform(click())
        onView(withId(R.id.tvNameModel)).check(matches(withText("Toyota Camry")))
    }

    @Test
    fun testRentButtonOpensDetailScreen() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnRent)).perform(click())
        onView(withId(R.id.sliderDays)).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelBookingReturnsToMain() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnRent)).perform(click())
        onView(withId(R.id.btnCancel)).perform(click())
        onView(withId(R.id.btnRent)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchViewDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)
        // Just verify the search view is present
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
    }

    @Test
    fun testSortMenuOpens() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnSort)).perform(click())
        onView(withText("Sort by Rating")).perform(click())
        onView(withId(R.id.tvNameModel)).check(matches(withText("Audi A4")))
    }

    @Test
    fun testFavoriteToggling() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.favoritesContainer)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btnFavorite)).perform(click())
        onView(withId(R.id.favoritesContainer)).check(matches(isDisplayed()))
    }

    @Test
    fun testRentExceedsMaxLimitShowsError() {
        ActivityScenario.launch(MainActivity::class.java)
        // Navigate to BMW X5 (index 3)
        onView(withId(R.id.btnNext)).perform(click(), click(), click())
        onView(withId(R.id.btnRent)).perform(click())
        // Swipe slider to 4 days (total 440 > 400)
        onView(withId(R.id.sliderDays)).perform(swipeRight(), swipeRight(), swipeRight())
        onView(withId(R.id.btnSave)).perform(click())
        // Must stay on detail screen
        onView(withId(R.id.sliderDays)).check(matches(isDisplayed()))
        onView(withId(R.id.btnCancel)).perform(click())
    }
}