package com.shortstack.hackertracker.ui.schedule.list

import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeDayRender
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeTimeRenderer


class ScheduleBuilder : RendererBuilder.BaseRendererBuilder<Any> {
    override fun getRendererBuilder(): RendererBuilder<Any> {
        return RendererBuilder.create<Any>()
                .bind(DatabaseEvent::class.java, EventRenderer())
                .bind(Day::class.java, RelativeDayRender())
                .bind(Time::class.java, RelativeTimeRenderer())
                .rendererBuilder
    }
}
