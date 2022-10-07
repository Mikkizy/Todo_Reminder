package com.lord_ukaka.todoapp.ui.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lord_ukaka.todoapp.data.TodoEntity
import com.lord_ukaka.todoapp.data.TodoRepository
import com.lord_ukaka.todoapp.util.Routes
import com.lord_ukaka.todoapp.util.TodoEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {
    val todos = repository.getTodos()
    //Receive a set of events as flow.
    //Channels are used instead of states for one-time events that returns streams of values or flows.
// These are mostly for events that you do not want to preserve after recomposition, eg, after screen rotation.
// You can send and receive values from channels using .send(value) or receive() respectively
    private val _todoEvent = Channel<TodoEvents>()
    val todoEvent = _todoEvent.receiveAsFlow()

    private var deletedTodo: TodoEntity? = null

    fun onEvent(event: TodoListEvents) {
        when (event) {
            is TodoListEvents.DeleteTodo -> {
                viewModelScope.launch {
                    deletedTodo = event.todo
                    repository.deleteTodo(event.todo)
                    sendTodoEvents(TodoEvents.ShowSnackbar("Task has been deleted!",
                        "Undo"))
                }
            }
            is TodoListEvents.OnClickCheckBox -> {
                viewModelScope.launch {
                    repository.insertTodo(todo = event.todo.copy(
                        isDone = event.isDone
                    ))
                }
            }
            is TodoListEvents.OnTodoClick -> {
                //Here we want to navigate to the add_edit screen while maintaining the states.
                sendTodoEvents(TodoEvents.Navigate(
                    Routes.ADD_EDIT_TODO + "?todoId=${event.todo.id}"))
            }
            is TodoListEvents.OnAddButtonClick -> {
               sendTodoEvents(TodoEvents.Navigate(Routes.ADD_EDIT_TODO))
            }
            is TodoListEvents.OnUndoDeleteClick -> {
                deletedTodo?.let { todo ->
                    viewModelScope.launch {
                        repository.insertTodo(todo)
                    }
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

