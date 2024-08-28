package com.isu.apitracker.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.isu.apitracker.data.repository.RepositoryImplementation
import com.isu.apitracker.data.srx.AppDatabase
import com.isu.apitracker.domain.Repository
import com.isu.apitracker.presentation.viewmodel.ApiTrackerViewModel
import com.isu.apitracker.presentation.viewmodel.ViewModelFactory
import com.isu.apitracker.presentation.screens.ApiListScreen
import com.isu.apitracker.presentation.screens.RequestResponseScreen
import com.isu.apitracker.presentation.ui.theme.CustomChuckerTheme
import com.isu.apitracker.util.toEm


class ApiTrackingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContent {
            CustomChuckerTheme {
                val navController= rememberNavController()
                val db= Room
                    .databaseBuilder(
                        this,
                        AppDatabase::class.java,
                        "app_database"
                    )
                    .fallbackToDestructiveMigration().build()
                val dao=db.transactionDataDao()
                val apiTrackerRepository:Repository = RepositoryImplementation(dao)
                val viewModelFactory= ViewModelFactory(apiTrackerRepository)
                val viewModel=ViewModelProvider(this,viewModelFactory).get(ApiTrackerViewModel::class.java)

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
    Text(lineHeight=12.sp.toEm(),
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