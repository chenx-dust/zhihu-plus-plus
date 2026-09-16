/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 */

package com.github.zly2006.zhihu.ui

import android.os.Parcelable
import androidx.activity.compose.PredictiveBackHandler
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.MutableThreePaneScaffoldState
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import com.github.zly2006.zhihu.filter.ContentOpenEventSupport
import com.github.zly2006.zhihu.filter.ContentOpenFrom
import com.github.zly2006.zhihu.navigation.Account
import com.github.zly2006.zhihu.navigation.Article
import com.github.zly2006.zhihu.navigation.ArticleType
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.NavDestination
import com.github.zly2006.zhihu.navigation.Navigator
import com.github.zly2006.zhihu.navigation.Person
import com.github.zly2006.zhihu.navigation.Pin
import com.github.zly2006.zhihu.navigation.Question
import com.github.zly2006.zhihu.navigation.Video
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.ui.subscreens.LIST_PANE_DEFAULT_WIDTH_DP_PREFERENCE_KEY
import com.github.zly2006.zhihu.viewmodel.sharedArticleAnswerSwitchState
import kotlinx.coroutines.CancellationException
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.UUID

/** Stable Android state used by the adaptive list/detail navigator. */
@Parcelize
@Keep
private data class AdaptivePaneDestination(
    val type: Type,
    val id: String = "",
    val title: String = "",
    val urlToken: String = "",
    val jumpTo: String = "",
    val setting: String = "",
    val readingQueueSourceId: String? = null,
) : Parcelable {
    enum class Type {
        Answer,
        Article,
        Question,
        Pin,
        Person,
        Appearance,
        Recommend,
        SystemAndUpdate,
        Developer,
        DeveloperColorScheme,
        RecommendBlocklist,
        RecommendBlockedHistory,
        IdentityManagement,
        ReadingSettings,
        SettingsSearch,
        OpenSourceLicenses,
    }

    private val stableIdentity: String
        get() = when (type) {
            Type.Appearance,
            Type.Recommend,
            Type.SystemAndUpdate,
            -> setting
            Type.Person -> {
                val identity = id.takeUnless { it.isBlank() || it == Person.EMPTY_ID } ?: urlToken
                "$identity\u0000$jumpTo"
            }
            else -> id
        }

    override fun equals(other: Any?): Boolean =
        other is AdaptivePaneDestination && type == other.type && stableIdentity == other.stableIdentity

    override fun hashCode(): Int = 31 * type.hashCode() + stableIdentity.hashCode()
}

// Entry identity is separate from content identity: revisiting a popped answer starts
// a new scope, while an answer still below a profile retains its existing state.
@Parcelize
private data class AdaptivePaneEntry(
    val destination: AdaptivePaneDestination,
    val key: String = UUID.randomUUID().toString(),
) : Parcelable {
    @IgnoredOnParcel
    val immersiveMode = mutableStateOf(false)
}

private fun NavDestination.toAdaptivePaneDestination(): AdaptivePaneDestination? = when (this) {
    is Article -> AdaptivePaneDestination(
        type = if (type == ArticleType.Answer) {
            AdaptivePaneDestination.Type.Answer
        } else {
            AdaptivePaneDestination.Type.Article
        },
        id = id.toString(),
        title = title,
        readingQueueSourceId = readingQueueSourceId,
    )
    is Question -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.Question,
        id = questionId.toString(),
        title = title,
        readingQueueSourceId = readingQueueSourceId,
    )
    is Pin -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.Pin,
        id = id.toString(),
        title = authorName,
        readingQueueSourceId = readingQueueSourceId,
    )
    is Person -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.Person,
        id = id,
        title = name,
        urlToken = urlToken,
        jumpTo = jumpTo,
    )
    is Account.AppearanceSettings -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.Appearance,
        setting = setting,
    )
    is Account.RecommendSettings -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.Recommend,
        setting = setting,
    )
    is Account.SystemAndUpdateSettings -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.SystemAndUpdate,
        setting = setting,
    )
    Account.DeveloperSettings -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.Developer,
    )
    Account.DeveloperSettings.ColorScheme -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.DeveloperColorScheme,
    )
    Account.RecommendSettings.Blocklist -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.RecommendBlocklist,
    )
    Account.RecommendSettings.BlockedFeedHistory -> AdaptivePaneDestination(
        type = AdaptivePaneDestination.Type.RecommendBlockedHistory,
    )
    Account.IdentityManagement -> AdaptivePaneDestination(AdaptivePaneDestination.Type.IdentityManagement)
    Account.ReadingSettings -> AdaptivePaneDestination(AdaptivePaneDestination.Type.ReadingSettings)
    Account.SettingsSearch -> AdaptivePaneDestination(AdaptivePaneDestination.Type.SettingsSearch)
    Account.OpenSourceLicenses -> AdaptivePaneDestination(AdaptivePaneDestination.Type.OpenSourceLicenses)
    else -> null
}

