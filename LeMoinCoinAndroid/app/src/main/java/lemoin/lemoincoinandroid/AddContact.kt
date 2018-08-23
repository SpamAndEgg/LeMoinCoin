package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.contact_add.*


class AddContact : AppCompatActivity(){

    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_add)



        // Database setup according to
        // https://medium.com/mindorks/android-architecture-components-room-and-kotlin-f7b725c8d1d
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)

        // If one of the QR buttons is clicked, use them to set the public or private key.


        btn_qr_contact_key.setOnClickListener{
            val intent = Intent(this@AddContact, QrCodeScanner::class.java)
            // Start the QR code scanner to get a key. The request code defines weather the qr scanner
            // scans the private key or the public key of the receiver.
            startActivityForResult(intent, 1)
        }

        btn_add_contact.setOnClickListener {
            // Check if the entered address has the correct size of 42 characters.
            if (txt_contact_key.text.matches("^xO[0-9a-fA-F]{40}".toRegex())){
                // Save app user data to the database and go to front page of app.
                // Create database object for the owner.
                var userData = StoredData()
                userData.ownerName = "contact"
                userData.walletName = txt_contact_name.text.toString()
                // Derive users address from private key and add it database object.
                userData.publicKey = txt_contact_key.text.toString()
                // Save owner information to database.
                insertContactInDb(userData)
                // Check if owner info was saved successfully, if so, forward to MainActivity.

                showContact()
            } else {
                txt_contact_add_feedback.text = "Contact address does not have the correct size."
            }
        }
    }


    // Overriding the "onActivityResult" allows to catch results that come back when the QR code
    // scanner is called.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                txt_contact_key.setText(data?.getStringExtra("Key"))
            }
        }
    }




    private fun insertContactInDb(storedData: StoredData) {
        val task = Runnable {
            // Save contact data to database.
            sDb?.storedDataDao()?.insert(storedData)
            // After saving, open the address page again.
            sUiHandler.post {
                val intent = Intent(this@AddContact, AddressPage::class.java)
                startActivity(intent)
            }}
        sDbWorkerThread.postTask(task)
    }

    private fun showContact() {
        val task = Runnable {
            val contactData = sDb?.storedDataDao()?.getAllData()
            sUiHandler.post{
                if (contactData == null) {
                    println("OPENING CONTEACT DATA FAIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIILED")
                } else {
                    println("HERE IS THE CONTACT DATAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                    println(contactData)
                }
            }

        }
        sDbWorkerThread.postTask(task)
    }


}