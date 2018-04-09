package lemoin.lemoincoinandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextBot.hint = "Burger King"

        get_acc_balance()
        btnAddressPage.setOnClickListener{
            val intent = Intent(this, AddressPage::class.java)
            startActivity(intent)
            //(R.layout.activity_address_page)


        }



    }

    // Function to get the current balance of the users account.
    fun get_acc_balance() {

        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.188.81:8000/getnote"
        //val url = "http://www.google.com"
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> {
            response ->
            editTextBot.hint = response.toString()
            //editTextBot.hint = response.substring(0, 23)
            println("---------------------------------------------------- YES")
        },
        Response.ErrorListener { editTextBot.hint = "Something went wrong!"} )

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

}
