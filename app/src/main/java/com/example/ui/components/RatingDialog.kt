package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexBorder
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexYellow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RatingDialog(
    bookingId: String = "",
    driverId: String = "",
    driverName: String = "Vikram Singh Sarthi",
    fare: Int = 35,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(5) }
    var commentText by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    val starLabels = mapOf(
        5 to "⭐⭐⭐⭐⭐ Bahut Badhiya!",
        4 to "⭐⭐⭐⭐ Achha Tha",
        3 to "⭐⭐⭐ Theek Tha",
        2 to "⭐⭐ Sudhar Ki Zaroorat",
        1 to "⭐ Anubhav Kharab Tha"
    )

    val quickCompliments = listOf(
        "Time par aaye",
        "Surakshit chalayi",
        "Helmet pehna tha",
        "Badhiya vyavahar",
        "Sahi rasta chuna"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("rating_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GrudexYellow.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "⭐ RIDE COMPLETED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrudexBlack,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // 2. Popup me likho: "Apni ride ko rate karein"
                Text(
                    text = "Apni ride ko rate karein",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrudexBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$driverName • ₹$fare",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 3. 5 Stars dikhao, user tap karke star select kar sake
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("star_rating_row"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        val isFilled = i <= selectedStars
                        Icon(
                            imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "$i Sitara",
                            tint = if (isFilled) GrudexYellow else Color(0xFFD0D5DD),
                            modifier = Modifier
                                .size(46.dp)
                                .clickable { selectedStars = i }
                                .padding(4.dp)
                                .testTag("star_rating_$i")
                        )
                    }
                }

                Text(
                    text = starLabels[selectedStars] ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedStars >= 4) Color(0xFF00875A) else if (selectedStars == 3) Color(0xFFD97706) else Color(0xFFDE350B),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick compliment chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickCompliments.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) GrudexYellow else GrudexLightGrey,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (isSelected) {
                                        selectedTags = selectedTags - tag
                                    } else {
                                        selectedTags = selectedTags + tag
                                        if (!commentText.contains(tag)) {
                                            commentText = if (commentText.isBlank()) tag else "$commentText, $tag"
                                        }
                                    }
                                }
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = GrudexBlack,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Neeche ek chhota sa comment box - "Kuch kehna chahenge?"
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(
                            text = "Kuch kehna chahenge?",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .testTag("rating_comment_box"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GrudexYellow,
                        unfocusedBorderColor = GrudexBorder,
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFAFAFA),
                        cursorColor = GrudexBlack
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 5. Submit button pe click karte hi rating Firebase me save ho jaye
                Button(
                    onClick = {
                        val finalComment = commentText.trim().ifBlank {
                            if (selectedTags.isNotEmpty()) selectedTags.joinToString(", ") else "Bahut accha safar!"
                        }
                        onSubmit(selectedStars, finalComment)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrudexYellow,
                        contentColor = GrudexBlack
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_rating_button")
                ) {
                    Text(
                        text = "Rating Submit Karein",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("skip_rating_button")
                ) {
                    Text(
                        text = "Abhi Nahi (Skip)",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
