package com.example.coolpokedex.data.repository

import android.util.Log
import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.network.ApiResponse
import com.example.coolpokedex.data.network.PokeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val apiService: PokeApiService
) {

    private val pokemonCache = mutableMapOf<Int, PokemonDetailInfo>()

    suspend fun getPokemonListWithDetails(offset: Int, limit: Int): ApiResponse<List<PokemonDetailInfo>> {
        val pokemonList: List<PokemonDetailInfo>
        try {
            pokemonList = withContext(Dispatchers.IO) {
                val result = (offset + 1..offset + limit).map { id ->
                    async {
                        val apiResponse = getPokemonDetail(id.toString())
                        when (apiResponse) {
                            is ApiResponse.Success -> return@async apiResponse.data
                            is ApiResponse.Error -> throw Exception(apiResponse.message)
                            ApiResponse.Loading -> return@async null
                        }
                    }
                }.awaitAll().filterNotNull()
                return@withContext result
            }

        } catch (e: Exception) {
            return ApiResponse.Error(e.message ?: "Error fetching pokemon list")
        }
        return ApiResponse.Success(pokemonList)
    }

    suspend fun getPokemonDetail(
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