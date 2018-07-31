package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mikepenz.materialdrawer.Drawer
import kotlinx.android.synthetic.main.send_coin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import kotlin.reflect.KClass

class SendCoin : AppCompatActivity() {

    private lateinit var result: Drawer
    // Define request codes for private and public key.
    private var CODE_PRI_KEY = 1
    private var CODE_PUB_KEY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send_coin)
        setSupportActionBar(toolbar_page)

        var isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        // Define the toolbar.
        result = drawer {
            toolbar = this@SendCoin.toolbar_page
            translucentStatusBar = true
            hasStableIds = true
            savedInstance = savedInstanceState
            showOnFirstLaunch = true
            // Toolbar items.
            primaryItem("Home") {
                icon = R.drawable.ic_home
                onClick (openActivity(MainActivity::class, isLoggedIn))
            }
            // Divider places a line as visual dividing element.
            divider {  }
            primaryItem("Send coin") {
                icon = R.drawable.ic_list
                selectable = false
            }
            divider {  }
            primaryItem("Addresses") {
                icon = R.drawable.ic_list
                onClick (openActivityLoggedIn(AddressPage::class, isLoggedIn))
            }
            divider {  }
            primaryItem("Logout") {
                icon = R.drawable.ic_logout
                onClick(openActivityLogOut(MainActivity::class))
            }
        }

        // Define action for "Send Coin" button.
        btn_send_coin.setOnClickListener{
            transferCoin()
        }

        // If one of the QR buttons is clicked, use them to set the public or private key.
        btn_qr_prikey_sc.setOnClickListener{
            val intent = Intent(this@SendCoin, QrCodeScanner::class.java)
            // Start the QR code scanner to get a key. The request code defines weather the qr scanner
            // scans the private key or the public key of the receiver.
            startActivityForResult(intent, CODE_PRI_KEY)
        }

        btn_qr_pubkey_sc.setOnClickListener{
            val intent = Intent(this@SendCoin, QrCodeScanner::class.java)
            // Start the QR code scanner to get a key. The request code defines weather the qr scanner
            // scans the private key or the public key of the receiver.
            startActivityForResult(intent, CODE_PUB_KEY)
        }


    }

    // Overriding the "onActivityResult" allows to catch results that come back when the QR code
    // scanner is called.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE_PRI_KEY) {
                txt_privat_key.setText(data?.getStringExtra("Key"))
            } else if (requestCode == CODE_PUB_KEY) {
                send_coin_add.setText(data?.getStringExtra("Key"))
            }
        }
    }

    // Function to open other screens when chosen in toolbar.
    private fun <T : Activity> openActivity(activity: KClass<T>, isLoggedIn: Boolean): (View?) -> Boolean = {
        val intent = Intent(this@SendCoin, activity.java)
        intent.putExtra("isLoggedIn", isLoggedIn)
        startActivity(intent)
        false
    }

    private fun return_string(): String {
        val rtn_str = "LALALALA123"
        return rtn_str
    }

    // Function to open other screens only when logged in.
    private fun <T : Activity> openActivityLoggedIn(activity: KClass<T>, isLoggedIn: Boolean): (View?) -> Boolean = {
        if(isLoggedIn) {
            val intent = Intent(this@SendCoin, activity.java)
            intent.putExtra("isLoggedIn", isLoggedIn)
            startActivity(intent)
        }
        false
    }

    private fun <T : Activity> openActivityLogOut(activity: KClass<T>): (View?) -> Boolean = {
        val intent = Intent(this@SendCoin, activity.java)
        intent.putExtra("isLoggedIn", false)
        startActivity(intent)
        false
    }


    // Function to open other screens when chosen in toolbar.
    private fun  <T : Activity> openActivityQr(activity: KClass<T>, requestCode: Int): (View?) -> Boolean = {
        val intent = Intent(this@SendCoin, activity.java)
        // Start the QR code scanner to get a key. The request code defines weather the qr scanner
        // scans the private key or the public key of the receiver.
        startActivityForResult(intent, requestCode)


        false
    }

    // Function to transfer coins from an account defined by the private key to another account.
    private fun transferCoin() {

        // Create new queue for HTTP requests.
        val queue = Volley.newRequestQueue(this)
        // Get the URL of the server to transfer coins.
        val serverUrl: String = getString(R.string.server_url)
        val url = serverUrl.plus("/transfer")
        // Create a JSON object containing the private key of the account to send money from, the
        // public key of the account to send money to and the amount of coins to transfer.
        val reqParam = JSONObject()
        reqParam.put("pri_key", txt_privat_key.text)
        reqParam.put("send_to", send_coin_add.text)
        reqParam.put("amount", send_coin_amount.text)
        // Create the request object.
        val req = JsonObjectRequest(Request.Method.POST, url, reqParam,
                Response.Listener{
                    response ->
                    // Write the status in the textbox as feedback for the user.
                    txt_send_status.hint = response.getString("status")

                }, Response.ErrorListener {
            txt_send_status.hint = "Transaction failed :("
        })
        // Add the request object to the queue.
        queue.add(req)
    }

}

