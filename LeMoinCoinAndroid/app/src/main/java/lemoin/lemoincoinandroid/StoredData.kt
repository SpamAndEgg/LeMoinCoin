package lemoin.lemoincoinandroid

import android.arch.persistence.room.*

@Entity(tableName = "storedData")
data class StoredData(@PrimaryKey(autoGenerate = true) var id: Long?,
                       @ColumnInfo(name = "name") var walletName: String,
                       @ColumnInfo(name = "privateKey") var privateKey: String,
                       @ColumnInfo(name = "publicKey") var publicKey: String,
                       @ColumnInfo(name = "owner") var ownerName: String

){
    constructor():this(null,"default","default","default", "default")
}