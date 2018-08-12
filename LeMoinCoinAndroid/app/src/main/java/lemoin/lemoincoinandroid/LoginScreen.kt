package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_screen.*
import org.kethereum.crypto.getAddress
import org.kethereum.crypto.publicKeyFromPrivate
import org.kethereum.extensions.hexToBigInteger
import org.kethereum.extensions.toHexStringNoPrefix
import org.kethereum.extensions.toHexStringZeroPadded

import java.math.BigInteger


class LoginScreen : AppCompatActivity() {

    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)



        // Database setup according to
        // https://medium.com/mindorks/android-architecture-components-room-and-kotlin-f7b725c8d1d
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)


        /*var newData = StoredData()
        newData.walletName = "Das2"
        newData.privateKey = "ist2"
        newData.publicKey = "ein Test!2"
        newData.ownerName = "owner"
        insertStoredDataInDb(newData)
        */

        // Check if an owner is already logged in. If so, forward to MainActivity.
        checkOwnerInfo()

        txt_user_key.setText("c98dd5164ee02a50b5ddb016cc1ca32fe2b5078b58329f2100711bd5aa039f4e")

        // If one of the QR buttons is clicked, use them to set the public or private key.


        btn_qr_user_key.setOnClickListener{
            val intent = Intent(this@LoginScreen, QrCodeScanner::class.java)
            // Start the QR code scanner to get a key. The request code defines weather the qr scanner
            // scans the private key or the public key of the receiver.
            startActivityForResult(intent, 1)
        }

        btn_user_data_submit.setOnClickListener {
            // Check if the entered private key has the correct size of 64 characters.
            if (txt_user_key.length() == 64){
                // Save app user data to the database and go to front page of app.
                // Create database object for the owner.
                var userData = StoredData()
                userData.ownerName = "owner"
                userData.privateKey = txt_user_key.text.toString()
                userData.walletName = txt_user_name.text.toString()
                // Derive users address from private key and add it database object.
                userData.publicKey = getAddressFromPrivateKey(userData.privateKey)
                // Save owner information to database.
                insertStoredDataInDb(userData)
                // Check if owner info was saved successfully, if so, forward to MainActivity.
                checkOwnerInfo()
            } else {
                txt_login_feedback.text = "Private key does not have the correct size."
            }
        }
    }


    // Overriding the "onActivityResult" allows to catch results that come back when the QR code
    // scanner is called.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                txt_user_key.setText(data?.getStringExtra("Key"))
            }
        }
    }



    private fun checkOwnerInfo() {
        val task = Runnable {
            val storageData = sDb?.storedDataDao()?.getOwner()
            sUiHandler.post{
                if (storageData == null || storageData.isEmpty()) {
                } else {
                    // Open new activity to get new owner.
                    // Go to login screen
                    val intent = Intent(this@LoginScreen, MainActivity::class.java)
                    // Start the QR code scanner to get a key. The request code defines weather the qr scanner
                    // scans the private key or the public key of the receiver.
                    startActivity(intent)
                }
            }
        }
        sDbWorkerThread.postTask(task)
    }

    private fun insertStoredDataInDb(storedData: StoredData) {
        val task = Runnable { sDb?.storedDataDao()?.insert(storedData) }
        println(task)
        sDbWorkerThread.postTask(task)
    }

    private fun getAddressFromPrivateKey(privateKey: String): String {
        // Convert private key string to big integer.
        val priKeyInt = privateKey.hexToBigInteger()
        // Derive public key as big integer.
        val pubKeyInt = publicKeyFromPrivate(priKeyInt)
        // Convert public key to hey.
        val pubKey = pubKeyInt.toHexStringNoPrefix()
        // Derive address as string and return it.
        return getAddress(pubKey)
    }

}
