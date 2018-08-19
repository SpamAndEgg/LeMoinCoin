package lemoin.lemoincoinandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
//import kotlinx.android.synthetic.main.send_coin.*
import kotlinx.android.synthetic.main.toolbar.*
import net.glxn.qrgen.android.QRCode
import android.widget.ImageView
import android.os.Handler
import android.widget.Toast

class ShareQR : AppCompatActivity() {

    private var sDb: StoredDataBase? = null
    private lateinit var sDbWorkerThread: DbWorkerThread
    private val sUiHandler = Handler()
    private lateinit var sharedFun: SharedFun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_qr)
        setSupportActionBar(toolbar_page)

        // get drawer
        sharedFun = SharedFun(this, this@ShareQR, savedInstanceState)
        sharedFun.setDrawer()

        // Create DB worker
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()
        sDb = StoredDataBase.getInstance(this)

        // get Adress from DB and create a QR code
        getQrCode()
    }

    private fun getQrCode() {
        val task = Runnable {
            var storageData = sDb?.storedDataDao()?.getOwnerAddress()
            storageData = "0x" + storageData
            sUiHandler.post{
                //Toast.makeText(getApplicationContext(),storageData,Toast.LENGTH_LONG).show()
                val myBitmap = QRCode.from(storageData).bitmap()
                val myImage = findViewById<ImageView>(R.id.imageShareQR) as ImageView
                myImage.setImageBitmap(myBitmap)
            }
        }
        sDbWorkerThread.postTask(task)
    }
}