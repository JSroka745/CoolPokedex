package com.example.coolpokedex.data.repository

import com.example.coolpokedex.data.model.pokemon.PokemonDetailInfo
import com.example.coolpokedex.data.model.pokemonList.PokemonListResponse
import com.example.coolpokedex.data.network.ApiResponse

interface PokemonRepository {

    suspend fun getPokemonListWithDetails(offset: Int, limit: Int): ApiResponse<List<PokemonDetailInfo>>

    suspend fun getPokemonList(offset: Int, limit: Int): ApiResponse<PokemonListResponse>

    suspend fun getPokemonDetail(id: String): ApiResponse<PokemonDetailInfo>
}