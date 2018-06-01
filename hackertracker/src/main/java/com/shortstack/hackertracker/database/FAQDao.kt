package com.shortstack.hackertracker.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.shortstack.hackertracker.models.FAQ
import io.reactivex.Flowable

/**
 * Created by Chris on 5/31/2018.
 */
@Dao
interface FAQDao {

    @Query("SELECT * FROM faq")
    fun getAll(): Flowable<List<FAQ>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(faq: List<FAQ>)
}