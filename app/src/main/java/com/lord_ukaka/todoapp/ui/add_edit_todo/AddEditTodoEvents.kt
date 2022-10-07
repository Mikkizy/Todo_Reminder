package com.lord_ukaka.todoapp.ui.add_edit_todo

sealed class AddEditTodoEvents {
    data class OnTitleChange(val title: String): AddEditTodoEvents()
    data class OnDescriptionChange(val description: String): AddEditTodoEvents()
    object OnSaveTodo: AddEditTodoEvents()
}
