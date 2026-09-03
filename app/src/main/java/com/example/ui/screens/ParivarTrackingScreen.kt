package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FamilyMember
import com.example.ui.components.GrudexMapCanvas
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
fun ParivarTrackingScreen(
    uiState: GrudexUiState,
    onToggleAutoShare: () -> Unit,
    onAddMember: (name: String, relation: String, phone: String) -> Unit,
    onRemoveMember: (id: String) -> Unit,
    onTriggerSos: () -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Top Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GrudexBlack)
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Parivar Tracking",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GrudexYellow
                                )
                                Text(
                                    text = "Apno Ki Suraksha, Har Safar Me",
                                    fontSize = 13.sp,
                                    color = Color.LightGray
                                )
                            }

                            // Emergency SOS Button in Header
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = GrudexRed,
                                modifier = Modifier
                                    .clickable { onTriggerSos() }
                                    .testTag("parivar_sos_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "SOS",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SOS",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Auto Share Live Location Toggle Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                                .background(GrudexGreen.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShareLocation,
                                contentDescription = null,
                                tint = GrudexGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Share Live Location",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )
                            Text(
                                text = "Ride shuru hote hi Parivar ko live location bhejein",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = uiState.isFamilyLiveSharingActive,
                            onCheckedChange = { onToggleAutoShare() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GrudexBlack,
                                checkedTrackColor = GrudexYellow
                            ),
                            modifier = Modifier.testTag("parivar_auto_share_switch")
                        )
                    }
                }
            }

            // Live Family Radar Preview Card (Special Feature)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Live Status Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GrudexYellowContainer)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DirectionsBike,
                                contentDescription = null,
                                tint = GrudexBlack,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rahul Ghar se 2km door hai, Bike par hai (32 km/h)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )
                        }

                        // Map Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            GrudexMapCanvas(
                                modifier = Modifier.fillMaxSize(),
                                showFamilyMarker = true,
                                familyStatusText = "Rahul (Live): Bike par"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GrudexGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "GPS Sthiti: Sattyapit (Accurate)",
                                    fontSize = 11.sp,
                                    color = GrudexGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Abhi Update Hua",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Family Members Title Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Parivar Ke Sadasya (${uiState.familyMembers.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )

                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_member_text_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GrudexDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Naya Jodein",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexDark
                        )
                    }
                }
            }

            // List of Family Members
            items(uiState.familyMembers) { member ->
                FamilyMemberCard(
                    member = member,
                    onCallMember = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${member.phone}")
                        }
                        context.startActivity(intent)
                    },
                    onRemove = { onRemoveMember(member.id) }
                )
            }

            // Bottom Info Note
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Grudex Suraksha Guarantee: Aapka data 100% surakshit aur private rehta hai.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Add Member Floating Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = GrudexYellow,
            contentColor = GrudexBlack,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp)
                .testTag("add_family_member_fab")
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sadasya Jodein", fontWeight = FontWeight.Bold)
            }
        }

        // Add Member Modal Dialog
        if (showAddDialog) {
            AddFamilyMemberDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, relation, phone ->
                    onAddMember(name, relation, phone)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun FamilyMemberCard(
    member: FamilyMember,
    onCallMember: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("family_card_${member.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Member Initials/Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(GrudexYellow, CircleShape)
                    .border(2.dp, GrudexBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.nameHindi.take(1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrudexBlack
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.nameHindi,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GrudexLightGrey
                    ) {
                        Text(
                            text = member.relationHindi,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = member.phone,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sthiti: ${member.lastLocationTextHindi}",
                        fontSize = 11.sp,
                        color = GrudexGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${member.batteryPercent}%",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // Action Buttons: Call & Delete
            IconButton(
                onClick = onCallMember,
                modifier = Modifier.testTag("call_member_${member.id}")
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = GrudexGreen)
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.testTag("remove_member_${member.id}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Hatao", tint = Color.LightGray)
            }
        }
    }
}

@Composable
private fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, relation: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Maa") }
    var phone by remember { mutableStateOf("") }

    val commonRelations = listOf("Maa", "Papa", "Bhai", "Behen", "Patni", "Dost")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Parivar Ka Naya Sadasya Jodein",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Aapki har ride me inke sath live location sajha hogi",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Naam (e.g. Maa, Papa)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GrudexYellow,
                        focusedLabelColor = GrudexBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_member_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (+91)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GrudexYellow,
                        focusedLabelColor = GrudexBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_member_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Rishta (Relation):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrudexBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonRelations.take(4).forEach { rel ->
                        val isSelected = relation == rel
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GrudexYellow else GrudexLightGrey,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { relation = rel }
                        ) {
                            Text(
                                text = rel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = GrudexBlack,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            onAdd(name, relation, phone)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrudexYellow,
                        contentColor = GrudexBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dialog_add_member_submit")
                ) {
                    Text(
                        text = "Surakshit Jodein",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Radd Karein", color = Color.Gray)
                }
            }
        }
    }
}
