package lemoin.lemoincoinandroid


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_address_page.*
import kotlinx.android.synthetic.main.toolbar.*

class AddressPage : AppCompatActivity() {

    private lateinit var sharedFun: SharedFun
    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_page)
        setSupportActionBar(toolbar_page)


        sharedFun = SharedFun(this, this@AddressPage, savedInstanceState)
        sharedFun.setDrawer()

        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)


        // Helps rendering items in list.
        showAddressBook()



        btn_add_contact.setOnClickListener {
            val intent = Intent(this@AddressPage, AddContact::class.java)
            startActivity(intent)
        }






    }

    private fun showAddressBook() {
        val task = Runnable {
            val contactData = sDb?.storedDataDao()?.getContact()
            sUiHandler.post{
                if (contactData == null) {
                    println("AddressPage.kt: OPENING CONTACT DATA FAILED")
                } else {
                    // Address page inspired by
                    // https://www.youtube.com/watch?v=jS0buQyfJfs&list=PL0dzCUj1L5JGfHj1lwxOq67zAJV3e1S9S
                    recyclerView_address_book.layoutManager = LinearLayoutManager(this)
                    // Contact data was loaded successfully and can now be displayed.
                    recyclerView_address_book.adapter = AdapterAddressBook(contactData)

                }
            }

        }
        sDbWorkerThread.postTask(task)
    }
}



