package com.example.model

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

data class ZoneFareItem(
    val id: String,
    val fromHindi: String,
    val toHindi: String,
    val fare: Int
)

data class KirayaSettings(
    val baseFare: Int = 20,           // Base Kiraya: Rs. 20 (1 km tak)
    val perKmCharge: Int = 8,          // Per KM Charge: Rs. 8 / km
    val nightCharge: Int = 10,         // Raat ka Charge (9pm-6am): + Rs. 10 extra
    val minimumFare: Int = 30,         // Minimum Kiraya: Rs. 30
    val isNightModeSimulation: Boolean = false, // Preview / Force night charge in tests
    val zoneFares: List<ZoneFareItem> = defaultZones()
) {
    companion object {
        fun defaultZones(): List<ZoneFareItem> = listOf(
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
            ),
            ZoneFareItem(
                id = "zone_3",
                fromHindi = "Gomti Nagar",
                toHindi = "Hazratganj",
                fare = 90
            )
        )
    }
}

data class FareCalculationResult(
    val finalFare: Int,
    val minEstimate: Int,
    val maxEstimate: Int,
    val estimateRangeText: String, // e.g. "85 - 95"
    val isZoneFareMatched: Boolean,
    val matchedZoneName: String? = null,
    val isNightChargeApplied: Boolean,
    val breakdownTextHindi: String
)

object KirayaCalculator {

    fun isNightTime(forceSimulation: Boolean = false, overrideIsNight: Boolean? = null): Boolean {
        if (overrideIsNight != null) return overrideIsNight
        if (forceSimulation) return true
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        // Raat 9:00 PM (21) se subah 6:00 AM (6)
        return hour >= 21 || hour < 6
    }

    fun calculateBikeFare(
        pickupTitle: String,
        dropTitle: String,
        distanceKm: Float,
        settings: KirayaSettings,
        overrideIsNight: Boolean? = null
    ): FareCalculationResult {
        // 1. Check if zone-wise fixed fare matches
        val matchedZone = settings.zoneFares.firstOrNull { zone ->
            val pNorm = pickupTitle.lowercase(Locale.ROOT)
            val dNorm = dropTitle.lowercase(Locale.ROOT)
            val fNorm = zone.fromHindi.lowercase(Locale.ROOT)
            val tNorm = zone.toHindi.lowercase(Locale.ROOT)

            val matchDirect = pNorm.contains(fNorm) && dNorm.contains(tNorm)
            val matchReverse = pNorm.contains(tNorm) && dNorm.contains(fNorm)
            matchDirect || matchReverse
        }

        val isNight = isNightTime(settings.isNightModeSimulation, overrideIsNight)
        val nightAddon = if (isNight) settings.nightCharge else 0

        if (matchedZone != null) {
            val total = matchedZone.fare + nightAddon
            val low = maxOf(settings.minimumFare, total - 5)
            val high = total + 5
            val breakdown = if (isNight) {
                "Zone Kiraya: ₹${matchedZone.fare} + Raat Extra: ₹$nightAddon"
            } else {
                "Zone Wise Fixed Kiraya (${matchedZone.fromHindi} ➔ ${matchedZone.toHindi})"
            }

            return FareCalculationResult(
                finalFare = total,
                minEstimate = low,
                maxEstimate = high,
                estimateRangeText = "$low - $high",
                isZoneFareMatched = true,
                matchedZoneName = "${matchedZone.fromHindi} to ${matchedZone.toHindi}",
                isNightChargeApplied = isNight,
                breakdownTextHindi = breakdown
            )
        }

        // 2. Standard Distance-based calculation:
        // Base Kiraya: Rs. 20 (1 km tak)
        val base = settings.baseFare
        val extraKm = (distanceKm - 1.0f).coerceAtLeast(0f)
        val kmCost = kotlin.math.round(extraKm * settings.perKmCharge).toInt()
        val calculatedBeforeMin = base + kmCost + nightAddon
        val finalFare = maxOf(settings.minimumFare, calculatedBeforeMin)

        val low = maxOf(settings.minimumFare, finalFare - 5)
        val high = finalFare + 5

        val breakdown = buildString {
            append("Base: ₹$base (1km tak) + ₹${settings.perKmCharge}/km (${String.format(Locale.US, "%.1f", distanceKm)} km)")
            if (isNight) {
                append(" + Raat Charge: ₹$nightAddon")
            }
            if (finalFare == settings.minimumFare && calculatedBeforeMin < settings.minimumFare) {
                append(" (Minimum Kiraya lagu)")
            }
        }

        return FareCalculationResult(
            finalFare = finalFare,
            minEstimate = low,
            maxEstimate = high,
            estimateRangeText = "$low - $high",
            isZoneFareMatched = false,
            isNightChargeApplied = isNight,
            breakdownTextHindi = breakdown
        )
    }

