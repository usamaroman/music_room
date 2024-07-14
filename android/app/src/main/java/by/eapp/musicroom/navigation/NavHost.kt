package by.eapp.musicroom.navigation


import RegistrationScreen
import androidx.compose.animation.AnimatedContentScope

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import by.eapp.musicroom.screens.login.LoginScreen

@Composable
fun NavHostController(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.RegistrationScreen.route
    ) {
        composable(
            route = Screens.RegistrationScreen.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = LinearOutSlowInEasing
                    )
                )
            },

        ) {
            RegistrationScreen(navController = navController)
        }
        composable(
            route = Screens.LoginScreen.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = LinearOutSlowInEasing
                    )
                )
            },

        ) {
            LoginScreen(navController = navController)
        }
        composable(route = Screens.MainScreen.route) {
            // MainScreen(navController = navController)
        }
    }
}