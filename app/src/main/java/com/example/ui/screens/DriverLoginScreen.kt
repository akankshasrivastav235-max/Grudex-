package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexYellow

@Composable
fun DriverLoginScreen(
    onLoginSuccess: (phone: String, name: String, vehicleType: String, vehicleNumber: String) -> Unit,
    onSwitchToCustomerApp: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Details & Mobile, 2: OTP
    var phone by remember { mutableStateOf("9876543210") }
    var name by remember { mutableStateOf("Vikram Singh Sarthi") }
    var vehicleNumber by remember { mutableStateOf("UP 32 BK 4082") }
    var selectedVehicleType by remember { mutableStateOf("Bike Taxi 🛵") }
    var otp by remember { mutableStateOf("4829") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        // Top Captain Header with Brand & Mascot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.36f)
                .background(GrudexYellow)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Grudex Brand Logo
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(90.dp)
                        .testTag("driver_auth_logo")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_grudex_logo),
                        contentDescription = "Grudex Driver Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "GRUDEX",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = GrudexBlack
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GrudexBlack
                    ) {
                        Text(
                            text = "CAPTAIN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexYellow,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Driver Partner App • Driver Login",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrudexDark
                )
            }
        }

        // Bottom Form Card
        Card(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.64f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step == 1) {
                    Text(
                        text = "Driver Sarthi Login",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Apna mobile number aur gadi ki jankari dalein",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mobile Number with +91 Prefix
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it.filter { char -> char.isDigit() } },
                        label = { Text("Driver Mobile Number") },
                        placeholder = { Text("9876543210") },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = GrudexBlack)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+91", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexBlack,
                            focusedLabelColor = GrudexBlack
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("driver_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Driver Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Driver Ka Poora Naam") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GrudexBlack)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexBlack,
                            focusedLabelColor = GrudexBlack
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("driver_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vehicle Number
                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it.uppercase() },
                        label = { Text("Gadi Ka Number (Vehicle No.)") },
                        leadingIcon = {
                            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = GrudexBlack)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexBlack,
                            focusedLabelColor = GrudexBlack
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("driver_vehicle_number_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle Type Selector Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Bike Taxi 🛵", "Auto 🛺", "Cab 🚕").forEach { type ->
                            val isSelected = selectedVehicleType == type
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GrudexYellow else Color(0xFFF1F1F4),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedVehicleType = type }
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = GrudexBlack,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button: Send OTP
                    Button(
                        onClick = {
                            if (phone.length == 10 && name.isNotBlank()) {
                                step = 2
                            }
                        },
                        enabled = phone.length == 10 && name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("driver_send_otp_button")
                    ) {
                        Text(
                            text = "OTP Bhejein (Next)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Direct Quick Login for Instant AI Studio Testing
                    TextButton(
                        onClick = {
                            onLoginSuccess(
                                if (phone.isNotBlank()) phone else "9876543210",
                                if (name.isNotBlank()) name else "Vikram Singh Sarthi",
                                selectedVehicleType,
                                if (vehicleNumber.isNotBlank()) vehicleNumber else "UP 32 BK 4082"
                            )
                        },
                        modifier = Modifier.testTag("driver_instant_login_button")
                    ) {
                        Text(
                            text = "⚡ Turant Duty Par Jayein (Quick Login)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Switch to Passenger App mode if user wants
                    TextButton(
                        onClick = onSwitchToCustomerApp,
                        modifier = Modifier.testTag("switch_to_customer_app_link")
                    ) {
                        Text(
                            text = "Sawari App (Customer Mode) Kholein",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    // Step 2: OTP Verification
                    Text(
                        text = "OTP Satyaapan (Verification)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "+91 $phone par bheja gaya 4-ankon ka OTP dalein",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 4) otp = it.filter { char -> char.isDigit() } },
                        label = { Text("4-Digit OTP") },
                        placeholder = { Text("4829") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GrudexBlack)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexBlack,
                            focusedLabelColor = GrudexBlack
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("driver_otp_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Demo OTP: 4829",
                        fontSize = 12.sp,
                        color = GrudexGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (otp.length == 4) {
                                onLoginSuccess(phone, name, selectedVehicleType, vehicleNumber)
                            }
                        },
                        enabled = otp.length == 4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("driver_verify_otp_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Satyaapit Karein & Duty Kholein",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { step = 1 },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Number Badlein (Back)", color = GrudexDark)
                    }
                }
            }
        }
    }
}
