package lemoin.lemoincoinandroid

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
        if (ContextCompat.checkSelfPermission(this@LoginScreen,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@LoginScreen,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@LoginScreen,
                        arrayOf(Manifest.permission.CAMERA),1)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }


        // Database setup according to
        // https://medium.com/mindorks/android-architecture-components-room-and-kotlin-f7b725c8d1d
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)


        // Check if an owner is already logged in. If so, forward to MainActivity.
        checkOwnerInfo()

        // If one of the QR buttons is clicked, use them to set the public or private key.


        btn_qr_user_key.setOnClickListener{
            val intent = Intent(this@LoginScreen, QrCodeScanner::class.java)
            // Start the QR code scanner to get a key. The request code defines weather the qr scanner
            // scans the private key or the public key of the receiver.
            startActivityForResult(intent, 1)
        }

        btn_user_data_submit.setOnClickListener {
            // Check if the entered private key has the correct size of 64 characters.
            if (txt_user_key.text.matches("[0-9a-fA-F]{64}".toRegex())){
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
                txt_login_feedback.text = "Private key not valid (has to be 64 hex chars)."
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
