// In this class functions are defined that are used by multiple activities.

package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.reflect.KClass
import android.support.v4.content.ContextCompat.startActivity
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject



class SharedFun (context: Activity, packageContext: Activity, savedInstanceState: Bundle?) {

    // Database setup according to
    // https://medium.com/mindorks/android-architecture-components-room-and-kotlin-f7b725c8d1d
    private val sDbWorkerThread = DbWorkerThread("dbWorkerThread")
    private var sDb: StoredDataBase? = null
    private lateinit var result: Drawer
    private lateinit var drawerOwnerName: PrimaryDrawerItem
    private val context = context
    private val packageContext = packageContext
    private val savedInstanceState = savedInstanceState
    private val localClassName = context.localClassName

    // Initializing code goes in the "init" function.
    init {
         sDbWorkerThread.start()
         sDb = StoredDataBase.getInstance(context)
    }

    fun setDrawer() {
        // Define the toolbar.
        result = context.drawer {
            toolbar = packageContext.toolbar_page
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
                if (localClassName == "MainActivity") {
                    selectable = false
                } else {
                    onClick(openActivity(MainActivity::class))
                }

            }
            // Divider places a line as visual dividing element.
            divider{}
            primaryItem("Send coin") {
                icon = R.drawable.ic_list
                if (localClassName == "SendCoin") {
                    selectable = false
                } else {
                    onClick(openActivity(SendCoin::class))
                }
            }
            divider {  }
            primaryItem("Addresses") {
                icon = R.drawable.ic_list
                if (localClassName == "AddressPage") {
                    selectable = false
                } else {
                    onClick(openActivity(AddressPage::class))
                }
            }
            divider {  }
            primaryItem("Logout") {
                icon = R.drawable.ic_logout
                onClick(openActivityLogOut())
            }
        }
        // Update the owner name by the value in the database
        updateDrawerOwnerName(result, drawerOwnerName)


    }

    private fun deleteOwnerInDb(){
        val task = Runnable {sDb?.storedDataDao()?.deleteOwner()}
        sDbWorkerThread.postTask(task)
    }

    fun updateOwnerAddress(addressText: TextView) {
        val task = Runnable {
            val ownerAddress = sDb?.storedDataDao()?.getOwnerAddress()
            addressText.text = "0x" + ownerAddress
        }
        sDbWorkerThread.postTask(task)
    }

    fun updateOwnerBalance(balanceText: TextView) {
        val task = Runnable {
            val ownerAddress = sDb?.storedDataDao()?.getOwnerAddress()
            updateAccBalance("0x" + ownerAddress, balanceText)

        }
        sDbWorkerThread.postTask(task)
    }



    private fun updateDrawerOwnerName(result: Drawer, drawerOwnerName: PrimaryDrawerItem) {
        val task = Runnable {
            val ownerInfo = sDb?.storedDataDao()?.getOwner()
            drawerOwnerName.withName(ownerInfo)
            result.updateItem(drawerOwnerName)
        }
        sDbWorkerThread.postTask(task)

    }

    // Function to get the current balance of a users account.
    private fun updateAccBalance(address: String?, balanceText: TextView) {
        // Create new queue for HTTP requests.
        val queue = Volley.newRequestQueue(context)
        // Get the URL of the server to show the balance.
        val serverUrl: String = context.getString(R.string.server_url)
        val url = serverUrl.plus("/get_balance")
        // Create a JSON object containing the public key, which will be used by the server to get
        // the balance.
        val reqParam = JSONObject()
        reqParam.put("pub_key", address)
        // Create the request object.
        val req = JsonObjectRequest(Request.Method.POST, url, reqParam,
                Response.Listener{
                    response ->
                    // Write the balance in the according field.
                    balanceText.hint = "Your Balance is " + response.getString("balance") + " LeMoins."

                }, Response.ErrorListener {
            balanceText.hint = "Balance couldn't be fetched :("
        })
        // Add the request object to the queue.
        queue.add(req)

    }



    private fun <T : Activity> openActivity(activity: KClass<T>): (View?) -> Boolean = {
        val intent = Intent(packageContext, activity.java)
        startActivity(context, intent, null)
        false
    }




    private fun openActivityLogOut(): (View?) -> Boolean = {

        deleteOwnerInDb()

        val intent = Intent(packageContext, LoginScreen::class.java)
        intent.putExtra("isLoggedIn", false)

        startActivity(context, intent, null)
        false
    }

}