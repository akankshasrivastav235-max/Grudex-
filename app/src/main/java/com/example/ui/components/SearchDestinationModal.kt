package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocationItem
import com.example.model.NearbyShop
import com.example.model.ShopCategory
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexDark
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexLightGrey
import com.example.ui.theme.GrudexYellow
import kotlinx.coroutines.delay

/**
 * High-performance, debounced and controlled search component.
 * Ensures typing is instant and smooth without UI thread stutter or focus loss.
 */
@Composable
fun SearchDestinationModal(
    popularLocations: List<LocationItem>,
    selectedCategory: ShopCategory?,
    nearbyShops: List<NearbyShop>,
    onSelectLocation: (LocationItem) -> Unit,
    onCategoryClick: (ShopCategory) -> Unit,
    onBookRideToShop: (NearbyShop) -> Unit,
    onDismiss: () -> Unit
) {
    // 1. Controlled search input state (instant updates for typing responsiveness)
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // 2. Debounced query state (300ms delay to prevent recalculating and re-rendering on every single keystroke)
    var debouncedQuery by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Debounce effect: wait 300ms before triggering filtering
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            debouncedQuery = ""
        } else {
            delay(300L)
            debouncedQuery = searchQuery.trim()
        }
    }

    // Auto-request focus once on initial mount smoothly without interrupting user
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // 3. Memoized filtering using debouncedQuery (not every keystroke)
    val filteredLocations = remember(debouncedQuery, popularLocations) {
        if (debouncedQuery.isBlank()) {
            popularLocations
        } else {
            popularLocations.filter {
                it.titleHindi.contains(debouncedQuery, ignoreCase = true) ||
                        it.subtitleHindi.contains(debouncedQuery, ignoreCase = true)
            }
        }
    }

    val filteredShops = remember(debouncedQuery, nearbyShops) {
        if (debouncedQuery.isBlank()) {
            nearbyShops
        } else {
            nearbyShops.filter {
                it.nameHindi.contains(debouncedQuery, ignoreCase = true) ||
                        it.specialtyHindi.contains(debouncedQuery, ignoreCase = true) ||
                        it.addressHindi.contains(debouncedQuery, ignoreCase = true)
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar
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

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Dedicated search input with stable focus and instant typing
            SearchQueryInputBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClearQuery = {
                    searchQuery = ""
                    debouncedQuery = ""
                },
                focusRequester = focusRequester,
                onSearchAction = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
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

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Unified LazyColumn to eliminate layout thrashing from nested weighted LazyColumns
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Section 1: Nearby Route Shops
                if (filteredShops.isNotEmpty()) {
                    item(key = "header_shops") {
                        Column {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${selectedCategory?.emoji ?: "📍"} Aas-Paas Ki Dukane (1-2 km ke andar):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrudexBlack
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(filteredShops, key = { "shop_${it.id}" }) { shop ->
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

                // Section 2: Popular / Search Result Destinations
                item(key = "header_destinations") {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (debouncedQuery.isBlank()) "Lokpriya / Suvidhajanak Sthal:" else "Khoj Parinaam:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (filteredLocations.isEmpty() && debouncedQuery.isNotBlank()) {
                    item(key = "empty_location_results") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Koi sthal nahi mila \"$debouncedQuery\"",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(filteredLocations, key = { "loc_${it.id}" }) { loc ->
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
}

/**
 * Isolated controlled Search Input Composable to keep keyboard typing 100% smooth,
 * responsive, and prevent losing focus.
 */
@Composable
fun SearchQueryInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    focusRequester: FocusRequester,
    onSearchAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Gantavya ya dukaan khojein", color = Color.Gray) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Khojein",
                tint = GrudexDark
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier.testTag("clear_search_button")
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Saaf karein",
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GrudexYellow,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedLabelColor = GrudexBlack,
            cursorColor = GrudexBlack
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .testTag("search_query_input")
    )
}
