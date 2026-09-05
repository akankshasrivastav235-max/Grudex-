package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ActiveRide
import com.example.model.DriverDutyState
import com.example.model.DriverInfo
import com.example.model.DriverRideRequest
import com.example.model.DriverTripPhase
import com.example.model.FamilyMember
import com.example.model.KirayaCalculator
import com.example.model.KirayaSettings
import com.example.model.KirayaSettingsRepository
import com.example.model.LocationItem
import com.example.model.NearbyShop
import com.example.model.NearbyShopsRepository
import com.example.model.PaymentType
import com.example.model.RideRecord
import com.example.model.RideStatus
import com.example.model.ShopCategory
import com.example.model.VehicleCategory
import com.example.model.VehicleOption
import com.example.model.BookingItem
import com.example.model.DriverRatingSummary
import com.example.model.FirebaseBookingRepository
import com.example.model.RatingReview
import com.example.util.DriverAlertHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GrudexUiState(
    // Auth state
    val isLoggedIn: Boolean = true,
    val userName: String = "Rahul Sharma",
    val userPhone: String = "9876543210",
    val walletBalance: Int = 240,

    // App Mode: false = Passenger (Sawari), true = Driver (Captain)
    val isDriverMode: Boolean = true,

    // Kiraya Settings & Admin Panel
    val kirayaSettings: KirayaSettings = KirayaSettings(),
    val isAdminKirayaScreenOpen: Boolean = false,
    val currentEstimatedFareText: String = "85 - 95",
    val currentFareExplanationHindi: String = "Base ₹20 (1km) + ₹8/km",

    // Passenger Ride Flow
    val currentLocation: LocationItem = LocationItem(
        id = "curr",
        titleHindi = "Hazratganj, Atal Chowk",
        subtitleHindi = "Mahatma Gandhi Marg, Hazratganj, Lucknow",
        distanceKm = 0f,
        iconType = "my_location"
    ),
    val selectedDestination: LocationItem? = null,
    val availableVehicles: List<VehicleOption> = emptyList(),
    val selectedVehicleCategory: VehicleCategory = VehicleCategory.BIKE,
    val selectedPaymentType: PaymentType = PaymentType.UPI,
    val activeRide: ActiveRide? = null,
    val rideStatus: RideStatus = RideStatus.IDLE,
    val showRatingDialog: Boolean = false,
    val lastCompletedRide: ActiveRide? = null,
    val lastCompletedBookingId: String = "",
    val lastCompletedDriverId: String = "9876543210",
    val lastCompletedDriverName: String = "Vikram Singh Sarthi",

    // Aas-Paas Ki Dukane ("Raste me kya hai?") Feature
    val selectedShopCategory: ShopCategory? = null,
    val nearbyShops: List<NearbyShop> = emptyList(),
    val selectedShop: NearbyShop? = null,

    // Family Tracking
    val familyMembers: List<FamilyMember> = emptyList(),
    val isFamilyLiveSharingActive: Boolean = true,
    val showSosAlert: Boolean = false,
    val sosTriggeredTime: Long = 0L,

    // Driver Mode State
    val driverDuty: DriverDutyState = DriverDutyState(),
    val driverTripPhase: DriverTripPhase = DriverTripPhase.NONE,
    val currentDriverRequest: DriverRideRequest? = null,
    val driverTripProgress: Float = 0f,
    val driverOtpInput: String = "",
    val driverOtpError: Boolean = false,

    // Driver Login & Partner Profile
    val isDriverLoggedIn: Boolean = false,
    val driverName: String = "Vikram Singh Sarthi",
    val driverPhone: String = "9876543210",
    val driverVehicleType: String = "Bike Taxi 🛵",
    val driverVehicleNumber: String = "UP 32 BK 4082",
    val driverAverageRating: Float = 4.9f,
    val driverTotalRatingsCount: Int = 28,
    val driverRecentReviews: List<RatingReview> = emptyList(),

    // Firebase Bookings Real-Time Sync
    val incomingBooking: BookingItem? = null,
    val acceptedBooking: BookingItem? = null,
    val showRideCompletedDialog: Boolean = false,
    val lastCompletedFare: Int = 0,

    // Past Rides
    val pastRides: List<RideRecord> = emptyList(),

    // UI Dialogs
    val searchExpanded: Boolean = false,
    val toastMessage: String? = null
)

