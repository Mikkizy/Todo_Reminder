package com.lord_ukaka.todoapp.util

sealed class TodoEvents {
    object PopBackStack: TodoEvents()
    data class Navigate(val route: String): TodoEvents()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): TodoEvents()
}
