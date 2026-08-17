package com.igcaptiongenerator.data.remote

import com.igcaptiongenerator.data.model.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CaptionApi {
    @Multipart
    @POST("generate-caption")
    suspend fun generateCaption(
        @Part file: MultipartBody.Part,
        @Part("tone") tone: RequestBody,
        @Part("language") language: RequestBody,
        @Part("hashtag_count") hashtagCount: RequestBody
    ): ApiResponse
}