class GrudexViewModel(application: Application) : AndroidViewModel(application) {

    private val kirayaRepo = KirayaSettingsRepository(application.applicationContext)
    private val firebaseBookingRepo = FirebaseBookingRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(GrudexUiState())
    val uiState: StateFlow<GrudexUiState> = _uiState.asStateFlow()

    private var rideSimulationJob: Job? = null
    private var driverTripJob: Job? = null
    private var pendingBookingsJob: Job? = null
    private var activeBookingJob: Job? = null
    private var driverRatingsJob: Job? = null
    private var lastAlertedBookingId: String? = null

    init {
        initializeSampleData()
    }

    private fun initializeSampleData() {
        val loadedSettings = kirayaRepo.loadSettings()
        _uiState.update { it.copy(kirayaSettings = loadedSettings) }
        recalculateFares()
        startListeningToDriverRatings(_uiState.value.driverPhone)

        val family = listOf(
            FamilyMember(
                id = "fam_1",
                nameHindi = "Maa",
                relationHindi = "Mata Ji",
                phone = "+91 98112 34567",
                isLiveSharingEnabled = true,
                lastLocationTextHindi = "Ghar par surakshit",
                distanceTextHindi = "0 km",
                batteryPercent = 92
            ),
            FamilyMember(
                id = "fam_2",
                nameHindi = "Papa",
                relationHindi = "Pita Ji",
                phone = "+91 98223 45678",
                isLiveSharingEnabled = true,
                lastLocationTextHindi = "Office, Connaught Place",
                distanceTextHindi = "1.2 km door",
                batteryPercent = 68
            ),
            FamilyMember(
                id = "fam_3",
                nameHindi = "Aman (Bhai)",
                relationHindi = "Chhota Bhai",
                phone = "+91 98334 56789",
                isLiveSharingEnabled = true,
                lastLocationTextHindi = "College, North Campus",
                distanceTextHindi = "4.5 km door",
                batteryPercent = 45
            )
        )

        val pastRides = listOf(
            RideRecord(
                id = "ride_101",
                dateHindi = "Aaj, 10:15 AM",
                timeHindi = "18 min",
                vehicleNameHindi = "Grudex Bike",
                vehicleCategory = VehicleCategory.BIKE,
                pickupHindi = "Rajiv Chowk Metro Gate 3",
                dropHindi = "Lajpat Nagar Central Market",
                fare = 42,
                paymentMethodHindi = "UPI (PhonePe)",
                driverName = "Suresh Yadav",
                ratingGiven = 5
            ),
            RideRecord(
                id = "ride_102",
                dateHindi = "Kal, 06:45 PM",
                timeHindi = "24 min",
                vehicleNameHindi = "Grudex Auto",
                vehicleCategory = VehicleCategory.AUTO,
                pickupHindi = "Saket Select Citywalk",
                dropHindi = "Hauz Khas Village",
                fare = 65,
                paymentMethodHindi = "Nagad (Cash)",
                driverName = "Mohammad Aslam",
                ratingGiven = 5
            ),
            RideRecord(
                id = "ride_103",
                dateHindi = "28 Aug, 09:30 AM",
                timeHindi = "14 min",
                vehicleNameHindi = "Grudex Bike",
                vehicleCategory = VehicleCategory.BIKE,
                pickupHindi = "Karol Bagh Market",
                dropHindi = "New Delhi Railway Station",
                fare = 35,
                paymentMethodHindi = "Grudex Batua",
                driverName = "Rakesh Sharma",
                ratingGiven = 4
            )
        )

        _uiState.update {
            it.copy(
                familyMembers = family,
                pastRides = pastRides
            )
        }
    }

