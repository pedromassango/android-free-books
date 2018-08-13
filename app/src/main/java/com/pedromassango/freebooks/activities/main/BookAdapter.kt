package com.pedromassango.freebooks.activities.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pedromassango.freebooks.ImageRequester
import com.pedromassango.freebooks.R
import com.pedromassango.freebooks.activities.reader.BookReaderActivity
import com.pedromassango.freebooks.data.Book
import kotlinx.android.synthetic.main.row_book_entry.view.*

/**
 * Created by Pedro Massango on 5/5/18.
 */


// Book ViewHolder
class ProductViewHolder internal constructor(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_book_entry, parent, false)) {

    internal fun bind(book: Book, imageRequester: ImageRequester) {
        with(itemView) {
            setTag(R.id.tag_product_entry, book) // add TAG to this itemView

            val bookInfo = book.volumeInfo
            row_book_title.text = bookInfo!!.title
            row_book_price.text = String.format("Pages: %s", bookInfo.pageCount)
            imageRequester.setImageFromUrl(row_book_image, bookInfo.imageLinks?.thumbnail)

            // click lister for an book (should open activity to read the book)
            setOnClickListener {
                val i = Intent(itemView.context, BookReaderActivity::class.java)
                val b = Bundle()
                b.putSerializable(BookReaderActivity.BOOK_INTENT_KEY, book)
                i.putExtras(b)

                // call activity to read the book
                itemView.context.startActivity(i)
            }
        }
    }
}

// Book Adapter
class BookAdapter internal constructor(private var books: MutableList<Book> = mutableListOf(),
                                       private val imageRequester: ImageRequester) : RecyclerView.Adapter<ProductViewHolder>() {

    internal fun addBooks(mBooks: List<Book>, clearCurrent: Boolean) {
        if(clearCurrent){
            this.books.clear()
        }

        this.books.addAll(mBooks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductViewHolder {
        return ProductViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: ProductViewHolder, i: Int) {
        viewHolder.bind(books[i], imageRequester)
    }

    override fun getItemCount(): Int {
        return books.size
    }
}
