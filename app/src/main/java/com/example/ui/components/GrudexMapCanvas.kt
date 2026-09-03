package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NearbyShop
import com.example.model.RideStatus
import com.example.ui.theme.GrudexBlack
import com.example.ui.theme.GrudexGreen
import com.example.ui.theme.GrudexRed
import com.example.ui.theme.GrudexYellow
import kotlin.math.sin

@Composable
fun GrudexMapCanvas(
    modifier: Modifier = Modifier,
    rideStatus: RideStatus = RideStatus.IDLE,
    tripProgress: Float = 0f,
    showFamilyMarker: Boolean = false,
    familyStatusText: String = "Rahul Ghar se 2km door hai, Bike par hai",
    nearbyShops: List<NearbyShop> = emptyList(),
    selectedShop: NearbyShop? = null,
    onShopPinClick: (NearbyShop) -> Unit = {},
    onLocateMeClick: () -> Unit = {}
) {
    var zoomLevel by remember { mutableFloatStateOf(1f) }

    // Pulsing animation for current location & searching radar
    val infiniteTransition = rememberInfiniteTransition(label = "map_radar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    // Moving ambient bikes animation
    val ambientBikeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_bikes"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Map Base Canvas (Light modern map color)
            drawRect(color = Color(0xFFF3F2EE))

            // 2. Parks & Greenery Areas
            drawRoundRect(
                color = Color(0xFFDCEAD2),
                topLeft = Offset(width * 0.08f, height * 0.12f),
                size = Size(width * 0.28f, height * 0.16f),
                cornerRadius = CornerRadius(20f, 20f)
            )
            drawRoundRect(
                color = Color(0xFFDCEAD2),
                topLeft = Offset(width * 0.65f, height * 0.42f),
                size = Size(width * 0.30f, height * 0.22f),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // 3. Water Body / River (Yamuna curve)
            val riverPath = Path().apply {
                moveTo(width * 0.85f, 0f)
                cubicTo(
                    width * 0.92f, height * 0.35f,
                    width * 0.78f, height * 0.70f,
                    width * 0.88f, height
                )
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFCCE1F2),
                style = Stroke(width = 46f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 4. Street Grid & Roads
            // Major highways (with gray border and bright white center)
            drawHighway(this, width, height)

            // 5. Metro Line (Dotted Purple/Blue)
            val metroPath = Path().apply {
                moveTo(0f, height * 0.32f)
                lineTo(width * 0.45f, height * 0.32f)
                lineTo(width * 0.75f, height * 0.80f)
            }
            drawPath(
                path = metroPath,
                color = Color(0xFF8E24AA),
                style = Stroke(
                    width = 6f,
                    cap = StrokeCap.Round,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(16f, 10f),
                        0f
                    )
                )
            )

            // 6. Draw Ambient Nearby Bikes (Simulating live Rapido bike taxi drivers on streets)
            drawAmbientBikes(this, width, height, ambientBikeOffset)

            // 7. Route and Markers when booking or active ride
            val pickupPos = Offset(width * 0.50f, height * 0.55f)
            val dropPos = Offset(width * 0.78f, height * 0.22f)

            if (rideStatus != RideStatus.IDLE) {
                // Route Polyline (Navigating path)
                val routePath = Path().apply {
                    moveTo(pickupPos.x, pickupPos.y)
                    lineTo(width * 0.50f, height * 0.42f)
                    lineTo(width * 0.70f, height * 0.42f)
                    lineTo(width * 0.70f, height * 0.22f)
                    lineTo(dropPos.x, dropPos.y)
                }

                // Route outline
                drawPath(
                    path = routePath,
                    color = Color(0xFF1976D2).copy(alpha = 0.4f),
                    style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Route main line
                drawPath(
                    path = routePath,
                    color = Color(0xFF2196F3),
                    style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Destination Marker (Red Pin)
                drawDestinationPin(this, dropPos)

                // Active moving ride vehicle or driver on way
                if (rideStatus == RideStatus.DRIVER_ON_WAY) {
                    val driverPos = Offset(
                        width * 0.42f + (pickupPos.x - width * 0.42f) * 0.6f,
                        height * 0.65f + (pickupPos.y - height * 0.65f) * 0.6f
                    )
                    drawActiveBikeMarker(this, driverPos, isDriverArriving = true)
                } else if (rideStatus == RideStatus.RIDE_IN_PROGRESS) {
                    // Position along route based on tripProgress
                    val currentPos = if (tripProgress < 0.35f) {
                        val p = tripProgress / 0.35f
                        Offset(pickupPos.x, pickupPos.y + (height * 0.42f - pickupPos.y) * p)
                    } else if (tripProgress < 0.75f) {
                        val p = (tripProgress - 0.35f) / 0.40f
                        Offset(
                            width * 0.50f + (width * 0.70f - width * 0.50f) * p,
                            height * 0.42f
                        )
                    } else {
                        val p = (tripProgress - 0.75f) / 0.25f
                        Offset(
                            width * 0.70f + (dropPos.x - width * 0.70f) * p,
                            height * 0.42f + (dropPos.y - height * 0.42f) * p
                        )
                    }
                    drawActiveBikeMarker(this, currentPos, isDriverArriving = false)
                }
            }

            // 8. User Current Location Marker (Pulsing blue beacon)
            drawCircle(
                color = Color(0xFF2196F3).copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = pickupPos
            )
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = pickupPos
            )
            drawCircle(
                color = Color(0xFF1976D2),
                radius = 8f,
                center = pickupPos
            )

            // 9. Family Live Tracking Marker if enabled
            if (showFamilyMarker) {
                val famPos = Offset(width * 0.58f, height * 0.48f)
                drawFamilyMarker(this, famPos)
            }

            // 10. Nearby Shop Ground Shadows & Highlighting
            nearbyShops.forEach { shop ->
                val shopPos = Offset(width * shop.mapCoordX, height * shop.mapCoordY)
                val isSelected = selectedShop?.id == shop.id
                if (isSelected) {
                    drawCircle(
                        color = GrudexYellow.copy(alpha = 0.45f),
                        radius = 34f,
                        center = shopPos
                    )
                }
                drawCircle(
                    color = Color(0x33000000),
                    radius = 8f,
                    center = Offset(shopPos.x, shopPos.y + 4f)
                )
            }
        }

        // Nearby Shop Interactive Pins
        nearbyShops.forEach { shop ->
            val isSelected = selectedShop?.id == shop.id
            val pinX = (maxWidth * shop.mapCoordX) - 30.dp
            val pinY = (maxHeight * shop.mapCoordY) - 52.dp

            Box(
                modifier = Modifier
                    .offset(x = pinX, y = pinY)
                    .clickable { onShopPinClick(shop) }
                    .testTag("map_shop_pin_${shop.id}"),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Floating badge with shop name and distance
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GrudexBlack else Color.White,
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) GrudexYellow else Color(0xFFCCCCCC)
                        ),
                        shadowElevation = if (isSelected) 8.dp else 3.dp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shop.nameHindi.split(" ").firstOrNull() ?: shop.nameHindi,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GrudexYellow else GrudexBlack
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "•",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = shop.distanceTextHindi.replace(" door", ""),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1976D2)
                            )
                        }
                    }

                    // Pin Icon with Category Emoji
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 40.dp else 34.dp)
                            .shadow(6.dp, CircleShape)
                            .background(
                                color = if (isSelected) GrudexYellow else GrudexBlack,
                                shape = CircleShape
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) GrudexBlack else GrudexYellow,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = shop.category.emoji,
                            fontSize = if (isSelected) 18.sp else 15.sp
                        )
                    }

                    // Pin pointer tip
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (isSelected) GrudexBlack else GrudexYellow,
                                CircleShape
                            )
                    )
                }
            }
        }

        // Map Floating Controls (Zoom In, Zoom Out, Locate Me)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmallFloatingActionButton(
                onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2f) },
                containerColor = Color.White,
                contentColor = GrudexBlack,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(
                onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f) },
                containerColor = Color.White,
                contentColor = GrudexBlack,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            FloatingActionButton(
                onClick = onLocateMeClick,
                containerColor = Color.White,
                contentColor = Color(0xFF1976D2),
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier.testTag("locate_me_button")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Mera Sthan (Locate Me)")
            }
        }

        // Family Tracking Floating Status Pill (when family marker active)
        if (showFamilyMarker) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                color = GrudexBlack,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(GrudexGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = familyStatusText,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun drawHighway(scope: DrawScope, width: Float, height: Float) {
    val roadBorderColor = Color(0xFFD6D3CD)
    val roadFillColor = Color.White

    // Horizontal Arterials
    val y1 = height * 0.22f
    val y2 = height * 0.42f
    val y3 = height * 0.68f

    // Vertical Arterials
    val x1 = width * 0.25f
    val x2 = width * 0.50f
    val x3 = width * 0.70f

    // Outer road borders
    scope.drawLine(roadBorderColor, Offset(0f, y1), Offset(width, y1), strokeWidth = 30f)
    scope.drawLine(roadBorderColor, Offset(0f, y2), Offset(width, y2), strokeWidth = 26f)
    scope.drawLine(roadBorderColor, Offset(0f, y3), Offset(width, y3), strokeWidth = 22f)

    scope.drawLine(roadBorderColor, Offset(x1, 0f), Offset(x1, height), strokeWidth = 22f)
    scope.drawLine(roadBorderColor, Offset(x2, 0f), Offset(x2, height), strokeWidth = 28f)
    scope.drawLine(roadBorderColor, Offset(x3, 0f), Offset(x3, height), strokeWidth = 24f)

    // Inner road fills
    scope.drawLine(roadFillColor, Offset(0f, y1), Offset(width, y1), strokeWidth = 24f)
    scope.drawLine(roadFillColor, Offset(0f, y2), Offset(width, y2), strokeWidth = 20f)
    scope.drawLine(roadFillColor, Offset(0f, y3), Offset(width, y3), strokeWidth = 16f)

    scope.drawLine(roadFillColor, Offset(x1, 0f), Offset(x1, height), strokeWidth = 16f)
    scope.drawLine(roadFillColor, Offset(x2, 0f), Offset(x2, height), strokeWidth = 22f)
    scope.drawLine(roadFillColor, Offset(x3, 0f), Offset(x3, height), strokeWidth = 18f)

    // Central Connaught Place Style Ring
    val centerCircle = Offset(width * 0.50f, height * 0.55f)
    scope.drawCircle(
        color = roadBorderColor,
        radius = 54f,
        center = centerCircle,
        style = Stroke(width = 24f)
    )
    scope.drawCircle(
        color = roadFillColor,
        radius = 54f,
        center = centerCircle,
        style = Stroke(width = 18f)
    )
    // Central Park Green in circle
    scope.drawCircle(
        color = Color(0xFFCBE3BE),
        radius = 42f,
        center = centerCircle
    )
}

private fun drawAmbientBikes(scope: DrawScope, width: Float, height: Float, offset: Float) {
    val bike1 = Offset(width * 0.15f + offset * (width * 0.3f), height * 0.22f)
    val bike2 = Offset(width * 0.50f, height * 0.25f + offset * (height * 0.35f))
    val bike3 = Offset(width * 0.70f, height * 0.85f - offset * (height * 0.3f))

    drawBikeIcon(scope, bike1, GrudexYellow)
    drawBikeIcon(scope, bike2, GrudexYellow)
    drawBikeIcon(scope, bike3, GrudexYellow)
}

private fun drawBikeIcon(scope: DrawScope, center: Offset, color: Color) {
    // Yellow circle badge with black helmet/bike motif
    scope.drawCircle(
        color = Color(0x33000000),
        radius = 16f,
        center = Offset(center.x + 2, center.y + 3)
    )
    scope.drawCircle(
        color = color,
        radius = 14f,
        center = center
    )
    scope.drawCircle(
        color = GrudexBlack,
        radius = 14f,
        center = center,
        style = Stroke(width = 2.5f)
    )
    // Bike handle & wheels dot
    scope.drawCircle(color = GrudexBlack, radius = 3.5f, center = Offset(center.x - 4f, center.y + 4f))
    scope.drawCircle(color = GrudexBlack, radius = 3.5f, center = Offset(center.x + 4f, center.y + 4f))
    scope.drawLine(
        color = GrudexBlack,
        start = Offset(center.x - 4f, center.y + 4f),
        end = Offset(center.x + 4f, center.y - 3f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}

private fun drawActiveBikeMarker(scope: DrawScope, center: Offset, isDriverArriving: Boolean) {
    // Pulse effect
    scope.drawCircle(
        color = GrudexYellow.copy(alpha = 0.35f),
        radius = 28f,
        center = center
    )
    // Shadow
    scope.drawCircle(
        color = Color(0x44000000),
        radius = 20f,
        center = Offset(center.x + 2f, center.y + 3f)
    )
    // Main bright yellow circle
    scope.drawCircle(
        color = GrudexYellow,
        radius = 18f,
        center = center
    )
    scope.drawCircle(
        color = GrudexBlack,
        radius = 18f,
        center = center,
        style = Stroke(width = 3f)
    )
    // Bike graphic in center
    scope.drawCircle(color = GrudexBlack, radius = 4f, center = Offset(center.x - 6f, center.y + 5f))
    scope.drawCircle(color = GrudexBlack, radius = 4f, center = Offset(center.x + 6f, center.y + 5f))
    scope.drawLine(
        color = GrudexBlack,
        start = Offset(center.x - 6f, center.y + 5f),
        end = Offset(center.x + 5f, center.y - 4f),
        strokeWidth = 3.5f,
        cap = StrokeCap.Round
    )
}

private fun drawDestinationPin(scope: DrawScope, center: Offset) {
    // Drop shadow
    scope.drawCircle(
        color = Color(0x33000000),
        radius = 8f,
        center = Offset(center.x, center.y + 6f)
    )

    // Red pin head
    val pinPath = Path().apply {
        moveTo(center.x, center.y)
        cubicTo(
            center.x - 14f, center.y - 12f,
            center.x - 14f, center.y - 32f,
            center.x, center.y - 32f
        )
        cubicTo(
            center.x + 14f, center.y - 32f,
            center.x + 14f, center.y - 12f,
            center.x, center.y
        )
        close()
    }
    scope.drawPath(path = pinPath, color = GrudexRed)
    scope.drawCircle(
        color = Color.White,
        radius = 6f,
        center = Offset(center.x, center.y - 20f)
    )
}

private fun drawFamilyMarker(scope: DrawScope, center: Offset) {
    scope.drawCircle(
        color = GrudexGreen.copy(alpha = 0.3f),
        radius = 32f,
        center = center
    )
    scope.drawCircle(
        color = GrudexGreen,
        radius = 18f,
        center = center
    )
    scope.drawCircle(
        color = Color.White,
        radius = 18f,
        center = center,
        style = Stroke(width = 3f)
    )
    // Family icon dot
    scope.drawCircle(
        color = Color.White,
        radius = 6f,
        center = Offset(center.x, center.y - 3f)
    )
}
