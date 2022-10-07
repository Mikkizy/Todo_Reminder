package com.lord_ukaka.todoapp.data

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: TodoEntity)

    suspend fun deleteTodo(todo: TodoEntity)

    suspend fun getTodoById(id: Int): TodoEntity?

    fun getTodos(): Flow<List<TodoEntity>>
}