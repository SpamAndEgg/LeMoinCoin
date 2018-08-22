package lemoin.lemoincoinandroid

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.contact_row.view.*

class AdapterAddressBook(contactData:List<StoredData>): RecyclerView.Adapter<CustomViewHolder>() {

    val contactData = contactData




    // Get addresses from database.

    // Number of items in the list.
    override fun getItemCount(): Int {
        return contactData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.contact_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // Change properties of your rows on create here.
        val add = contactData[position].publicKey
        // Only give out the last part of the address for a better overview.
        holder.itemView.textView_contact_address.text = "...".plus(add.takeLast(15))
        //holder.itemView.textView_contact_address.text = add
        holder.itemView.textView_contact_name.text = contactData[position].walletName
        // Get information of the clicked object for further use in the onClickListener.
        holder.this_address = contactData[position].publicKey
        holder.this_name = contactData[position].walletName
        holder.this_id = contactData[position].id
    }
}

class CustomViewHolder(view: View, var this_address: String = "not given", var this_name: String = "not given", var this_id: Long? = null): RecyclerView.ViewHolder(view) {
    init {
        val sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        val sDb = StoredDataBase.getInstance(view.context)

        view.setOnClickListener {
            // Define what happens if click on a contact.
            val intent = Intent(view.context, SendCoin::class.java)
            intent.putExtra("contactAddress", this_address)
            intent.putExtra("contactName", this_name)
            view.context.startActivity(intent)
        }

        view.setOnLongClickListener {

            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("Do you want to delete this contact?")
            builder.setPositiveButton("Yes"){dialog, which ->
                val task = Runnable {
                    sDb?.storedDataDao()?.deleteContact(this_id)
                    val intent = Intent(view.context, AddressPage::class.java)
                    view.context.startActivity(intent)
                }
                sDbWorkerThread.postTask(task)}

            builder.setNegativeButton("No"){dialog, which ->}

            val dialog: AlertDialog = builder.create()
            dialog.show()
            true}
    }
}

