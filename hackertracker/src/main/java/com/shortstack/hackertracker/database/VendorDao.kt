package com.shortstack.hackertracker.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.shortstack.hackertracker.models.Vendor
import io.reactivex.Flowable


/**
 * Created by Chris on 3/31/2018.
 */
@Dao
interface VendorDao {

    @Query("SELECT * FROM vendor")
    fun getAll(): LiveData<List<Vendor>>

    @Query("SELECT * FROM vendor WHERE con = :con")
    fun getAll(con: String): LiveData<List<Vendor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<Vendor>)

    @Delete
    fun delete(user: Vendor)

}