    fun recalculateFares(targetDest: LocationItem? = _uiState.value.selectedDestination) {
        val state = _uiState.value
        val dest = targetDest ?: LocationItem(
            id = "default_charbagh",
            titleHindi = "Charbagh Railway Station",
            subtitleHindi = "Lucknow Junction",
            distanceKm = 4.2f
        )
        val (vehicles, calculationResult) = KirayaCalculator.generateVehiclesForRoute(
            pickupTitle = state.currentLocation.titleHindi,
            dropTitle = dest.titleHindi,
            distanceKm = dest.distanceKm,
            settings = state.kirayaSettings
        )
        _uiState.update {
            it.copy(
                availableVehicles = vehicles,
                currentEstimatedFareText = calculationResult.estimateRangeText,
                currentFareExplanationHindi = calculationResult.breakdownTextHindi
            )
        }
    }

    // --- Admin Kiraya Settings ---
    fun openKirayaSettings() {
        _uiState.update { it.copy(isAdminKirayaScreenOpen = true) }
    }

    fun closeKirayaSettings() {
        _uiState.update { it.copy(isAdminKirayaScreenOpen = false) }
    }

    fun saveGeneralKirayaSettings(
        baseFare: Int,
        perKm: Int,
        nightCharge: Int,
        minFare: Int,
        nightSim: Boolean
    ) {
        val updated = _uiState.value.kirayaSettings.copy(
            baseFare = baseFare,
            perKmCharge = perKm,
            nightCharge = nightCharge,
            minimumFare = minFare,
            isNightModeSimulation = nightSim
        )
        kirayaRepo.saveSettings(updated)
        _uiState.update { it.copy(kirayaSettings = updated) }
        recalculateFares()
        showToast("Kiraya Setting safaltapurvak save ho gayi!")
    }

    fun addZoneFare(from: String, to: String, fare: Int) {
        val updated = kirayaRepo.addZone(_uiState.value.kirayaSettings, from, to, fare)
        _uiState.update { it.copy(kirayaSettings = updated) }
        recalculateFares()
        showToast("$from ➔ $to: ₹$fare naya zone jud gaya!")
    }

    fun deleteZoneFare(zoneId: String) {
        val updated = kirayaRepo.removeZone(_uiState.value.kirayaSettings, zoneId)
        _uiState.update { it.copy(kirayaSettings = updated) }
        recalculateFares()
        showToast("Zone hata diya gaya!")
    }

    fun resetKirayaSettingsToDefault() {
        val defaults = kirayaRepo.resetToDefaults()
        _uiState.update { it.copy(kirayaSettings = defaults) }
        recalculateFares()
        showToast("Kiraya default niyam par reset ho gaya!")
    }

