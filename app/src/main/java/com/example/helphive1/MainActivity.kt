 package com.example.helphive1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.helphive1.model.DBGestor
import com.example.helphive1.ui.composable.home.HomeScreen
import com.example.helphive1.ui.composable.home.HomeViewModel

import com.example.helphive1.ui.composable.loginPage
// import com.example.helphive1.ui.composable.navigationBar.NavigationScreen
import com.example.helphive1.ui.composable.profile.ProfileScreen
import com.example.helphive1.ui.composable.publicationDetail.PublicacionDetalle
// import com.example.helphive1.ui.composable.publicationDetail.PublicacionDetalle
import com.example.helphive1.ui.composable.registerPage
import com.example.helphive1.ui.composable.search.SearchScreen
import com.example.helphive1.ui.composable.search.SearchViewModel
import com.example.helphive1.ui.theme.HelpHive1Theme
import com.google.firebase.auth.FirebaseAuth

 sealed class Screen(val ruta:String){


     object LOGIN : Screen( "Login")
     object REGISTER : Screen("Register")
     object PROFILE : Screen( "Profile")
     object HOME : Screen("Home")
     object SEARCH : Screen("Search")
     object PROFILE_AUTOR : Screen("ProfileAutor/{id}")
     object PUBLICATION_DETAILS : Screen("PublicationDetails/{publicacionId}")
 }


 class MainActivity : ComponentActivity() {

     val firebaseAuth = FirebaseAuth.getInstance()
     val currentUser = firebaseAuth.currentUser
   //  val usuarioId = currentUser?.uid ?: ""


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContent {
             HelpHive1Theme {
                 // A surface container using the 'background' color from the theme

                 //       loginPage()
                 //         HomeScreen()

                 val navController = rememberNavController()
                 val homeViewModel = viewModel<HomeViewModel>()
                 val searchViewModel = viewModel<SearchViewModel>()

                 NavHost(
                     navController = navController,
                     startDestination = Screen.LOGIN.ruta
                 ) {
                     // Definimos las rutas de las pantallas

                     // Pantalla login
                     composable(Screen.LOGIN.ruta) {
                         loginPage(navController = navController)
                     }
                     // Pantalla de registro
                     composable(Screen.REGISTER.ruta) {
                         registerPage(onRegisterClicked = {
                             navController.navigate(Screen.LOGIN.ruta)
                         }, databaseManager = DBGestor())
                     }

                     composable(Screen.PROFILE.ruta) {
                         ProfileScreen(navController = navController )
                     }

                     composable(Screen.PROFILE_AUTOR.ruta + "/{id}"){ backStackEntry ->
                         val id = backStackEntry.arguments?.getString("id")


                         if (id != null) {
                             //   PublicacionDetalle(publicacionId = publicacionId)
                             ProfileScreen(navController = navController, profileID = id)

                             val mensaje = "Usuario encontrado : $id"
                             Log.d("Etiqueta", mensaje)
                         }
                     }

                     composable(Screen.HOME.ruta) {
                         HomeScreen(navController = navController, publications = homeViewModel.publicaciones.value )
                     }

/*
                     composable(Screen.PROFILE_AUTOR.ruta + "/{id}"){ backStackEntry ->
                         val id = backStackEntry.arguments?.getString("id")


                         if (id != null) {
                             //   PublicacionDetalle(publicacionId = publicacionId)
                             ProfileScreen(navController = navController, profileID = id)

                             val mensaje = "Usuario encontrado : $id"
                             Log.d("Etiqueta", mensaje)
                         }
                     }


 */


                     composable(Screen.PUBLICATION_DETAILS.ruta + "/{publicacionId}") { backStackEntry ->
                         val publicacionId = backStackEntry.arguments?.getString("publicacionId")

                         if (publicacionId != null) {
                          //   PublicacionDetalle(publicacionId = publicacionId)
                             PublicacionDetalle(publicacionId = publicacionId, nav = navController)

                             val mensaje = "Publicacion encontrada : $publicacionId"
                             Log.d("Etiqueta", mensaje)
                         }
                     }


                    composable(Screen.SEARCH.ruta){
                        SearchScreen(navController = navController , searchViewModel = searchViewModel )
                    }
                 }


             }
         }
     }
 }