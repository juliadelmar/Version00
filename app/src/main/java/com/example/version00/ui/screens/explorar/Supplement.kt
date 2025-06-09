package com.example.version00.ui.screens.explorar

data class SupplementResponse(
    val common: List<SupplementCommon> = emptyList(),
    val branded: List<SupplementBranded> = emptyList()
)

data class SupplementCommon(
    val food_name: String,
    val serving_qty: Double,
    val serving_unit: String,
    val nf_calories: Double,
    val photo: Photo?
)

data class SupplementBranded(
    val brand_name: String,
    val food_name: String,
    val serving_qty: Double,
    val serving_unit: String,
    val nf_calories: Double,
    val photo: Photo?
)

data class Photo(
    val thumb: String?
)
