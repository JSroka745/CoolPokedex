package com.example.coolpokedex.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.network.ApiResponse
import com.example.coolpokedex.data.network.RetrofitInstance
import com.example.coolpokedex.data.repository.PokemonRepository
import androidx.compose.runtime.State
import kotlinx.coroutines.launch

class PokemonDetailScreenViewModel : ViewModel() {

    private val pokemonRepository = PokemonRepository(RetrofitInstance.api)
    private val _pokemonDetailScreenState = mutableStateOf(PokemonDetailUiState(pokemon = null))
    val pokemonDetailScreenState: State<PokemonDetailUiState> = _pokemonDetailScreenState

    fun loadPokemon(id: String) {
        if (!_pokemonDetailScreenState.value.isLoading) {
            viewModelScope.launch {
                _pokemonDetailScreenState.value = _pokemonDetailScreenState.value.copy(isLoading = true)
                val result = pokemonRepository.getPokemonDetail(id)

                when (result) {
                    is ApiResponse.Error -> _pokemonDetailScreenState.value =
                        _pokemonDetailScreenState.value.copy(error = result.message, isLoading = false)

                    is ApiResponse.Success -> _pokemonDetailScreenState.value =
                        _pokemonDetailScreenState.value.copy(isLoading = false, error = null, pokemon = result.data)

                    ApiResponse.Loading -> {}
                }

            }
        }

    }
}

data class PokemonDetailUiState(
    val isLoading: Boolean = false,
    val pokemon: PokemonDetailInfo?,
    val error: String? = null
)