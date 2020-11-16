package com.iat359.biteme

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.iat359.biteme.RecipeReaderContract.RecipeEntry;

private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${RecipeEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${RecipeEntry.NAME} TEXT," +
                "${RecipeEntry.IMAGE_NAME} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${RecipeEntry.TABLE_NAME}"

class RecipeReaderDbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RecipeReader.db"
    }
}