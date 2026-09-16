/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 */

package com.github.zly2006.zhihu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import com.github.zly2006.zhihu.navigation.NavDestination
import com.github.zly2006.zhihu.navigation.Video

/** Identifies one adaptive host while a NavHost transition keeps two routes alive. */
data class AdaptiveHostKey(
    val routeId: String,
    val contentKey: Any?,
)

/** A one-shot request to replace the adaptive detail pane selection. */
class AdaptiveNavigationRequest(
    val id: Long,
    val destination: NavDestination,
    val ownerRouteId: String,
) {
    // NavHost can keep two route entries composed during an animated transition.
    // The same request is therefore visible to more than one host; claiming it
    // on the shared object makes delivery idempotent without widening the
    // composable function type.
    private var claimed = false

    fun claim(): Boolean = if (claimed) {
        false
    } else {
        claimed = true
        true
    }
}

/**
 * Platform hook for presenting a list and its selected content side by side.
 *
 * The default host keeps the existing single navigation stack. Android supplies
 * an adaptive implementation; other platforms do not need to know about the
 * AndroidX adaptive APIs.
 */
typealias AdaptiveContentHost = @Composable (
    listContent: @Composable () -> Unit,
    detailContent: @Composable (NavDestination) -> Unit,
    onSinglePaneDetailChanged: (Boolean) -> Unit,
    onDetailDestinationChanged: (NavDestination?) -> Unit,
    onDestinationOpened: (NavDestination, String?) -> Unit,
    listContentKey: Any?,
    navigationRequest: AdaptiveNavigationRequest?,
    onNavigationRequestConsumed: (Long) -> Unit,
    onHostActiveChanged: (Boolean) -> Unit,
) -> Unit

/** Currently selected content, exposed to list cards for a lightweight highlight. */
val LocalAdaptiveSelection = compositionLocalOf<NavDestination?> { null }

/** Bottom-bar space owned by the outer application scaffold. */
val LocalAdaptiveDetailBottomPadding = compositionLocalOf { 0.dp }

/** Optional Android-aware video opener for content rendered in the detail pane. */
val LocalAdaptiveVideoOpener = compositionLocalOf<((Video, NavDestination?) -> Unit)?> { null }