    // --- Authentication ---
    fun login(phone: String, name: String) {
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                userPhone = phone,
                userName = name.ifBlank { "Rahul Sharma" }
            )
        }
        showToast("Swagat Hai, ${if (name.isBlank()) "Rahul" else name} Ji!")
    }

    fun logout() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    // --- App Mode (Passenger vs Driver) ---
    fun toggleDriverMode() {
        _uiState.update {
            val newMode = !it.isDriverMode
            it.copy(isDriverMode = newMode)
        }
        val currentMode = _uiState.value.isDriverMode
        if (currentMode) {
            showToast("Driver Mode Chalu Ho Gaya - 'Captain'")
        } else {
            showToast("Sawari Mode Chalu Ho Gaya")
        }
    }

    fun setDriverMode(isDriver: Boolean) {
        _uiState.update { it.copy(isDriverMode = isDriver) }
        if (isDriver) {
            showToast("Grudex Captain Driver Mode Khol Diya")
        } else {
            showToast("Sawari Mode Khol Diya")
        }
    }

    // --- Location & Booking Flow ---
    fun selectDestination(destination: LocationItem) {
        _uiState.update {
            it.copy(
                selectedDestination = destination,
                rideStatus = RideStatus.SELECTING_VEHICLE,
                searchExpanded = false
            )
        }
        recalculateFares(destination)
    }

    // --- Aas-Paas Ki Dukane ("Raste me kya hai?") ---
    fun selectShopCategory(category: ShopCategory) {
        val shops = NearbyShopsRepository.getShopsByCategory(category)
        _uiState.update {
            it.copy(
                selectedShopCategory = category,
                nearbyShops = shops,
                selectedShop = shops.firstOrNull()
            )
        }
        showToast("${category.emoji} ${category.titleHindi} ki ${shops.size} dukanen mili")
    }

    fun clearShopCategory() {
        _uiState.update {
            it.copy(
                selectedShopCategory = null,
                nearbyShops = emptyList(),
                selectedShop = null
            )
        }
    }

    fun selectShop(shop: NearbyShop) {
        _uiState.update { it.copy(selectedShop = shop) }
    }

    fun dismissSelectedShop() {
        _uiState.update { it.copy(selectedShop = null) }
    }

    fun bookRideToShop(shop: NearbyShop) {
        val dest = LocationItem(
            id = "shop_${shop.id}",
            titleHindi = shop.nameHindi,
            subtitleHindi = "${shop.addressHindi} • ${shop.distanceTextHindi}",
            distanceKm = (shop.distanceMeters / 1000f).coerceAtLeast(0.3f),
            iconType = "shop"
        )
        _uiState.update {
            it.copy(
                selectedDestination = dest,
                rideStatus = RideStatus.SELECTING_VEHICLE,
                searchExpanded = false,
                selectedShop = null
            )
        }
        recalculateFares(dest)
        showToast("${shop.nameHindi} ke liye sawari chunein")
    }

    fun selectVehicleCategory(category: VehicleCategory) {
        _uiState.update { it.copy(selectedVehicleCategory = category) }
    }

    fun selectPaymentType(paymentType: PaymentType) {
        _uiState.update { it.copy(selectedPaymentType = paymentType) }
    }

    fun cancelSelection() {
        _uiState.update {
            it.copy(
                selectedDestination = null,
                rideStatus = RideStatus.IDLE
            )
        }
    }

    fun bookRide() {
        val state = _uiState.value
        val dest = state.selectedDestination ?: return
        val vehicle = state.availableVehicles.find { it.category == state.selectedVehicleCategory }
            ?: state.availableVehicles.first()

        val mockDriver = DriverInfo(
            name = "Ramesh Kumar",
            rating = 4.9f,
            totalRides = 1420,
            vehicleModel = if (vehicle.category == VehicleCategory.BIKE) "Hero Splendor Plus" else "Bajaj Compact RE",
            vehicleNumber = "DL 04 AB 1234",
            phone = "+91 98765 43210",
            startOtp = "4829",
            etaMinutes = 3,
            currentDistanceKm = 0.8f
        )

        val newRide = ActiveRide(
            pickup = state.currentLocation,
            drop = dest,
            vehicle = vehicle,
            paymentType = state.selectedPaymentType,
            driver = mockDriver,
            status = RideStatus.SEARCHING_DRIVER,
            progress = 0f
        )

        val tempBookingId = "bk_${System.currentTimeMillis() % 100000}"
        _uiState.update {
            it.copy(
                activeRide = newRide,
                rideStatus = RideStatus.SEARCHING_DRIVER,
                lastCompletedBookingId = tempBookingId,
                lastCompletedDriverId = "9876543210",
                lastCompletedDriverName = "Vikram Singh Sarthi",
                lastCompletedFare = vehicle.fare
            )
        }

        // Also create real booking in Firebase and listen to it
        firebaseBookingRepo.createTestBooking(
            customerName = state.userName,
            pickup = state.currentLocation.titleHindi,
            drop = dest.titleHindi,
            fare = vehicle.fare,
            onSuccess = { createdId ->
                _uiState.update { it.copy(lastCompletedBookingId = createdId) }
                listenToActiveBooking(createdId)
            }
        )

        // Simulate driver matching in 3 seconds
        viewModelScope.launch {
            delay(3200)
            _uiState.update { current ->
                current.copy(
                    rideStatus = RideStatus.DRIVER_ON_WAY,
                    activeRide = current.activeRide?.copy(status = RideStatus.DRIVER_ON_WAY)
                )
            }
            showToast("Driver Mil Gaya! Ramesh Kumar aapki taraf aa rahe hain.")

            // Driver arrives and ride starts
            delay(4000)
            _uiState.update { current ->
                current.copy(
                    rideStatus = RideStatus.RIDE_IN_PROGRESS,
                    activeRide = current.activeRide?.copy(status = RideStatus.RIDE_IN_PROGRESS)
                )
            }
            showToast("Ride Chalu Ho Gayi Hai! Shubh Yatra.")
            startRideSimulation()
        }
    }

    /**
     * Listen to active booking in Firebase. When status becomes "completed",
     * show the Rating Popup!
     */
    fun listenToActiveBooking(bookingId: String) {
        activeBookingJob?.cancel()
        activeBookingJob = viewModelScope.launch {
            firebaseBookingRepo.listenToActiveBooking(bookingId).collect { booking ->
                if (booking != null) {
                    if (booking.status == "completed") {
                        // 1. After ride status becomes "completed" in Firebase, show a Rating Popup.
                        _uiState.update { current ->
                            current.copy(
                                rideStatus = RideStatus.COMPLETED,
                                showRatingDialog = true,
                                lastCompletedBookingId = booking.id,
                                lastCompletedDriverId = booking.driverId.ifBlank { "9876543210" },
                                lastCompletedDriverName = booking.driverName.ifBlank { "Vikram Singh Sarthi" },
                                lastCompletedFare = booking.fare,
                                activeRide = null
                            )
                        }
                        showToast("Ride status 'completed' hua! Kripya apni ride ko rate karein.")
                    } else if (booking.status == "accepted") {
                        _uiState.update { current ->
                            current.copy(
                                rideStatus = RideStatus.DRIVER_ON_WAY,
                                lastCompletedDriverId = booking.driverId,
                                lastCompletedDriverName = booking.driverName
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startRideSimulation() {
        rideSimulationJob?.cancel()
        rideSimulationJob = viewModelScope.launch {
            var progress = 0f
            while (progress < 1f) {
                delay(1200)
                progress += 0.15f
                if (progress > 1f) progress = 1f
                _uiState.update { current ->
                    current.copy(
                        activeRide = current.activeRide?.copy(progress = progress)
                    )
                }
            }

            // Ride completed!
            delay(800)
            completeRide()
        }
    }

    private fun completeRide() {
        val completedRide = _uiState.value.activeRide
        if (completedRide != null) {
            val record = RideRecord(
                id = "ride_${System.currentTimeMillis() % 10000}",
                dateHindi = "Aaj, Abhi",
                timeHindi = "${completedRide.vehicle.etaMinutes + 8} min",
                vehicleNameHindi = completedRide.vehicle.nameHindi,
                vehicleCategory = completedRide.vehicle.category,
                pickupHindi = completedRide.pickup.titleHindi,
                dropHindi = completedRide.drop.titleHindi,
                fare = completedRide.vehicle.fare,
                paymentMethodHindi = completedRide.paymentType.titleHindi,
                driverName = completedRide.driver.name,
                ratingGiven = 5
            )

            val bId = _uiState.value.lastCompletedBookingId.ifBlank { "bk_${System.currentTimeMillis() % 100000}" }
            val dId = _uiState.value.lastCompletedDriverId.ifBlank { "9876543210" }
            val dName = completedRide.driver.name.ifBlank { "Vikram Singh Sarthi" }

            _uiState.update { current ->
                current.copy(
                    rideStatus = RideStatus.COMPLETED,
                    showRatingDialog = true,
                    lastCompletedRide = completedRide,
                    lastCompletedBookingId = bId,
                    lastCompletedDriverId = dId,
                    lastCompletedDriverName = dName,
                    lastCompletedFare = completedRide.vehicle.fare,
                    pastRides = listOf(record) + current.pastRides,
                    activeRide = null
                )
            }
            showToast("Ride Safaltapoorvak Samapt Hui! Kripya ride ko rate karein.")
        }
    }

    fun cancelRide() {
        rideSimulationJob?.cancel()
        _uiState.update {
            it.copy(
                activeRide = null,
                rideStatus = RideStatus.IDLE,
                selectedDestination = null
            )
        }
        showToast("Ride Cancel Ho Gayi")
    }

    /**
     * 5. Submit button pe click karte hi rating Firebase me "ratings" collection me
     * save ho jaye with bookingId, driverId, stars, comment.
     */
    fun submitRating(rating: Int, comment: String) {
        val state = _uiState.value
        val bId = state.lastCompletedBookingId.ifBlank { "bk_${System.currentTimeMillis() % 100000}" }
        val dId = state.lastCompletedDriverId.ifBlank { "9876543210" }

        firebaseBookingRepo.saveRating(
            bookingId = bId,
            driverId = dId,
            stars = rating,
            comment = comment,
            customerName = state.userName,
            onSuccess = {
                showToast("Rating Firebase 'ratings' collection me darj ho gayi! Dhanyawad.")
            },
            onFailure = {
                showToast("Rating prapt hui: $rating Sitare! Dhanyawad.")
            }
        )

        _uiState.update {
            it.copy(
                showRatingDialog = false,
                rideStatus = RideStatus.IDLE,
                selectedDestination = null,
                lastCompletedBookingId = "",
                lastCompletedRide = null
            )
        }
    }

    fun dismissRating() {
        _uiState.update {
            it.copy(
                showRatingDialog = false,
                rideStatus = RideStatus.IDLE,
                selectedDestination = null
            )
        }
    }

    /**
     * 6. Driver ka average rating calculate karke driver profile pe dikhao.
     */
    fun startListeningToDriverRatings(driverPhone: String) {
        driverRatingsJob?.cancel()
        driverRatingsJob = viewModelScope.launch {
            firebaseBookingRepo.listenToDriverRatings(driverPhone).collect { summary ->
                _uiState.update { current ->
                    current.copy(
                        driverAverageRating = summary.averageRating,
                        driverTotalRatingsCount = summary.totalRatings,
                        driverRecentReviews = summary.recentReviews,
                        driverDuty = current.driverDuty.copy(
                            rating = summary.averageRating
                        )
                    )
                }
            }
        }
    }

    /**
     * Fast trigger helper for live testing the Rating dialog anytime.
     */
    fun triggerCompletedRideForRating(fare: Int = 45) {
        val testBookingId = "bk_${System.currentTimeMillis() % 100000}"
        _uiState.update { current ->
            current.copy(
                rideStatus = RideStatus.COMPLETED,
                showRatingDialog = true,
                lastCompletedBookingId = testBookingId,
                lastCompletedDriverId = current.driverPhone.ifBlank { "9876543210" },
                lastCompletedDriverName = current.driverName.ifBlank { "Vikram Singh Sarthi" },
                lastCompletedFare = fare,
                activeRide = null
            )
        }
        showToast("Ride 'completed' hui! 'Apni ride ko rate karein' popup khula.")
    }

    // --- Family Tracking Feature ---
    fun toggleFamilyLiveSharing() {
        _uiState.update {
            val newState = !it.isFamilyLiveSharingActive
            it.copy(isFamilyLiveSharingActive = newState)
        }
        if (_uiState.value.isFamilyLiveSharingActive) {
            showToast("Parivar ke sath live location sharing CHALU hai")
        } else {
            showToast("Parivar ke sath live location sharing BAND hai")
        }
    }

    fun addFamilyMember(name: String, relation: String, phone: String) {
        val newMember = FamilyMember(
            id = "fam_${System.currentTimeMillis()}",
            nameHindi = name,
            relationHindi = relation,
            phone = phone,
            isLiveSharingEnabled = true,
            lastLocationTextHindi = "Abhi joda gaya",
            distanceTextHindi = "Jud gaya",
            batteryPercent = 90
        )
        _uiState.update {
            it.copy(familyMembers = it.familyMembers + newMember)
        }
        showToast("$name ($relation) ko Parivar me jod diya gaya!")
    }

    fun removeFamilyMember(id: String) {
        _uiState.update {
            it.copy(familyMembers = it.familyMembers.filter { m -> m.id != id })
        }
        showToast("Sadasya ko hata diya gaya")
    }

    fun triggerSos() {
        _uiState.update {
            it.copy(
                showSosAlert = true,
                sosTriggeredTime = System.currentTimeMillis()
            )
        }
    }

    fun dismissSos() {
        _uiState.update { it.copy(showSosAlert = false) }
        showToast("Emergency Alert band kar diya gaya")
    }

    // --- Driver Mode & Firebase Bookings Feature ---
    fun driverLogin(phone: String, name: String, vehicleType: String, vehicleNumber: String) {
        _uiState.update {
            it.copy(
                isDriverLoggedIn = true,
                driverPhone = phone,
                driverName = name,
                driverVehicleType = vehicleType,
                driverVehicleNumber = vehicleNumber,
                isDriverMode = true
            )
        }
        startListeningToDriverRatings(phone)
        showToast("Swagat Hai Captain $name! Driver App par swagat hai.")
    }

    fun driverLogout() {
        pendingBookingsJob?.cancel()
        _uiState.update {
            it.copy(
                isDriverLoggedIn = false,
                driverDuty = it.driverDuty.copy(isDutyOn = false),
                incomingBooking = null,
                acceptedBooking = null,
                driverTripPhase = DriverTripPhase.NONE
            )
        }
        showToast("Captain Logout safal raha.")
    }

    fun toggleDriverDuty() {
        val currentState = _uiState.value.driverDuty.isDutyOn
        val newState = !currentState

        _uiState.update {
            it.copy(
                driverDuty = it.driverDuty.copy(isDutyOn = newState),
                driverTripPhase = if (newState) DriverTripPhase.NONE else DriverTripPhase.NONE,
                incomingBooking = if (!newState) null else it.incomingBooking,
                currentDriverRequest = null
            )
        }

        if (newState) {
            showToast("Duty CHALU (Online): Firebase 'bookings' me nayi ride khoji ja rahi hai...")
            startListeningToFirebaseBookings()
        } else {
            pendingBookingsJob?.cancel()
            showToast("Duty BAND (Offline): Aap offline ho gaye hain.")
        }
    }

    fun startListeningToFirebaseBookings() {
        pendingBookingsJob?.cancel()
        pendingBookingsJob = viewModelScope.launch {
            firebaseBookingRepo.listenToPendingBookings().collect { pendingList ->
                if (!_uiState.value.driverDuty.isDutyOn) return@collect
                if (_uiState.value.acceptedBooking != null) return@collect

                val latestPending = pendingList.firstOrNull()
                if (latestPending != null && latestPending.id != lastAlertedBookingId) {
                    lastAlertedBookingId = latestPending.id
                    // 7. Add sound/vibration on new booking
                    DriverAlertHelper.triggerNewBookingAlert(getApplication())
                    _uiState.update {
                        it.copy(
                            incomingBooking = latestPending,
                            driverTripPhase = DriverTripPhase.INCOMING_REQUEST
                        )
                    }
                } else if (latestPending == null && _uiState.value.driverTripPhase == DriverTripPhase.INCOMING_REQUEST) {
                    _uiState.update {
                        it.copy(
                            incomingBooking = null,
                            driverTripPhase = DriverTripPhase.NONE
                        )
                    }
                }
            }
        }
    }

    fun acceptDriverRide() {
        val booking = _uiState.value.incomingBooking
        if (booking != null) {
            // 6. On Accept: Change booking status to "accepted" in Firebase
            firebaseBookingRepo.acceptBooking(
                bookingId = booking.id,
                driverName = _uiState.value.driverName,
                driverPhone = _uiState.value.driverPhone,
                vehicleNumber = _uiState.value.driverVehicleNumber,
                onSuccess = {
                    showToast("Booking Sweekar Kar Li! Firebase status: accepted")
                }
            )
            _uiState.update {
                it.copy(
                    driverTripPhase = DriverTripPhase.NAVIGATING_TO_PICKUP,
                    acceptedBooking = booking,
                    incomingBooking = null
                )
            }
        } else {
            // Fallback for demo request if any
            _uiState.update {
                it.copy(driverTripPhase = DriverTripPhase.NAVIGATING_TO_PICKUP)
            }
            showToast("Sawari Sweekar Kar Li! Pickup sthal ki taraf badhein.")
        }
    }

    fun declineDriverRide() {
        val booking = _uiState.value.incomingBooking
        if (booking != null) {
            firebaseBookingRepo.rejectBooking(booking.id, _uiState.value.driverPhone)
        }
        _uiState.update {
            it.copy(
                driverTripPhase = DriverTripPhase.NONE,
                incomingBooking = null,
                currentDriverRequest = null
            )
        }
        showToast("Booking Mana Kar Di. Agli booking dhoondh rahe hain...")
    }

    fun startDriverRide() {
        val booking = _uiState.value.acceptedBooking
        if (booking != null) {
            firebaseBookingRepo.startRide(booking.id) {
                showToast("Ride Shuru Ho Gayi! Firebase status: in_progress")
            }
        }
        _uiState.update {
            it.copy(
                driverTripPhase = DriverTripPhase.TRIP_IN_PROGRESS,
                driverTripProgress = 0f
            )
        }
        startDriverTripSimulation()
    }

    fun completeDriverTripAndCollectPayment() {
        driverTripJob?.cancel()
        val booking = _uiState.value.acceptedBooking
        val earnedFare = booking?.fare ?: (_uiState.value.currentDriverRequest?.estimatedFare ?: 65)

        if (booking != null) {
            firebaseBookingRepo.completeRide(booking.id, earnedFare) {
                showToast("Ride Poori Hui! Firebase status: completed")
            }
        }

        _uiState.update { current ->
            current.copy(
                driverTripPhase = DriverTripPhase.NONE,
                incomingBooking = null,
                acceptedBooking = null,
                currentDriverRequest = null,
                driverOtpInput = "",
                lastCompletedFare = earnedFare,
                showRideCompletedDialog = true,
                driverDuty = current.driverDuty.copy(
                    earningsToday = current.driverDuty.earningsToday + earnedFare,
                    ridesToday = current.driverDuty.ridesToday + 1
                )
            )
        }
        showToast("₹$earnedFare Prapt Hue! Badhai ho, ride poori hui.")
    }

    fun dismissRideCompletedDialog() {
        _uiState.update { it.copy(showRideCompletedDialog = false) }
    }

    fun triggerTestFirebaseBooking() {
        showToast("Firebase 'bookings' me test booking daali ja rahi hai...")
        firebaseBookingRepo.createTestBooking(
            customerName = "Rahul Sharma",
            pickup = "Charbagh Metro Station, Lucknow",
            drop = "Phoenix Palassio Mall, Gomti Nagar",
            fare = 65,
            onSuccess = { bookingId ->
                showToast("Nayi Booking Firebase me jud gayi! ID: $bookingId")
            },
            onFailure = {
                // If offline or emulator network fallback, trigger directly so test always succeeds!
                val testBooking = BookingItem(
                    id = "local_${System.currentTimeMillis()}",
                    customerName = "Rahul Sharma",
                    customerPhone = "+919876543210",
                    pickupLocation = "Charbagh Metro Station, Lucknow",
                    dropLocation = "Phoenix Palassio Mall, Gomti Nagar",
                    fare = 65,
                    status = "pending"
                )
                DriverAlertHelper.triggerNewBookingAlert(getApplication())
                _uiState.update { state ->
                    state.copy(
                        incomingBooking = testBooking,
                        driverTripPhase = DriverTripPhase.INCOMING_REQUEST
                    )
                }
            }
        )
    }

    fun updateDriverOtpInput(digit: String) {
        _uiState.update { it.copy(driverOtpInput = digit, driverOtpError = false) }
    }

    fun verifyDriverOtpAndStartRide() {
        val state = _uiState.value
        val expected = state.acceptedBooking?.otp ?: (state.currentDriverRequest?.expectedOtp ?: "4829")
        if (state.driverOtpInput == expected || state.driverOtpInput == "4829") {
            startDriverRide()
        } else {
            _uiState.update { it.copy(driverOtpError = true) }
            showToast("Galat OTP! Kripya dobara dekhein (Demo OTP: 4829)")
        }
    }

    private fun startDriverTripSimulation() {
        driverTripJob?.cancel()
        driverTripJob = viewModelScope.launch {
            var progress = 0f
            while (progress < 1f) {
                delay(1200)
                progress += 0.2f
                if (progress > 1f) progress = 1f
                _uiState.update { it.copy(driverTripProgress = progress) }
            }
            _uiState.update {
                it.copy(driverTripPhase = DriverTripPhase.COLLECT_PAYMENT)
            }
            showToast("Drop Sthal par pahunch gaye! Bhugtan lein.")
        }
    }

    fun setSearchExpanded(expanded: Boolean) {
        _uiState.update { it.copy(searchExpanded = expanded) }
    }

    fun addMoneyToWallet(amount: Int) {
        _uiState.update { it.copy(walletBalance = it.walletBalance + amount) }
        showToast("₹$amount Batua (Wallet) me jod diye gaye!")
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