private fun NavDestination.readingQueueSourceId(): String? = when (this) {
    is Article -> readingQueueSourceId
    is Question -> readingQueueSourceId
    is Pin -> readingQueueSourceId
    else -> null
}

private fun NavDestination.readingQueueOpenFrom(): String? = when {
    readingQueueSourceId()?.startsWith("question:") == true -> ContentOpenFrom.QUESTION_FEED
    readingQueueSourceId()?.startsWith("collection:") == true -> ContentOpenFrom.COLLECTION
    readingQueueSourceId()?.startsWith("history:") == true -> ContentOpenFrom.HISTORY
    readingQueueSourceId()?.startsWith("home:") == true -> ContentOpenFrom.HOME_FEED
    else -> null
}

private fun adaptiveOpenFrom(
    source: NavDestination?,
    target: NavDestination,
): String? = ContentOpenEventSupport
    .inferOpenFrom(source, target)
    .takeUnless { it == ContentOpenFrom.UNKNOWN }
    ?: target.readingQueueOpenFrom()

private fun AdaptivePaneDestination.toNavDestination(): NavDestination? = when (type) {
    AdaptivePaneDestination.Type.Answer -> Article(
        type = ArticleType.Answer,
        id = id.toLongOrNull() ?: return null,
        title = title,
        readingQueueSourceId = readingQueueSourceId,
    )
    AdaptivePaneDestination.Type.Article -> Article(
        type = ArticleType.Article,
        id = id.toLongOrNull() ?: return null,
        title = title,
        readingQueueSourceId = readingQueueSourceId,
    )
    AdaptivePaneDestination.Type.Question -> Question(
        questionId = id.toLongOrNull() ?: return null,
        title = title,
        readingQueueSourceId = readingQueueSourceId,
    )
    AdaptivePaneDestination.Type.Pin -> Pin(
        id = id.toLongOrNull() ?: return null,
        authorName = title,
        readingQueueSourceId = readingQueueSourceId,
    )
    AdaptivePaneDestination.Type.Person -> Person(
        id = id,
        urlToken = urlToken,
        name = title,
        jumpTo = jumpTo,
    )
    AdaptivePaneDestination.Type.Appearance -> Account.AppearanceSettings(setting)
    AdaptivePaneDestination.Type.Recommend -> Account.RecommendSettings(setting)
    AdaptivePaneDestination.Type.SystemAndUpdate -> Account.SystemAndUpdateSettings(setting)
    AdaptivePaneDestination.Type.Developer -> Account.DeveloperSettings
    AdaptivePaneDestination.Type.DeveloperColorScheme -> Account.DeveloperSettings.ColorScheme
    AdaptivePaneDestination.Type.RecommendBlocklist -> Account.RecommendSettings.Blocklist
    AdaptivePaneDestination.Type.RecommendBlockedHistory -> Account.RecommendSettings.BlockedFeedHistory
    AdaptivePaneDestination.Type.IdentityManagement -> Account.IdentityManagement
    AdaptivePaneDestination.Type.ReadingSettings -> Account.ReadingSettings
    AdaptivePaneDestination.Type.SettingsSearch -> Account.SettingsSearch
    AdaptivePaneDestination.Type.OpenSourceLicenses -> Account.OpenSourceLicenses
}

@Composable
private fun AdaptiveEmptyPane(
    text: String,
    icon: ImageVector = Icons.AutoMirrored.Outlined.Article,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(bottom = 8.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(text = text, color = Color.Gray)
    }
}

