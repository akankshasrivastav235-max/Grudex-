package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.model.LocationItem
import com.example.ui.components.SearchDestinationModal
import com.example.ui.screens.HomeScreen
import com.example.viewmodel.GrudexUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Grudex", appName)
  }

  @Test
  fun `search input accepts text smoothly`() {
    val testLocations = listOf(
      LocationItem("1", "Charbagh Railway Station", "Lucknow", 3.2f)
    )

    composeTestRule.setContent {
      SearchDestinationModal(
        popularLocations = testLocations,
        selectedCategory = null,
        nearbyShops = emptyList(),
        onSelectLocation = {},
        onCategoryClick = {},
        onBookRideToShop = {},
        onDismiss = {}
      )
    }

    composeTestRule.onNodeWithTag("search_query_input").performTextInput("Charbagh")
  }

  @Test
  fun `home drop location search input accepts text and filters`() {
    composeTestRule.setContent {
      HomeScreen(
        uiState = GrudexUiState(),
        onDestinationSelected = {},
        onVehicleCategorySelected = {},
        onPaymentTypeSelected = {},
        onBookRideClick = {},
        onCancelRideClick = {},
        onTriggerSos = {},
        onOpenSearch = {},
        onCloseSearch = {}
      )
    }

    composeTestRule.onNodeWithTag("search_destination_box").performTextInput("Charbagh")
  }
}
