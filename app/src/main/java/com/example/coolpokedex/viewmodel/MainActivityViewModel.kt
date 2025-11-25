package com.example.coolpokedex.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.network.ApiResponse
import com.example.coolpokedex.data.network.RetrofitInstance
import com.example.coolpokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch


class MainActivityViewModel : ViewModel() {

    private val pokemonRepository = PokemonRepository(RetrofitInstance.api)
    private val _pokemonListState = mutableStateOf(PokemonListUiState())
    val pokemonListState: State<PokemonListUiState> = _pokemonListState

    private var currentPage = 0
    private var limit = 20
    private var endReached = false

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        if (pokemonListState.value.isLoading || endReached) {
            return
        }
        viewModelScope.launch() {

            _pokemonListState.value = _pokemonListState.value.copy(isLoading = true, error = null)
            val offset = currentPage * limit
            val result = pokemonRepository.getPokemonListWithDetails(offset, limit)
            when (result) {
                is ApiResponse.Success -> {
                    _pokemonListState.value = _pokemonListState.value.copy(
                        pokemons = _pokemonListState.value.pokemons + result.data,
                        isLoading = false
                    )
                    if (result.data.size < limit) endReached = true
                    currentPage++
                }

                is ApiResponse.Error -> {
                    _pokemonListState.value = _pokemonListState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }

                is ApiResponse.Loading -> {
                    _pokemonListState.value.isLoading == true
                }
            }
        }
    }
}

data class PokemonListUiState(
    val isLoading: Boolean = false,
    val pokemons: List<PokemonDetailInfo> = emptyList(),
    val error: String? = null
)
