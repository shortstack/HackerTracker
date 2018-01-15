package com.shortstack.hackertracker.models

import java.io.Serializable

data class Vendors(
        var vendors : Array<Vendor>
)

data class Vendor(
        var title : String,
        var description : String,
        var link : String,
        var partner : Int = 0
) : Serializable

