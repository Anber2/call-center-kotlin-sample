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


    var petsListAdapter: PetsListAdapter? = null

    var imgList = mutableMapOf<String, PetsModel>()

    var icon: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        getConfigData()

        getPetsList()// this fun. will call pits data.
    }

    // this fun. will initialize a Bitmap 'icon' and it will bt used when the pit image from network is not load or delayed
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
            myIntent.putExtra("content_url", imgList.values.elementAt(i).petUrl)
            startActivity(myIntent)

        }


    }

    // fun isWorkingTime() will be called when user click on chat or call button
    private fun isWorkingTime() {

        if (isWithInWorkDaysAndHours()) {

            showAlert("Thank you for getting in touch with us. We’ll get back to you as soon as possible")

        } else {
            showAlert("Work hours has ended. Please contact us again on the next work day")


        }

    }

    // fun showAlert(msg: String) will be called when user click on chat or call button to display an AlertDialog
    private fun showAlert(msg: String) {


        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("ok") { dialog, which ->
            }.show()


    }

    private fun isWithInWorkDaysAndHours(): Boolean {

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        if (day >= Calendar.MONDAY && day <= Calendar.FRIDAY) {

            if (hour in 9..18) {
                return true
            }
        } else {
            return false
        }

        return false
    }

    // this fun. will call working days and hours. It will call AsyncTask in GetReq.kt which uses okhttp3 for networking
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
                                //val workHours = jsonObject.getString("workHours")


                                //////////////////////
                                // As config file, workHours = "M-F 9:00 - 18:00" ???
                                //
                                // 'M-F' refers for working days which means Monday to friday,
                                // but it's not possible to fitch. It would be much better to fitch if the json data was like, for example :
                                // "work_days" : {"from" : "Monday", "to" : "Friday" }
                                // "work_hours" : {"from" : "9:00", "to" : "18:00" }
                                //
                                // The provided design shows that the working hours are from 10:00 to 18:00,
                                // not from 9:00 as in the json file.
                                //
                                // Since "workHours" is not suitable to fitch ,
                                // working days and hours will be calculated locally.
                                //////////////////////


                                if (!isChatEnabled) {

                                    this@MainActivity.runOnUiThread {
                                        buttonChat!!.visibility = View.GONE
                                    }
                                }
                                if (!isCallEnabled) {

                                    this@MainActivity.runOnUiThread {
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

    // this fun. will call working days and hours. It will call AsyncTask in GetReq.kt which uses okhttp3 for networking
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

                                    // This fun will download pits images instead of Glid or picasso
                                    getPitImage(image_url, title)
                                }

                                this@MainActivity.runOnUiThread {

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

    // getPitImage  will update the downloaded pits images to  (mutableMapOf) . It will call AsyncTask in DownloadImageTask.kt which uses okhttp3 for networking
    private fun getPitImage(imageUrl: String, key: String) {

        val asyncTask =
            DownloadImageTask(
                object : AsyncResImgDown {
                    override fun processFinish(output: Bitmap) {

                        if (null != output) {

                            // a double check to avoid more exceptions
                            if (imgList.keys.contains(key)) {

                                imgList!![key]!!.bm = output

                                this@MainActivity.runOnUiThread {
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