/**
 * Android implementation of the list/detail host. It intercepts destinations
 * that can be rendered in a pane and delegates all other routes to the normal
 * application navigator.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AndroidAdaptiveContentHost(
    listContent: @Composable () -> Unit,
    detailContent: @Composable (NavDestination) -> Unit,
    onSinglePaneDetailChanged: (Boolean) -> Unit,
    onDetailDestinationChanged: (NavDestination?) -> Unit,
    onDestinationOpened: (NavDestination, String?) -> Unit,
    listContentKey: Any?,
    navigationRequest: AdaptiveNavigationRequest?,
    onNavigationRequestConsumed: (Long) -> Unit,
    onHostActiveChanged: (Boolean) -> Unit,
) {
    val rootNavigator = LocalNavigator.current
    val videoOpener = LocalAdaptiveVideoOpener.current
    val hostKey = listContentKey as? AdaptiveHostKey
    val hostRouteId = hostKey?.routeId
    val actualListContentKey = hostKey?.contentKey ?: listContentKey
    DisposableEffect(Unit) {
        onHostActiveChanged(true)
        onDispose { onHostActiveChanged(false) }
    }
    val settings = rememberSettingsStore()
    var listPaneDefaultWidthDp by remember(settings) {
        mutableIntStateOf(
            settings
                .getInt(LIST_PANE_DEFAULT_WIDTH_DP_PREFERENCE_KEY, 320)
                .coerceIn(280, 440),
        )
    }
    DisposableEffect(settings) {
        val subscription = settings.observeKeyChanges { changedKey ->
            if (changedKey == LIST_PANE_DEFAULT_WIDTH_DP_PREFERENCE_KEY) {
                listPaneDefaultWidthDp = settings
                    .getInt(
                        LIST_PANE_DEFAULT_WIDTH_DP_PREFERENCE_KEY,
                        320,
                    ).coerceIn(280, 440)
            }
        }
        onDispose(subscription::close)
    }
    val windowDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).copy(
        horizontalPartitionSpacerSize = 0.dp,
        defaultPanePreferredWidth = listPaneDefaultWidthDp.dp,
        shouldAutoFocusCurrentDestination = false,
    )
    // This is the sole detail back stack. Its entries own both saveable UI state
    // and ViewModels; the adaptive scaffold only controls where they are rendered.
    var entries by rememberSaveable(
        stateSaver = listSaver<List<AdaptivePaneEntry>, AdaptivePaneEntry>(
            save = { it },
            restore = { it },
        ),
    ) { mutableStateOf(emptyList()) }
    val savedStateHolder = rememberSaveableStateHolder()
    val storeProvider = rememberViewModelStoreProvider()
    val selectedEntry = entries.lastOrNull()
    val selectedPaneDestination = selectedEntry?.destination
    val selectedDestination = selectedPaneDestination?.toNavDestination()
    val directive = if (selectedEntry?.immersiveMode?.value == true && selectedDestination is Article) {
        windowDirective.copy(
            maxHorizontalPartitions = 1,
            maxVerticalPartitions = 1,
            excludedBounds = emptyList(),
        )
    } else {
        windowDirective
    }
    val isSinglePane = directive.maxHorizontalPartitions == 1

    fun updateEntries(next: List<AdaptivePaneEntry>) {
        val retainedKeys = next.mapTo(mutableSetOf()) { it.key }
        entries.filter { it.key !in retainedKeys }.forEach {
            savedStateHolder.removeState(it.key)
            // The provider waits for outgoing AnimatedContent to release its scope.
            storeProvider.clearKey(it.key)
        }
        entries = next
    }

    fun scaffoldValue(hasDetail: Boolean) = calculateThreePaneScaffoldValue(
        maxHorizontalPartitions = directive.maxHorizontalPartitions,
        maxVerticalPartitions = directive.maxVerticalPartitions,
        adaptStrategies = ListDetailPaneScaffoldDefaults.adaptStrategies(),
        currentDestination = ThreePaneScaffoldDestinationItem<Nothing>(
            if (hasDetail) ListDetailPaneScaffoldRole.Detail else ListDetailPaneScaffoldRole.List,
        ),
    )
    val targetScaffoldValue = scaffoldValue(selectedEntry != null)
    val scaffoldState = remember { MutableThreePaneScaffoldState(targetScaffoldValue) }
    LaunchedEffect(targetScaffoldValue) {
        scaffoldState.animateTo(targetScaffoldValue)
    }
    PredictiveBackHandler(enabled = entries.isNotEmpty()) { progress ->
        try {
            progress.collect { event ->
                scaffoldState.seekTo(
                    fraction = event.progress,
                    targetState = scaffoldValue(entries.size > 1),
                    isPredictiveBackInProgress = true,
                )
            }
            updateEntries(entries.dropLast(1))
            scaffoldState.animateTo(scaffoldValue(entries.isNotEmpty()))
        } catch (_: CancellationException) {
            scaffoldState.animateTo(targetScaffoldValue)
        }
    }

    SideEffect {
        onSinglePaneDetailChanged(isSinglePane && selectedPaneDestination != null)
        onDetailDestinationChanged(selectedDestination)
    }

    fun openDestination(destination: NavDestination, fromList: Boolean) {
        val paneDestination = destination.toAdaptivePaneDestination() ?: return
        val currentDestination = entries.lastOrNull()?.destination
        if (currentDestination == paneDestination) return
        val retained = when {
            fromList -> emptyList()
            paneDestination.type == AdaptivePaneDestination.Type.Answer ->
                entries.dropLastWhile { it.destination.type == AdaptivePaneDestination.Type.Answer }
            else -> entries
        }
        onDestinationOpened(
            destination,
            if (fromList) {
                destination.readingQueueOpenFrom()
            } else {
                adaptiveOpenFrom(currentDestination?.toNavDestination(), destination)
            },
        )
        updateEntries(retained + AdaptivePaneEntry(paneDestination))
    }

    LaunchedEffect(navigationRequest?.id, hostRouteId) {
        val request = navigationRequest ?: return@LaunchedEffect
        if (request.ownerRouteId != hostRouteId || !request.claim()) return@LaunchedEffect
        openDestination(request.destination, fromList = false)
        onNavigationRequestConsumed(request.id)
    }

    var previousListContentKey by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(actualListContentKey) {
        val currentListContentKey = actualListContentKey?.toString()
        if (entries.isNotEmpty() && previousListContentKey != currentListContentKey) {
            updateEntries(emptyList())
        }
        previousListContentKey = currentListContentKey
    }

    fun localNavigator(fromList: Boolean): Navigator = Navigator(
        onNavigate = { destination ->
            when {
                destination is Video -> videoOpener?.invoke(destination, entries.lastOrNull()?.destination?.toNavDestination())
                    ?: rootNavigator.onNavigate(destination)
                destination.toAdaptivePaneDestination() == null -> rootNavigator.onNavigate(destination)
                else -> openDestination(destination, fromList)
            }
        },
        onNavigateBack = {
            if (entries.isNotEmpty()) {
                updateEntries(entries.dropLast(1))
            } else {
                rootNavigator.onNavigateBack()
            }
        },
        onNavigateTopLevel = rootNavigator.onNavigateTopLevel,
    )

    val listNavigator = localNavigator(fromList = true)
    val detailNavigator = localNavigator(fromList = false)

    ListDetailPaneScaffold(
        directive = directive,
        scaffoldState = scaffoldState,
        listPane = {
            AnimatedPane {
                CompositionLocalProvider(
                    LocalNavigator provides listNavigator,
                    LocalAdaptiveSelection provides selectedDestination,
                ) {
                    listContent()
                }
            }
        },
        detailPane = {
            AnimatedPane {
                CompositionLocalProvider(
                    LocalNavigator provides detailNavigator,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = LocalAdaptiveDetailBottomPadding.current),
                    ) {
                        if (selectedEntry == null) {
                            AdaptiveEmptyPane("请选择内容")
                        } else {
                            AnimatedContent(
                                targetState = selectedEntry,
                                transitionSpec = {
                                    val initial = initialState.destination.toNavDestination()
                                    val target = targetState.destination.toNavDestination()
                                    if (initial is Article || target is Article) {
                                        articleAnswerContentTransform(
                                            sharedArticleAnswerSwitchState.answerTransitionDirection,
                                        )
                                    } else {
                                        EnterTransition.None togetherWith ExitTransition.None
                                    }
                                },
                                label = "adaptive-detail-content",
                            ) { entry ->
                                val destination = entry.destination.toNavDestination()
                                if (destination == null) {
                                    AdaptiveEmptyPane("暂不支持在详情窗格中打开该内容")
                                } else {
                                    key(entry.key) {
                                        val owner = rememberViewModelStoreOwner(key = entry.key, provider = storeProvider)
                                        val lifecycleOwner = rememberLifecycleOwner(
                                            maxLifecycle = if (entry == selectedEntry) Lifecycle.State.RESUMED else Lifecycle.State.STARTED,
                                        )
                                        savedStateHolder.SaveableStateProvider(entry.key) {
                                            CompositionLocalProvider(
                                                LocalViewModelStoreOwner provides owner,
                                                LocalLifecycleOwner provides lifecycleOwner,
                                                LocalArticleNavController provides null,
                                                LocalAdaptiveDetailImmersiveMode provides entry.immersiveMode,
                                            ) {
                                                detailContent(destination)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

// Kept local to this Android source set because the adaptive API is not part
// of the common Compose Multiplatform dependency graph yet.
private fun PaneScaffoldDirective.copy(
    maxHorizontalPartitions: Int = this.maxHorizontalPartitions,
    horizontalPartitionSpacerSize: Dp = this.horizontalPartitionSpacerSize,
    maxVerticalPartitions: Int = this.maxVerticalPartitions,
    verticalPartitionSpacerSize: Dp = this.verticalPartitionSpacerSize,
    defaultPanePreferredWidth: Dp = this.defaultPanePreferredWidth,
    excludedBounds: List<Rect> = this.excludedBounds,
    defaultPanePreferredHeight: Dp = this.defaultPanePreferredHeight,
    shouldAutoFocusCurrentDestination: Boolean = this.shouldAutoFocusCurrentDestination,
): PaneScaffoldDirective = PaneScaffoldDirective(
    maxHorizontalPartitions = maxHorizontalPartitions,
    horizontalPartitionSpacerSize = horizontalPartitionSpacerSize,
    maxVerticalPartitions = maxVerticalPartitions,
    verticalPartitionSpacerSize = verticalPartitionSpacerSize,
    defaultPanePreferredWidth = defaultPanePreferredWidth,
    defaultPanePreferredHeight = defaultPanePreferredHeight,
    excludedBounds = excludedBounds,
    shouldAutoFocusCurrentDestination = shouldAutoFocusCurrentDestination,
)

private fun articleAnswerContentTransform(
    direction: ArticleAnswerTransitionDirection,
) = when (direction) {
    ArticleAnswerTransitionDirection.VERTICAL_NEXT ->
        slideInVertically(tween(300)) { it }
            .plus(fadeIn(tween(300))) togetherWith
            (
                slideOutVertically(tween(300)) { -it }
                    .plus(fadeOut(tween(300)))
            )

    ArticleAnswerTransitionDirection.VERTICAL_PREVIOUS ->
        slideInVertically(tween(300)) { -it }
            .plus(fadeIn(tween(300))) togetherWith
            (
                slideOutVertically(tween(300)) { it }
                    .plus(fadeOut(tween(300)))
            )

    ArticleAnswerTransitionDirection.HORIZONTAL_NEXT ->
        slideInHorizontally(tween(300)) { it }
            .plus(fadeIn(tween(300))) togetherWith
            (
                slideOutHorizontally(tween(300)) { -it }
                    .plus(fadeOut(tween(300)))
            )

    ArticleAnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
        slideInHorizontally(tween(300)) { -it }
            .plus(fadeIn(tween(300))) togetherWith
            (
                slideOutHorizontally(tween(300)) { it }
                    .plus(fadeOut(tween(300)))
            )

    ArticleAnswerTransitionDirection.DEFAULT ->
        EnterTransition.None togetherWith ExitTransition.None
}
