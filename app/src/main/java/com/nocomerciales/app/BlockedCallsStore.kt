package com.nocomerciales.app

import android.content.Context

/**
 * Registro local (sin red, sin base de datos) de las llamadas bloqueadas,
 * para poder mostrarlas en la pantalla principal.
 */
data class BlockedCall(val number: String, val timestamp: Long)

object BlockedCallsStore {

    private const val PREFS_NAME = "blocked_calls"
    private const val KEY_ENTRIES = "entries"
    private const val MAX_ENTRIES = 200
    private const val FIELD_SEPARATOR = "|"
    private const val ENTRY_SEPARATOR = "\n"

    @Synchronized
    fun addBlockedCall(context: Context, number: String, timestamp: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_ENTRIES, "") ?: ""
        val newEntry = "$timestamp$FIELD_SEPARATOR$number"
        val updated = if (existing.isBlank()) {
            newEntry
        } else {
            (listOf(newEntry) + existing.split(ENTRY_SEPARATOR)).take(MAX_ENTRIES).joinToString(ENTRY_SEPARATOR)
        }
        prefs.edit().putString(KEY_ENTRIES, updated).apply()
    }

    @Synchronized
    fun getBlockedCalls(context: Context): List<BlockedCall> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ENTRIES, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(ENTRY_SEPARATOR).mapNotNull { line ->
            val parts = line.split(FIELD_SEPARATOR, limit = 2)
            val timestamp = parts.getOrNull(0)?.toLongOrNull()
            val number = parts.getOrNull(1)
            if (timestamp != null && number != null) BlockedCall(number, timestamp) else null
        }
    }
}
