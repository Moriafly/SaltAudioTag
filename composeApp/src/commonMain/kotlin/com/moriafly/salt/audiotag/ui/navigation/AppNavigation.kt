/*
 * Salt Audio Tag
 * Copyright (C) 2025 Moriafly
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 */

package com.moriafly.salt.audiotag.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moriafly.salt.audiotag.ui.screen.audiotag.AudioTagScreen
import com.moriafly.salt.audiotag.ui.screen.main.MainScreen

val LocalNavController = compositionLocalOf<NavController> {
    error("LocalNavController is not provided.")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = "",
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            composable(ScreenRoute.MAIN) { MainScreen() }
            composable(ScreenRoute.AUDIO_TAG) { AudioTagScreen() }
        }
    }
}

private val AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition: EnterTransition
    get() = slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(350)
    )

private val AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition: ExitTransition
    get() = slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(350)
    )

private val AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition: EnterTransition
    get() = slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(350)
    )

private val AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition: ExitTransition
    get() = slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(350)
    )
