package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.send_coin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RetryPolicy
import javax.xml.datatype.DatatypeConstants.SECONDS
import android.text.Editable
import android.text.TextWatcher
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class SendCoin : AppCompatActivity() {

    // Define request codes for private and public key.
    private var CODE_PUB_KEY = 2
    private lateinit var sharedFun: SharedFun
    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send_coin)
        setSupportActionBar(toolbar_page)

        sharedFun = SharedFun(this, this@SendCoin, savedInstanceState)
        sharedFun.setDrawer()
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()
        sDb = StoredDataBase.getInstance(this)

        // Get address from address book (if one was sent).
        val sendToAdd = intent.getStringExtra("contactAddress")

        if(sendToAdd != null) {
            txt_receiver_address.setText(sendToAdd)
            txt_send_to_name.text = ("Send coins to " + intent.getStringExtra("contactName"))
        }

        txt_receiver_address.setText(sendToAdd)

        send_coin_amount.addTextChangedListener(onTextChangedListener())


        // Define action for "Send Coin" button.
        btn_send_coin.setOnClickListener{
            if (txt_receiver_address.text.matches("^0x[0-9a-fA-F]{40}".toRegex())){
                transferCoin()
            } else {
                txt_send_to_name.text = "Invalid address format ('x0' + 40 Hex)"
            }
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
            if (requestCode == CODE_PUB_KEY) {
                txt_receiver_address.setText(data?.getStringExtra("Key"))
            }
        }
    }




    // Function to transfer coins from an account defined by the private key to another account.
    private fun transferCoin() {

        val task = Runnable {
            // First get the private key from the database.
            val privateKey = sDb?.storedDataDao()?.getPrivateKey()
            // Then make the transfer request.
            sUiHandler.post {
                // Create new queue for HTTP requests.
                val queue = Volley.newRequestQueue(this)

                // Get the URL of the server to transfer coins.
                val serverUrl: String = getString(R.string.server_url)
                val url = serverUrl.plus("/transfer")
                // While the amount of coins is entered with two decimal points but is handled by
                // the smart contract as an int, it has to be converted.
                var amountOfCoin = send_coin_amount.text.toString()
                amountOfCoin = amountOfCoin.replace("[.,]".toRegex(), "")
                // Remove starting zeros from the amount of coin string.
                amountOfCoin = amountOfCoin.replace("^0+".toRegex(), "")
                println("The amount of coins to send issssssssssssssssssssssssssssssssssssssssss")
                println(amountOfCoin)
                // Create a JSON object containing the private key of the account to send money from, the
                // public key of the account to send money to and the amount of coins to transfer.
                val reqParam = JSONObject()
                reqParam.put("pri_key", privateKey)
                reqParam.put("send_to", txt_receiver_address.text)
                reqParam.put("amount", amountOfCoin)
                // Create the request object.
                val req = JsonObjectRequest(Request.Method.POST, url, reqParam,
                        Response.Listener{
                            response ->
                            // Write the status in the text box as feedback for the user.
                            txt_send_status.hint = response.getString("status")

                        }, Response.ErrorListener {
                    txt_send_status.hint = "Transaction failed :("
                })
                req.setRetryPolicy(DefaultRetryPolicy(100000, 0, 1f))

                // Add the request object to the queue.
                queue.add(req)
            }
        }
        sDbWorkerThread.postTask(task)
    }

    private fun onTextChangedListener(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Display input numbers always in the format "#.##".
                send_coin_amount.removeTextChangedListener(this)

                try {
                    var originalString = s.toString().replace("[,.]".toRegex(), "")
                    val parsed = originalString.toDouble()
                    val numberToFormat: NumberFormat
                    val formattedString: String

                    if (parsed == 0.0) {
                        formattedString = "0.00"
                    } else {
                        numberToFormat = NumberFormat.getNumberInstance()
                        numberToFormat.minimumFractionDigits = 2
                        formattedString = numberToFormat.format(parsed/100)

                    }
                    // Setting formatted text in text box.
                    send_coin_amount.setText(formattedString)
                    send_coin_amount.setSelection(formattedString.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }
                send_coin_amount.addTextChangedListener(this)
            }
            override fun afterTextChanged(s: Editable) {

            }
        }
    }

}

