package com.example.notes.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesLocalDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyDatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT NOT NULL," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_TIMESTAMP LONG NOT NULL)"

    fun insertNote(noteData: NoteData): Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TITLE, noteData.title)
            put(COLUMN_CONTENT, noteData.content)
            put(COLUMN_TIMESTAMP, noteData.createTime)
        }

        return db.insert(TABLE_NAME, null, values).toInt()
    }

    fun removeNote(noteID: Int): Int {
        val db = writableDatabase

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(noteID.toString())

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    fun readOneNote(noteID: Int): NoteData {
        val db = readableDatabase

        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_CONTENT,
            COLUMN_TIMESTAMP
        )

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(noteID.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (!cursor.moveToFirst()) {
            throw IllegalStateException("note not found in database")
        }

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

        cursor.close()

        return NoteData(title, content, id, timestamp)
    }

    private fun readAllNotes(): Cursor {
        val db = readableDatabase

        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_CONTENT,
            COLUMN_TIMESTAMP
        )

        return db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
    }

    fun noteListFromDb(context: Context): MutableList<NoteData> {
        val cursor = NotesLocalDBHelper(context).readAllNotes()
        val notesList = mutableListOf<NoteData>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

                val note = NoteData(title, content, id, timestamp)
                notesList.add(note)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return notesList
    }

    fun updateNote(id: Int, newTitle: String, newContent: String): Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TITLE, newTitle)
            put(COLUMN_CONTENT, newContent)
        }

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.update(TABLE_NAME, values, selection, selectionArgs)
    }

    fun getActionCount(): Long {
        val db = readableDatabase

        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_TIMESTAMP
        )

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf("0")

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var counter = 0L

        if (!cursor.moveToFirst()) {
            // handle missing counter row
            createActionCount()
        } else {
            counter = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

            cursor.close()
        }


        return counter
    }

    fun increaseActionCount() {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TIMESTAMP, getActionCount() + 1)
        }

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf("0")

        if (0 == db.update(TABLE_NAME, values, selection, selectionArgs)){
            // handle missing counter
            throw IllegalStateException("action count not updated on local")
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    private fun createActionCount() {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_ID, 0)
            put(COLUMN_TITLE, "")
            put(COLUMN_CONTENT, "")
            put(COLUMN_TIMESTAMP, 0)
        }

        db.insert(TABLE_NAME, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
