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
    val isDriverMode: Boolean = false,

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

    // Past Rides
    val pastRides: List<RideRecord> = emptyList(),

    // UI Dialogs
    val searchExpanded: Boolean = false,
    val toastMessage: String? = null
)

class GrudexViewModel(application: Application) : AndroidViewModel(application) {

    private val kirayaRepo = KirayaSettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(GrudexUiState())
    val uiState: StateFlow<GrudexUiState> = _uiState.asStateFlow()

    private var rideSimulationJob: Job? = null
    private var driverTripJob: Job? = null

    init {
        initializeSampleData()
    }

    private fun initializeSampleData() {
        val loadedSettings = kirayaRepo.loadSettings()
        _uiState.update { it.copy(kirayaSettings = loadedSettings) }
        recalculateFares()

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

        _uiState.update {
            it.copy(
                activeRide = newRide,
                rideStatus = RideStatus.SEARCHING_DRIVER
            )
        }

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

            _uiState.update { current ->
                current.copy(
                    rideStatus = RideStatus.COMPLETED,
                    showRatingDialog = true,
                    lastCompletedRide = completedRide,
                    pastRides = listOf(record) + current.pastRides,
                    activeRide = null
                )
            }
            showToast("Ride Safaltapoorvak Samapt Hui! Dhanyawad.")
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

    fun submitRating(rating: Int, compliment: String) {
        _uiState.update {
            it.copy(
                showRatingDialog = false,
                rideStatus = RideStatus.IDLE,
                selectedDestination = null
            )
        }
        showToast("Rating Prapt Hui: $rating Sitare! Feedback ke liye dhanyawad.")
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

    // --- Driver Mode Feature ---
    fun toggleDriverDuty() {
        val currentState = _uiState.value.driverDuty.isDutyOn
        val newState = !currentState

        _uiState.update {
            it.copy(
                driverDuty = it.driverDuty.copy(isDutyOn = newState),
                driverTripPhase = if (newState) DriverTripPhase.NONE else DriverTripPhase.NONE,
                currentDriverRequest = null
            )
        }

        if (newState) {
            showToast("Duty CHALU: Aap Online hain! Sawari khoji ja rahi hai...")
            simulateIncomingDriverRequest()
        } else {
            showToast("Duty BAND: Aap Offline hain.")
        }
    }

    private fun simulateIncomingDriverRequest() {
        viewModelScope.launch {
            delay(3500)
            if (!_uiState.value.driverDuty.isDutyOn) return@launch

            val request = DriverRideRequest(
                id = "req_992",
                passengerName = "Akanksha Ji",
                pickupHindi = "Rajiv Chowk Metro Gate 2",
                dropHindi = "Lajpat Nagar Central Market",
                distanceKm = 4.2f,
                estimatedFare = 52,
                passengerRating = 4.95f,
                expectedOtp = "4829"
            )

            _uiState.update {
                it.copy(
                    driverTripPhase = DriverTripPhase.INCOMING_REQUEST,
                    currentDriverRequest = request
                )
            }
        }
    }

    fun acceptDriverRide() {
        _uiState.update {
            it.copy(driverTripPhase = DriverTripPhase.NAVIGATING_TO_PICKUP)
        }
        showToast("Sawari Sweekar Kar Li! Pickup sthal ki taraf badhein.")

        // After reaching pickup
        viewModelScope.launch {
            delay(3000)
            _uiState.update {
                it.copy(driverTripPhase = DriverTripPhase.ARRIVED_ENTER_OTP)
            }
            showToast("Aap Pickup par pahunch gaye hain! Sawari se OTP lein.")
        }
    }

    fun declineDriverRide() {
        _uiState.update {
            it.copy(
                driverTripPhase = DriverTripPhase.NONE,
                currentDriverRequest = null
            )
        }
        showToast("Sawari Mana Kar Di. Agli sawari dhoondh rahe hain...")
        simulateIncomingDriverRequest()
    }

    fun updateDriverOtpInput(digit: String) {
        _uiState.update { it.copy(driverOtpInput = digit, driverOtpError = false) }
    }

    fun verifyDriverOtpAndStartRide() {
        val state = _uiState.value
        val expected = state.currentDriverRequest?.expectedOtp ?: "4829"
        if (state.driverOtpInput == expected || state.driverOtpInput == "4829") {
            _uiState.update {
                it.copy(
                    driverTripPhase = DriverTripPhase.TRIP_IN_PROGRESS,
                    driverOtpError = false,
                    driverTripProgress = 0f
                )
            }
            showToast("OTP Sahi Hai! Ride Chalu Ho Gayi.")
            startDriverTripSimulation()
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
            showToast("Gantavya par pahunch gaye! Bhugtan lein.")
        }
    }

    fun completeDriverTripAndCollectPayment() {
        val earnedFare = _uiState.value.currentDriverRequest?.estimatedFare ?: 52
        _uiState.update { current ->
            current.copy(
                driverTripPhase = DriverTripPhase.NONE,
                currentDriverRequest = null,
                driverOtpInput = "",
                driverDuty = current.driverDuty.copy(
                    earningsToday = current.driverDuty.earningsToday + earnedFare,
                    ridesToday = current.driverDuty.ridesToday + 1
                )
            )
        }
        showToast("₹$earnedFare Prapt Hue! Badhai ho, ride poori hui.")
        simulateIncomingDriverRequest()
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
