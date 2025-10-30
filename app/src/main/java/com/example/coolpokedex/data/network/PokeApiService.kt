package com.example.coolpokedex.data.network

import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.model.pokemonList.PokemonListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.String

interface PokeApiService {
    @GET("pokemon/{idOrName}")
    suspend fun getPokemonDetail(@Path("idOrName") idOrName: String): PokemonDetailInfo

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonListResponse

}