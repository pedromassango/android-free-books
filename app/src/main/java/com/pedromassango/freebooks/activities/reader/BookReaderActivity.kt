package com.pedromassango.freebooks.activities.reader

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import com.pedromassango.freebooks.R
import com.pedromassango.freebooks.data.Book
import kotlinx.android.synthetic.main.activity_book_reader.*

class BookReaderActivity : AppCompatActivity() {

    companion object {
        const val BOOK_INTENT_KEY = "com.pedromassango.freebooks.activities.reader.BOOK_INTENT_KEY"
    }

    // Received book from intent
    private lateinit var book: Book

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_reader)

        // set Toolbar as actionBar
        setSupportActionBar(toolbar)

        // intent should always have a Book,  so it should never be null.
        checkNotNull(intent)

        // receive the book
        book = intent.getSerializableExtra(BOOK_INTENT_KEY) as Book


        web_view.webChromeClient = WebChromeClient()

        // WebView settings
        val settings = web_view.settings
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.builtInZoomControls = true
        settings.javaScriptEnabled = true // enable javaScript

        // format the book link to be read online
        //val onlineBookReadUrl = getBookLink( book.volumeInfo.webReaderLink)

        // load the book, to be read
        web_view.loadUrl( book.accessInfo!!.webReaderLink)

        //TODO: make app load the PDF  inside the webView instead of start some intent.
        //TODO: make app load the PDF  inside the webView instead of start some intent.
    }

}
