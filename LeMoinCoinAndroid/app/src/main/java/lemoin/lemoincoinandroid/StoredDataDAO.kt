package lemoin.lemoincoinandroid

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*

@Dao
interface StoredDataDao {

    @Query("SELECT * from storedData")
    fun getAll(): List<StoredData>

    @Insert(onConflict = REPLACE)
    fun insert(storedData: StoredData)

    @Query("SELECT name FROM storedData WHERE owner LIKE 'owner'")
    fun getOwner(): String

    @Query("SELECT privateKey FROM storedData WHERE owner LIKE 'owner'")
    fun getPrivateKey(): String

    @Query("SELECT publicKey FROM storedData WHERE owner LIKE 'owner'")
    fun getOwnerAddress(): String

    @Query("DELETE FROM storedData WHERE owner LIKE 'owner'")
    fun deleteOwner()

    @Query("SELECT * FROM storedData WHERE owner LIKE 'contact' ORDER BY name")
    fun getContact(): List<StoredData>

    @Query("SELECT name FROM storedData WHERE publicKey IN (:arg0)")
    fun getContactByAddressList(addressList: MutableList<String>): List<String>

    @Query("SELECT name FROM storedData WHERE LOWER(publicKey) LIKE :arg0 LIMIT 1")
    fun getContactByAddress(address:String): String

    @Query("DELETE FROM storedData WHERE owner LIKE 'contact'")
    fun deleteAllContact()

    @Query("DELETE FROM storedData WHERE id LIKE :arg0")
    fun deleteContact(idToDelete: Long?)

    @Query("SELECT * FROM storedData")
    fun getAllData(): List<StoredData>

    @Query("DELETE from storedData")
    fun deleteAllData()
}