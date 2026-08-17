package com.igcaptiongenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caption_results")
data class CaptionResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caption: String,
    val hashtags: List<String>,
    val tone: String,
    val language: String,
    val timestamp: Long = System.currentTimeMillis()
)
