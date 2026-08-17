package com.igcaptiongenerator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.igcaptiongenerator.data.model.CaptionResult
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptionDao {
    @Query("SELECT * FROM caption_results ORDER BY timestamp DESC LIMIT 10")
    fun getRecentResults(): Flow<List<CaptionResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: CaptionResult)

    @Query("DELETE FROM caption_results WHERE id NOT IN (SELECT id FROM caption_results ORDER BY timestamp DESC LIMIT 10)")
    suspend fun pruneOldResults()
}
