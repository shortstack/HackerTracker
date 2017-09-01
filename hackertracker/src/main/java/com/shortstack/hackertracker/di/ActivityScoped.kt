package com.shortstack.hackertracker.di

import java.lang.annotation.Documented
import javax.inject.Scope

/**
 * In Dagger, an unscoped component cannot depend on a scoped component. As
 * {@link AppComponent} is a scoped component ({@code @Singleton}, we create a custom
 * scope to be used by all fragment components. Additionally, a component with a specific scope
 * cannot have a sub component with the same scope.
 */
@Documented
@Scope
@kotlin.annotation.Retention(kotlin.annotation.AnnotationRetention.RUNTIME)
annotation class ActivityScoped
