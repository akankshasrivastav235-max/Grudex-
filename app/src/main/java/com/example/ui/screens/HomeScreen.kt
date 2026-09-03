package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LocationItem
import com.example.model.NearbyShop
import com.example.model.PaymentType
import com.example.model.RideStatus
import com.example.model.ShopCategory
import com.example.model.VehicleCategory
import com.example.model.VehicleOption
import com.example.ui.components.GrudexMapCanvas
import com.example.ui.components.NearbyShopsCarousel
import com.example.ui.components.RouteShopsCategoryBar
import com.example.ui.components.SelectedShopDetailCard
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexBorder
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexRed
import com.example.ui.theme.GrudexYellow
import com.example.ui.theme.GrudexYellowContainer
import com.example.viewmodel.GrudexUiState

@Composable
fun HomeScreen(
    uiState: GrudexUiState,
    onDestinationSelected: (LocationItem) -> Unit,
    onVehicleCategorySelected: (VehicleCategory) -> Unit,
    onPaymentTypeSelected: (PaymentType) -> Unit,
    onBookRideClick: () -> Unit,
    onCancelRideClick: () -> Unit,
    onTriggerSos: () -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onShopCategorySelected: (ShopCategory) -> Unit = {},
    onShopSelected: (NearbyShop) -> Unit = {},
    onDismissSelectedShop: () -> Unit = {},
    onBookRideToShop: (NearbyShop) -> Unit = {},
    onOpenKirayaSettings: () -> Unit = {}
) {
    val context = LocalContext.current

    // Sample popular locations in Lucknow in Hindi
    val popularLocations = remember {
        listOf(
            LocationItem(
                id = "loc_1",
                titleHindi = "Charbagh Railway Station",
                subtitleHindi = "Platform Road, Charbagh, Lucknow",
                distanceKm = 3.2f
            ),
            LocationItem(
                id = "loc_2",
                titleHindi = "Phoenix Palassio Mall, Gomti Nagar",
                subtitleHindi = "Amar Shaheed Path, Sector 7, Lucknow",
                distanceKm = 7.5f
            ),
            LocationItem(
                id = "loc_3",
                titleHindi = "Bara Imambara & Rumi Darwaza",
                subtitleHindi = "Machchhi Bhavan, Chowk, Lucknow",
                distanceKm = 4.8f
            ),
            LocationItem(
                id = "loc_4",
                titleHindi = "Aminabad Main Market",
                subtitleHindi = "Gadbada Jhala, Aminabad, Lucknow",
                distanceKm = 2.1f
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map Canvas in background with animated roads, ambient bikes, and nearby shop pins
        GrudexMapCanvas(
            modifier = Modifier.fillMaxSize(),
            rideStatus = uiState.rideStatus,
            tripProgress = uiState.activeRide?.progress ?: 0f,
            showFamilyMarker = uiState.rideStatus == RideStatus.RIDE_IN_PROGRESS && uiState.isFamilyLiveSharingActive,
            familyStatusText = "Rahul Ghar se 2km door hai, Bike par hai",
            nearbyShops = uiState.nearbyShops,
            selectedShop = uiState.selectedShop,
            onShopPinClick = onShopSelected,
            onLocateMeClick = {}
        )

        // Top Bar: Location & SOS Button
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Grudex Official Logo Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.testTag("home_official_logo_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_grudex_logo),
                            contentDescription = "Grudex Official Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GRUDEX",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Current Location Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Aapka Sthan (Live)",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.currentLocation.titleHindi,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Admin Kiraya Quick Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GrudexBlack,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .clickable { onOpenKirayaSettings() }
                        .testTag("home_kiraya_admin_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚙️ Kiraya",
                            color = GrudexYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // SOS Emergency Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GrudexRed,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .clickable { onTriggerSos() }
                        .testTag("home_sos_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "SOS",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SOS",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Parivar Auto-Share Live Banner (if on ride)
            if (uiState.rideStatus == RideStatus.RIDE_IN_PROGRESS && uiState.isFamilyLiveSharingActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GrudexGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Parivar ke sath live location automatically share ho rahi hai",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Bottom Content depends on Ride Status
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            when (uiState.rideStatus) {
                RideStatus.IDLE -> {
                    // "Kahan Jana Hai?" Search Box Card
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_idle_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Namaste, ${uiState.userName.split(" ").firstOrNull() ?: "Rahul"} Ji!",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Kahan Jana Hai?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Search Box Field
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = GrudexLightGrey,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenSearch() }
                                    .testTag("search_destination_box")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = GrudexDark
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Apna drop sthal khojein...",
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // "Raste me kya hai?" Nearby Shops Section
                            RouteShopsCategoryBar(
                                selectedCategory = uiState.selectedShopCategory,
                                onCategoryClick = onShopCategorySelected,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("route_shops_category_bar")
                            )

                            if (uiState.nearbyShops.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                NearbyShopsCarousel(
                                    shops = uiState.nearbyShops,
                                    selectedShop = uiState.selectedShop,
                                    onShopClick = onShopSelected,
                                    onBookRide = onBookRideToShop,
                                    modifier = Modifier.testTag("nearby_shops_carousel")
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Popular / Quick Destinations in Hindi
                            Text(
                                text = "Lokpriya Jagayein (Quick Destination)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            popularLocations.take(3).forEach { loc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onDestinationSelected(loc) }
                                        .padding(vertical = 10.dp)
                                        .testTag("quick_location_${loc.id}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(GrudexYellow.copy(alpha = 0.25f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = GrudexBlack,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = loc.titleHindi,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrudexBlack
                                        )
                                        Text(
                                            text = loc.subtitleHindi,
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = "${loc.distanceKm} km",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GrudexDark
                                    )
                                }
                            }
                        }
                    }
                }

                RideStatus.SELECTING_VEHICLE -> {
                    // Fare Estimate & Vehicle Options (Bike, Auto, Cab)
                    VehicleSelectionCard(
                        uiState = uiState,
                        onVehicleSelected = onVehicleCategorySelected,
                        onPaymentSelected = onPaymentTypeSelected,
                        onBookRide = onBookRideClick,
                        onCancel = onCancelRideClick
                    )
                }

                RideStatus.SEARCHING_DRIVER -> {
                    // Searching Animation Card with Radar Pulse
                    SearchingDriverCard(
                        uiState = uiState,
                        onCancelRide = onCancelRideClick
                    )
                }

                RideStatus.DRIVER_ON_WAY -> {
                    // Driver Details Card (Name, Photo, Bike No, OTP, Call button)
                    DriverDetailsCard(
                        uiState = uiState,
                        isArriving = true,
                        onCallDriver = {
                            val phone = uiState.activeRide?.driver?.phone ?: "+919876543210"
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        },
                        onCancelRide = onCancelRideClick
                    )
                }

                RideStatus.RIDE_IN_PROGRESS -> {
                    // Live Tracking Card
                    LiveRideTrackingCard(
                        uiState = uiState,
                        onCallDriver = {
                            val phone = uiState.activeRide?.driver?.phone ?: "+919876543210"
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        },
                        onTriggerSos = onTriggerSos
                    )
                }

                else -> {}
            }
        }

        // Selected Shop Detail Bottom Sheet overlay when user taps a shop pin or carousel item
        if (uiState.selectedShop != null && uiState.rideStatus == RideStatus.IDLE) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                SelectedShopDetailCard(
                    shop = uiState.selectedShop,
                    onClose = onDismissSelectedShop,
                    onBookRide = onBookRideToShop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Full Search Modal when user taps "Kahan Jana Hai?"
        AnimatedVisibility(
            visible = uiState.searchExpanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 250)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 200)
            )
        ) {
            SearchDestinationModal(
                popularLocations = popularLocations,
                selectedCategory = uiState.selectedShopCategory,
                nearbyShops = uiState.nearbyShops,
                onSelectLocation = onDestinationSelected,
                onCategoryClick = onShopCategorySelected,
                onBookRideToShop = onBookRideToShop,
                onDismiss = onCloseSearch
            )
        }
    }
}

@Composable
private fun VehicleSelectionCard(
    uiState: GrudexUiState,
    onVehicleSelected: (VehicleCategory) -> Unit,
    onPaymentSelected: (PaymentType) -> Unit,
    onBookRide: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vehicle_selection_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Drop Sthal:",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = uiState.selectedDestination?.titleHindi ?: "Gantavya",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )
                }
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.testTag("cancel_destination_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Radd Karein")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Anumanit Kiraya Highlight Banner (Requirement 4)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GrudexYellowContainer,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GrudexYellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("anumanit_kiraya_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(GrudexYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Anumanit Kiraya: Rs. ${uiState.currentEstimatedFareText}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack
                        )
                        if (uiState.currentFareExplanationHindi.isNotBlank()) {
                            Text(
                                text = uiState.currentFareExplanationHindi,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = GrudexDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle Option Cards: Bike, Auto, Cab
            uiState.availableVehicles.forEach { vehicle ->
                val isSelected = uiState.selectedVehicleCategory == vehicle.category

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) GrudexYellow.copy(alpha = 0.2f) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) GrudexYellow else GrudexBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onVehicleSelected(vehicle.category) }
                        .testTag("vehicle_option_${vehicle.category.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Vehicle Icon Badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isSelected) GrudexYellow else GrudexLightGrey,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (vehicle.category) {
                                    VehicleCategory.BIKE -> Icons.Default.TwoWheeler
                                    VehicleCategory.AUTO -> Icons.Default.ElectricRickshaw
                                    VehicleCategory.CAB -> Icons.Default.DirectionsCar
                                },
                                contentDescription = vehicle.nameHindi,
                                tint = GrudexBlack,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = vehicle.nameHindi,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrudexBlack
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${vehicle.etaMinutes} min door",
                                    fontSize = 12.sp,
                                    color = GrudexGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "${vehicle.subtitleHindi} (${vehicle.capacityHindi})",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Anumanit: Rs. ${(vehicle.fare - 5).coerceAtLeast(20)} - ${vehicle.fare + 5}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GrudexDark
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${vehicle.fare}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )
                            Text(
                                text = "₹${vehicle.originalFare}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bhugtan Madhyam (Payment):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrudexBlack
                )

                Row {
                    PaymentType.values().forEach { payment ->
                        val isChosen = uiState.selectedPaymentType == payment
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isChosen) GrudexBlack else GrudexLightGrey,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onPaymentSelected(payment) }
                        ) {
                            Text(
                                text = when (payment) {
                                    PaymentType.CASH -> "Nagad"
                                    PaymentType.UPI -> "UPI"
                                    PaymentType.WALLET -> "Batua"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isChosen) Color.White else GrudexBlack,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Book Ride Button
            Button(
                onClick = onBookRide,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrudexYellow,
                    contentColor = GrudexBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("book_ride_button")
            ) {
                Text(
                    text = "${when (uiState.selectedVehicleCategory) {
                        VehicleCategory.BIKE -> "Grudex Bike"
                        VehicleCategory.AUTO -> "Grudex Auto"
                        VehicleCategory.CAB -> "Grudex Mini Cab"
                    }} Book Karein • Anumanit Rs. ${uiState.currentEstimatedFareText}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun SearchingDriverCard(
    uiState: GrudexUiState,
    onCancelRide: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "search_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("searching_driver_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(pulseScale)
                    .background(GrudexYellow.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(GrudexYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = GrudexBlack,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kareebi Driver Dhoondh Rahe Hain...",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = GrudexBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sabse achha captain khoja ja raha hai",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                color = GrudexYellow,
                trackColor = GrudexLightGrey,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onCancelRide,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("cancel_search_button")
            ) {
                Text(
                    text = "Ride Cancel Karein",
                    color = GrudexRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DriverDetailsCard(
    uiState: GrudexUiState,
    isArriving: Boolean,
    onCallDriver: () -> Unit,
    onCancelRide: () -> Unit
) {
    val driver = uiState.activeRide?.driver

    Card(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("driver_details_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArriving) "Driver Aa Rahe Hain" else "Ride Shuru Ho Gayi Hai",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack
                    )
                    Text(
                        text = "${driver?.etaMinutes ?: 3} minute me pickup par pahunchenge",
                        fontSize = 12.sp,
                        color = GrudexGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // OTP to Start Ride Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GrudexYellowContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrudexYellow)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "START OTP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexDark
                        )
                        Text(
                            text = driver?.startOtp ?: "4829",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Driver Profile Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver Avatar
                Image(
                    painter = painterResource(id = R.drawable.ic_driver_avatar),
                    contentDescription = driver?.name ?: "Driver",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.dp, GrudexYellow, CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driver?.name ?: "Ramesh Kumar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = GrudexYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${driver?.rating ?: 4.9} (${driver?.totalRides ?: 1420} rides)",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "${driver?.vehicleModel} • ${driver?.vehicleNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GrudexDark
                    )
                }

                // Call Driver Button
                Surface(
                    shape = CircleShape,
                    color = GrudexGreen,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable { onCallDriver() }
                        .testTag("call_driver_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Driver ko Call Karein",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fare & Payment Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrudexLightGrey, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kul Kiraya: ₹${uiState.activeRide?.vehicle?.fare ?: 35}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )
                Text(
                    text = "Madhyam: ${uiState.activeRide?.paymentType?.titleHindi ?: "UPI"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onCancelRide,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("driver_cancel_ride_button")
            ) {
                Text(
                    text = "Ride Cancel Karein",
                    color = GrudexRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LiveRideTrackingCard(
    uiState: GrudexUiState,
    onCallDriver: () -> Unit,
    onTriggerSos: () -> Unit
) {
    val activeRide = uiState.activeRide

    Card(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_ride_tracking_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ride Chalu Hai (Live)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexGreen
                    )
                    Text(
                        text = "Gantavya: ${activeRide?.drop?.titleHindi ?: "Ghar"}",
                        fontSize = 13.sp,
                        color = GrudexBlack,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GrudexRed,
                    modifier = Modifier
                        .clickable { onTriggerSos() }
                        .testTag("live_ride_sos_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SOS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar of Trip
            val progress = activeRide?.progress ?: 0.3f
            LinearProgressIndicator(
                progress = { progress },
                color = GrudexYellow,
                trackColor = GrudexLightGrey,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${((1f - progress) * (activeRide?.vehicle?.etaMinutes ?: 10)).toInt() + 1} min baaki",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )
                Text(
                    text = "${(progress * 100).toInt()}% poora",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Driver & Vehicle summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_driver_avatar),
                    contentDescription = "Driver",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeRide?.driver?.name ?: "Ramesh Kumar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )
                    Text(
                        text = "${activeRide?.driver?.vehicleModel} • ${activeRide?.driver?.vehicleNumber}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = onCallDriver,
                    modifier = Modifier.testTag("live_call_driver_button")
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = GrudexGreen)
                }
            }
        }
    }
}

@Composable
private fun SearchDestinationModal(
    popularLocations: List<LocationItem>,
    selectedCategory: ShopCategory?,
    nearbyShops: List<NearbyShop>,
    onSelectLocation: (LocationItem) -> Unit,
    onCategoryClick: (ShopCategory) -> Unit,
    onBookRideToShop: (NearbyShop) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredLocations = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            popularLocations
        } else {
            popularLocations.filter {
                it.titleHindi.contains(searchQuery, ignoreCase = true) ||
                        it.subtitleHindi.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredShops = remember(searchQuery, nearbyShops) {
        if (searchQuery.isBlank()) {
            nearbyShops
        } else {
            nearbyShops.filter {
                it.nameHindi.contains(searchQuery, ignoreCase = true) ||
                        it.specialtyHindi.contains(searchQuery, ignoreCase = true) ||
                        it.addressHindi.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_destination_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_search_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Band Karein")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Kahan Jana Hai?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Gantavya ya dukaan khojein") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrudexDark) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrudexYellow,
                    focusedLabelColor = GrudexBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_query_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // "Raste me kya hai?" Category Filter Row
            RouteShopsCategoryBar(
                selectedCategory = selectedCategory,
                onCategoryClick = onCategoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("modal_route_shops_category_bar")
            )

            if (filteredShops.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${selectedCategory?.emoji ?: "📍"} Aas-Paas Ki Dukane (1-2 km ke andar):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(filteredShops) { shop ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GrudexLightGrey),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("modal_shop_item_${shop.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = GrudexYellow,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = shop.category.emoji, fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = shop.nameHindi,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GrudexBlack,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = shop.distanceTextHindi,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1976D2)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "⭐ ${shop.rating}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrudexBlack
                                        )
                                    }
                                    Text(
                                        text = shop.specialtyHindi,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onBookRideToShop(shop)
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GrudexYellow,
                                        contentColor = GrudexBlack
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("modal_book_shop_${shop.id}")
                                ) {
                                    Text(
                                        text = "Wahan Chalo",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Lokpriya / Suvidhajanak Sthal:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                items(filteredLocations) { loc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLocation(loc) }
                            .padding(vertical = 12.dp)
                            .testTag("search_result_${loc.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GrudexLightGrey, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GrudexBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = loc.titleHindi,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )
                            Text(
                                text = loc.subtitleHindi,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${loc.distanceKm} km",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexDark
                            )
                            val estMin = maxOf(30, (20 + (loc.distanceKm - 1f).coerceAtLeast(0f) * 8).toInt() - 5)
                            val estMax = estMin + 10
                            Text(
                                text = "Anumanit ~₹$estMin-$estMax",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
