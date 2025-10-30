package com.example.coolpokedex.data.model.pokemonList

data class Pokemon(
    val name: String,
    val url: String,


) {

    val id: Int
        get() = url.trimEnd('/').split("/").last().toInt()
}