package com.example.coolpokedex.data.model.pokemonList

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    val count: Int,
    val next: String,
    val previous: Any,

    @SerializedName("results")
    val pokemons: List<Pokemon>
)