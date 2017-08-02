package com.shortstack.hackertracker.Model

import java.util.Arrays
import java.util.HashSet

class Filter(val typesArray: Array<String>) {

    constructor(types: Set<String>) : this(types.toTypedArray()) {}

    val typesSet: Set<String>
        get() = HashSet(Arrays.asList(*typesArray))

}
