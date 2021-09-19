package com.example.todoapp.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val taskEventChannel = Channel<TaskEvent>()
    val taskEventFlow = taskEventChannel.receiveAsFlow()

    val task = savedStateHandle.get<Task>(Constants.KEY_TASK)

    var taskName =
        savedStateHandle.get<String>(Constants.KEY_TASK_NAME) ?: task?.name ?: ""
        set(value) {
            field = value
            savedStateHandle.set(Constants.KEY_TASK_NAME, value)
        }

    var taskIsImportant =
        savedStateHandle.get<Boolean>(Constants.KEY_TASK_IS_IMPORTANT) ?: task?.isImportant ?: false
        set(value) {
            field = value
            savedStateHandle.set(Constants.KEY_TASK_IS_IMPORTANT, value)
        }

    fun onSaveTaskClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskName, isImportant = taskIsImportant)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, isImportant = taskIsImportant)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage(message: String) {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.ShowInvalidInputMessage(message))
        }
    }

    private fun createTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
            taskEventChannel.send(TaskEvent.NavigateBackWithResult(Constants.ADD_TASK_RESULT_OK))
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
            taskEventChannel.send(TaskEvent.NavigateBackWithResult(Constants.EDIT_TASK_RESULT_OK))
        }
    }

    sealed class TaskEvent() {
        data class ShowInvalidInputMessage(val message: String) : TaskEvent()
        data class NavigateBackWithResult(val result: Int) : TaskEvent()
    }
}