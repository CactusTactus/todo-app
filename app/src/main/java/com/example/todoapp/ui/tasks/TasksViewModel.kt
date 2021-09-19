package com.example.todoapp.ui.tasks

import androidx.lifecycle.*
import com.example.todoapp.data.PreferencesManager
import com.example.todoapp.data.SortingOrder
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tasksEventChannel = Channel<TasksEvent>()

    val searchQueryLiveData = savedStateHandle.getLiveData(Constants.KEY_SEARCH_QUERY, "")

    val tasksEventFlow = tasksEventChannel.receiveAsFlow()
    val preferencesFlow = preferencesManager.preferencesFlow

    @ExperimentalCoroutinesApi
    private val tasksFlow = combine(
        searchQueryLiveData.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortingOrder, filterPreferences.hideCompleted)
    }

    @ExperimentalCoroutinesApi
    val tasks = tasksFlow.asLiveData()

    fun onSortingOrderSelected(sortingOrder: SortingOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortingOrder(sortingOrder)
        }
    }

    fun onHideCompletedClick(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompeted(hideCompleted)
        }
    }


    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(isCompeted = isChecked))
        }
    }

    fun onTaskSwiped(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
        }
    }

    fun onUndoDeleteClick(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun onAddNewTaskClick() {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
        }
    }

    fun onTaskOperationResult(result: Int) {
        when (result) {
            Constants.ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            Constants.EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(message: String) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(message))
        }
    }

    fun onDeleteAllCompletedClick() {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
        }
    }

    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent() // always the same action -> object is more performant
        object NavigateToDeleteAllCompletedScreen: TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val message: String) : TasksEvent()
    }
}

