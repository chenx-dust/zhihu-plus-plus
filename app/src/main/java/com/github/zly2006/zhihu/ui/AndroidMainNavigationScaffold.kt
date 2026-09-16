/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 */

package com.github.zly2006.zhihu.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.testTag
import com.github.zly2006.zhihu.navigation.TopLevelDestination

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
)
@Composable
fun AndroidMainNavigationScaffold(
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
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    val navigationSuiteState = rememberNavigationSuiteScaffoldState()
    val shouldShowNavigation = showMainNavigationBar &&
        isTopLevelDestination &&
        (!navigationSuiteType.isHorizontalNavigation() || !isSinglePaneDetail) &&
        (!navigationSuiteType.isHorizontalNavigation() || !autoHideBottomBar || isBottomBarVisible)

    LaunchedEffect(shouldShowNavigation) {
        navigationSuiteState.snapTo(
            if (shouldShowNavigation) {
                NavigationSuiteScaffoldValue.Visible
            } else {
                NavigationSuiteScaffoldValue.Hidden
            },
        )
    }

    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = if (!isDarkTheme) {
            NavigationBarItemDefaults.colors().copy(
                selectedIndicatorColor =
                    MaterialTheme.colorScheme.secondaryContainer
                        .copy(alpha = 0.92f)
                        .compositeOver(MaterialTheme.colorScheme.secondary),
            )
        } else {
            NavigationBarItemDefaults.colors()
        },
        navigationRailItemColors = if (!isDarkTheme) {
            NavigationRailItemDefaults.colors().copy(
                selectedIndicatorColor =
                    MaterialTheme.colorScheme.secondaryContainer
                        .copy(alpha = 0.92f)
                        .compositeOver(MaterialTheme.colorScheme.secondary),
            )
        } else {
            NavigationRailItemDefaults.colors()
        },
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        state = navigationSuiteState,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            shortNavigationBarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            navigationBarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        navigationSuiteItems = {
            navigationItems.forEach { item ->
                item(
                    selected = currentDestination?.let { it::class == item.destination::class } == true,
                    onClick = { onItemClick(item.destination) },
                    alwaysShowLabel = true,
                    colors = itemColors,
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    modifier = Modifier.testTag("nav_tab_${item.destination.name.lowercase()}"),
                )
            }
        },
    ) {
        Scaffold(
            modifier = Modifier,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = FabPosition.Center,
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

private fun NavigationSuiteType.isHorizontalNavigation(): Boolean =
    this == NavigationSuiteType.ShortNavigationBarCompact ||
        this == NavigationSuiteType.ShortNavigationBarMedium ||
        this == NavigationSuiteType.NavigationBar
