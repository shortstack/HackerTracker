package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.FAQ
import io.reactivex.Flowable

/**
 * Created by Chris on 5/31/2018.
 */
@Dao
interface FAQDao {

    @Query("SELECT * FROM faq WHERE conference = :conference")
    fun getAll(conference: String): LiveData<List<FAQ>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(faq: List<FAQ>): List<Long>
}