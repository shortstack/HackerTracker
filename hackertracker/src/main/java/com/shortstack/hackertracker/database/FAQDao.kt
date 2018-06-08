package com.shortstack.hackertracker.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.FAQ
import io.reactivex.Flowable

/**
 * Created by Chris on 5/31/2018.
 */
@Dao
interface FAQDao {

    @Query("SELECT * FROM faq WHERE con = :con")
    fun getAll(con: String): LiveData<List<FAQ>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(faq: List<FAQ>)
}