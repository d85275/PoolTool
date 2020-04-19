package com.e.pooltool.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface PlayerRecordDao {

    @Query("select * from playerrecorditem")
    fun getAll(): Single<List<PlayerRecordItem>>

    @Query("select * from playerrecorditem where id in (:userIds)")
    fun getAllByIds(userIds: IntArray): List<PlayerRecordItem>

    @Query("select * from playerrecorditem where name = (:playerName)")
    fun getRecords(playerName: String): Single<List<PlayerRecordItem>>

    @Query("delete from playerrecorditem where name = (:name)")
    fun delete(name: String)

    @Insert
    fun insertAll(vararg playerRecords: PlayerRecordItem)

    @Delete
    fun delete(player: PlayerRecordItem)

}