package com.example.todoapp.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.TaskDao
import com.example.todoapp.di.ApplicationCoroutineScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope, // viewModelScope destroys after fragment dismisses (right after button click)
): ViewModel() {
    fun onConfirmClick() {
        coroutineScope.launch {
            taskDao.deleteAllCompletedTasks()
        }
    }
}