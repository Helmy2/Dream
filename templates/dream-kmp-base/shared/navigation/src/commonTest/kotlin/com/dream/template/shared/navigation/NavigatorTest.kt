package com.dream.template.shared.navigation

import com.dream.template.shared.core.presentation.SampleRoute
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigatorTest {
    @Test
    fun initializesWithStartDestination() {
        val navigator = Navigator(startDestination = SampleRoute)

        assertEquals(listOf(SampleRoute), navigator.backStack.toList())
    }

    @Test
    fun navigatesForwardAndBack() {
        val navigator = Navigator(startDestination = SampleRoute)

        navigator.goTo(SampleRoute)
        assertEquals(listOf(SampleRoute, SampleRoute), navigator.backStack.toList())

        navigator.goBack()
        assertEquals(listOf(SampleRoute), navigator.backStack.toList())
    }

    @Test
    fun doesNotPopStartDestination() {
        val navigator = Navigator(startDestination = SampleRoute)

        navigator.goBack()

        assertEquals(listOf(SampleRoute), navigator.backStack.toList())
    }
}