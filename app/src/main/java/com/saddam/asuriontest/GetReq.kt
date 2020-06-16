package com.saddam.asuriontest

import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

import java.io.IOException
import javax.net.ssl.SSLSession

class GetReq(asyncResponse: AsyncResponse) : AsyncTask<String, String, String>()  {

    val JSON = "application/json; charset=utf-8".toMediaType()

    var client = OkHttpClient()

    var delegate: AsyncResponse? = null

    var ssl: SSLSession? = null

    init {
        delegate = asyncResponse
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class)
    internal fun run(url: String): String {
        val request = Request.Builder()
            .url(url)

            .build()

        client.newCall(request).execute().use { response -> return response.body!!.string() }

    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun doInBackground(vararg params: String): String {

        var res = ""
        try {
            res = run(params[0])
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return res
    }

    override fun onPostExecute(s: String) {
        delegate!!.processFinish(s)

    }
}