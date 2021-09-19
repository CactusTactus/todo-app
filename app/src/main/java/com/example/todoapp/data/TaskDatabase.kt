package com.example.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todoapp.di.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>, // remove circular dependency
        @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao
            val tasks = mutableListOf<Task>().apply {
                add(Task(name = "Task 0", isCompeted = true, isImportant = true))
            }
            (1..5).forEach { i -> tasks.add(Task(name = "Task $i")) }

            coroutineScope.launch {
                dao.insertAll(tasks)
            }
        }
    }
}