package com.weathersnap.ui.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.camera.CameraScreen
import com.weathersnap.ui.report.CreateReportScreen
import com.weathersnap.ui.saved.SavedReportsScreen
import com.weathersnap.ui.weather.WeatherScreen

object Routes {
    const val WEATHER = "weather"
    const val CREATE_REPORT = "create_report/{weatherJson}"
    const val CAMERA = "camera"
    const val SAVED_REPORTS = "saved_reports"

    fun createReport(weather: Weather): String {
        val json = Uri.encode(Gson().toJson(weather))
        return "create_report/$json"
    }
}

private val enterTransition: EnterTransition =
    slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))

private val exitTransition: ExitTransition =
    slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(300))

private val popEnterTransition: EnterTransition =
    slideInHorizontally(tween(300)) { -it / 3 } + fadeIn(tween(300))

private val popExitTransition: ExitTransition =
    slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))

@Composable
fun WeatherSnapNavHost() {
    val navController = rememberNavController()
    val gson = remember { Gson() }

    // Shared state for captured image path between camera and report screens
    var capturedImagePath by rememberSaveable { mutableStateOf<String?>(null) }
    // Current weather preserved across navigation (frozen snapshot)
    var currentWeather by remember { mutableStateOf<Weather?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER,
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        composable(Routes.WEATHER) {
            WeatherScreen(
                onCreateReport = { weather ->
                    // Freeze the weather snapshot at this moment
                    currentWeather = weather
                    navController.navigate(Routes.createReport(weather))
                },
                onViewReports = {
                    navController.navigate(Routes.SAVED_REPORTS)
                }
            )
        }

        composable(
            route = Routes.CREATE_REPORT,
            arguments = listOf(
                navArgument("weatherJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Decode weather from route arg (ensures exact snapshot is preserved)
            val weatherJson = backStackEntry.arguments?.getString("weatherJson") ?: ""
            val routeWeather = try {
                gson.fromJson(Uri.decode(weatherJson), Weather::class.java)
            } catch (e: Exception) {
                null
            }

            // Use route weather or fallback to stored weather
            val weather = routeWeather ?: currentWeather

            CreateReportScreen(
                weather = weather,
                capturedImagePath = capturedImagePath,
                onBack = {
                    capturedImagePath = null
                    navController.popBackStack()
                },
                onCapturePhoto = {
                    navController.navigate(Routes.CAMERA)
                },
                onSaved = {
                    capturedImagePath = null
                    currentWeather = null
                    navController.navigate(Routes.SAVED_REPORTS) {
                        popUpTo(Routes.WEATHER) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.CAMERA) {
            CameraScreen(
                onImageCaptured = { path ->
                    capturedImagePath = path
                    navController.popBackStack()
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SAVED_REPORTS) {
            SavedReportsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
