package com.example.todoapp.util

/**
 * Provides compile-time safety for conditions inside "when" block.
 */
val <T> T.exhaustive: T
    get() = this