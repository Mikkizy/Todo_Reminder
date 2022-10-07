package com.lord_ukaka.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity)
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    @Query("SELECT * FROM todoentity WHERE id = :id")
    suspend fun getTodoById(id: Int): TodoEntity?
    // A flow helps you get streams of data as opposed to a suspend function that gets only one value
    @Query("SELECT * FROM todoentity")
    fun getTodos(): Flow<List<TodoEntity>>
}