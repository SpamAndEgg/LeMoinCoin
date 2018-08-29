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
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import lemoin.lemoincoinandroid.R.id.recyclerView_transaction

import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


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
            primaryItem("Share address") {
                icon = R.drawable.ic_list
                if (localClassName == "ShareQR") {
                    selectable = false
                } else {
                    onClick(openActivity(ShareQR::class))
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
            getTransactionList("0x" + ownerAddress)

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
                    val amountCoin = response.getString("balance").replace("[.,]".toRegex(),"").toDouble()
                    // Display the number with two decimals.
                    val numberToFormat = NumberFormat.getNumberInstance()
                    numberToFormat.minimumFractionDigits = 2
                    val formattedString = numberToFormat.format(amountCoin/100)

                    // Write the balance in the according field.
                    balanceText.hint = "Your Balance is " + formattedString + " LeMoins."

                }, Response.ErrorListener {
            balanceText.hint = "Balance couldn't be fetched :("
        })
        // Add the request object to the queue.
        queue.add(req)

    }

    class Transaction{
        var txn_from = ""
        var txn_to = ""
        var address_id = -1
        var amount = 0
        var txn_to_this_add = false
        var timestamp = 0
        var date = ""
    }

    // Function to get a list of all transaction from and to this address.
    private fun getTransactionList(address: String?) {
        // Create new queue for HTTP requests.
        val queue = Volley.newRequestQueue(context)
        // Get the URL of the api with this address.
        val apiUrl: String = "http://api-ropsten.etherscan.io/api?module=account&action=tokentx&sort=asc&apikey=2BEN9YZS76RB9Z4AKM384NT7J9WJGU6J22&address="
        val url = apiUrl.plus(address)
        // Create a JSON object containing the public key, which will be used by the server to get
        // the balance.
        val reqParam = JSONObject()
        var trackTransaction = mutableListOf<Transaction>()
        var trackAddress = mutableListOf<String>()
        var trackAddressName = mutableListOf<String?>()

        // Create the request object.
        val req = JsonObjectRequest(Request.Method.POST, url, reqParam,
                Response.Listener { response ->
                    val amountCoin = response
                    val resultJson = amountCoin.getJSONArray("result")



                    for (i_txn: Int in resultJson.length() downTo 1) {
                        var thisObject = JSONObject(resultJson[i_txn - 1].toString())
                        var thisTxn = Transaction()
                        // Check if this txn has the LeMoinCoin contract, otherwise ignore it.
                        if (thisObject.getString("tokenName").equals("LeMoin Coin")) {
                            thisTxn.amount = thisObject.getInt("value")
                            thisTxn.txn_from = thisObject.getString("from")
                            thisTxn.txn_to = thisObject.getString("to")
                            thisTxn.timestamp = thisObject.getInt("timeStamp")

                            var isoDate = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
                            isoDate.timeZone = TimeZone.getTimeZone("GMT+2")
                            var date = isoDate.format(Date(thisTxn.timestamp.toLong() * 1000))
                            thisTxn.date = date

                            // Check if the transaction was made to this address.
                            thisTxn.txn_to_this_add = thisObject.getString("to") == address
                            // Make a new list containing every address interacted with this account
                            // only once. Also save the id of the address list of this transaction.
                            // Therewith the addresses that have been interacted with are later
                            // retrieved from the address book, if existing.
                            if (thisTxn.txn_to_this_add) {
                                if (!trackAddress.contains(thisTxn.txn_from)) {
                                    // Case: Address was sending to this add and is not in list yet.
                                    trackAddress.add(thisTxn.txn_from)
                                }
                                // Get the id of the address list.
                                thisTxn.address_id = trackAddress.indexOf(thisTxn.txn_from)
                            } else {
                                if (!trackAddress.contains(thisTxn.txn_to)) {
                                    // Case: This address was sending to other address, which is not in
                                    // this list yet.
                                    trackAddress.add(thisTxn.txn_to)
                                }
                                // Get the id of the address list.
                                thisTxn.address_id = trackAddress.indexOf(thisTxn.txn_to)
                            }
                            println(trackAddress)
                            trackTransaction.add(thisTxn)
                        }

                    }
                    println(trackAddress.size)
                    for (i_add in 1..trackAddress.size) {


                        var task = Runnable {
                            val addressName = sDb?.storedDataDao()?.getContactByAddress(trackAddress[i_add-1].decapitalize())
                            trackAddressName.add(addressName)

                            println(addressName)
                        }

                        sDbWorkerThread.postTask(task)
                    }
                    println("STATION 1111111111111111111111111111111111111111111111111111111111111")
                    // Start the recycler view of all transactions of this account.
                    context.recyclerView_transaction.layoutManager = LinearLayoutManager(context)
                    context.recyclerView_transaction.adapter = AdapterTransaction(trackTransaction, trackAddressName)


                }, Response.ErrorListener {

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