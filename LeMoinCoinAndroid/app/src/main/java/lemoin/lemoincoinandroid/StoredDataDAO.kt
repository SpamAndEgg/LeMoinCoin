package lemoin.lemoincoinandroid

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*

@Dao
interface StoredDataDao {

    @Query("SELECT * from storedData")
    fun getAll(): List<StoredData>

    @Insert(onConflict = REPLACE)
    fun insert(storedData: StoredData)

    //@Query("DELETE from weatherData")
    //fun deleteAll()
}