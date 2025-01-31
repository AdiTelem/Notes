package com.example.notes.model
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RetrofitInterface {
    companion object {
        val instance: RetrofitInterface by lazy {
            Retrofit.Builder()
                .baseUrl("https://cabb1d1dd0f69787c459.free.beeceptor.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface::class.java)
        }
    }

    @GET("notes/{id}")
    fun getNoteByID(@Path("id") noteID: Int): Call<NoteData>

    @GET("notes")
    fun getAllNote(): Call<List<NoteData> >

    @POST("notes")
    fun createNote(@Body noteData: NoteData): Call<Void>

    @PUT("notes/{id}")
    fun updateNote(@Path("id") noteID: Int, @Body noteData: NoteData): Call<Void>

    @DELETE("notes/{id}")
    fun deleteNote(@Path("id") noteID: Int): Call<Void>
}