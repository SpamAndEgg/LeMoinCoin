package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.R.attr.divider
import kotlinx.android.synthetic.main.activity_main.*
import lemoin.lemoincoinandroid.R.id.txt_your_add
import me.dm7.barcodescanner.zbar.ZBarScannerView
import java.io.IOException
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)

        var newData = StoredData()
        newData.walletName = "Das"
        newData.privateKey = "ist"
        newData.publicKey = "ein Test!"

        insertStoredDataInDb(newData)

        fetchStoredDataFromDb()


        drawer {

            //toolbar = this@MainActivity.toolbar
            translucentStatusBar = false

            primaryItem("Home") {
                icon = R.drawable.ic_home
                selectable = false
            }
            // Divider places a line as visual dividing element.
            divider {  }

            primaryItem("Send coin") {
                icon = R.drawable.ic_list
                onClick (openActivity(SendCoin::class))
            }
            divider {  }
            primaryItem("Addresses") {
                icon = R.drawable.ic_list
                onClick (openActivity(AddressPage::class))
            }
            divider {  }
            primaryItem("Logout") {
                icon = R.drawable.ic_logout
            }

        }

        get_acc_balance()
        btnAddressPage.setOnClickListener{
            val intent = Intent(this, AddressPage::class.java)
            startActivity(intent)
            //(R.layout.activity_address_page)


        }

        btnAddPage.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
            //(R.layout.activity_address_page)


        }

        txt_your_add.hint = "Burger King"

    }

    private fun <T : Activity> openActivity(activity: KClass<T>): (View?) -> Boolean = {
        startActivity(Intent(this@MainActivity, activity.java))
        false
    }

    // Function to get the current balance of the users account.
    fun get_acc_balance() {

        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.188.81:8000/getnote"
        //val url = "http://www.google.com"
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> {
            response ->
            //editTextBot.hint = response.toString()
            //editTextBot.hint = response.substring(0, 23)
            println("---------------------------------------------------- YES")
        },
        Response.ErrorListener { txt_your_add.hint = "Something went wrong!"} )

        queue.add(stringRequest)

        /*
        val url = "http://www.google.com"

        val request = Request.Builder().url(url).build()
        println(request)
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                println("You failed to get users balance.")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                println("Balance was fetched successfully!")
                println(body)
            }
        })
        */

    }

    private fun bindDataWithUi(storedData: StoredData?) {
        txt_your_balance.hint = storedData?.publicKey.toString()
    }

    private fun fetchStoredDataFromDb() {
        val task = Runnable {
            val weatherData =
                    sDb?.storedDataDao()?.getAll()
            sUiHandler.post({
                if (weatherData == null || weatherData?.size == 0) {
                    txt_your_balance.hint = "No data in cache..!!"
                } else {
                    bindDataWithUi(storedData = weatherData?.get(0))
                }
            })
        }
        sDbWorkerThread.postTask(task)
    }

    private fun insertStoredDataInDb(storedData: StoredData) {
        val task = Runnable { sDb?.storedDataDao()?.insert(storedData) }
        sDbWorkerThread.postTask(task)
    }

}
