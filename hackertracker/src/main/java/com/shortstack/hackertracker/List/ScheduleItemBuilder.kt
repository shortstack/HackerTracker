package com.shortstack.hackertracker.List

import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Model.Day
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Time
import com.shortstack.hackertracker.Renderer.ItemRenderer
import com.shortstack.hackertracker.Renderer.RelativeDayRender
import com.shortstack.hackertracker.Renderer.RelativeTimeRenderer


class ScheduleItemBuilder : RendererBuilder<Item>() {
    init {

        bind(Item::class.java, ItemRenderer())
                .bind(Day::class.java, RelativeDayRender())
                .bind(Time::class.java, RelativeTimeRenderer())
    }
}
