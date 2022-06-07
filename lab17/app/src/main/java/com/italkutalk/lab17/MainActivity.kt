package com.italkutalk.lab17

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button
    private lateinit var textView : TextView
    //定義資料結構存放 Server 回傳的資料
    data class MyObject(
        val id:Int,
        val name:String
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query = findViewById(R.id.btn_query)
        textView = findViewById(R.id.textView)
        btn_query.setOnClickListener {2
            //關閉按鈕避免再次查詢
            btn_query.isEnabled = false
            //發送請求
            sendRequest()
        }
    }
    //發送請求
    private fun sendRequest() {
        /*
        //環保署空氣品質指標 API
        val url = "https://opendata.epa.gov.tw/webapi/api/rest/datastore/" +
                "355000000I-000259?filters=County" +
                "%20eq%20%27%E8%87%BA%E5%8C%97%E5%B8%82%27&" +
                "sort=SiteName&offset=0&limit=1000"
         */
        //本書原內容採用環保署空氣品質指標 API，但近期對方修改資料的取得方式，故範例提供更穩定的資料來源
        val url = "https://jsonplaceholder.typicode.com/comments?postId=1"

        //建立 Request.Builder 物件，藉由 url()將網址傳入，再建立 Request 物件
        val req = Request.Builder()
            .url(url)
            .build()
        //建立 OkHttpClient 物件，藉由 newCall()發送請求，並在 enqueue()接收回傳
        OkHttpClient().newCall(req).enqueue(object : Callback {
            //發送成功執行此方法
            override fun onResponse(call: Call, response: Response) {
                //使用 response.body?.string()取得 JSON 字串
                val json = response.body?.string()
                //建立 Gson 並使用其 fromJson()方法，將 JSON 字串以 MyObject 格式輸出
                //val myObject = Gson().fromJson(json, MyObject::class.java)
                val parser = JsonParser()
                val JsonArray = parser.parse(json).asJsonArray
                val gson = Gson()
                val userlist = mutableListOf<MyObject>()
                val count = 1
                for(user in JsonArray){
                    val userO = gson.fromJson(user,MyObject::class.java)
                    userlist.add(userO)
                }
                val items = arrayOfNulls<String>(userlist.size)
                userlist.forEachIndexed{ index, data->
                    items[index] = "id：${data.id}, name：${data.name}"
                }
                runOnUiThread {
                    //開啟按鈕可再次查詢
                    btn_query.isEnabled = true
                    //建立 AlertDialog 物件並顯示字串陣列
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("ID Name")
                        .setItems(items, null)
                        .show()
                }
            }
            //發送失敗執行此方法
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    //開啟按鈕可再次查詢
                    btn_query.isEnabled = true
                    Toast.makeText(this@MainActivity,
                        "查詢失敗$e", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}