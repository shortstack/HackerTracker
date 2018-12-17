package com.shortstack.hackertracker.ui.schedule.list

import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeDayRender
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeTimeRenderer


class ScheduleBuilder : RendererBuilder.BaseRendererBuilder<Any> {
    override fun getRendererBuilder(): RendererBuilder<Any> {
        return RendererBuilder.create<Any>()
                .bind(FirebaseEvent::class.java, EventRenderer())
                .bind(Day::class.java, RelativeDayRender())
                .bind(Time::class.java, RelativeTimeRenderer())
                .rendererBuilder
    }
}
