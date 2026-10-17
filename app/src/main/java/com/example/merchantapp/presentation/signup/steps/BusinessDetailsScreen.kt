
package com.example.merchantapp.presentation.signup.steps

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.merchantapp.presentation.signup.SignUpIntent
import com.example.merchantapp.presentation.signup.SignUpUiState
import com.example.merchantapp.ui.theme.MerchantBorder
import com.example.merchantapp.ui.theme.MerchantSecondaryText
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private val businessCategories = listOf(
    "Retail & Grocery",
    "Restaurant & Cafe",
    "E-Commerce",
    "Services",
    "Fashion & Beauty",
    "Electronics"
)

@Composable
fun BusinessDetailsScreen(
    state: SignUpUiState,
    onIntent: (SignUpIntent) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isCategoryExpanded by remember {
        mutableStateOf(false)
    }

    val onBusinessNameChange: (String) -> Unit = { input ->
        val filtered = input.filter {
            it.isLetter() || it.isWhitespace()
        }
        onIntent(SignUpIntent.UpdateBusinessName(filtered))
    }

    val onBusinessAddressChange: (String) -> Unit = { address ->
        onIntent(SignUpIntent.UpdateBusinessAddress(address))
    }

    val onBusinessWebsiteChange: (String) -> Unit = { website ->
        onIntent(SignUpIntent.UpdateBusinessWebsite(website))
    }

    val onBusinessCategoryChange: (String) -> Unit = { category ->
        onIntent(SignUpIntent.UpdateBusinessCategory(category))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            BusinessDetailsHeader(
                onBackClick = {
                    onIntent(SignUpIntent.PreviousStepClicked)
                },
                onSaveDraftClick = {
                    onIntent(SignUpIntent.SaveAsDraftClicked)
                }
            )

            BusinessDetailsProgress()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Now, tell us more about your business",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Complete your business details to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MerchantSecondaryText
                )

                Spacer(Modifier.height(24.dp))

                BusinessNameField(
                    value = state.businessName,
                    onValueChange = onBusinessNameChange
                )

                Spacer(Modifier.height(16.dp))

                BusinessCategoryField(
                    selectedCategory = state.businessCategory,
                    categories = businessCategories,
                    expanded = isCategoryExpanded,
                    onExpandedChange = {
                        isCategoryExpanded = it
                    },
                    onCategorySelected = onBusinessCategoryChange
                )

                Spacer(Modifier.height(16.dp))

                BusinessAddressField(
                    value = state.businessAddress,
                    onValueChange = onBusinessAddressChange,
                    onPickLocationClick = {
                        onIntent(
                            SignUpIntent.ToggleMapPickerDialog(true)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                JoiningContractField(
                    isDownloaded = state.isContractDownloaded,
                    onDownloadClick = {
                        onIntent(SignUpIntent.DownloadContractClicked)
                    }
                )

                Spacer(Modifier.height(16.dp))

                BusinessWebsiteField(
                    value = state.businessWebsite,
                    onValueChange = onBusinessWebsiteChange
                )
            }
        }

        BusinessDetailsFooter(
            enabled = state.isNextEnabled,
            onNextClick = {
                onIntent(SignUpIntent.NextStepClicked)
            }
        )
    }

    if (state.showMapPicker) {
        BusinessLocationDialog(
            context = context,
            onConfirmAddress = onBusinessAddressChange,
            onDismiss = {
                onIntent(
                    SignUpIntent.ToggleMapPickerDialog(false)
                )
            },
            onConfirmStarted = {
                // The dialog stays visible while resolving the address.
            },
            onConfirmFinished = {
                onIntent(
                    SignUpIntent.ToggleMapPickerDialog(false)
                )
            },
            coroutineScope = coroutineScope
        )
    }
}

@Composable
private fun BusinessDetailsHeader(
    onBackClick: () -> Unit,
    onSaveDraftClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable(onClick = onBackClick)
            )

            Text(
                text = "Step 4 of 8",
                style = MaterialTheme.typography.bodyMedium,
                color = MerchantSecondaryText
            )
        }

        Text(
            text = "Save as draft",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.clickable(onClick = onSaveDraftClick)
        )
    }
}

@Composable
private fun BusinessDetailsProgress() {
    LinearProgressIndicator(
        progress = { 4f / 8f },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MerchantBorder.copy(alpha = 0.25f),
        strokeCap = StrokeCap.Butt
    )
}

@Composable
private fun BusinessNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Business name",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("e.g. Saudi Market")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun BusinessCategoryField(
    selectedCategory: String,
    categories: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Business category",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        BusinessCategorySelector(
            selectedCategory = selectedCategory,
            categories = categories,
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            onCategorySelected = onCategorySelected
        )
    }
}

