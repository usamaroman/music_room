package by.eapp.musicroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import by.eapp.musicroom.navigation.NavHostController
import by.eapp.musicroom.screens.AuthorizationViewModel
import by.eapp.musicroom.screens.components.LoadingScreen
import by.eapp.musicroom.screens.view.submit.SubmitCode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthorizationViewModel by viewModels()
        setContent {
            val navController = rememberNavController()
            NavHostController(navController = navController, viewModel = viewModel)
        }
    }
}

