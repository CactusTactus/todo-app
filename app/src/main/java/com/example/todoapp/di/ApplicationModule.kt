package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context,
        callback: TaskDatabase.Callback,
    ): TaskDatabase {
        return Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(taskDatabase: TaskDatabase): TaskDao = taskDatabase.taskDao

    @Provides
    @Singleton
    @ApplicationCoroutineScope
    fun provideApplicationScope(): CoroutineScope =  CoroutineScope(SupervisorJob())
}