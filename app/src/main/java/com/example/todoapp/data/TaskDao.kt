package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task WHERE is_completed = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("SELECT * FROM task WHERE (is_completed != :hideCompleted OR is_completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY is_important DESC, name") // concat string
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE (is_completed != :hideCompleted OR is_completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY is_important DESC, created_date") // concat string
    fun getTasksSortedByCreatedDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    fun getTasks(searchQuery: String, sortingOrder: SortingOrder, hideCompleted: Boolean): Flow<List<Task>> {
        return when(sortingOrder) {
            SortingOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
            SortingOrder.BY_DATE -> getTasksSortedByCreatedDate(searchQuery, hideCompleted)
        }
    }
}