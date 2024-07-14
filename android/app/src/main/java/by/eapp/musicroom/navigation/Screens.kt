package by.eapp.musicroom.navigation

sealed class Screens(
    val route: String
) {
    data object RegistrationScreen: Screens("registration")
    data object LoginScreen: Screens("login")
    data object MainScreen: Screens("main")
}