package com.saddam.asuriontest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {


    internal var petsListAdapter: PetsListAdapter? = null

    val petsListModel = mutableListOf<PetsModel>()
    var imgList = mutableMapOf<String, PetsModel>()

    var icon: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        init()

        getConfigData()

        getPetsList()
    }


    private fun init() {

        icon = BitmapFactory.decodeResource(
            this@MainActivity.getResources(),
            R.drawable.ic_launcher_foreground
        )

        buttonChat!!.setOnClickListener {
            isWorkingTime()
        }

        buttonCall!!.setOnClickListener {
            isWorkingTime()
        }

        pitsListView.setOnItemClickListener { adapterView, view, i, l ->

            val myIntent = Intent(this, WebViewActivity::class.java)
            myIntent.putExtra("content_url", petsListModel.get(i).petUrl)
            startActivity(myIntent)

        }


    }

    private fun isWorkingTime() {

        if (isWithInWorkHours() && isWithInWorkDays()) {

            showAlert("Thank you for getting in touch with us. We’ll get back to you as soon as possible")

        } else {
            showAlert("Work hours has ended. Please contact us again on the next work day")


        }

    }

    private fun showAlert(msg: String) {


        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("ok") { dialog, which ->
            }.show()


    }

    private fun isWithInWorkDays(): Boolean {

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        when (day) {
            Calendar.SATURDAY -> {
                return false

            }
            Calendar.SUNDAY -> {

                return false

            }

        }

        return true

    }

    private fun isWithInWorkHours(): Boolean {

        val cal = Calendar.getInstance()
        cal.time = Date()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return hour in 8..18

    }


    private fun getConfigData() {


        val asyncTask =
            GetReq(
                object : AsyncResponse {
                    override fun processFinish(output: Any) {


                        if (output != "") {

                            try {

                                val jsonObject = JSONObject(output.toString())

                                val isChatEnabled = jsonObject.getBoolean("isChatEnabled")
                                val isCallEnabled = jsonObject.getBoolean("isCallEnabled")

                                if (!isChatEnabled) {

                                    runOnUiThread {

                                        buttonChat!!.visibility = View.GONE

                                    }
                                }
                                if (!isCallEnabled) {
                                    runOnUiThread {
                                        buttonCall!!.visibility = View.GONE
                                    }
                                }

                            } catch (ex: IOException) {
                                ex.printStackTrace()
                            }


                        }

                    }
                }

            )
        asyncTask.execute("https://api.jsonbin.io/b/5ee6d12419b60f7aa95a265c")

    }

    private fun getPetsList() {


        val asyncTask =
            GetReq(
                object : AsyncResponse {
                    override fun processFinish(output: Any) {


                        if (output != "") {
                            try {

                                val jsonArray = JSONArray(output.toString())

                                for (i in 0 until jsonArray.length()) {

                                    val jsonObject = jsonArray.getJSONObject(i)

                                    val image_url = jsonObject.getString("image_url")

                                    val title = jsonObject.getString("title")

                                    val content_url = jsonObject.getString("content_url")


                                    imgList.put(
                                        title, PetsModel(
                                            i,
                                            image_url,
                                            title,
                                            content_url,
                                            icon
                                        )
                                    )

                                    getPitImage(image_url, title)


                                }

                                runOnUiThread {

                                    petsListAdapter =
                                        PetsListAdapter(this@MainActivity, imgList)
                                    pitsListView.adapter = petsListAdapter

                                }


                            } catch (ex: IOException) {
                                ex.printStackTrace()
                            }

                        }
                    }
                }

            )
        asyncTask.execute("https://api.jsonbin.io/b/5ee6d0bd19b60f7aa95a2625")

    }

    private fun getPitImage(imageUrl: String, key: String) {

        val asyncTask =
            DownloadImageTask(
                object : AsyncResImgDown {
                    override fun processFinish(output: Bitmap) {


                        if (null != output) {


                            if (imgList.keys.contains(key)) {
                                imgList!![key]!!.bm = output ?: icon

                                runOnUiThread {
                                    petsListAdapter!!.notifyDataSetChanged()
                                }
                            }
                        }

                    }
                }

            )
        asyncTask.execute(imageUrl)

    }


}


