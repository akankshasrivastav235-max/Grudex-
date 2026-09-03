package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
fun AuthScreen(
    onLoginSuccess: (phone: String, name: String) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Mobile & Name, 2: OTP
    var phone by remember { mutableStateOf("9876543210") }
    var name by remember { mutableStateOf("Rahul Sharma") }
    var otp by remember { mutableStateOf("4829") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        // Top Yellow Banner with Brand & Mascot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.38f)
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
                        .size(105.dp)
                        .testTag("auth_official_logo")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_grudex_logo),
                        contentDescription = "Grudex Official Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "GRUDEX",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = GrudexBlack
                )

                Text(
                    text = "Bharat Ki Apni Bike Taxi • Lucknow",
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
                .weight(0.62f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step == 1) {
                    Text(
                        text = "Login Karein ya Naya Account Banayein",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Apna mobile number aur naam darj karein",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mobile Number Input
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = GrudexDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+91",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = GrudexBlack
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexYellow,
                            focusedLabelColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_phone_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Aapka Naam (Full Name)") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GrudexDark)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexYellow,
                            focusedLabelColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Get OTP Button
                    Button(
                        onClick = {
                            if (phone.isNotBlank()) {
                                step = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_get_otp_button")
                    ) {
                        Text(
                            text = "OTP Prapt Karein",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Demo Login Button
                    OutlinedButton(
                        onClick = {
                            onLoginSuccess(phone, name)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_demo_login_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GrudexGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Turant Demo Login Karein",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GrudexBlack
                        )
                    }
                } else {
                    // OTP Verification Step
                    Text(
                        text = "OTP Sattyapit Karein",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "+91 $phone par 4-digit code bheja gaya hai",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 4) otp = it },
                        label = { Text("4-Digit OTP Dalein") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GrudexDark)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrudexYellow,
                            focusedLabelColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_otp_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Demo ke liye OTP: 4829",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = GrudexGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onLoginSuccess(phone, name)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrudexYellow,
                            contentColor = GrudexBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_verify_otp_button")
                    ) {
                        Text(
                            text = "Aage Badhein",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { step = 1 },
                        modifier = Modifier.testTag("auth_change_number_button")
                    ) {
                        Text("Number Badlein", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
