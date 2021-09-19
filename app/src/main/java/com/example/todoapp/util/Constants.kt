package com.example.todoapp.util

import android.app.Activity

object Constants {
    const val KEY_TASK = "task"
    const val KEY_TASK_NAME = "task_name"
    const val KEY_TASK_IS_IMPORTANT = "task_is_important"
    const val KEY_SEARCH_QUERY = "search_query"
    const val KEY_TASK_OPERATION_RESULT = "task_operation_result"

    const val REQUEST_KEY_TASK_OPERATION_RESULT = "request_task_operation_result"

    const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
    const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
}