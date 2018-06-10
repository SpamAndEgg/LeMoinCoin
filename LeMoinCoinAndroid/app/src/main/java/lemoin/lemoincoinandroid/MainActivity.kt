package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mikepenz.materialdrawer.Drawer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import lemoin.lemoincoinandroid.R.id.*
import org.json.JSONObject
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    private lateinit var result: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_page)

        // Define the toolbar.
        result = drawer {

            toolbar = this@MainActivity.toolbar_page
            translucentStatusBar = true
            hasStableIds = true
            savedInstance = savedInstanceState
            showOnFirstLaunch = true

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

        // Fetch the account balance on startup.
        getAccBalance()
        // Define action for "Get Balance" button.
        btn_show_balance.setOnClickListener{
            getAccBalance()
        }

    }

    // Function to open other screens when chosen in toolbar.
    private fun <T : Activity> openActivity(activity: KClass<T>): (View?) -> Boolean = {
        startActivity(Intent(this@MainActivity, activity.java))
        false
    }

    // Function to get the current balance of a users account.
    private fun getAccBalance() {

        // Create new queue for HTTP requests.
        val queue = Volley.newRequestQueue(this)
        // Get the URL of the server to show the balance.
        val serverUrl: String = getString(R.string.server_url)
        val url = serverUrl.plus("/get_balance")
        // Create a JSON object containing the public key, which will be used by the server to get
        // the balance.
        val reqParam = JSONObject()
        reqParam.put("pub_key", txt_your_add.text)
        // Create the request object.
        val req = JsonObjectRequest(Request.Method.POST, url, reqParam,
                Response.Listener{
                    response ->
                    // Write the balance in the according field.
                    txt_your_balance.hint = "Balance: " + response.getString("balance")

                }, Response.ErrorListener {
                    txt_your_balance.hint = "Balance couldn't be fetched :("
        })
        // Add the request object to the queue.
        queue.add(req)
    }
}
