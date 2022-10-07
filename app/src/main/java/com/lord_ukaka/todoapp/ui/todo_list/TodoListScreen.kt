package com.lord_ukaka.todoapp.ui.todo_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lord_ukaka.todoapp.util.TodoEvents
import kotlinx.coroutines.flow.collect

@Composable
fun TodoListScreen(
    onNavigate: (TodoEvents.Navigate) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    // Collect the todo_tasks as a state
    val todos = viewModel.todos.collectAsState(initial = emptyList())
    val scaffoldState  = rememberScaffoldState()
    // We need a LaunchedEffect because it helps us run suspend functions in the scope of a composable.
    // It also runs once, during the creation of the composable and when the key changes.
    LaunchedEffect(key1 = true) {
        //Collect the events which were sent as flow
       viewModel.todoEvent.collect { event ->
           when(event) {
               is TodoEvents.ShowSnackbar -> {
                   // Use scaffoldstate to show snackbar.
                   // The result shows the action state of the snackbar.
                   val result = scaffoldState.snackbarHostState.showSnackbar(
                       event.message, event.action
                   )
                   if (result == SnackbarResult.ActionPerformed) {
                       viewModel.onEvent(TodoListEvents.OnUndoDeleteClick)
                   }
               }
               is TodoEvents.Navigate -> onNavigate(event)
               else -> Unit
           }
       }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(TodoListEvents.OnAddButtonClick) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize() ){
            items(todos.value) { todo ->
               TodoItem(
                   todo = todo,
                   onEvent = viewModel::onEvent,
                   modifier =  Modifier
                       .fillMaxWidth()
                       .clickable {
                           viewModel.onEvent(TodoListEvents.OnTodoClick(todo))
                       }
                       .padding(16.dp)
               )
            }
        }
    }
}