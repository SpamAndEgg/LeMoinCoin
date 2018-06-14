package lemoin.lemoincoinandroid

import android.arch.persistence.room.*
import android.content.Context

@Database(entities = arrayOf(StoredData::class), version = 1)
abstract class StoredDataBase : RoomDatabase() {

    abstract fun storedDataDao(): StoredDataDao

    companion object {
        private var INSTANCE: StoredDataBase? = null

        fun getInstance(context: Context): StoredDataBase? {
            if (INSTANCE == null) {
                synchronized(StoredDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StoredDataBase::class.java, "stored.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}