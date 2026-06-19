package com.dream.template.shared.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.dream.template.shared.core.presentation.AppRoute

class Navigator(startDestination: AppRoute) {
    val backStack: SnapshotStateList<AppRoute> = mutableStateListOf(startDestination)

    fun goTo(destination: AppRoute) {
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}