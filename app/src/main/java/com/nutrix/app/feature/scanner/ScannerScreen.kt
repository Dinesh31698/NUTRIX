package com.nutrix.app.feature.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nutrix.app.core.designsystem.NutrixColors
import java.util.concurrent.Executors

enum class ScanMode {
    BARCODE,
    LABEL,
    PHOTO
}

@Composable
fun ScannerScreen(
    onBarcodeDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedMode by remember { mutableStateOf(ScanMode.PHOTO) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isFlashOn by remember { mutableStateOf(false) }
    var detectedStatus by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Laser Animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        // Camera Preview / Fallback
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    val executor = Executors.newSingleThreadExecutor()

                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            val barcodeScanner = BarcodeScanning.getClient()

                            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            val rawValue = barcodes.firstOrNull()?.rawValue
                                            if (!rawValue.isNullOrBlank()) {
                                                detectedStatus = "Barcode: "
                                                onBarcodeDetected(rawValue)
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                        } catch (e: Exception) {
                            // Camera binding exception
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Permission placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NutrixColors.CardSurface),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = NutrixColors.TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera Permission Needed",
                        color = NutrixColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Grant camera permission to scan barcodes and food packages.",
                        color = NutrixColors.TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NutrixColors.CardSurface.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NutrixColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Scan Food",
                    color = NutrixColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Scan packaged products, fruits, snacks etc.",
                    color = NutrixColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Scanner Reticle Overlay Frame
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .border(2.dp, NutrixColors.CardBorder.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Scanning Laser Line
            Canvas(modifier = Modifier.fillMaxSize()) {
                val y = size.height * laserPosition
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            NutrixColors.ElectricBlue,
                            NutrixColors.MintGreen,
                            NutrixColors.ElectricBlue,
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }

        // Bottom Controls Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, NutrixColors.BackgroundDark.copy(alpha = 0.95f))
                    )
                )
                .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Selector (Barcode / Label / Photo)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(NutrixColors.CardSurface.copy(alpha = 0.8f))
                    .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ScanModeItem("Barcode", Icons.Default.QrCode, selectedMode == ScanMode.BARCODE) {
                    selectedMode = ScanMode.BARCODE
                }
                ScanModeItem("Label", Icons.Default.Description, selectedMode == ScanMode.LABEL) {
                    selectedMode = ScanMode.LABEL
                }
                ScanModeItem("Photo", Icons.Default.CameraAlt, selectedMode == ScanMode.PHOTO) {
                    selectedMode = ScanMode.PHOTO
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons Row: Gallery, Shutter, Flash
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Gallery Picker
                IconButton(
                    onClick = {
                        // Default sample Lay's barcode
                        onBarcodeDetected("028400017008")
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.CardSurface)
                        .border(1.dp, NutrixColors.CardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Gallery",
                        tint = NutrixColors.TextPrimary
                    )
                }

                // Shutter Circle Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.CardBorder)
                        .clickable {
                            // Triggers detection with sample product (Lay's)
                            onBarcodeDetected("028400017008")
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NutrixColors.ElectricBlue, NutrixColors.Magenta)
                                )
                            )
                    )
                }

                // Flash Toggle
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.CardSurface)
                        .border(1.dp, NutrixColors.CardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) NutrixColors.WarmAmber else NutrixColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick emulator / testing simulation pill
            Text(
                text = "Tap shutter or gallery to scan Lay\'s Chips demo",
                color = NutrixColors.TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ScanModeItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) NutrixColors.ElectricBlue else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else NutrixColors.TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else NutrixColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
