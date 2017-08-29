package com.shortstack.hackertracker.Model

import java.util.*

class Filter(val typesArray : Array<String>) {

    constructor() : this(Array<String>(0, { "" })) {}

    constructor(types : Set<String>) : this(types.toTypedArray()) {}

    val typesSet : Set<String>
        get() = HashSet(Arrays.asList(*typesArray))

}
