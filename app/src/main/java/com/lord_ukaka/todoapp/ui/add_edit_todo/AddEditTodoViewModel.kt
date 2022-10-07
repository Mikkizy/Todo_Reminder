package com.lord_ukaka.todoapp.ui.add_edit_todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lord_ukaka.todoapp.data.TodoEntity
import com.lord_ukaka.todoapp.data.TodoRepository
import com.lord_ukaka.todoapp.util.TodoEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    // We need to define savedstatehandle that saves the state of the UI during system deaths of
    // the app lifecycle.
    savedStateHandle: SavedStateHandle
): ViewModel() {
    var todo by mutableStateOf<TodoEntity?>(null)
        //Private set is used to make the variable modifiable only within the viewModel.
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    private val _todoEvent = Channel<TodoEvents>()
    val todoEvent = _todoEvent.receiveAsFlow()

    init {
        val todoId  = savedStateHandle.get<Int>("todoId")!!
        // If todoId is not equal to -1, that means we clicked on an existing todo_task
        if (todoId != -1) {
            viewModelScope.launch {
                repository.getTodoById(todoId)?.let { todo ->
                    title = todo.title
                    description = todo.description?: ""
                    this@AddEditTodoViewModel.todo = todo
                }
                // I think the code below may work too.
                /*todo = repository.getTodoById(todoId)
                title = todo?.title ?: ""
                description = todo?.description ?: ""
                this@AddEditTodoViewModel.todo = todo*/
            }
        }
    }
    fun onEvent(event: AddEditTodoEvents) {
        when (event) {
            is AddEditTodoEvents.OnTitleChange -> {
                title = event.title
            }
            is AddEditTodoEvents.OnDescriptionChange -> {
                description = event.description
            }
            is AddEditTodoEvents.OnSaveTodo -> {
                viewModelScope.launch {
                    if (title.isBlank()) {
                        sendTodoEvents(TodoEvents.ShowSnackbar("Title cannot be empty"))
                        return@launch
                    }
                    repository.insertTodo(
                        TodoEntity(
                            title = title,
                            description = description,
                            isDone = todo?.isDone ?: false,
                            id = todo?.id
                        )
                    )
                    sendTodoEvents(TodoEvents.PopBackStack)
                }
            }
        }
    }
    private fun sendTodoEvents(event: TodoEvents) {
        //Create a coroutine and bind to the lifetime of the viewModel asynchronously
        viewModelScope.launch {
            _todoEvent.send(event)
        }

    }
}