@Composable
private fun BusinessCategorySelector(
    selectedCategory: String,
    categories: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text("Choose your business category")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown"
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MerchantBorder,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                disabledPlaceholderColor = MerchantSecondaryText,
                disabledTrailingIconColor =
                    MaterialTheme.colorScheme.onSurface
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    onExpandedChange(true)
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            categories.forEach { category ->
                val selected = selectedCategory == category

                DropdownMenuItem(
                    text = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onCategorySelected(category)
                        onExpandedChange(false)
                    },
                    modifier = Modifier.background(
                        if (selected) {
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.15f
                            )
                        } else {
                            Color.Transparent
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun BusinessAddressField(
    value: String,
    onValueChange: (String) -> Unit,
    onPickLocationClick: () -> Unit
) {
    Column {
        Text(
            text = "Business address",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Enter or pick location from map")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Pick Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(
                        onClick = onPickLocationClick
                    )
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun JoiningContractField(
    isDownloaded: Boolean,
    onDownloadClick: () -> Unit
) {
    Column {
        Text(
            text = "Joining contract",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDownloadClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDownloaded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
            )
        ) {
            Text(
                text = if (isDownloaded) {
                    "Contract Downloaded ✓"
                } else {
                    "Download contract"
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun BusinessWebsiteField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        val label = buildAnnotatedString {
            append("Business website ")

            withStyle(
                SpanStyle(
                    color = MerchantSecondaryText.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            ) {
                append("( Optional )")
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Enter or paste your website URL here")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun BusinessDetailsFooter(
    enabled: Boolean,
    onNextClick: () -> Unit
) {
    Box(modifier = Modifier.padding(24.dp)) {
        Button(
            onClick = onNextClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary
                    .copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun BusinessLocationDialog(
    context: Context,
    onConfirmAddress: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmStarted: () -> Unit,
    onConfirmFinished: () -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val defaultLocation = LatLng(30.0444, 31.2357)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            defaultLocation,
            15f
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true
                        )
                    )

                    Text(
                        text = "📍",
                        fontSize = 32.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Button(
                    onClick = {
                        val target = cameraPositionState.position.target

                        coroutineScope.launch {
                            onConfirmStarted()

                            val address = resolveAddress(
                                context = context,
                                latitude = target.latitude,
                                longitude = target.longitude
                            )

                            onConfirmAddress(address)
                            onConfirmFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Confirm Location",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private suspend fun resolveAddress(
    context: Context,
    latitude: Double,
    longitude: Double
): String {
    val fallbackAddress = "Lat: $latitude, Lng: $longitude"

    return try {
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())

            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )

            addresses
                ?.firstOrNull()
                ?.getAddressLine(0)
                ?.takeIf { it.isNotBlank() }
                ?: fallbackAddress
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        fallbackAddress
    }
}


//package com.example.merchantapp.presentation.signup.steps
//
//import android.app.DownloadManager
//import android.content.Context
//import android.location.Geocoder
//import android.os.Environment
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.outlined.LocationOn
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Icon
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import androidx.core.net.toUri
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.merchantapp.presentation.signup.SignUpEffect
//import com.example.merchantapp.presentation.signup.SignUpIntent
//import com.example.merchantapp.presentation.signup.SignUpViewModel
//import com.example.merchantapp.ui.theme.MerchantBorder
//import com.example.merchantapp.ui.theme.MerchantSecondaryText
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.GoogleMap
//import com.google.maps.android.compose.MapUiSettings
//import com.google.maps.android.compose.rememberCameraPositionState
//import java.util.Locale
//
//@Composable
//fun BusinessDetailsScreen(
//    onNavigateBack: () -> Unit,
//    onNavigateNext: () -> Unit,
//    viewModel: SignUpViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    val context = LocalContext.current
//    var isCategoryExpanded by remember { mutableStateOf(false) }
//    val categories = listOf("Retail & Grocery", "Restaurant & Cafe", "E-Commerce", "Services", "Fashion & Beauty", "Electronics")
//
//    // التعامل مع الـ Effects الواردة من الـ ViewModel (مثل التحميل، رسائل الـ Toast، أو التنقل)
//    LaunchedEffect(viewModel) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                SignUpEffect.NavigateBack -> onNavigateBack()
//                SignUpEffect.NavigateToNextStep -> onNavigateNext()
//                is SignUpEffect.ShowToast -> {
//                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
//                }
//                is SignUpEffect.TriggerDownload -> {
//                    try {
//                        val request = DownloadManager.Request(effect.pdfUrl.toUri()).apply {
//                            setTitle("Merchant Joining Contract")
//                            setDescription("Downloading contract PDF...")
//                            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Merchant_Contract.pdf")
//                        }
//
//                        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                        manager.enqueue(request)
//                    } catch (e: Exception) {
//                        Toast.makeText(context, "خطأ في التحميل", Toast.LENGTH_LONG).show()
//                    }
//                }
//                else -> {}
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .statusBarsPadding()
//            .background(MaterialTheme.colorScheme.background),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .verticalScroll(rememberScrollState())
//        ) {
//            /* TOP BAR */
//            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.clickable { viewModel.onIntent(SignUpIntent.PreviousStepClicked) }
//                        )
//                        Text(
//                            text = "Step 4 of 8",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = MerchantSecondaryText
//                        )
//                    }
//
//                    Text(
//                        text = "Save as draft",
//                        style = MaterialTheme.typography.bodyMedium.copy(
//                            color = MaterialTheme.colorScheme.primary,
//                            textDecoration = TextDecoration.Underline
//                        ),
//                        modifier = Modifier.clickable { viewModel.onIntent(SignUpIntent.SaveAsDraftClicked) }
//                    )
//                }
//            }
//
//            /* LINEAR PROGRESS INDICATOR */
//            LinearProgressIndicator(
//                progress = { 4f / 8f },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(4.dp),
//                color = MaterialTheme.colorScheme.primary,
//                trackColor = MerchantBorder.copy(alpha = 0.25f),
//                strokeCap = StrokeCap.Butt
//            )
//
//            /* CONTENT */
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp)
//            ) {
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(text = "Now, tell us more about your business", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Complete your business details to continue.", style = MaterialTheme.typography.bodyMedium, color = MerchantSecondaryText)
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                /* BUSINESS NAME */
//                Text(text = "Business name", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = state.businessName,
//                    onValueChange = { input ->
//                        val filtered = input.filter { it.isLetter() || it.isWhitespace() }
//                        viewModel.onIntent(SignUpIntent.UpdateBusinessName(filtered))
//                    },
//                    placeholder = { Text("e.g. Saudi Market") },
//                    modifier = Modifier.fillMaxWidth().height(52.dp),
//                    singleLine = true,
//                    textStyle = MaterialTheme.typography.bodyLarge,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        unfocusedBorderColor = MerchantBorder,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary
//                    )
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                /* BUSINESS CATEGORY */
//                Text(text = "Business category", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Box(modifier = Modifier.fillMaxWidth()) {
//                    OutlinedTextField(
//                        value = state.businessCategory,
//                        onValueChange = {},
//                        readOnly = true,
//                        placeholder = { Text("Choose your business category") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(52.dp)
//                            .clickable { isCategoryExpanded = true },
//                        enabled = false,
//                        trailingIcon = {
//                            Icon(
//                                imageVector = Icons.Default.KeyboardArrowDown,
//                                contentDescription = "Dropdown",
//                                modifier = Modifier.clickable { isCategoryExpanded = true }
//                            )
//                        },
//                        textStyle = MaterialTheme.typography.bodyLarge,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            disabledBorderColor = MerchantBorder,
//                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
//                            disabledPlaceholderColor = MerchantSecondaryText
//                        )
//                    )
//
//                    Box(
//                        modifier = Modifier
//                            .matchParentSize()
//                            .clickable { isCategoryExpanded = true }
//                    ) {}
//
//                    DropdownMenu(
//                        expanded = isCategoryExpanded,
//                        onDismissRequest = { isCategoryExpanded = false },
//                        modifier = Modifier
//                            .background(MaterialTheme.colorScheme.surface)
//                            .fillMaxWidth(0.88f)
//                    ) {
//                        categories.forEach { category ->
//                            val isSelected = state.businessCategory == category
//                            DropdownMenuItem(
//                                text = {
//                                    Text(
//                                        text = category,
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
//                                    )
//                                },
//                                onClick = {
//                                    viewModel.onIntent(SignUpIntent.UpdateBusinessCategory(category))
//                                    isCategoryExpanded = false
//                                },
//                                modifier = Modifier.background(
//                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
//                                )
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                /* BUSINESS ADDRESS */
//                Text(text = "Business address", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = state.businessAddress,
//                    onValueChange = { viewModel.onIntent(SignUpIntent.UpdateBusinessAddress(it)) },
//                    placeholder = { Text("Enter or pick location from map") },
//                    modifier = Modifier.fillMaxWidth().height(52.dp),
//                    singleLine = true,
//                    trailingIcon = {
//                        Icon(
//                            imageVector = Icons.Outlined.LocationOn,
//                            contentDescription = "Pick Location",
//                            tint = MaterialTheme.colorScheme.primary,
//                            modifier = Modifier.clickable {
//                                viewModel.onIntent(SignUpIntent.ToggleMapPickerDialog(true))
//                            }
//                        )
//                    },
//                    textStyle = MaterialTheme.typography.bodyLarge,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        unfocusedBorderColor = MerchantBorder,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary
//                    )
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                /* JOINING CONTRACT */
//                Text(text = "Joining contract", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedButton(
//                    onClick = { viewModel.onIntent(SignUpIntent.DownloadContractClicked) },
//                    modifier = Modifier.fillMaxWidth().height(52.dp),
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = if (state.isContractDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
//                    )
//                ) {
//                    Text(text = if (state.isContractDownloaded) "Contract Downloaded ✓" else "Download contract", style = MaterialTheme.typography.labelLarge)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                /* BUSINESS WEBSITE */
//                val websiteLabel = buildAnnotatedString {
//                    append("Business website ")
//                    withStyle(
//                        style = SpanStyle(
//                            color = MerchantSecondaryText.copy(alpha = 0.6f),
//                            fontSize = 13.sp
//                        )
//                    ) {
//                        append("( Optional )")
//                    }
//                }
//
//                Text(text = websiteLabel, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = state.businessWebsite,
//                    onValueChange = { viewModel.onIntent(SignUpIntent.UpdateBusinessWebsite(it)) },
//                    placeholder = { Text("Enter or paste your website URL here") },
//                    modifier = Modifier.fillMaxWidth().height(52.dp),
//                    singleLine = true,
//                    textStyle = MaterialTheme.typography.bodyLarge,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        unfocusedBorderColor = MerchantBorder,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary
//                    )
//                )
//            }
//        }
//
//        /* NEXT BUTTON */
//        Box(modifier = Modifier.padding(24.dp)) {
//            Button(
//                onClick = {
//                    viewModel.onIntent(SignUpIntent.NextStepClicked)
//                    onNavigateNext()
//                },
//                enabled = state.isNextEnabled,
//                modifier = Modifier.fillMaxWidth().height(48.dp),
//                shape = RoundedCornerShape(8.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
//                    disabledContentColor = Color.White.copy(alpha = 0.7f)
//                )
//            ) {
//                Text(text = "Next", style = MaterialTheme.typography.labelLarge)
//            }
//        }
//    }
//
//    /* GOOGLE MAP PICKER DIALOG */
//    if (state.showMapPicker) {
//        Dialog(onDismissRequest = { viewModel.onIntent(SignUpIntent.ToggleMapPickerDialog(false)) }) {
//            Surface(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(450.dp),
//                shape = RoundedCornerShape(16.dp),
//                color = MaterialTheme.colorScheme.surface
//            ) {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.SpaceBetween
//                ) {
//                    val defaultLocation = LatLng(30.0444, 31.2357)
//                    val cameraPositionState = rememberCameraPositionState {
//                        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
//                    }
//
//                    Box(modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()) {
//                        GoogleMap(
//                            modifier = Modifier.fillMaxSize(),
//                            cameraPositionState = cameraPositionState,
//                            uiSettings = MapUiSettings(zoomControlsEnabled = true)
//                        )
//
//                        Text(
//                            text = "📍",
//                            fontSize = 32.sp,
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//
//                    Button(
//                        onClick = {
//                            val targetLocation = cameraPositionState.position.target
//                            try {
//                                val geocoder = Geocoder(context, Locale.getDefault())
//                                @Suppress("DEPRECATION")
//                                val addresses = geocoder.getFromLocation(targetLocation.latitude, targetLocation.longitude, 1)
//
//                                val addressText = if (!addresses.isNullOrEmpty()) {
//                                    addresses[0].getAddressLine(0) ?: "Selected Location"
//                                } else {
//                                    "Lat: ${targetLocation.latitude}, Lng: ${targetLocation.longitude}"
//                                }
//
//                                viewModel.onIntent(SignUpIntent.UpdateBusinessAddress(addressText))
//                            } catch (e: Exception) {
//                                viewModel.onIntent(SignUpIntent.UpdateBusinessAddress("Lat: ${targetLocation.latitude}, Lng: ${targetLocation.longitude}"))
//                            }
//                            viewModel.onIntent(SignUpIntent.ToggleMapPickerDialog(false))
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                            .height(48.dp),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text(text = "Confirm Location", style = MaterialTheme.typography.labelLarge)
//                    }
//                }
//            }
//        }
//    }
//}