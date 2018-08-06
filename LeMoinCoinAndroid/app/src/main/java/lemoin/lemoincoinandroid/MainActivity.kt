package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils.isEmpty
import android.view.View
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.PrimaryDrawerItemKt
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import lemoin.lemoincoinandroid.R.id.*
import org.json.JSONObject
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()
    private lateinit var result: Drawer
    private lateinit var drawerOwnerName: PrimaryDrawerItem
    private lateinit var sharedFun: SharedFun


    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_page)

        sharedFun = SharedFun(this, this@MainActivity, savedInstanceState)

        // Get info if user is logged in.
        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)
        // Set the switch state according to weather user is logged in or not.
        switch_login.isChecked = isLoggedIn


// Define the toolbar.
        /*result = this.drawer {
            toolbar = this@MainActivity.toolbar_page
            translucentStatusBar = true
            hasStableIds = true
            savedInstance = savedInstanceState
            showOnFirstLaunch = true
            // Toolbar items.
            drawerOwnerName = primaryItem ( " " ){
                selectable = false
            }


            primaryItem("Home") {
                icon = R.drawable.ic_home
                selectable = false
            }
            // Divider places a line as visual dividing element.
            divider{}
            primaryItem("Send coin") {
                icon = R.drawable.ic_list
                onClick(openActivityLoggedIn(SendCoin::class))
            }
            divider {  }
            primaryItem("Addresses") {
                icon = R.drawable.ic_list
                onClick (openActivityLoggedIn(AddressPage::class))
                selectable = false
            }
            divider {  }
            primaryItem("Logout") {
                icon = R.drawable.ic_logout
                onClick(openActivityLogOut())
            }
        }*/

        sharedFun.setDrawer()

        // Database setup according to
        // https://medium.com/mindorks/android-architecture-components-room-and-kotlin-f7b725c8d1d
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)

        // Fetch the account balance on startup.
        //getAccBalance()
        // Define action for "Get Balance" button.
        btn_show_balance.setOnClickListener{
            openActivity(AddressPage::class)
        }

        txt_login_status.text = "switch is " + switch_login.isChecked

        switch_login.setOnClickListener(View.OnClickListener {
            txt_login_status.text = "Login is " + switch_login.isChecked
        })

        // Update the owner name shown in the drawer.
        //sharedFun.updateDrawerOwnerName()


    }



    // Function to open other screens when chosen in toolbar.
    private fun <T : Activity> openActivity(activity: KClass<T>): (View?) -> Boolean = {
        val intent = Intent(this@MainActivity, activity.java)
        intent.putExtra("isLoggedIn", switch_login.isChecked)
        startActivity(intent)
        false
    }

    // Function to open other screens only when logged in.
    private fun <T : Activity> openActivityLoggedIn(activity: KClass<T>): (View?) -> Boolean = {
       if(switch_login.isChecked) {
           val intent = Intent(this@MainActivity, activity.java)
           intent.putExtra("isLoggedIn", switch_login.isChecked)
           startActivity(intent)
       }
        false
    }

    private fun openActivityLogOut(): (View?) -> Boolean = {

        sharedFun.deleteOwnerInDb()

        val intent = Intent(this@MainActivity, LoginScreen::class.java)
        intent.putExtra("isLoggedIn", false)
        startActivity(intent)
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

    private fun bindDataWithUi(storedData: StoredData?) {
        println("ALL STORED DATA -----------------------------------------")
        println(storedData)
        txt_your_balance.hint = storedData?.publicKey.toString()
    }



    private fun fetchDataFromDb() {
        val task = Runnable {
            val storageData = sDb?.storedDataDao()?.getAll()

            sUiHandler.post{
                if (storageData == null || storageData.isEmpty()) {
                    txt_your_balance.hint = "No data in cache..!!"
                } else {
                    bindDataWithUi(storedData = storageData[0])
                }
            }
        }
        sDbWorkerThread.postTask(task)
    }


    private fun insertStoredDataInDb(storedData: StoredData) {
        val task = Runnable { sDb?.storedDataDao()?.insert(storedData) }
        println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHERE")
        println(task)
        sDbWorkerThread.postTask(task)
    }



}
