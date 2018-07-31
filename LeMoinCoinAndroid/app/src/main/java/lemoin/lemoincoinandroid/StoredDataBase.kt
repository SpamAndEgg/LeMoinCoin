package lemoin.lemoincoinandroid

import android.arch.persistence.room.*
import android.content.Context

@Database(entities = arrayOf(StoredData::class), version = 2)
abstract class StoredDataBase : RoomDatabase() {

    abstract fun storedDataDao(): StoredDataDao

    companion object {
        private var INSTANCE: StoredDataBase? = null

        fun getInstance(context: Context): StoredDataBase? {
            if (INSTANCE == null) {
                synchronized(StoredDataBase::class) {
                    // fallbackToDestructiveMigration will drop databases if a new version of
                    // database is set up.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StoredDataBase::class.java, "storage.db")
                            .fallbackToDestructiveMigration()
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