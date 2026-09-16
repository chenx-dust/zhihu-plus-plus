/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 */

package com.github.zly2006.zhihu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.zly2006.zhihu.navigation.TopLevelDestination

data class MainNavigationItem(
    val destination: TopLevelDestination,
    val label: String,
    val icon: ImageVector,
)

typealias MainNavigationScaffold = @Composable (
    modifier: Modifier,
    isDarkTheme: Boolean,
    showMainNavigationBar: Boolean,
    isTopLevelDestination: Boolean,
    isSinglePaneDetail: Boolean,
    autoHideBottomBar: Boolean,
    isBottomBarVisible: Boolean,
    navigationItems: List<MainNavigationItem>,
    currentDestination: TopLevelDestination?,
    onItemClick: (TopLevelDestination) -> Unit,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) -> Unit

@Composable
fun DefaultMainNavigationScaffold(
    modifier: Modifier,
    isDarkTheme: Boolean,
    showMainNavigationBar: Boolean,
    isTopLevelDestination: Boolean,
    isSinglePaneDetail: Boolean,
    autoHideBottomBar: Boolean,
    isBottomBarVisible: Boolean,
    navigationItems: List<MainNavigationItem>,
    currentDestination: TopLevelDestination?,
    onItemClick: (TopLevelDestination) -> Unit,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val bottomPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues().calculateBottomPadding()
    Scaffold(
        modifier = modifier,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (showMainNavigationBar && isTopLevelDestination) {
                AnimatedVisibility(
                    visible = !isSinglePaneDetail && (!autoHideBottomBar || isBottomBarVisible),
                    enter = slideInVertically(tween(200)) { it },
                    exit = slideOutVertically(tween(200)) { it },
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.height(64.dp + bottomPadding),
                    ) {
                        navigationItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentDestination?.let { it::class == item.destination::class } == true,
                                onClick = { onItemClick(item.destination) },
                                label = { Text(item.label) },
                                alwaysShowLabel = true,
                                colors = if (!isDarkTheme) {
                                    NavigationBarItemDefaults.colors().copy(
                                        selectedIndicatorColor =
                                            MaterialTheme.colorScheme.secondaryContainer
                                                .copy(alpha = 0.92f)
                                                .compositeOver(MaterialTheme.colorScheme.secondary),
                                    )
                                } else {
                                    NavigationBarItemDefaults.colors()
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .testTag("nav_tab_${item.destination.name.lowercase()}"),
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}
