package com.misterjerry.runningtracker.core.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.misterjerry.runningtracker.ui.Home.HomeRoot
import com.misterjerry.runningtracker.ui.Run.RunRoot
import com.misterjerry.runningtracker.ui.RunDetail.RunDetailRoot

@Composable
fun NavigationRoot() {
    val backStack = rememberNavBackStack(Route.Home)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeRoot(
                    onStartRunClick = {
                        backStack.add(Route.Run)
                    },
                    onRunClick = { id ->
                        backStack.add(Route.RunDetail(id))
                    }
                )
            }
            entry<Route.Run> {
                RunRoot(
                    onFinish = {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    }
                )
            }
            entry<Route.RunDetail> {
                RunDetailRoot(runId = it.runId)
            }
        }
    )
}