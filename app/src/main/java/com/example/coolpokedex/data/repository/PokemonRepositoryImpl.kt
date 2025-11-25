package com.example.coolpokedex.data.repository

import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.model.pokemonList.PokemonListResponse
import com.example.coolpokedex.data.network.ApiResponse
import com.example.coolpokedex.data.network.PokeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokeApiService
) : PokemonRepository {
    private val pokemonCache = mutableMapOf<Int, PokemonDetailInfo>()

    override suspend fun getPokemonListWithDetails(offset: Int, limit: Int): ApiResponse<List<PokemonDetailInfo>> {
        return withContext(Dispatchers.IO) {
            try {
                val apiListResponse = getPokemonList(offset, limit)
                when (apiListResponse) {
                    is ApiResponse.Error -> {
                        throw Exception(apiListResponse.message)
                    }

                    is ApiResponse.Loading -> {
                        ApiResponse.Loading
                    }

                    is ApiResponse.Success -> {
                        val pokemonDetailList = apiListResponse.data.pokemons.map { pokemon ->
                            async {
                                getPokemonDetail(pokemon.id.toString())
                            }
                        }.awaitAll()
                        val successfulDetails = pokemonDetailList.mapNotNull {
                            if (it is ApiResponse.Success) {
                                it.data
                            } else {
                                null
                            }
                        }
                        ApiResponse.Success(successfulDetails)
                    }
                }
            } catch (ex: Exception) {
                ApiResponse.Error(ex.message ?: "Error fetching pokemon list with details", ex)
            }
        }
    }

    override suspend fun getPokemonList(offset: Int, limit: Int): ApiResponse<PokemonListResponse> {
        return try {
            withContext(Dispatchers.IO) {
                val response = apiService.getPokemonList(offset = offset, limit = limit)
                ApiResponse.Success(response)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Error fetching pokemon list")
        }
    }

    override suspend fun getPokemonDetail(
        id: String
    ): ApiResponse<PokemonDetailInfo> {
        return try {

            val pokeId = id.toIntOrNull() ?: return ApiResponse.Error("wrong pokemon id")
            pokemonCache[pokeId]?.let {
                return ApiResponse.Success(it)
            }
            withContext(Dispatchers.IO) {
                val response = apiService.getPokemonDetail(id)
                val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
                val entryWithUrl = response.copy(imgUrl = url)
                ApiResponse.Success(entryWithUrl)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Nie udało się pobrać szczegółów Pokémon")
        }
    }
}