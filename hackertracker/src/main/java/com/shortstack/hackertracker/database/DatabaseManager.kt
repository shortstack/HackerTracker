package com.shortstack.hackertracker.database

import android.arch.persistence.room.Room
import android.content.Context
import com.shortstack.hackertracker.models.Conference

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager(private val context: Context) {

    val cons: ConferenceDatabase
    var db: MyRoomDatabase

    init {
        cons = Room.databaseBuilder(context, ConferenceDatabase::class.java, CONFERENCE_DATABASE)
                .allowMainThreadQueries().build()
        cons.init()

        val current = cons.conferenceDao().getCurrentCon()
        db = MyRoomDatabase.buildDatabase(context, current.directory)
    }

    fun initConference(conference: Conference) {
        db = MyRoomDatabase.buildDatabase(context, conference.directory)
    }

    companion object {
        private const val CONFERENCE_DATABASE = "conference"
    }


}