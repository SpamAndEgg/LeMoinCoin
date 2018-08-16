package lemoin.lemoincoinandroid

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.contact_row.view.*
import javax.xml.transform.Templates

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
        holder.itemView.textView_contact_address.text = contactData[position].publicKey
        holder.itemView.textView_contact_name.text = contactData[position].walletName
        // Get information of the clicked object for further use in the onClickListener.
        holder.this_address = contactData[position].publicKey
        holder.this_name = contactData[position].walletName
    }
}

class CustomViewHolder(view: View, var this_address: String = "not given", var this_name: String = "not given"): RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            // Define what happens if click on a contact.
            val intent = Intent(view.context, SendCoin::class.java)
            intent.putExtra("contactAddress", this_address)
            intent.putExtra("contactName", this_name)
            view.context.startActivity(intent)
        }
    }

}