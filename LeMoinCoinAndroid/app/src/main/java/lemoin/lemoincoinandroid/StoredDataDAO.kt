package lemoin.lemoincoinandroid

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*

@Dao
interface StoredDataDao {

    @Query("SELECT * from storedData")
    fun getAll(): List<StoredData>

    @Insert(onConflict = REPLACE)
    fun insert(storedData: StoredData)

    @Query("SELECT * FROM storedData WHERE owner LIKE 'owner'")
    fun getOwner(): List<StoredData>

    //@Query("DELETE from weatherData")
    //fun deleteAll()
}