package com.pedromassango.freebooks

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pedromassango.freebooks.data.Book
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Pedro Massango on 5/5/18.
 */
interface IBooksService {

    class BookList {
        var kind: String? = null
        var totalItems: Int = 0

        @SerializedName("items")
        var items = ArrayList<Book>()
    }

    class VolumeInfo {
        @SerializedName("title")
        @Expose
        var title: String? = null
        var subtitle: String? = null
        var publisher: String? = null
        var description: String? = null
        @SerializedName("pageCount")
        @Expose
        var pageCount: Int? = null
        @SerializedName("imageLinks")
        @Expose
        var imageLinks: ImageLinks? = null
        @SerializedName("previewLink")
        @Expose
        var previewLink: String? = null
    }

    class ImageLinks {
        @SerializedName("smallThumbnail")
        @Expose
        var smallThumbnail: String? = null
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null
    }

    @GET("volumes")
    fun getBooks(@Query("q") query: String,
                 @Query("filter") filter: String,
                 @Query("startIndex") startIndex: Int = 0,
                 @Query("maxResults") maxResults: Int = 40): Call<BookList>
}