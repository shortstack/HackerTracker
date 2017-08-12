package com.shortstack.hackertracker.Network

import com.shortstack.hackertracker.Model.Types
import io.reactivex.Single
import io.reactivex.Single.zip
import io.reactivex.functions.BiFunction


class SyncRepository(private val databaseService: DatabaseService) {

    fun getSchedule(): Single<FullResponse> {

        return zip(
                databaseService.getSchedule,
                databaseService.getEventTypes,

                BiFunction<SyncResponse, Types, FullResponse> {
                    schedule, types ->
                    createModel(schedule, types)
                })
    }

    private fun createModel(f1: SyncResponse, f2: Types): FullResponse {
        return FullResponse(f1, f2)
    }

}
