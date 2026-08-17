package com.igcaptiongenerator.data.repository

import com.igcaptiongenerator.data.local.CaptionDao
import com.igcaptiongenerator.data.model.ApiResponse
import com.igcaptiongenerator.data.model.CaptionResult
import com.igcaptiongenerator.data.remote.CaptionApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaptionRepository @Inject constructor(
    private val api: CaptionApi,
    private val dao: CaptionDao
) {
    fun getRecentResults(): Flow<List<CaptionResult>> = dao.getRecentResults()

    suspend fun generate(imageFile: File, tone: String, language: String, hashtagCount: Int): CaptionResult {
        val filePart = MultipartBody.Part.createFormData(
            "file", imageFile.name,
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
        val response: ApiResponse = api.generateCaption(
            file = filePart,
            tone = tone.toRequestBody("text/plain".toMediaTypeOrNull()),
            language = language.toRequestBody("text/plain".toMediaTypeOrNull()),
            hashtagCount = hashtagCount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        )
        val result = CaptionResult(
            caption = response.caption,
            hashtags = response.hashtags,
            tone = tone,
            language = language
        )
        dao.insert(result)
        dao.pruneOldResults()
        return result
    }
}
