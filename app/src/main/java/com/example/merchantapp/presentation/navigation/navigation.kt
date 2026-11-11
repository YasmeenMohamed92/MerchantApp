package com.example.merchantapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.merchantapp.data.local.EncryptedSessionManager
import com.example.merchantapp.presentation.home.HomeScreen
import com.example.merchantapp.presentation.login.LoginScreen
import com.example.merchantapp.presentation.signup.SignUpRoute
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Login : Screen

    @Serializable
    data object SignUp : Screen

    @Serializable
    data class Home(
        val merchantId: String
    ) : Screen
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Read the encrypted session once when the navigation graph is created.
    val sessionManager = remember(context) {
        EncryptedSessionManager(
            context.applicationContext
        )
    }

    val savedMerchantId = remember(sessionManager) {
        sessionManager.getSavedMerchantId()
    }

    val startDestination: Screen =
        if (!savedMerchantId.isNullOrBlank()) {
            Screen.Home(
                merchantId = savedMerchantId
            )
        } else {
            Screen.Login
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable<Screen.Login> {

            LoginScreen(
                onNavigateToHome = { merchantId ->

                    navController.navigate(
                        Screen.Home(
                            merchantId = merchantId
                        )
                    ) {
                        popUpTo<Screen.Login> {
                            inclusive = true
                        }
                    }
                },

                onNavigateToSignUp = {
                    navController.navigate(
                        Screen.SignUp
                    )
                }
            )
        }

        composable<Screen.SignUp> {

            SignUpRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },

                onCompleteSignUp = { merchantId ->

                    navController.navigate(
                        Screen.Home(
                            merchantId = merchantId
                        )
                    ) {
                        popUpTo<Screen.Login> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Screen.Home> { backStackEntry ->

            val route =
                backStackEntry.toRoute<Screen.Home>()

            HomeScreen(
                merchantId = route.merchantId
            )
        }
    }
}

