package com.shortstack.hackertracker.database

import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.Constants.CONFERENCES_FILE
import com.shortstack.hackertracker.fromFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Conference::class), (Event::class), (Type::class), (Vendor::class),
    (Speaker::class), (FAQ::class), (Location::class), (EventSpeakerJoin::class)], version = 1)
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
                conferenceDao().insertAll(it.conferences)

//                conferenceLiveData.postValue(conferenceDao().getCurrentCon())

                it.conferences.forEach {
                    updateDatabase(it, FullResponse.getLocalFullResponse(it))
                }
            }
        }
    }

    @Transaction
    fun updateDatabase(conference: Conference, response: FullResponse) {
        Logger.d("Updating conference: ${conference.code}")

        response.run {
            types?.let {
                typeDao().insertAll(it.types)
            }

            speakers?.let {
                speakerDao().insertAll(it.speakers)
            }

            locations?.let {
                locationDao().insertAll(it.locations)
            }

            events?.let {
                it.events.forEach { event ->
                    eventDao().upsert(event)
                }

                it.events.forEach { event ->
                    event.speakers.forEach {
                        try {
                            val join = EventSpeakerJoin(event.id, it)
                            eventSpeakerDao().insert(join)
                        } catch (ex: Exception) {
                            Logger.e("Could not insert ${event.id} + $it. ${ex.message}")
                        }
                    }
                }
            }

            vendors?.let {
                vendorDao().insertAll(it.vendors)
            }

            faqs?.let {
                try {
                    faqDao().insertAll(it.faqs)
                } catch (ex: Exception) {
                    Logger.e("Could not insert $it. ${ex.message}")
                }
            }
        }

        conferenceDao().upsert(conference)
    }

    companion object {

        @Volatile
        private var INSTANCE: HTDatabase? = null

        private fun getInstance(context: Context, conferenceLiveData: MutableLiveData<DatabaseConference>): HTDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context, conferenceLiveData)
                }


        fun buildDatabase(context: Context, conferenceLiveData: MutableLiveData<DatabaseConference>): HTDatabase {
            Logger.d("Creating database! " + (System.currentTimeMillis() - App.application.timeToLaunch))

            return Room.databaseBuilder(context, HTDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Logger.d("Database onCreate! " + (System.currentTimeMillis() - App.application.timeToLaunch))
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Logger.d("Database onOpen! " + (System.currentTimeMillis() - App.application.timeToLaunch))
                            updateDatabase(context, conferenceLiveData)
                        }
                    }).build().also {
                        INSTANCE = it
                    }
        }

        private fun updateDatabase(context: Context, conferenceLiveData: MutableLiveData<DatabaseConference>) {
            Single.fromCallable {
                val instance = getInstance(context, conferenceLiveData)


                // TODO: Check if it needs to be updated.
//                if (BuildConfig.DEBUG) {
//                    instance.clearAllTables()
//                }
//
//                instance.setup(conferenceLiveData)

                val currentCon = instance.conferenceDao().getCurrentCon()
                Logger.d("Setting current conference $currentCon")
                conferenceLiveData.postValue(currentCon)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }

        private const val DATABASE_NAME = "database"

    }
}