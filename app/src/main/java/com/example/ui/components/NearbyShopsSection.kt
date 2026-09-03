package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NearbyShop
import com.example.model.ShopCategory
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexYellow

/**
 * "Raste me kya hai?" - Aas-Paas Ki Dukane category selection bar.
 * 4 Buttons: Mithai Dukaan 🍬, Chai & Nashta ☕, Medical Store 💊, Kirana Store 🛒
 */
@Composable
fun RouteShopsCategoryBar(
    selectedCategory: ShopCategory?,
    onCategoryClick: (ShopCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Raste me kya hai?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrudexBlack
                )
                Text(
                    text = "Aas-paas ki dukane (1-2 km ke andar)",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            if (selectedCategory != null) {
                Surface(
                    color = GrudexYellow.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "Map par dikh raha hai",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrudexBlack,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4 Category Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShopCategory.values().forEach { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) GrudexBlack else GrudexLightGrey,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) GrudexYellow else Color(0xFFE0E0E0)
                    ),
                    modifier = Modifier
                        .clickable { onCategoryClick(category) }
                        .testTag("category_btn_${category.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.emoji,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.titleHindi,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) GrudexYellow else GrudexBlack
                        )
                    }
                }
            }
        }
    }
}

/**
 * Horizontal Carousel of nearby shops when a category is selected.
 */
@Composable
fun NearbyShopsCarousel(
    shops: List<NearbyShop>,
    selectedShop: NearbyShop?,
    onShopClick: (NearbyShop) -> Unit,
    onBookRide: (NearbyShop) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shops.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shops) { shop ->
                val isSelected = selectedShop?.id == shop.id
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFFFFDE7) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) GrudexYellow else Color(0xFFE5E5E5)
                    ),
                    elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp),
                    modifier = Modifier
                        .width(260.dp)
                        .clickable { onShopClick(shop) }
                        .testTag("shop_card_${shop.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GrudexYellow
                            ) {
                                Text(
                                    text = shop.distanceTextHindi,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrudexBlack,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA000),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${shop.rating}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrudexBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = shop.nameHindi,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrudexBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = shop.addressHindi,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // "Wahan Chalo" Button
                        Button(
                            onClick = { onBookRide(shop) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GrudexYellow,
                                contentColor = GrudexBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("wahan_chalo_mini_btn_${shop.id}")
                        ) {
                            Icon(
                                Icons.Default.DirectionsBike,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = GrudexBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Wahan Chalo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Detailed bottom / popup card when a specific shop or map pin is clicked.
 * Shows shop name, exact distance, rating, specialty, and prominent "Wahan Chalo" button.
 */
@Composable
fun SelectedShopDetailCard(
    shop: NearbyShop,
    onClose: () -> Unit,
    onBookRide: (NearbyShop) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("selected_shop_detail_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header Row with Category, Distance & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GrudexBlack
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shop.category.emoji,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = shop.category.titleHindi,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GrudexYellow.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = GrudexBlack,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = shop.distanceTextHindi,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrudexBlack
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("close_shop_detail_btn")
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Band karein",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shop Name
            Text(
                text = shop.nameHindi,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GrudexBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${shop.rating}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "(${shop.reviewCount} Google Samiksha)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (shop.isOpenNow) "Abhi Khula Hai" else "Band Hai",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (shop.isOpenNow) GrudexGreen else Color.Red
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Navigation,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = shop.addressHindi,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specialty / Item info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GrudexLightGrey,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = shop.specialtyHindi,
                        fontSize = 12.sp,
                        color = GrudexBlack,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Wahan Chalo" Primary Ride Booking Button
            Button(
                onClick = { onBookRide(shop) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrudexYellow,
                    contentColor = GrudexBlack
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("wahan_chalo_btn_${shop.id}")
            ) {
                Icon(
                    Icons.Default.DirectionsBike,
                    contentDescription = null,
                    tint = GrudexBlack,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Wahan Chalo (Ride Book Karein)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrudexBlack
                )
            }
        }
    }
}
