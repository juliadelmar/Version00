package com.example.version00.ui.model

data class MealResponse(
    val meals: List<Meal>?
)
data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strMealThumb: String
)

// Para facilitar el uso en UI, puedes crear una extensión o función que transforme Meal en algo más usable:
data class MealUIModel(
    val id: String,
    val name: String,
    val category: String?,
    val thumbnail: String
)

fun Meal.toUIModel() = MealUIModel(
    id = idMeal,
    name = strMeal,
    category = strCategory,
    thumbnail = strMealThumb
)
