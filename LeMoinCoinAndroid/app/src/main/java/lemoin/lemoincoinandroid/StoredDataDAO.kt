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

    @Query("SELECT publicKey FROM storedData WHERE owner LIKE 'owner'")
    fun getOwnerAddress(): String

    @Query("DELETE FROM storedData WHERE owner LIKE 'owner'")
    fun deleteOwner()

    //@Query("DELETE from weatherData")
    //fun deleteAll()
}