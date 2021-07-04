package com.example.news_fresh

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.news_fresh.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: NewsListAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData()
        mAdapter = NewsListAdapter(this)
        binding.recyclerView.adapter = mAdapter
    }

    private fun fetchData() {

        // this url is not working
        // val newsUrl = "https://newsapi.org/v2/top-headlines?country=in&apiKey=de760802658848c1a3367682a282f51b"

        // this url is  working
        val newsUrl = "https://saurav.tech/NewsAPI/top-headlines/category/technology/in.json"

        // Request a json response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, newsUrl, null,
            {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
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

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}
