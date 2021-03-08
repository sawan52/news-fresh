package com.example.news_fresh

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData()
        mAdapter = NewsListAdapter(this)
        recyclerView.adapter = mAdapter
    }

    private fun fetchData() {

        //val newsUrl = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=5cd8435ddad64a248fc99096aa387ab4"

        val newsUrl = "https://api.mediastack.com/v1/news?access_key=7babedcefa182208673dfc2a0a48178f&keywords=tennis&countries=in"

        val retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Request a json response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, newsUrl, null,
            {
                val newsJsonArray = it.getJSONArray("data")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("image")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            {
                val x = it.networkResponse.statusCode
                //Toast.makeText(this, "Something went wrong $x", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Error code: $x", Toast.LENGTH_SHORT).show()
            })

        jsonObjectRequest.retryPolicy = retryPolicy

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}
