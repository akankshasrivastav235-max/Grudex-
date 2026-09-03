package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KirayaCalculator
import com.example.model.KirayaSettings
import com.example.model.ZoneFareItem
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexBorder
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexRed
import com.example.ui.theme.GrudexYellow
import com.example.ui.theme.GrudexYellowContainer

@Composable
fun KirayaSettingScreen(
    currentSettings: KirayaSettings,
    onSaveGeneralSettings: (baseFare: Int, perKm: Int, nightCharge: Int, minFare: Int, nightSim: Boolean) -> Unit,
    onAddZone: (from: String, to: String, fare: Int) -> Unit,
    onDeleteZone: (zoneId: String) -> Unit,
    onResetToDefaults: () -> Unit,
    onBack: () -> Unit
) {
    // Local form state for general fare settings
    var baseFareText by remember(currentSettings.baseFare) {
        mutableStateOf(currentSettings.baseFare.toString())
    }
    var perKmText by remember(currentSettings.perKmCharge) {
        mutableStateOf(currentSettings.perKmCharge.toString())
    }
    var nightChargeText by remember(currentSettings.nightCharge) {
        mutableStateOf(currentSettings.nightCharge.toString())
    }
    var minFareText by remember(currentSettings.minimumFare) {
        mutableStateOf(currentSettings.minimumFare.toString())
    }
    var isNightSimActive by remember(currentSettings.isNightModeSimulation) {
        mutableStateOf(currentSettings.isNightModeSimulation)
    }

    // Local form state for adding new zone
    var newFromText by remember { mutableStateOf("") }
    var newToText by remember { mutableStateOf("") }
    var newFareText by remember { mutableStateOf("") }
    var newZoneError by remember { mutableStateOf<String?>(null) }

    // Live calculation demo for feedback
    val parsedBase = baseFareText.toIntOrNull() ?: currentSettings.baseFare
    val parsedPerKm = perKmText.toIntOrNull() ?: currentSettings.perKmCharge
    val parsedNight = nightChargeText.toIntOrNull() ?: currentSettings.nightCharge
    val parsedMin = minFareText.toIntOrNull() ?: currentSettings.minimumFare

    val previewSettings = currentSettings.copy(
        baseFare = parsedBase,
        perKmCharge = parsedPerKm,
        nightCharge = parsedNight,
        minimumFare = parsedMin,
        isNightModeSimulation = isNightSimActive
    )

    // Demo: Hazratganj to Alambagh (~9.5 km)
    val demoResult = remember(previewSettings) {
        KirayaCalculator.calculateBikeFare(
            pickupTitle = "Hazratganj",
            dropTitle = "Alambagh",
            distanceKm = 9.5f,
            settings = previewSettings
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("kiraya_setting_screen")
    ) {
        // --- Top Header ---
        item {
            Surface(
                color = GrudexBlack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("kiraya_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Peeche Jayein",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Kiraya Set Karo ⚙️",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GrudexYellow
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GrudexBlack,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Grudex Fare, Rates aur Zone Wise Kiraya Nirdharit Karein",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }

                    // Reset button
                    IconButton(
                        onClick = onResetToDefaults,
                        modifier = Modifier.testTag("reset_defaults_button")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Default Karein",
                            tint = GrudexYellow
                        )
                    }
                }
            }
        }

        // --- Live Preview of Anumanit Kiraya Banner ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GrudexYellowContainer),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("anumanit_kiraya_preview_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(GrudexYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "₹", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = GrudexBlack)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Customer Ko Live Dikhne Wala Kiraya:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GrudexDark
                        )
                        Text(
                            text = "Anumanit Kiraya: Rs. ${demoResult.estimateRangeText}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrudexBlack
                        )
                        Text(
                            text = "Udaharan (9.5 km): Base ₹$parsedBase + ₹$parsedPerKm/km ${if (demoResult.isNightChargeApplied) "+ Raat ₹$parsedNight" else ""}",
                            fontSize = 11.sp,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
        }

        // --- Section 1: Sadharan Kiraya Settings (General Rates) ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("general_fare_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = GrudexBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Mool Kiraya Niyam (Base Rules)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexBlack
                        )
                    }

                    Text(
                        text = "Sabhi sadharan rides ke liye yahi rate card lagoo hoga",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // 1. Base Kiraya (1 km tak)
                    SimpleFareInput(
                        label = "Base Kiraya: 1 km tak ka kiraya",
                        helper = "Pehle 1 kilometer ka nirdharit kiraya (Default: ₹20)",
                        prefix = "₹",
                        value = baseFareText,
                        onValueChange = { baseFareText = it.filter { ch -> ch.isDigit() } },
                        testTag = "input_base_fare"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Per KM Charge
                    SimpleFareInput(
                        label = "Per KM Charge (₹ / km)",
                        helper = "1 km ke baad har kilometer ka charge (Default: ₹8)",
                        prefix = "₹",
                        value = perKmText,
                        onValueChange = { perKmText = it.filter { ch -> ch.isDigit() } },
                        testTag = "input_per_km"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Raat ka Charge (9pm-6am)
                    SimpleFareInput(
                        label = "Raat ka Charge: 9 PM se 6 AM tak extra",
                        helper = "Raat me lagne wala atirikt charge (Default: +₹10)",
                        prefix = "+ ₹",
                        value = nightChargeText,
                        onValueChange = { nightChargeText = it.filter { ch -> ch.isDigit() } },
                        testTag = "input_night_charge"
                    )

                    // Night Simulation Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NightlightRound,
                                contentDescription = null,
                                tint = Color(0xFF5C6BC0),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Raat Charge Test / Force Simulate",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = GrudexBlack
                            )
                        }
                        Switch(
                            checked = isNightSimActive,
                            onCheckedChange = { isNightSimActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GrudexBlack,
                                checkedTrackColor = GrudexYellow
                            ),
                            modifier = Modifier.testTag("switch_night_simulate")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Minimum Kiraya
                    SimpleFareInput(
                        label = "Minimum Kiraya (Kam se Kam Kiraya)",
                        helper = "Kissi bhi ride ka kam se kam itna kiraya hoga (Default: ₹30)",
                        prefix = "₹",
                        value = minFareText,
                        onValueChange = { minFareText = it.filter { ch -> ch.isDigit() } },
                        testTag = "input_min_fare"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val base = baseFareText.toIntOrNull() ?: 20
                            val km = perKmText.toIntOrNull() ?: 8
                            val night = nightChargeText.toIntOrNull() ?: 10
                            val min = minFareText.toIntOrNull() ?: 30
                            onSaveGeneralSettings(base, km, night, min, isNightSimActive)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_general_fare_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ye Kiraya Niyam Save Karein 💾",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // --- Section 2: Alag Alag Jagah ka Alag Kiraya (Zone Wise Kiraya) ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("zone_fare_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Route,
                            contentDescription = null,
                            tint = GrudexBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Zone Wise Kiraya (Fixed Routes)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexBlack
                        )
                    }

                    Text(
                        text = "Agar sawari inme se kisi route par safar karti hai, to fixed kiraya lagta hai",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Table Header
                    Surface(
                        color = GrudexLightGrey,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kahan Se ➔ Kahan Tak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Fixed Kiraya",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )
                            Spacer(modifier = Modifier.width(36.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Existing Zone Items
                    if (currentSettings.zoneFares.isEmpty()) {
                        Text(
                            text = "Abhi koi fixed zone nahi joda gaya hai.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    } else {
                        currentSettings.zoneFares.forEach { zone ->
                            ZoneFareRowItem(
                                item = zone,
                                onDelete = { onDeleteZone(zone.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Form to Add New Zone
                    Surface(
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GrudexBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Naya Zone Jodein (+ Add New Route)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )
                            Text(
                                text = "Jaise: 'Hazratganj' se 'Charbagh' = Rs. 70",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            // "Kahan se" Input
                            OutlinedTextField(
                                value = newFromText,
                                onValueChange = {
                                    newFromText = it
                                    newZoneError = null
                                },
                                label = { Text("Kahan se (Pickup Area)") },
                                placeholder = { Text("e.g. Hazratganj") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrudexBlack,
                                    unfocusedBorderColor = GrudexBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_zone_from")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // "Kahan tak" Input
                            OutlinedTextField(
                                value = newToText,
                                onValueChange = {
                                    newToText = it
                                    newZoneError = null
                                },
                                label = { Text("Kahan tak (Drop Area)") },
                                placeholder = { Text("e.g. Charbagh") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrudexBlack,
                                    unfocusedBorderColor = GrudexBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_zone_to")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // "Kitna Kiraya" Input
                            OutlinedTextField(
                                value = newFareText,
                                onValueChange = {
                                    newFareText = it.filter { ch -> ch.isDigit() }
                                    newZoneError = null
                                },
                                label = { Text("Kitna Kiraya (₹ Fixed)") },
                                placeholder = { Text("e.g. 70") },
                                prefix = { Text("₹ ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrudexBlack,
                                    unfocusedBorderColor = GrudexBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_zone_fare")
                            )

                            newZoneError?.let { err ->
                                Text(
                                    text = err,
                                    color = GrudexRed,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val from = newFromText.trim()
                                    val to = newToText.trim()
                                    val fare = newFareText.toIntOrNull()
                                    if (from.isBlank() || to.isBlank() || fare == null || fare <= 0) {
                                        newZoneError = "Kripya sabhi fields ('Kahan se', 'Kahan tak', 'Kiraya') theek se bharein"
                                    } else {
                                        onAddZone(from, to, fare)
                                        newFromText = ""
                                        newToText = ""
                                        newFareText = ""
                                        newZoneError = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GrudexBlack,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("add_zone_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+ Naya Zone Jodein",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Spacer
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SimpleFareInput(
    label: String,
    helper: String,
    prefix: String,
    value: String,
    onValueChange: (String) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GrudexBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            prefix = {
                Text(
                    text = prefix,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrudexBlack,
                unfocusedBorderColor = GrudexBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
        Text(
            text = helper,
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp)
        )
    }
}

@Composable
private fun ZoneFareRowItem(
    item: ZoneFareItem,
    onDelete: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrudexBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .testTag("zone_row_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.fromHindi,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )
                    Text(
                        text = " ➔ ",
                        fontSize = 12.sp,
                        color = GrudexDark
                    )
                    Text(
                        text = item.toHindi,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )
                }
                Text(
                    text = "Fixed Route Fare",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GrudexYellow
            ) {
                Text(
                    text = "₹${item.fare}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrudexBlack,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(36.dp)
                    .testTag("delete_zone_${item.id}")
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hatao",
                    tint = GrudexRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
