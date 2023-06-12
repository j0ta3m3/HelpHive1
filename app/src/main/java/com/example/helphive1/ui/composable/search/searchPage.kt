package com.example.helphive1.ui.composable.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helphive1.Screen
import com.example.helphive1.model.Publication
import com.example.helphive1.reusable.NavigationButtons

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel) {
    val context = LocalContext.current
    val searchQueryMutableState = remember { mutableStateOf("") }
    val searchQuery = searchQueryMutableState.value
    val setSearchQuery: (String) -> Unit = { newValue ->
        searchQueryMutableState.value = newValue
    }

    val searchResults by searchViewModel.searchResults.observeAsState(emptyList())

    val onSearchClicked: () -> Unit = {
        searchViewModel.searchPublicationsByLocation(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Publicaciones") },
                backgroundColor = Color(android.graphics.Color.parseColor("#aaeeaa")),
                contentColor = Color.White
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFB700.toInt()),
                            Color(0xFFFFC300.toInt()),
                            Color(0xFFFFDD00.toInt()),
                            Color(0xFFFFEA00.toInt())
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchQuery,
                    onValueChange = { setSearchQuery(it) },
                    label = { Text("Buscar por localidad") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onSearchClicked,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text("Buscar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (searchResults.isNotEmpty()) {
                LazyColumn {
                    items(searchResults.take(10)) { publication ->
                        PublicationItem(publication = publication, navController = navController)
                    }
                }
            } else {
                Text("No se encontraron resultados")
            }

        }
    }
}

@Composable
fun PublicationItem(publication: Publication, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.PUBLICATION_DETAILS.ruta + "/${publication.id}") }
            .padding(16.dp),
        backgroundColor = Color.White,
        elevation = 8.dp,
        contentColor = Color.DarkGray
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = publication.titulo,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = publication.descripcion,
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remuneración: ${publication.remuneracion} €",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Categoría: ${publication.categoria}",
                style = MaterialTheme.typography.body2
            )
        }
    }
}
