package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RideRecord
import com.example.model.VehicleCategory
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexYellow
import com.example.viewmodel.GrudexUiState

@Composable
fun MeriRidesScreen(
    uiState: GrudexUiState,
    onRebookRide: (RideRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrudexBlack)
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column {
                Text(
                    text = "Meri Rides",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrudexYellow
                )
                Text(
                    text = "Aapke Sabhi Safar Ka Vivaran",
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            }
        }

        // Summary Statistics Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Kul Rides", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.pastRides.size}",
                        fontSize = 20.sp,
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
                val totalSpent = uiState.pastRides.sumOf { it.fare }
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Kul Kharch", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹$totalSpent",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack
                    )
                }
            }
        }

        // Rides List
        if (uiState.pastRides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Abhi koi ride nahi hai",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Apna pehla safar book karein!",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .padding(bottom = 80.dp)
            ) {
                items(uiState.pastRides) { record ->
                    RideHistoryCard(
                        record = record,
                        onRebook = { onRebookRide(record) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RideHistoryCard(
    record: RideRecord,
    onRebook: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("ride_record_${record.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Vehicle, Date, Fare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(GrudexYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (record.vehicleCategory) {
                                VehicleCategory.BIKE -> Icons.Default.DirectionsBike
                                VehicleCategory.AUTO -> Icons.Default.ElectricRickshaw
                                VehicleCategory.CAB -> Icons.Default.DirectionsCar
                            },
                            contentDescription = null,
                            tint = GrudexBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = record.vehicleNameHindi,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexBlack
                        )
                        Text(
                            text = "${record.dateHindi} • ${record.timeHindi}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${record.fare}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GrudexGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Safal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pickup & Drop locations
            Column(modifier = Modifier.fillMaxWidth()) {
                // Pickup
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(GrudexGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = record.pickupHindi,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GrudexBlack
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Drop
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFE53935), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = record.dropHindi,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GrudexBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Driver name, rating, and re-book button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Captain: ${record.driverName}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = GrudexYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${record.ratingGiven}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexDark
                        )
                    }
                }

                Button(
                    onClick = onRebook,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrudexYellow,
                        contentColor = GrudexBlack
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("rebook_ride_${record.id}")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Wapas Book Karein",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
