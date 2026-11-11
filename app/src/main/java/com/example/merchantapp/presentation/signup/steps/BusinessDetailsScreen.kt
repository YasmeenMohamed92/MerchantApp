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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.merchantapp.R
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
    "Retail & Grocery" to R.string.category_retail_grocery,
    "Restaurant & Cafe" to R.string.category_restaurant_cafe,
    "E-Commerce" to R.string.category_e_commerce,
    "Services" to R.string.category_services,
    "Fashion & Beauty" to R.string.category_fashion_beauty,
    "Electronics" to R.string.category_electronics
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
                    text = stringResource(R.string.business_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.business_subtitle),
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
                // Keep the dialog visible while resolving the address.
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
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.clickable(
                    onClick = onBackClick
                )
            )

            Text(
                text = stringResource(R.string.step_progress, 4, 8),
                style = MaterialTheme.typography.bodyMedium,
                color = MerchantSecondaryText
            )
        }

        Text(
            text = stringResource(R.string.save_draft),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.clickable(
                onClick = onSaveDraftClick
            )
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
            text = stringResource(R.string.business_name),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    stringResource(R.string.business_name_hint)
                )
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
    categories: List<Pair<String, Int>>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.business_category),
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
    categories: List<Pair<String, Int>>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val selectedCategoryResource = categories
        .firstOrNull { it.first == selectedCategory }
        ?.second

    val selectedCategoryLabel = selectedCategoryResource?.let {
        stringResource(it)
    } ?: selectedCategory

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategoryLabel,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    stringResource(R.string.choose_business_category)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.dropdown)
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
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface
            )
        ) {
            categories.forEach { (categoryValue, categoryResource) ->
                val selected = selectedCategory == categoryValue

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(categoryResource),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        // Keep the original English value in the MVI state.
                        onCategorySelected(categoryValue)
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
            text = stringResource(R.string.business_address),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    stringResource(R.string.business_address_hint)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = stringResource(R.string.pick_location),
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
            text = stringResource(R.string.joining_contract),
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
                text = stringResource(
                    if (isDownloaded) {
                        R.string.contract_downloaded
                    } else {
                        R.string.download_contract
                    }
                ),
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
    val businessWebsiteText = stringResource(
        R.string.business_website
    )
    val optionalText = stringResource(
        R.string.optional
    )

    Column {
        val label = buildAnnotatedString {
            append(businessWebsiteText)
            append(" ")

            withStyle(
                SpanStyle(
                    color = MerchantSecondaryText.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            ) {
                append("($optionalText)")
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
                Text(
                    stringResource(R.string.business_website_hint)
                )
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
    Box(
        modifier = Modifier.padding(24.dp)
    ) {
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
                text = stringResource(R.string.next),
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

    Dialog(
        onDismissRequest = onDismiss
    ) {
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
                        val target =
                            cameraPositionState.position.target

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
                        text = stringResource(R.string.confirm_location),
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
            val geocoder = Geocoder(
                context,
                Locale.getDefault()
            )

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