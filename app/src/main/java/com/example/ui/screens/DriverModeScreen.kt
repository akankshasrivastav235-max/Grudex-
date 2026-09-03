package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DriverTripPhase
import com.example.model.RideStatus
import com.example.ui.components.GrudexMapCanvas
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexRed
import com.example.ui.theme.GrudexYellow
import com.example.ui.theme.GrudexYellowContainer
import com.example.viewmodel.GrudexUiState

@Composable
fun DriverModeScreen(
    uiState: GrudexUiState,
    onToggleDuty: () -> Unit,
    onAcceptRide: () -> Unit,
    onDeclineRide: () -> Unit,
    onOtpChange: (String) -> Unit,
    onVerifyOtpAndStart: () -> Unit,
    onCompleteTripAndCollect: () -> Unit,
    onSwitchToPassenger: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "driver_alert")
    val alertScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        // Top Captain Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrudexBlack)
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSwitchToPassenger,
                        modifier = Modifier.testTag("driver_back_to_passenger")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Sawari Mode",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Grudex Captain",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexYellow
                        )
                        Text(
                            text = "Driver Partner App",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }

                // Switch back to Passenger Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.DarkGray,
                    modifier = Modifier
                        .clickable { onSwitchToPassenger() }
                        .testTag("switch_to_passenger_pill")
                ) {
                    Text(
                        text = "Sawari Banein",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Duty Toggle Card: "Duty Chalu Karein" / "Duty Band Karein"
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
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
                                if (uiState.driverDuty.isDutyOn) GrudexGreen.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (uiState.driverDuty.isDutyOn) GrudexGreen else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (uiState.driverDuty.isDutyOn) "Duty Chalu Hai (Online)" else "Duty Band Hai (Offline)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (uiState.driverDuty.isDutyOn) GrudexGreen else GrudexBlack
                        )
                        Text(
                            text = if (uiState.driverDuty.isDutyOn) "Nayi sawari khoji ja rahi hai..." else "Sawari paane ke liye duty chalu karein",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Switch(
                    checked = uiState.driverDuty.isDutyOn,
                    onCheckedChange = { onToggleDuty() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GrudexBlack,
                        checkedTrackColor = GrudexYellow
                    ),
                    modifier = Modifier.testTag("driver_duty_switch")
                )
            }
        }

        // Driver Earnings & Stats Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Aaj Ki Kamai", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${uiState.driverDuty.earningsToday}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexGreen
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Aaj Ki Rides", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.driverDuty.ridesToday} Rides",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Rating", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = GrudexYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${uiState.driverDuty.rating}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic State Area: Incoming Ride Alert, Navigation, OTP Entry, or Map
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Live Map in background
            GrudexMapCanvas(
                modifier = Modifier.fillMaxSize(),
                rideStatus = if (uiState.driverTripPhase == DriverTripPhase.TRIP_IN_PROGRESS) RideStatus.RIDE_IN_PROGRESS else RideStatus.IDLE,
                tripProgress = uiState.driverTripProgress
            )

            // Overlays based on driverTripPhase
            when (uiState.driverTripPhase) {
                DriverTripPhase.INCOMING_REQUEST -> {
                    // "Nayi Sawari Aayi Hai!" Popup Card
                    val req = uiState.currentDriverRequest
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .testTag("incoming_ride_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Alert Header with Sound/Alert animation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .scale(alertScale)
                                            .background(GrudexYellow, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = GrudexBlack,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Nayi Sawari Aayi Hai!",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GrudexBlack
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GrudexYellowContainer
                                ) {
                                    Text(
                                        text = "₹${req?.estimatedFare ?: 52}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GrudexBlack,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Passenger details
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${req?.passengerName} • ${req?.passengerRating} ★",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrudexDark
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "(${req?.distanceKm} km door)",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pickup & Drop
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(GrudexGreen, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pickup: ${req?.pickupHindi}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GrudexBlack
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(GrudexRed, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Drop: ${req?.dropHindi}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GrudexBlack
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action buttons: Sweekar Karein (Accept) / Mana Karein (Decline)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDeclineRide,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("driver_decline_ride")
                                ) {
                                    Text("Mana Karein", color = GrudexRed, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = onAcceptRide,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GrudexYellow,
                                        contentColor = GrudexBlack
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("driver_accept_ride")
                                ) {
                                    Text("Sweekar Karein", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                DriverTripPhase.NAVIGATING_TO_PICKUP -> {
                    // Navigating to passenger pickup
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .testTag("driver_navigating_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Sawari Ke Paas Jayein",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )
                            Text(
                                text = "Pickup: ${uiState.currentDriverRequest?.pickupHindi ?: "Rajiv Chowk"}",
                                fontSize = 13.sp,
                                color = GrudexDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pahunchne me 2 min baaki",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrudexGreen
                                )
                                Text(
                                    text = "Sawari: ${uiState.currentDriverRequest?.passengerName}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                DriverTripPhase.ARRIVED_ENTER_OTP -> {
                    // "OTP Dalein" Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .testTag("driver_enter_otp_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ride Shuru Karne Ke Liye OTP Dalein",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Sawari (${uiState.currentDriverRequest?.passengerName}) se 4-ankon ka OTP lein",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = uiState.driverOtpInput,
                                onValueChange = onOtpChange,
                                label = { Text("4-Digit OTP") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = uiState.driverOtpError,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrudexYellow,
                                    focusedLabelColor = GrudexBlack
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("driver_otp_input")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Demo OTP: 4829",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GrudexGreen
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onVerifyOtpAndStart,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrudexYellow,
                                    contentColor = GrudexBlack
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("driver_start_ride_button")
                            ) {
                                Text(
                                    text = "Ride Chalu Karein",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                DriverTripPhase.TRIP_IN_PROGRESS -> {
                    // In Progress Ride
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .testTag("driver_in_progress_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Ride Chalu Hai (Drop Par Jayein)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexGreen
                            )
                            Text(
                                text = "Gantavya: ${uiState.currentDriverRequest?.dropHindi}",
                                fontSize = 13.sp,
                                color = GrudexDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { uiState.driverTripProgress },
                                color = GrudexYellow,
                                trackColor = GrudexLightGrey,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Safar Pragati: ${(uiState.driverTripProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                DriverTripPhase.COLLECT_PAYMENT -> {
                    // Collect Payment & End Ride
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .testTag("driver_collect_payment_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GrudexGreen,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Gantavya Par Pahunch Gaye!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Sawari se kiraya prapt karein:",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "₹${uiState.currentDriverRequest?.estimatedFare ?: 52}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = onCompleteTripAndCollect,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrudexGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("driver_collect_button")
                            ) {
                                Text(
                                    text = "Bhugtan Prapt Hua (Ride Samapt)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                DriverTripPhase.NONE -> {
                    // Offline / Idle helper note
                    if (!uiState.driverDuty.isDutyOn) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GrudexBlack.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Sawari paane ke liye upar 'Duty Chalu' karein",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
