package com.example.notes.model

import android.content.Context
import com.example.notes.R
import com.example.notes.model.enums.SystemUI

class SharedPref {
    companion object {
        private const val NOTE_COUNTER_KEY = "counter"
        fun getNoteCounter(context: Context): Int {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_pref_gallery),
                Context.MODE_PRIVATE
            )
            return sharedPref.getInt(NOTE_COUNTER_KEY, 0)
        }

        fun setNoteCounter(context: Context, newCounter: Int) {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_pref_gallery),
                Context.MODE_PRIVATE
            )
            sharedPref.edit().putInt(NOTE_COUNTER_KEY, newCounter).apply()
        }

        private const val SYSTEM_UI_KEY = "system_ui"
        fun getSystemUI(context: Context): SystemUI {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_pref_gallery),
                Context.MODE_PRIVATE
            )

            val savedStatus = sharedPref.getString(SYSTEM_UI_KEY, SystemUI.FRAGMENTS.toString())
            val systemUIString = savedStatus ?: SystemUI.FRAGMENTS.toString()
            return SystemUI.valueOf(systemUIString)
        }

        fun setSystemUI(context: Context, systemUI: SystemUI) {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_pref_gallery),
                Context.MODE_PRIVATE
            )
            sharedPref.edit().putString(SYSTEM_UI_KEY, systemUI.toString()).apply()
        }
    }
}