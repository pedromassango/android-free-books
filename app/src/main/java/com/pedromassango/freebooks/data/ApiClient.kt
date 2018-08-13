package com.pedromassango.freebooks.data

import com.pedromassango.freebooks.IBooksService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Pedro Massango on 5/5/18.
 */
object ApiClient {

    // Return a retrofit instance
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val booksService = retrofit.create(IBooksService::class.java)!!
}