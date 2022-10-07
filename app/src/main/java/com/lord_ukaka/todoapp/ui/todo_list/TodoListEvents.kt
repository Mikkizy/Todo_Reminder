package com.lord_ukaka.todoapp.ui.todo_list

import com.lord_ukaka.todoapp.data.TodoEntity

sealed class TodoListEvents {
    data class DeleteTodo(val todo: TodoEntity): TodoListEvents()
    data class OnClickCheckBox(val todo: TodoEntity, val isDone: Boolean): TodoListEvents()
    object OnUndoDeleteClick: TodoListEvents()
    data class OnTodoClick(val todo: TodoEntity): TodoListEvents()
    object OnAddButtonClick: TodoListEvents()
}
