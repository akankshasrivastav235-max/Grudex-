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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DriverTripPhase
import com.example.model.RideStatus
import com.example.ui.components.GrudexMapCanvas
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexRed
import com.example.ui.theme.GrudexYellow
import com.example.ui.theme.GrudexYellowContainer
import com.example.viewmodel.GrudexUiState

@Composable
fun DriverHomeScreen(
    uiState: GrudexUiState,
    onToggleDuty: () -> Unit,
    onAcceptBooking: () -> Unit,
    onRejectBooking: () -> Unit,
    onStartRide: () -> Unit,
    onCompleteRide: () -> Unit,
    onDismissCompletedDialog: () -> Unit,
    onTestBooking: () -> Unit,
    onSwitchToPassenger: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // Pulsing animation for active incoming alert
    val infiniteTransition = rememberInfiniteTransition(label = "driver_booking_alert")
    val alertScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_scale"
    )

    val isOnline = uiState.driverDuty.isDutyOn
    var showReviewsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        // Top Header: Captain Branding, Driver Info, Mode Switcher
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrudexBlack)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(GrudexYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = GrudexBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Grudex Captain",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexYellow
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isOnline) GrudexGreen else Color.Gray
                            ) {
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${uiState.driverName} • ${uiState.driverVehicleNumber}",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GrudexYellow,
                                modifier = Modifier
                                    .clickable { showReviewsDialog = true }
                                    .testTag("driver_top_rating_chip")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${uiState.driverAverageRating} ★",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GrudexBlack
                                    )
                                }
                            }
                        }
                    }
                }

                // Actions: Passenger App mode toggle & Logout
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF2C2C2E),
                        modifier = Modifier
                            .clickable { onSwitchToPassenger() }
                            .testTag("driver_to_sawari_pill")
                    ) {
                        Text(
                            text = "Sawari Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.size(36.dp).testTag("driver_logout_btn")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Logout",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Screen 2 Requirement: Prominent Switch "Online / Offline"
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOnline) GrudexYellowContainer else Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("driver_duty_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                if (isOnline) GrudexGreen.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (isOnline) GrudexGreen else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isOnline) "Duty Chalu Hai (Online)" else "Duty Band Hai (Offline)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isOnline) GrudexBlack else Color.DarkGray
                        )
                        Text(
                            text = if (isOnline) "Firebase 'bookings' sun rahe hain..." else "Sawari paane ke liye switch on karein",
                            fontSize = 12.sp,
                            color = if (isOnline) Color(0xFF333333) else Color.Gray
                        )
                    }
                }

                // Switch Online / Offline
                Switch(
                    checked = isOnline,
                    onCheckedChange = { onToggleDuty() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GrudexBlack,
                        checkedTrackColor = GrudexYellow,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    ),
                    modifier = Modifier.testTag("driver_duty_switch")
                )
            }
        }

        // Daily Earnings & Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Aaj Ki Kamai", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "₹${uiState.driverDuty.earningsToday}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexGreen
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Kul Rides", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${uiState.driverDuty.ridesToday} Rides",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { showReviewsDialog = true }
                    .testTag("driver_rating_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Average Rating", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.driverTotalRatingsCount}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexBlack
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${uiState.driverAverageRating} ★",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack
                        )
                    }
                    Text(
                        text = "Firebase 'ratings'",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00875A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Screen 2: Interactive Map Area & Overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Live Map Canvas
            GrudexMapCanvas(
                modifier = Modifier.fillMaxSize(),
                rideStatus = when {
                    uiState.driverTripPhase == DriverTripPhase.TRIP_IN_PROGRESS -> RideStatus.RIDE_IN_PROGRESS
                    uiState.acceptedBooking != null -> RideStatus.DRIVER_ON_WAY
                    else -> RideStatus.IDLE
                },
                tripProgress = uiState.driverTripProgress
            )

            // Test Booking Quick Action Floating Pill (for seamless local / Firebase testing)
            if (isOnline && uiState.incomingBooking == null && uiState.acceptedBooking == null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GrudexBlack.copy(alpha = 0.85f),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .clickable { onTestBooking() }
                        .testTag("driver_test_booking_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Science,
                            contentDescription = null,
                            tint = GrudexYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🧪 Test Firebase Booking Bhejein",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Screen 2 Requirement 5: BIG POPUP on New Booking from Firebase
            // Showing: Pickup Location, Drop Location, Customer Name, Fare, Accept & Reject buttons
            val booking = uiState.incomingBooking
            if (booking != null) {
                Card(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .testTag("incoming_booking_big_popup")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        // Alert Header with Sound/Vibration animation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .scale(alertScale)
                                        .background(GrudexYellow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = GrudexBlack,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🚨 Nayi Booking Aayi Hai!",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GrudexBlack
                                    )
                                    Text(
                                        text = "Firebase 'bookings' pending",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            // Big Fare Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GrudexYellowContainer
                            ) {
                                Text(
                                    text = "₹${booking.fare}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GrudexBlack,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Customer Name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GrudexBlack)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${booking.customerName} • 4.9 ★",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${booking.distanceKm} km door)",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Locations: Pickup and Drop
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Pickup Location
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(GrudexGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Pickup Location (Sawari Yahan Hai)",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = booking.pickupLocation,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrudexBlack
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Drop Location
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(GrudexRed, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Drop Location (Gantavya)",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = booking.dropLocation,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrudexBlack
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Requirement 5: 2 buttons - Accept and Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRejectBooking,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("driver_reject_booking_button")
                            ) {
                                Text(
                                    text = "Mana Karein (Reject)",
                                    color = GrudexRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = onAcceptBooking,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrudexYellow,
                                    contentColor = GrudexBlack
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("driver_accept_booking_button")
                            ) {
                                Text(
                                    text = "Sweekar (Accept)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Screen 2 Requirement 6: On Accept: Show customer location on map & Start Ride button
            val accepted = uiState.acceptedBooking
            if (accepted != null) {
                Card(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .testTag("accepted_ride_bottom_sheet")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Customer Information & Call button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(GrudexYellow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GrudexBlack,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = accepted.customerName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GrudexBlack
                                    )
                                    Text(
                                        text = "Kiraya: ₹${accepted.fare} • Nagad / UPI",
                                        fontSize = 12.sp,
                                        color = GrudexGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Call Customer Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GrudexGreen.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .clickable {
                                        try {
                                            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${accepted.customerPhone}")
                                            }
                                            context.startActivity(callIntent)
                                        } catch (_: Exception) {}
                                    }
                                    .testTag("driver_call_customer_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Call,
                                        contentDescription = "Call Customer",
                                        tint = GrudexGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Call",
                                        fontWeight = FontWeight.Bold,
                                        color = GrudexGreen,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Destination / Pickup Address
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8F8FA), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            if (uiState.driverTripPhase == DriverTripPhase.TRIP_IN_PROGRESS) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(GrudexRed, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Drop: ${accepted.dropLocation}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GrudexBlack
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { uiState.driverTripProgress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = GrudexYellow,
                                    trackColor = Color.LightGray
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(GrudexGreen, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pickup: ${accepted.pickupLocation}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GrudexBlack
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(GrudexRed, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Drop: ${accepted.dropLocation}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Start Ride OR Complete Ride
                        if (uiState.driverTripPhase == DriverTripPhase.TRIP_IN_PROGRESS) {
                            Button(
                                onClick = onCompleteRide,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrudexGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("driver_complete_ride_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ride Samapt Karein & ₹${accepted.fare} Lein",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        } else {
                            // Requirement 6: Show Start Ride button
                            Button(
                                onClick = onStartRide,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrudexYellow,
                                    contentColor = GrudexBlack
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("driver_start_ride_button")
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Ride (Ride Shuru Karein)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Trip Completion Celebration Dialog
    if (uiState.showRideCompletedDialog) {
        Dialog(onDismissRequest = onDismissCompletedDialog) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("ride_completed_celebration_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(GrudexGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ride Safaltapoorvak Poori Hui!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "₹${uiState.lastCompletedFare} Kiraya Prapt Hua",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Aapki kamai me jod diya gaya hai.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDismissCompletedDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_completed_dialog_button")
                    ) {
                        Text(
                            text = "Agli Sawari Ke Liye Taiyar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Driver Reviews & Ratings Dialog from Firebase
        if (showReviewsDialog) {
            AlertDialog(
                onDismissRequest = { showReviewsDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showReviewsDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Theek Hai", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = GrudexYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sawari Ratings & Reviews",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = GrudexBlack
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Average rating summary box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9F9FB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Average Rating",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${uiState.driverAverageRating}",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GrudexBlack
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "★ / 5.0",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrudexYellow
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GrudexGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${uiState.driverTotalRatingsCount} Sawari Reviews",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GrudexGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Haal Hi Ke Feedback (Firebase 'ratings')",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexBlack
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (uiState.driverRecentReviews.isEmpty()) {
                            Text(
                                text = "Abhi tak koi comment nahi mila. Naye rides complete karne par yahan live dikhai dega.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                uiState.driverRecentReviews.take(4).forEach { review ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF4F4F6),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = review.customerName.ifBlank { "Sawari" },
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GrudexBlack
                                                )
                                                Row {
                                                    repeat(review.stars) {
                                                        Icon(
                                                            Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = GrudexYellow,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            if (review.comment.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = "\"${review.comment}\"",
                                                    fontSize = 12.sp,
                                                    color = GrudexDark
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
