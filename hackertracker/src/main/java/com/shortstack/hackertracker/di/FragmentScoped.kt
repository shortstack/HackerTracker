package com.shortstack.hackertracker.di

import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(kotlin.annotation.AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class FragmentScoped
