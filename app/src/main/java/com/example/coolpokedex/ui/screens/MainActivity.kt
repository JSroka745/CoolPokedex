package com.example.coolpokedex.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.ui.theme.CoolPokedexTheme
import com.example.coolpokedex.utils.ColorUtil
import com.example.coolpokedex.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CoolPokedexTheme {
                myApp()
            }
        }
    }
}

@Serializable
object PokemonList

@Serializable
data class PokemonDetail(var pokeId: Int = 0)


@Composable
fun myApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = PokemonList, modifier = modifier) {
        composable<PokemonList> {
            PokemonListScreen(
                viewModel = hiltViewModel<MainActivityViewModel>(),
                onPokemonClick = { id ->
                    navController.navigate(PokemonDetail(id))
                }
            )
        }

        composable<PokemonDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<PokemonDetail>()

            PokemonDetailScreen(
                id = args.pokeId,
                onSwipeLeft = {
                    val nextId = args.pokeId + 1
                    navController.navigate(PokemonDetail(nextId)) {
                        popUpTo(PokemonDetail(args.pokeId)) {
                            inclusive = true
                        }
                    }
                },
                onSwipeRight = {
                    if (args.pokeId > 1) {
                        val prevId = args.pokeId - 1
                        navController.navigate(PokemonDetail(prevId)) {
                            popUpTo(PokemonDetail(args.pokeId)) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PokemonListScreen(onPokemonClick: (Int) -> Unit, viewModel: MainActivityViewModel = hiltViewModel<MainActivityViewModel>()) {
    val pokemons = viewModel.pokemonListState.value.pokemons
    val error = viewModel.pokemonListState.value.error
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(
                message = "Błąd: $error",
            )
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem.index >= totalItems - 9
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadPokemonList()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (pokemons.isNotEmpty()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.padding(innerPadding)
            ) {
                items(items = pokemons, key = { pokes -> pokes.id }) { pokemon ->
                    PokemonEntry(
                        pokemon = pokemon,
                        modifier = Modifier,
                        onPokemonClick = onPokemonClick
                    )
                }
            }
        }
    }
}


@Composable
fun PokemonEntry(
    pokemon: PokemonDetailInfo,
    modifier: Modifier = Modifier,
    onPokemonClick: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onPokemonClick(pokemon.id) }
                .padding(horizontal = 1.dp, vertical = 8.dp)
        ) {

            //id
            Text(
                text = pokemon.id.toString(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.10f)
            )
            //image
            AsyncImage(
                model = pokemon.imgUrl,
                contentDescription = "Obrazek pokemona ${pokemon.name}",
                modifier = Modifier
                    .weight(0.20f)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.30f)
            )

            Text(
                text = pokemon.types[0].type.name,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(0.2f)
                    .padding(2.dp)
                    .background(color = ColorUtil.getTypeColor(pokemon.types[0].type.name), shape = RoundedCornerShape(4.dp))
                    .padding(1.dp)
            )

            if (pokemon.types.size > 1) {
                Text(
                    text = pokemon.types[1].type.name,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(1.dp)
                        .background(
                            color = ColorUtil.getTypeColor(pokemon.types[1].type.name),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(2.dp)
                )
            } else {
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoolPokedexTheme {
        Scaffold { innerPadding ->
            myApp(Modifier.padding(innerPadding))
        }
    }
}