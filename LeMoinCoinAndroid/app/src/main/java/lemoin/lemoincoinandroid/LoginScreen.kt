package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_screen.*

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


        var newData = StoredData()
        newData.walletName = "Das2"
        newData.privateKey = "ist2"
        newData.publicKey = "ein Test!2"
        newData.ownerName = "owner1"


        insertStoredDataInDb(newData)

        fetchOwnerFromDb()




        // If one of the QR buttons is clicked, use them to set the public or private key.


        btn_qr_user_key.setOnClickListener{
            val intent = Intent(this@LoginScreen, QrCodeScanner::class.java)
            // Start the QR code scanner to get a key. The request code defines weather the qr scanner
            // scans the private key or the public key of the receiver.
            startActivityForResult(intent, 1)
        }

        btn_user_data_submit.setOnClickListener {
            return@setOnClickListener

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



    private fun fetchOwnerFromDb() {
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
        println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHERE")
        println(task)
        sDbWorkerThread.postTask(task)
    }

}
