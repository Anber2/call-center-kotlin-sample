package com.saddam.asuriontest

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_webview.*
import android.content.Intent


class WebViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val myIntent = intent
        val url = myIntent.getStringExtra("content_url")


        webView!!.getSettings().setJavaScriptEnabled(true)

        webView!!.webViewClient = object : WebViewClient() {


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
             }


            override fun onPageFinished(view: WebView?, url: String?) {

            }

        }
        webView.loadUrl(url!!)


    }
}