package com.example.todoapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortingOrder {
    BY_NAME,
    BY_DATE,
}

data class FilterPreferences(
    val sortingOrder: SortingOrder,
    val hideCompleted: Boolean,
)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private object PreferenceKeys {
        val SORTING_ORDER = stringPreferencesKey("sorting_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_PREFERENCES)
    private val dataStore: DataStore<Preferences> = context.dataStore

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortingOrder = SortingOrder.valueOf(
                preferences[PreferenceKeys.SORTING_ORDER] ?: SortingOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferenceKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortingOrder, hideCompleted)
        }

    suspend fun updateSortingOrder(sortingOrder: SortingOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SORTING_ORDER] = sortingOrder.name
        }
    }

    suspend fun updateHideCompeted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    companion object {
        const val APP_PREFERENCES = "app_preferences"
    }
}