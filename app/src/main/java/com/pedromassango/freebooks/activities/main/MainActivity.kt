package com.pedromassango.freebooks.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.pedromassango.freebooks.data.ApiClient
import com.pedromassango.freebooks.IBooksService
import com.pedromassango.freebooks.ImageRequester
import com.pedromassango.freebooks.R
import com.pedromassango.freebooks.data.Book
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var adapter: BookAdapter? = null

    // A query strings to get a random query and show that books by default
    private val queries = arrayOf("Mathematica", "Fisica", "Quimica", "Ingles", "Hardware", "Android")
    // Typed query
    private var query = ""
    // num of items to gem from server at first time, and each time the user reach the bottom of scrollView
    private var currentPage = 0
    // whether we are loading data
    private var isLoading: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set Toolbar as actionBar
        setSupportActionBar(app_bar)

        // To load books image
        val imageRequester = ImageRequester.getInstance(this)

        recycler_books.setHasFixedSize(true)
        val mLayoutManager = GridLayoutManager(this, resources.getInteger(R.integer.shr_column_count))
        recycler_books.layoutManager = mLayoutManager
        adapter = BookAdapter(imageRequester = imageRequester)
        recycler_books.adapter = adapter
        recycler_books.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // total number of items in the data set held by the adapter
                val totalItemCount = mLayoutManager.itemCount
                //adapter position of the first visible view.
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

                Log.e(TAG, "Total: $totalItemCount")
                Log.e(TAG, "LAST: $lastVisibleItem")

                //if it isn't currently loading, we check to see if it reached
                //the visibleThreshold(ex,5) and need to reload more data,
                //if it need to reload some more data, execute loadData() to
                //fetch the data (showed next)
                if (!isLoading && (totalItemCount <= (lastVisibleItem + 1))) {
                    showProgress(true)
                    queryBooks(query)
                }
            }

        })

        // Get books from google books with a random query
        val position = Random().nextInt(queries.size)
        // save the current query
        query = queries[position]
        // search with a random query
        queryBooks(query)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_reading) {

            //TODO: show downloaded books

            Toast.makeText(
                    this@MainActivity,
                    "TODO()",
                    Toast.LENGTH_LONG)
                    .show()
        } else if (item.itemId == R.id.action_search) {
            showTypeQueryDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun showTypeQueryDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null, false)

        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.type_a_query)
                .setCancelable(false)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.search, { _, _ ->

                    // get typed query
                    val query = view.findViewById<EditText>(R.id.edt_search).text.toString()

                    // if no typed text, do nothing
                    if (query.trim().isEmpty()) {
                        return@setPositiveButton
                    }

                    // show a feedback message
                    Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG).show()

                    // start the search
                    queryBooks(query)
                })

        // show the dialog
        dialog.create().show()
    }

    private var lastQuery = ""
    private fun queryBooks(query: String) {
        val service = ApiClient.booksService

        // set it as title
        val mTitle = "$title - $query"
        collapsing_toolbar.title = mTitle

        // If new query, reset pagination
        if (lastQuery != query) {
            // save the last query
            lastQuery = query
            currentPage = 0
        } else {
            currentPage += 15
        }

        // Why use the API-KEY?
        //val apiKey = getString(R.string.app_books_api_key)

        service.getBooks(query.trim(),
                filter = "free-ebooks", // Get only free books
                startIndex = currentPage,
                maxResults = 15 // max result per request
        ).enqueue(object : Callback<IBooksService.BookList> {
            override fun onFailure(call: Call<IBooksService.BookList>?, t: Throwable?) {
                Log.e(TAG, "FAILED TO CONNECT")
                Log.e(TAG, t!!.printStackTrace().toString())
                showGetBooksError()
            }

            override fun onResponse(call: Call<IBooksService.BookList>?, response: Response<IBooksService.BookList>?) {
                Log.e(TAG, "SUCCESS")

                try {
                    val data = response?.body()

                    // if no books, show a message
                    if (data?.items?.size == 0) {
                        Toast.makeText(this@MainActivity, "No books load", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Set books in recyclerView
                    showBooks(data = data!!.items)

                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    showGetBooksError() // show a message
                }
            }
        })
    }

    private fun showBooks(data: List<Book>) {
        // remove progress bar
        showProgress(false)

        val isNewQuery = (lastQuery != query)
        // Set books in recyclerView
        adapter!!.addBooks(data, isNewQuery)

        // change the current number of items that we already got from server
        currentPage = adapter!!.itemCount
    }

    private fun showGetBooksError() {
        Toast.makeText(
                this@MainActivity,
                "Failed to get books. Try again.",
                Toast.LENGTH_LONG)
                .show()

        showProgress(false)
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progress_bar.visibility = View.VISIBLE
            isLoading = true
        } else {
            progress_bar.visibility = View.GONE
            isLoading = false
        }
    }

}
