package com.example.merchantapp.presentation.navigation

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merchantapp.presentation.home.HomeScreen
import com.example.merchantapp.presentation.login.LoginScreen
import com.example.merchantapp.presentation.signup.SignUpRoute

sealed interface Screen {
    @Serializable
    data object Login : Screen

    @Serializable
    data object SignUp : Screen

    @Serializable
    data object Home : Screen
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {

        composable<Screen.Login> {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Login> { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp)
                }
            )
        }

        composable<Screen.SignUp> {
            SignUpRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCompleteSignUp = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Home> {
            HomeScreen()
        }
    }
}
//package com.example.merchantapp.presentation.navigation
//
//import kotlinx.serialization.Serializable
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.merchantapp.presentation.home.HomeScreen
//import com.example.merchantapp.presentation.login.LoginScreen
//import com.example.merchantapp.presentation.signup.steps.BusinessDetailsScreen
//import com.example.merchantapp.presentation.signup.steps.PersonalInfoScreen
//import com.example.merchantapp.presentation.signup.steps.SettlementAccountScreen
//
//sealed interface Screen {
//    @Serializable
//    data object Login : Screen
//
//    @Serializable
//    data object SignUpPersonalInfo : Screen
//
//    @Serializable
//    data object SignUpBusinessDetails : Screen
//
//    @Serializable
//    data object SignUpSettlement : Screen
//
//    @Serializable
//    data object Home : Screen
//}
//
//@Composable
//fun AppNavGraph(
//    navController: NavHostController = rememberNavController()
//) {
//    NavHost(
//        navController = navController,
//        startDestination = Screen.Login
//    ) {
//
//        composable<Screen.Login> {
//            LoginScreen(
//                onNavigateToHome = {
//                    navController.navigate(Screen.Home) {
//                        popUpTo<Screen.Login> { inclusive = true }
//                    }
//                },
//                onNavigateToSignUp = {
//                    navController.navigate(Screen.SignUpPersonalInfo)
//                }
//            )
//        }
//
//        // الخطوة الثانية: المعلومات الشخصية مربوطة بالـ ViewModel مباشرة
//        composable<Screen.SignUpPersonalInfo> {
//            PersonalInfoScreen(
//                onNavigateBack = { navController.popBackStack() },
//                onNavigateNext = { navController.navigate(Screen.SignUpBusinessDetails) }
//            )
//        }
//
//        // الخطوة الرابعة: بيانات النشاط التجاري مربوطة بالـ ViewModel مباشرة
//        composable<Screen.SignUpBusinessDetails> {
//            BusinessDetailsScreen(
//                onNavigateBack = { navController.popBackStack() },
//                onNavigateNext = { navController.navigate(Screen.SignUpSettlement) }
//            )
//        }
//
//        // الخطوة الثامنة: حساب التسوية مربوطة بالـ ViewModel مباشرة
//        composable<Screen.SignUpSettlement> {
//            SettlementAccountScreen(
//                onNavigateBack = { navController.popBackStack() },
//                onCompleteSignUp = {
//                    navController.navigate(Screen.Home) {
//                        popUpTo<Screen.Login> { inclusive = true }
//                    }
//                }
//            )
//        }
//
//        composable<Screen.Home> {
//            HomeScreen()
//        }
//    }
//}
