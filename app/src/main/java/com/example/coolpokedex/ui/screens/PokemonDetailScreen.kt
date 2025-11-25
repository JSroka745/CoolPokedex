package com.example.coolpokedex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.model.pokemon.Stat
import com.example.coolpokedex.utils.ColorUtil
import com.example.coolpokedex.viewmodel.PokemonDetailScreenViewModel

@Composable
fun PokemonDetailScreen(id: Int, onSwipeLeft: () -> Unit = {}, onSwipeRight: () -> Unit = {}, viewModel: PokemonDetailScreenViewModel = hiltViewModel<PokemonDetailScreenViewModel>()) {


    val pokemonState = viewModel.pokemonDetailScreenState


    LaunchedEffect(true) {
        viewModel.loadPokemon(id.toString())
    }

    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // Po zakończeniu przeciągania resetujemy przesunięcie
                        offsetX = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        offsetX += dragAmount
                        // Ustawiamy próg, po którym gest jest uznawany za "swipe"
                        val swipeThreshold = 200 // Możesz dostosować tę wartość

                        if (offsetX > swipeThreshold) {
                            onSwipeRight()
                            offsetX = 0f
                        } else if (offsetX < -swipeThreshold) {
                            onSwipeLeft()
                            offsetX = 0f // Reset
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ){


    when {
        pokemonState.value.isLoading -> {
            Text("Ładuje pokeomna o id: ${id}")
        }

        pokemonState.value.error != null -> {
            Text("Błąd: ${pokemonState.value.error}")

            Button(onClick = {
                viewModel.loadPokemon(id.toString())
            }) {
                Text(text = "retry")
            }
        }

        pokemonState.value.pokemon != null -> {
            PokemonDetail(pokemonState.value.pokemon!!)
        }
    }}

}

@Composable
fun PokemonDetail(pokemon: PokemonDetailInfo, topPadding: Dp = 60.dp) {

    val scrollState = rememberScrollState()

    Surface(
        color = ColorUtil.getTypeColor(pokemon.types[0].type.name),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .zIndex(1f)
                    .weight(0.2f)
                    .fillMaxSize()
                //.align(Alignment.CenterHorizontally)
            ) {
                AsyncImage(
                    model = pokemon.imgUrl,
                    contentDescription = "pokemon id:${pokemon.id}",
                    // alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = topPadding),
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                // shape = AbsoluteCutCornerShape(topLeft = 24.dp, topRight = 24.dp, bottomLeft = 0.dp, bottomRight = 0.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .weight(0.8f)
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = topPadding / 1.5f, start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .verticalScroll(scrollState)
                ) {
                    BaseInfo(pokemon)
                    PokemonStats(pokemon.stats)
                }
            }
        }
    }
}

@Composable
fun BaseInfo(pokemon: PokemonDetailInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "no.${pokemon.id}",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = pokemon.name.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Row {
            Icon(imageVector = Icons.Default.Height, contentDescription = "height")
            val heightMeters = pokemon.height / 10f

            Text(text = "Height: ${"%.1f".format(heightMeters)} m")
        }

        Row {
            val weightKg = pokemon.weight / 10f
            Icon(imageVector = Icons.Default.Scale, contentDescription = "weight")
            Text(text = "Weight: ${"%.1f".format(weightKg)} kg")
        }
    }

}

@Composable
fun PokemonStats(stats: List<Stat>) {

    Surface(
        modifier = Modifier.padding(horizontal = 3.dp, vertical = 10.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 50.dp,
        tonalElevation = 10.dp

    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            stats.forEach { stat ->
                Statistic(stat.stat.name, stat.base_stat, 200, ColorUtil.getStatColor(stat.stat.name))
            }
        }
    }
}

@Composable
fun Statistic(statName: String, value: Int, maxValue: Int, color: Color) {

    val fraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    Column(
    ) {
        Text(text = statName)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(color = Color.Gray, shape = CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(20.dp)
                    .background(color = color, shape = CircleShape)
            ) {
                Text(
                    text = value.toString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
