package com.inegru.expensetrackr.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inegru.expensetrackr.data.db.converter.DateConverter
import com.inegru.expensetrackr.data.db.dao.ExpenseDao
import com.inegru.expensetrackr.data.db.model.ExpenseEntity

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {

        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}