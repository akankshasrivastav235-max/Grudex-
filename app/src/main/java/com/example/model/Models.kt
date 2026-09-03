package com.example.model

enum class VehicleCategory {
    BIKE,
    AUTO,
    CAB
}

data class VehicleOption(
    val id: String,
    val category: VehicleCategory,
    val nameHindi: String,
    val subtitleHindi: String,
    val fare: Int,
    val originalFare: Int,
    val etaMinutes: Int,
    val capacityHindi: String,
    val discountTagHindi: String? = null
)

enum class PaymentType(val titleHindi: String, val subtitleHindi: String) {
    CASH("Nagad (Cash)", "Driver ko seedhe nagad dein"),
    UPI("UPI (GPay / PhonePe / Paytm)", "Instant digital bhugtan"),
    WALLET("Grudex Batua (Wallet)", "Shesh Rashi: ₹240")
}

data class DriverInfo(
    val name: String,
    val rating: Float,
    val totalRides: Int,
    val vehicleModel: String,
    val vehicleNumber: String,
    val phone: String,
    val startOtp: String,
    val etaMinutes: Int,
    val currentDistanceKm: Float
)

data class LocationItem(
    val id: String,
    val titleHindi: String,
    val subtitleHindi: String,
    val distanceKm: Float,
    val iconType: String = "place"
)

enum class RideStatus {
    IDLE,
    SELECTING_VEHICLE,
    SEARCHING_DRIVER,
    DRIVER_ON_WAY,
    RIDE_IN_PROGRESS,
    PAYMENT_DUE,
    COMPLETED
}

data class ActiveRide(
    val pickup: LocationItem,
    val drop: LocationItem,
    val vehicle: VehicleOption,
    val paymentType: PaymentType,
    val driver: DriverInfo,
    val status: RideStatus = RideStatus.IDLE,
    val progress: Float = 0f, // 0.0 to 1.0
    val isFamilyTrackingActive: Boolean = true
)

data class RideRecord(
    val id: String,
    val dateHindi: String,
    val timeHindi: String,
    val vehicleNameHindi: String,
    val vehicleCategory: VehicleCategory,
    val pickupHindi: String,
    val dropHindi: String,
    val fare: Int,
    val paymentMethodHindi: String,
    val driverName: String,
    val ratingGiven: Int = 5,
    val statusHindi: String = "Safal (Completed)"
)

data class FamilyMember(
    val id: String,
    val nameHindi: String,
    val relationHindi: String,
    val phone: String,
    val isLiveSharingEnabled: Boolean = true,
    val lastLocationTextHindi: String = "Ghar par",
    val distanceTextHindi: String = "0 km door",
    val batteryPercent: Int = 85
)

data class DriverDutyState(
    val isDutyOn: Boolean = false,
    val earningsToday: Int = 740,
    val ridesToday: Int = 7,
    val rating: Float = 4.88f,
    val dutyHoursTextHindi: String = "4 ghante 15 min"
)

data class DriverRideRequest(
    val id: String,
    val passengerName: String,
    val pickupHindi: String,
    val dropHindi: String,
    val distanceKm: Float,
    val estimatedFare: Int,
    val passengerRating: Float,
    val expectedOtp: String = "4829"
)

enum class ShopCategory(
    val id: String,
    val titleHindi: String,
    val emoji: String,
    val descriptionHindi: String
) {
    SWEETS("sweets", "Mithai Dukaan", "🍬", "Taaza mithai aur lassi"),
    TEA_SNACKS("tea_snacks", "Chai & Nashta", "☕", "Kullhad chai aur samosa"),
    MEDICAL("medical", "Medical Store", "💊", "Dawaein aur prathamik upchar"),
    GROCERY("grocery", "Kirana Store", "🛒", "Ration aur daily zarooratein")
}

data class NearbyShop(
    val id: String,
    val nameHindi: String,
    val category: ShopCategory,
    val addressHindi: String,
    val distanceMeters: Int,
    val distanceTextHindi: String,
    val rating: Float,
    val reviewCount: Int,
    val specialtyHindi: String,
    val isOpenNow: Boolean = true,
    val mapCoordX: Float, // Relative X on map (0.0 to 1.0)
    val mapCoordY: Float  // Relative Y on map (0.0 to 1.0)
)

enum class DriverTripPhase {
    NONE,
    INCOMING_REQUEST,
    NAVIGATING_TO_PICKUP,
    ARRIVED_ENTER_OTP,
    TRIP_IN_PROGRESS,
    COLLECT_PAYMENT
}
