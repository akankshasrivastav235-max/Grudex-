package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.LocationItem
import com.example.ui.components.RatingDialog
import com.example.ui.components.SosDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DriverModeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KirayaSettingScreen
import com.example.ui.screens.MeriRidesScreen
import com.example.ui.screens.ParivarTrackingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexYellow
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GrudexViewModel

enum class MainTab(val titleHindi: String) {
    HOME("Home"),
    PARIVAR("Parivar"),
    RIDES("Meri Rides"),
    PROFILE("Profile")
}

class MainActivity : ComponentActivity() {

    private val viewModel: GrudexViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }
                var selectedTab by remember { mutableStateOf(MainTab.HOME) }

                // Display toast messages from ViewModel
                LaunchedEffect(uiState.toastMessage) {
                    uiState.toastMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                if (!uiState.isLoggedIn) {
                    AuthScreen(
                        onLoginSuccess = { phone, name ->
                            viewModel.login(phone, name)
                        }
                    )
                } else if (uiState.isAdminKirayaScreenOpen) {
                    // Admin Panel: Kiraya Set Karo ⚙️
                    BackHandler {
                        viewModel.closeKirayaSettings()
                    }
                    KirayaSettingScreen(
                        currentSettings = uiState.kirayaSettings,
                        onSaveGeneralSettings = { base, km, night, min, sim ->
                            viewModel.saveGeneralKirayaSettings(base, km, night, min, sim)
                        },
                        onAddZone = { from, to, fare ->
                            viewModel.addZoneFare(from, to, fare)
                        },
                        onDeleteZone = { zoneId ->
                            viewModel.deleteZoneFare(zoneId)
                        },
                        onResetToDefaults = {
                            viewModel.resetKirayaSettingsToDefault()
                        },
                        onBack = {
                            viewModel.closeKirayaSettings()
                        }
                    )
                } else if (uiState.isDriverMode) {
                    // Driver App Mode
                    DriverModeScreen(
                        uiState = uiState,
                        onToggleDuty = { viewModel.toggleDriverDuty() },
                        onAcceptRide = { viewModel.acceptDriverRide() },
                        onDeclineRide = { viewModel.declineDriverRide() },
                        onOtpChange = { viewModel.updateDriverOtpInput(it) },
                        onVerifyOtpAndStart = { viewModel.verifyDriverOtpAndStartRide() },
                        onCompleteTripAndCollect = { viewModel.completeDriverTripAndCollectPayment() },
                        onSwitchToPassenger = { viewModel.toggleDriverMode() }
                    )
                } else {
                    // Passenger App Mode with Bottom Navigation
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .testTag("main_bottom_nav")
                            ) {
                                NavigationBarItem(
                                    selected = selectedTab == MainTab.HOME,
                                    onClick = { selectedTab = MainTab.HOME },
                                    icon = {
                                        Icon(
                                            Icons.Default.DirectionsBike,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = MainTab.HOME.titleHindi,
                                            fontWeight = if (selectedTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GrudexBlack,
                                        selectedTextColor = GrudexBlack,
                                        indicatorColor = GrudexYellow,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_home")
                                )

                                NavigationBarItem(
                                    selected = selectedTab == MainTab.PARIVAR,
                                    onClick = { selectedTab = MainTab.PARIVAR },
                                    icon = {
                                        Icon(
                                            Icons.Default.FamilyRestroom,
                                            contentDescription = "Parivar"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = MainTab.PARIVAR.titleHindi,
                                            fontWeight = if (selectedTab == MainTab.PARIVAR) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GrudexBlack,
                                        selectedTextColor = GrudexBlack,
                                        indicatorColor = GrudexYellow,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_parivar")
                                )

                                NavigationBarItem(
                                    selected = selectedTab == MainTab.RIDES,
                                    onClick = { selectedTab = MainTab.RIDES },
                                    icon = {
                                        Icon(
                                            Icons.Default.ReceiptLong,
                                            contentDescription = "Meri Rides"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = MainTab.RIDES.titleHindi,
                                            fontWeight = if (selectedTab == MainTab.RIDES) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GrudexBlack,
                                        selectedTextColor = GrudexBlack,
                                        indicatorColor = GrudexYellow,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_rides")
                                )

                                NavigationBarItem(
                                    selected = selectedTab == MainTab.PROFILE,
                                    onClick = { selectedTab = MainTab.PROFILE },
                                    icon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "Profile"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = MainTab.PROFILE.titleHindi,
                                            fontWeight = if (selectedTab == MainTab.PROFILE) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GrudexBlack,
                                        selectedTextColor = GrudexBlack,
                                        indicatorColor = GrudexYellow,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_profile")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = selectedTab, label = "tab_transition") { tab ->
                                when (tab) {
                                    MainTab.HOME -> HomeScreen(
                                        uiState = uiState,
                                        onDestinationSelected = { viewModel.selectDestination(it) },
                                        onVehicleCategorySelected = { viewModel.selectVehicleCategory(it) },
                                        onPaymentTypeSelected = { viewModel.selectPaymentType(it) },
                                        onBookRideClick = { viewModel.bookRide() },
                                        onCancelRideClick = { viewModel.cancelRide() },
                                        onTriggerSos = { viewModel.triggerSos() },
                                        onOpenSearch = { viewModel.setSearchExpanded(true) },
                                        onCloseSearch = { viewModel.setSearchExpanded(false) },
                                        onShopCategorySelected = { viewModel.selectShopCategory(it) },
                                        onShopSelected = { viewModel.selectShop(it) },
                                        onDismissSelectedShop = { viewModel.dismissSelectedShop() },
                                        onBookRideToShop = { viewModel.bookRideToShop(it) },
                                        onOpenKirayaSettings = { viewModel.openKirayaSettings() }
                                    )

                                    MainTab.PARIVAR -> ParivarTrackingScreen(
                                        uiState = uiState,
                                        onToggleAutoShare = { viewModel.toggleFamilyLiveSharing() },
                                        onAddMember = { name, rel, phone ->
                                            viewModel.addFamilyMember(name, rel, phone)
                                        },
                                        onRemoveMember = { viewModel.removeFamilyMember(it) },
                                        onTriggerSos = { viewModel.triggerSos() }
                                    )

                                    MainTab.RIDES -> MeriRidesScreen(
                                        uiState = uiState,
                                        onRebookRide = { record ->
                                            viewModel.selectDestination(
                                                LocationItem(
                                                    id = "rebook_${record.id}",
                                                    titleHindi = record.dropHindi,
                                                    subtitleHindi = "Pichli Ride se",
                                                    distanceKm = 4.0f
                                                )
                                            )
                                            selectedTab = MainTab.HOME
                                        }
                                    )

                                    MainTab.PROFILE -> ProfileScreen(
                                        uiState = uiState,
                                        onSwitchToDriverMode = { viewModel.toggleDriverMode() },
                                        onAddMoneyToWallet = { viewModel.addMoneyToWallet(it) },
                                        onLogout = { viewModel.logout() },
                                        onOpenKirayaSettings = { viewModel.openKirayaSettings() }
                                    )
                                }
                            }
                        }
                    }
                }

                // SOS Emergency Dialog
                if (uiState.showSosAlert) {
                    SosDialog(
                        onDismiss = { viewModel.dismissSos() },
                        familyMemberName = uiState.familyMembers.firstOrNull()?.nameHindi ?: "Maa",
                        familyPhone = uiState.familyMembers.firstOrNull()?.phone ?: "+919811234567"
                    )
                }

                // Rating Dialog after Ride
                if (uiState.showRatingDialog) {
                    RatingDialog(
                        driverName = uiState.lastCompletedRide?.driver?.name ?: "Ramesh Kumar",
                        fare = uiState.lastCompletedRide?.vehicle?.fare ?: 35,
                        onDismiss = { viewModel.dismissRating() },
                        onSubmit = { rating, feedback ->
                            viewModel.submitRating(rating, feedback)
                        }
                    )
                }
            }
        }
    }
}
