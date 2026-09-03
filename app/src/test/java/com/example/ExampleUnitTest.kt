package com.example

import com.example.model.KirayaCalculator
import com.example.model.KirayaSettings
import com.example.model.VehicleCategory
import com.example.model.ZoneFareItem
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testMinimumFareEnforcement() {
    val settings = KirayaSettings(
      baseFare = 20,
      perKmCharge = 8,
      nightCharge = 10,
      minimumFare = 30,
      isNightModeSimulation = false
    )
    // For 0.5 km: base is 20, but minimum is 30. During day, fare should be 30
    val result = KirayaCalculator.calculateBikeFare(
      pickupTitle = "Gomti Nagar",
      dropTitle = "Near Patrakarpuram",
      distanceKm = 0.5f,
      settings = settings,
      overrideIsNight = false
    )
    assertEquals(30, result.finalFare)
    assertEquals("30 - 35", result.estimateRangeText)
  }

  @Test
  fun testDistanceCalculationWithNightCharge() {
    val daySettings = KirayaSettings(
      baseFare = 20,
      perKmCharge = 8,
      nightCharge = 10,
      minimumFare = 30,
      isNightModeSimulation = false
    )
    // 5 km on bike:
    // Distance after base (1km) = 4km * 8 = 32
    // Base = 20
    // Total day = 52
    val dayResult = KirayaCalculator.calculateBikeFare(
      pickupTitle = "Alambagh",
      dropTitle = "Telibagh",
      distanceKm = 5f,
      settings = daySettings,
      overrideIsNight = false
    )
    assertEquals(52, dayResult.finalFare)
    assertEquals("47 - 57", dayResult.estimateRangeText)

    // With night charge: 52 + 10 = 62
    val nightResult = KirayaCalculator.calculateBikeFare(
      pickupTitle = "Alambagh",
      dropTitle = "Telibagh",
      distanceKm = 5f,
      settings = daySettings,
      overrideIsNight = true
    )
    assertEquals(62, nightResult.finalFare)
    assertTrue(nightResult.isNightChargeApplied)
  }

  @Test
  fun testZoneFareMatching() {
    val settings = KirayaSettings(
      baseFare = 20,
      perKmCharge = 8,
      nightCharge = 10,
      minimumFare = 30,
      isNightModeSimulation = false,
      zoneFares = listOf(
        ZoneFareItem(
          id = "zone_1",
          fromHindi = "Hazratganj",
          toHindi = "Charbagh",
          fare = 70
        ),
        ZoneFareItem(
          id = "zone_2",
          fromHindi = "Alambagh",
          toHindi = "Airport",
          fare = 150
        )
      )
    )

    // Hazratganj to Charbagh should match fixed rate of 70 (day)
    val matched = KirayaCalculator.calculateBikeFare(
      pickupTitle = "Hazratganj Main Chauraha, Lucknow",
      dropTitle = "Charbagh Railway Station, Lucknow",
      distanceKm = 4.0f,
      settings = settings,
      overrideIsNight = false
    )
    assertTrue(matched.isZoneFareMatched)
    assertEquals(70, matched.finalFare)
    assertEquals("65 - 75", matched.estimateRangeText)

    // Alambagh to Airport should match fixed rate of 150 (day)
    val airportMatched = KirayaCalculator.calculateBikeFare(
      pickupTitle = "Alambagh Bus Stand",
      dropTitle = "Chaudhary Charan Singh Airport",
      distanceKm = 11.0f,
      settings = settings,
      overrideIsNight = false
    )
    assertTrue(airportMatched.isZoneFareMatched)
    assertEquals(150, airportMatched.finalFare)
  }

  @Test
  fun testVehicleOptionsGeneration() {
    val settings = KirayaSettings(
      baseFare = 20,
      perKmCharge = 8,
      nightCharge = 10,
      minimumFare = 30,
      isNightModeSimulation = false
    )

    val (vehicles, result) = KirayaCalculator.generateVehiclesForRoute(
      pickupTitle = "Chowk",
      dropTitle = "Aminabad",
      distanceKm = 3.0f,
      settings = settings
    )

    assertEquals(3, vehicles.size)
    val bike = vehicles.first { it.category == VehicleCategory.BIKE }
    val auto = vehicles.first { it.category == VehicleCategory.AUTO }
    val cab = vehicles.first { it.category == VehicleCategory.CAB }

    assertTrue(bike.fare <= auto.fare)
    assertTrue(auto.fare <= cab.fare)
    assertEquals(result.finalFare, bike.fare)
  }
}
