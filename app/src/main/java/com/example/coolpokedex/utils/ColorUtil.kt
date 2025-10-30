package com.example.coolpokedex.utils

import androidx.compose.ui.graphics.Color
import com.example.coolpokedex.ui.theme.*

object ColorUtil {

    fun getTypeColor(typeName: String): Color {
        return when (typeName.lowercase()) {
            "grass" -> Color(0xFF78C850)
            "fire" -> Color(0xFFF08030)
            "water" -> Color(0xFF6890F0)
            "bug" -> Color(0xFFA8B820)
            "normal" -> Color(0xFFA8A878)
            "poison" -> Color(0xFFA040A0)
            "electric" -> Color(0xFFF8D030)
            "ground" -> Color(0xFFE0C068)
            "fairy" -> Color(0xFFEE99AC)
            "fighting" -> Color(0xFFC03028)
            "psychic" -> Color(0xFFF85888)
            "rock" -> Color(0xFFB8A038)
            "ghost" -> Color(0xFF705898)
            "ice" -> Color(0xFF98D8D8)
            "dragon" -> Color(0xFF7038F8)
            "dark" -> Color(0xFF705848)
            "steel" -> Color(0xFFB8B8D0)
            "flying" -> Color(0xFFA890F0)
            else -> Color.Gray // Domyślny kolor dla nieznanych typów
        }
    }

    fun getStatColor(
        string: String
    ): Color {
        return when (string.lowercase()) {
            "hp" -> HPColor
            "defense" ->DefColor
            "attack" -> AtkColor
            "special-attack" -> SpAtkColor
            "special-defense" -> SpDefColor
            "speed" -> SpdColor
            else -> Color.White

        }
    }
}