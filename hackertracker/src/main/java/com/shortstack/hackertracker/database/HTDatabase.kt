package com.shortstack.hackertracker.database

import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import com.google.gson.Gson
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants.CONFERENCES_FILE
import com.shortstack.hackertracker.fromFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import javax.inject.Inject

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Conference::class), (Event::class), (Type::class), (Vendor::class),
    (Speaker::class), (FAQ::class), (Location::class), (EventSpeakerJoin::class)], version = 2)
@TypeConverters(value = [(Converters::class)])
abstract class HTDatabase : RoomDatabase() {

    abstract fun conferenceDao(): ConferenceDao

    abstract fun eventDao(): EventDao

    abstract fun typeDao(): TypeDao

    abstract fun speakerDao(): SpeakerDao

    abstract fun vendorDao(): VendorDao

    abstract fun faqDao(): FAQDao

    abstract fun locationDao(): LocationDao

    abstract fun eventSpeakerDao(): EventSpeakerDao

    @Inject
    lateinit var gson: Gson

    fun setup(conferenceLiveData: MutableLiveData<DatabaseConference>) {
        App.application.component.inject(this)

        gson.fromFile<Conferences>(CONFERENCES_FILE, root = null)?.let { conferences ->
            conferences.let {
                // Select the first one available.
                it.conferences.first().isSelected = true

                conferenceLiveData.postValue(DatabaseConference(it.conferences.first()))

                it.conferences.forEach { conference ->
                    val local = conferenceDao().get().find { it.conference.id == conference.id }?.conference
                    val response = FullResponse.getLocalFullResponse(conference, local)

                    if (response.isNotEmpty()) {
                        conferenceDao().upsert(conference)
                        updateDatabase(conference, response)
                    }
                }
            }
        }
    }

    @Transaction
    fun updateDatabase(conference: Conference, response: FullResponse) {

    }

    companion object {

        @Volatile
        private var INSTANCE: HTDatabase? = null

        private fun getInstance(context: Context, conferenceLiveData: MutableLiveData<DatabaseConference>): HTDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context)
                }


        fun buildDatabase(context: Context): HTDatabase {
            return Room.databaseBuilder(context, HTDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
//                    .fallbackToDestructiveMigration()
                    .addMigrations(object : Migration(1, 2) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            database.execSQL("DROP TABLE Vendor")
                            database.execSQL("CREATE TABLE Vendor(" +
                                    "`id` INTEGER NOT NULL," +
                                    "`name` TEXT NOT NULL," +
                                    "description TEXT," +
                                    "link TEXT," +
                                    "partner INTEGER NOT NULL," +
                                    "updatedAt TEXT NOT NULL," +
                                    "conference TEXT NOT NULL," +
                                    "PRIMARY KEY(`id`)," +
                                    "FOREIGN KEY(`conference`) REFERENCES Conference(code)" +
                                    "ON DELETE CASCADE" +
                                    ")")
                        }
                    })
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                        }
                    }).build().also {
                        INSTANCE = it
                    }
        }


        private const val DATABASE_NAME = "database"

    }
}