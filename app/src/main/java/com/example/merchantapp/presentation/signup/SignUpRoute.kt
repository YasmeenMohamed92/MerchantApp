package com.example.merchantapp.presentation.signup

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.merchantapp.presentation.signup.steps.BusinessDetailsScreen
import com.example.merchantapp.presentation.signup.steps.PersonalInfoScreen
import com.example.merchantapp.presentation.signup.steps.SettlementAccountScreen

@Composable
fun SignUpRoute(
    onNavigateBack: () -> Unit,
    onCompleteSignUp: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val downloadId = remember { mutableLongStateOf(-1L) }

    val currentViewModel by rememberUpdatedState(viewModel)

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentViewModel.onIntent(
                SignUpIntent.BankStatementUploadSucceeded
            )
        }
    }

    DisposableEffect(context, viewModel) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(
                receiverContext: Context?,
                intent: Intent?
            ) {
                if (intent?.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    return
                }

                val completedId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID,
                    -1L
                )

                if (completedId != downloadId.longValue || completedId == -1L) {
                    return
                }

                val manager = context.getSystemService(
                    Context.DOWNLOAD_SERVICE
                ) as DownloadManager

                val query = DownloadManager.Query().setFilterById(completedId)

                manager.query(query).use { cursor ->
                    if (!cursor.moveToFirst()) {
                        currentViewModel.onIntent(
                            SignUpIntent.ContractDownloadFailed(
                                "Unable to verify contract download"
                            )
                        )
                        return@use
                    }

                    val status = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                            DownloadManager.COLUMN_STATUS
                        )
                    )

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        currentViewModel.onIntent(
                            SignUpIntent.ContractDownloadSucceeded
                        )
                    } else {
                        val reason = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                DownloadManager.COLUMN_REASON
                            )
                        )

                        currentViewModel.onIntent(
                            SignUpIntent.ContractDownloadFailed(
                                "Contract download failed. Reason: $reason"
                            )
                        )
                    }
                }
            }
        }

        val filter = IntentFilter(
            DownloadManager.ACTION_DOWNLOAD_COMPLETE
        )

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignUpEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is SignUpEffect.NavigateToStep -> {
                }

                SignUpEffect.SignUpCompleted -> {
                    onCompleteSignUp()
                }

                is SignUpEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is SignUpEffect.TriggerDownload -> {
                    try {
                        val request = DownloadManager.Request(
                            effect.pdfUrl.toUri()
                        ).apply {
                            setTitle("Merchant Joining Contract")
                            setDescription("Downloading contract PDF...")
                            setNotificationVisibility(
                                DownloadManager.Request
                                    .VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                            )
                            setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                "Merchant_Contract.pdf"
                            )
                        }

                        val manager = context.getSystemService(
                            Context.DOWNLOAD_SERVICE
                        ) as DownloadManager

                        downloadId.longValue = manager.enqueue(request)

                    } catch (exception: Exception) {
                        currentViewModel.onIntent(
                            SignUpIntent.ContractDownloadFailed(
                                exception.message
                                    ?: "Unable to start contract download"
                            )
                        )
                    }
                }

                SignUpEffect.SelectBankStatementFile -> {
                    filePickerLauncher.launch("application/pdf")
                }

                else -> {}
            }
        }
    }

    when (state.currentStep) {
        2 -> PersonalInfoScreen(
            state = state,
            onIntent = viewModel::onIntent
        )

        4 -> BusinessDetailsScreen(
            state = state,
            onIntent = viewModel::onIntent
        )

        8 -> SettlementAccountScreen(
            state = state,
            onIntent = viewModel::onIntent,
            onUploadClicked = {
                viewModel.onIntent(
                    SignUpIntent.UploadBankStatementClicked
                )
            }
        )

        else -> {
            PersonalInfoScreen(
                state = state,
                onIntent = viewModel::onIntent
            )
        }
    }
}
