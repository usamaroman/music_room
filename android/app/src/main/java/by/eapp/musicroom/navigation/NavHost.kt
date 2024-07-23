package by.eapp.musicroom.navigation


import RegistrationScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
//            val viewModel = AuthorizationViewModel()
//            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable(route = Screens.MainScreen.route) {
            // MainScreen(navController = navController)
        }
    }
}