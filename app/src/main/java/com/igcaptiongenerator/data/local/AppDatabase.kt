package com.igcaptiongenerator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.igcaptiongenerator.data.model.CaptionResult

@Database(entities = [CaptionResult::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun captionDao(): CaptionDao
}
