package com.isu.apitracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.isu.apitracker.presentation.screens.ApiListScreen
import com.isu.apitracker.presentation.screens.RequestResponseScreen
import com.isu.apitracker.ui.theme.CustomChuckerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApiTrackingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContent {
            CustomChuckerTheme {
                val navController= rememberNavController()
                val viewModel:ApiTrackerViewModel= hiltViewModel()
                NavHost(navController =navController , startDestination ="HomeScreen" ) {
                    composable("HomeScreen"){
                        ApiListScreen(navController, viewModel)
                    }
                    composable("RequestResponseScreen"){
                        RequestResponseScreen(navController, viewModel)
                    }
                }

            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    CustomChuckerTheme {
        Greeting2("Android")
    }
}