    fun generateVehiclesForRoute(
        pickupTitle: String,
        dropTitle: String,
        distanceKm: Float,
        settings: KirayaSettings,
        overrideIsNight: Boolean? = null
    ): Pair<List<VehicleOption>, FareCalculationResult> {
        val bikeResult = calculateBikeFare(pickupTitle, dropTitle, distanceKm, settings, overrideIsNight)
        val bikeFare = bikeResult.finalFare
        val autoFare = maxOf(settings.minimumFare + 15, kotlin.math.round(bikeFare * 1.55f).toInt())
        val cabFare = maxOf(settings.minimumFare + 50, kotlin.math.round(bikeFare * 2.5f).toInt())

        val vehicles = listOf(
            VehicleOption(
                id = "v_bike",
                category = VehicleCategory.BIKE,
                nameHindi = "Grudex Bike",
                subtitleHindi = "Sabse Tez aur Kifayati",
                fare = bikeFare,
                originalFare = kotlin.math.round(bikeFare * 1.25f).toInt(),
                etaMinutes = 2,
                capacityHindi = "1 Sawari",
                discountTagHindi = "20% Chhoot"
            ),
            VehicleOption(
                id = "v_auto",
                category = VehicleCategory.AUTO,
                nameHindi = "Grudex Auto",
                subtitleHindi = "Aaramdayak 3 Seater",
                fare = autoFare,
                originalFare = kotlin.math.round(autoFare * 1.15f).toInt(),
                etaMinutes = 4,
                capacityHindi = "3 Sawari"
            ),
            VehicleOption(
                id = "v_cab",
                category = VehicleCategory.CAB,
                nameHindi = "Grudex Mini Cab",
                subtitleHindi = "AC Gaadi me Safar",
                fare = cabFare,
                originalFare = kotlin.math.round(cabFare * 1.18f).toInt(),
                etaMinutes = 6,
                capacityHindi = "4 Sawari"
            )
        )

        return Pair(vehicles, bikeResult)
    }
}

class KirayaSettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("grudex_kiraya_settings_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BASE_FARE = "base_fare"
        private const val KEY_PER_KM = "per_km"
        private const val KEY_NIGHT_CHARGE = "night_charge"
        private const val KEY_MIN_FARE = "min_fare"
        private const val KEY_NIGHT_SIM = "night_sim"
        private const val KEY_ZONES_JSON = "zones_json"
    }

    fun loadSettings(): KirayaSettings {
        val baseFare = prefs.getInt(KEY_BASE_FARE, 20)
        val perKm = prefs.getInt(KEY_PER_KM, 8)
        val nightCharge = prefs.getInt(KEY_NIGHT_CHARGE, 10)
        val minFare = prefs.getInt(KEY_MIN_FARE, 30)
        val nightSim = prefs.getBoolean(KEY_NIGHT_SIM, false)
        val zonesJson = prefs.getString(KEY_ZONES_JSON, null)

        val zones = if (zonesJson.isNullOrBlank()) {
            KirayaSettings.defaultZones()
        } else {
            try {
                val array = JSONArray(zonesJson)
                val list = mutableListOf<ZoneFareItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ZoneFareItem(
                            id = obj.optString("id", "zone_$i"),
                            fromHindi = obj.optString("from", ""),
                            toHindi = obj.optString("to", ""),
                            fare = obj.optInt("fare", 50)
                        )
                    )
                }
                if (list.isEmpty()) KirayaSettings.defaultZones() else list
            } catch (_: Exception) {
                KirayaSettings.defaultZones()
            }
        }

        return KirayaSettings(
            baseFare = baseFare,
            perKmCharge = perKm,
            nightCharge = nightCharge,
            minimumFare = minFare,
            isNightModeSimulation = nightSim,
            zoneFares = zones
        )
    }

    fun saveSettings(settings: KirayaSettings) {
        val jsonArray = JSONArray()
        settings.zoneFares.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("from", item.fromHindi)
            obj.put("to", item.toHindi)
            obj.put("fare", item.fare)
            jsonArray.put(obj)
        }

        prefs.edit()
            .putInt(KEY_BASE_FARE, settings.baseFare)
            .putInt(KEY_PER_KM, settings.perKmCharge)
            .putInt(KEY_NIGHT_CHARGE, settings.nightCharge)
            .putInt(KEY_MIN_FARE, settings.minimumFare)
            .putBoolean(KEY_NIGHT_SIM, settings.isNightModeSimulation)
            .putString(KEY_ZONES_JSON, jsonArray.toString())
            .apply()
    }

    fun addZone(current: KirayaSettings, from: String, to: String, fare: Int): KirayaSettings {
        val newZone = ZoneFareItem(
            id = "zone_${System.currentTimeMillis()}",
            fromHindi = from.trim(),
            toHindi = to.trim(),
            fare = fare
        )
        val updatedZones = current.zoneFares + newZone
        val updated = current.copy(zoneFares = updatedZones)
        saveSettings(updated)
        return updated
    }

    fun removeZone(current: KirayaSettings, zoneId: String): KirayaSettings {
        val updatedZones = current.zoneFares.filterNot { it.id == zoneId }
        val updated = current.copy(zoneFares = updatedZones)
        saveSettings(updated)
        return updated
    }

    fun resetToDefaults(): KirayaSettings {
        val defaults = KirayaSettings()
        saveSettings(defaults)
        return defaults
    }
}
