/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.zly2006.zhihu.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.rememberNavController
import com.github.zly2006.zhihu.navigation.EmptyDetail
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.github.zly2006.zhihu.account.LoginScreen
import com.github.zly2006.zhihu.filter.ContentOpenFrom
import com.github.zly2006.zhihu.navigation.Account
import com.github.zly2006.zhihu.navigation.Article
import com.github.zly2006.zhihu.navigation.ArticleType
import com.github.zly2006.zhihu.navigation.ArticleTypeNavType
import com.github.zly2006.zhihu.navigation.CollectionContent
import com.github.zly2006.zhihu.navigation.Collections
import com.github.zly2006.zhihu.navigation.Daily
import com.github.zly2006.zhihu.navigation.Follow
import com.github.zly2006.zhihu.navigation.History
import com.github.zly2006.zhihu.navigation.Home
import com.github.zly2006.zhihu.navigation.HotList
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.Login
import com.github.zly2006.zhihu.navigation.MainTabs
import com.github.zly2006.zhihu.navigation.MyCollections
import com.github.zly2006.zhihu.navigation.NavDestination
import com.github.zly2006.zhihu.navigation.Navigator
import com.github.zly2006.zhihu.navigation.Notification
import com.github.zly2006.zhihu.navigation.OnlineHistory
import com.github.zly2006.zhihu.navigation.Person
import com.github.zly2006.zhihu.navigation.Pin
import com.github.zly2006.zhihu.navigation.Question
import com.github.zly2006.zhihu.navigation.Search
import com.github.zly2006.zhihu.navigation.SentenceSimilarityTest
import com.github.zly2006.zhihu.navigation.TopLevelDestination
import com.github.zly2006.zhihu.navigation.Topic
import com.github.zly2006.zhihu.navigation.Video
import com.github.zly2006.zhihu.navigation.WriteAnswer
import com.github.zly2006.zhihu.navigation.WritePin
import com.github.zly2006.zhihu.navigation.loginNavigationRequestFlow
import com.github.zly2006.zhihu.platform.PlatformBackHandler
import com.github.zly2006.zhihu.platform.platformName
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.reading.rememberReadingPlayerController
import com.github.zly2006.zhihu.reading.saveReadingPlaybackSpeed
import com.github.zly2006.zhihu.ui.components.CompactReadingPlayerButton
import com.github.zly2006.zhihu.ui.components.NoOpPagerNestedScrollConnection
import com.github.zly2006.zhihu.ui.components.ReadingPlayerBar
import com.github.zly2006.zhihu.ui.components.ReadingQueueSheet
import com.github.zly2006.zhihu.ui.subscreens.AppearanceSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.BlockedFeedHistoryScreen
import com.github.zly2006.zhihu.ui.subscreens.ColorSchemeScreen
import com.github.zly2006.zhihu.ui.subscreens.ContentFilterSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.DeveloperSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.IdentityManagementScreen
import com.github.zly2006.zhihu.ui.subscreens.OpenSourceLicensesScreen
import com.github.zly2006.zhihu.ui.subscreens.ReadingSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.SettingsSearchScreen
import com.github.zly2006.zhihu.ui.subscreens.SystemAndUpdateSettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

private sealed class MainTabPage(
    val bottomDestination: TopLevelDestination,
    val key: String,
) {
    data object HomePage : MainTabPage(Home, "home")

    data object FollowPage : MainTabPage(Follow, "follow")

    data object HotListPage : MainTabPage(HotList, "hotlist")

    data object DailyPage : MainTabPage(Daily, "daily")

    data object OnlineHistoryPage : MainTabPage(OnlineHistory, "online_history")

    data object MyCollectionsPage : MainTabPage(MyCollections, "my_collections")

    data object AccountPage : MainTabPage(Account, "account")
}

internal val LocalReadingPlayerOverlayPadding = staticCompositionLocalOf { 0.dp }

/**
 * The root article navigation controller, when a screen is rendered in the
 * regular NavHost. Adaptive detail panes deliberately override this with null
 * so article answer switching is handled by the pane navigator instead.
 */
val LocalArticleNavController = staticCompositionLocalOf<NavHostController?> { null }

/**
 * Zhihu++ 的共享应用主壳。
 *
 * 这个 composable 是顶层体验的唯一所有者：渲染可配置底部导航栏，承载横向主 tab pager，向子页面提供 [LocalNavigator]，
 * 并注册跨平台共享的 typed [NavDestination] route。设计上把顶层 tab 收在 [MainTabs] 内部，而不是把每个 tab
 * 都作为独立 NavHost 页面 push，这样 tab 重选、回到顶部、顶/底栏自动隐藏和持久化 tab 选择都能使用同一套状态模型。
 *
 * 用户可见的主壳设置通过 [preferenceState] 流入。设置页退出时只 reload 这份状态，不重建 NavHost，从而在应用底栏和主题相关变更时
 * 保留已加载页面、返回栈和滚动位置。
 */
@OptIn(ExperimentalFoundationApi::class)
@Suppress("RestrictedApi")
@Composable
fun ZhihuMain(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainTabNavigationTarget: TopLevelDestination?,
    navigate: (NavDestination) -> Unit,
    navigateContent: (NavDestination, NavHostController) -> Unit = { destination, _ -> navigate(destination) },
    enableLandscapeListDetail: Boolean = false,
    isListDetailCapable: Boolean = true,
    setCurrentMainTabOpenFrom: (String?) -> Unit,
    consumeMainTabNavigationTarget: (TopLevelDestination) -> Unit,
    preferenceState: ZhihuMainPreferenceState,
    isDarkTheme: Boolean,
    articleContent: @Composable (Article, NavBackStackEntry) -> Unit,
    mainNavigationScaffold: MainNavigationScaffold = ::DefaultMainNavigationScaffold,
    adaptiveContentHost: AdaptiveContentHost = { listContent, _, _, _, _, _, _, _, _ -> listContent() },
    articlePaneContent: @Composable (Article) -> Unit = {
        error("$platformName 暂不支持在自适应详情窗格中打开文章")
    },
    onAdaptiveDestinationOpened: (NavDestination, String?) -> Unit = { _, _ -> },
    onAdaptiveVideoOpened: (Video, NavDestination?) -> Unit = { video, _ -> navigate(video) },
    showMainNavigationBar: Boolean = true,
    showHomeTopActions: Boolean = true,
    onCurrentMainTabDestinationChange: (TopLevelDestination) -> Unit = {},
    sentenceSimilarityContent: @Composable () -> Unit = {
        error("$platformName 暂不支持句子相似度测试")
    },
    blocklistSettingsNlpContent: @Composable (onNavigateBack: () -> Unit) -> Unit = {
        error("$platformName 暂不支持 NLP 智能屏蔽设置")
    },
    articleEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    articleExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
) = BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val containerWidth = maxWidth
    val isLargeLandscape = canShowLandscapeListDetail(
        enabled = enableLandscapeListDetail,
        deviceSupportsListDetail = isListDetailCapable,
        preferenceEnabled = preferenceState.landscapeListDetailEnabled,
        width = containerWidth,
        height = maxHeight,
    )
    val bottomPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues().calculateBottomPadding()
    val duo3HomeAccount = preferenceState.duo3HomeAccount
    val tapToScrollToTopEnabled = preferenceState.tapToScrollToTopEnabled
    val autoHideBottomBar = preferenceState.autoHideBottomBar
    val collectionDirectBrowseEnabled = preferenceState.collectionDirectBrowseEnabled
    val selectedBottomBarItemKeys = preferenceState.selectedBottomBarItemKeys
    val startDestination = preferenceState.startDestination
    val reloadBottomBarPreferences = preferenceState::reload
    val readingPlayer = rememberReadingPlayerController()
    val readingPlayerState by readingPlayer.state
    val settings = rememberSettingsStore()
    /** 右侧详情独立持有返回栈；栏内导航保留上级，从左侧重新选项时清空。 */
    val paneStateHolder = rememberSaveableStateHolder()
    val detailNavController = rememberNavController()
    val detailEntry by detailNavController.currentBackStackEntryAsState()
    val selectedContentDestination = detailEntry.readingDestinationOrNull()
    val hasOpenSecondaryDetail = detailEntry?.destination?.hasRoute<EmptyDetail>() == false
    var showReadingQueue by remember { mutableStateOf(false) }
    var isReadingPlayerExpandedByUser by remember { mutableStateOf(false) }
    var readingPlayerHeightPx by remember { mutableIntStateOf(0) }
    val readingPlayerOverlayOffsetState = remember { ReadingPlayerOverlayOffsetState() }
    val density = LocalDensity.current
    val currentOnMainTabDestinationChange by rememberUpdatedState(onCurrentMainTabDestinationChange)

    val navEntry by navController.currentBackStackEntryAsState()
    val showMainNavigation = navEntry?.destination?.hasRoute<MainTabs>() == true
    val isListPaneContext = navEntry.isListPaneDestination()
    // 窄屏从详情进入主栈页面时隐藏详情；返回原列表项后恢复同一详情和页面状态。
    var detailOwnerEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    val showListDetail = isLargeLandscape && isListPaneContext
    val showDetailPane = hasOpenSecondaryDetail &&
        (showListDetail || navEntry?.id == detailOwnerEntryId)
    val hasOpenDetail = showDetailPane && selectedContentDestination != null
    val useSecondaryContentNavigation = enableLandscapeListDetail && isListPaneContext
    val detailLifecycleOwner = rememberLifecycleOwner(
        maxLifecycle = if (showListDetail || showDetailPane) Lifecycle.State.RESUMED else Lifecycle.State.CREATED,
    )
    val listLifecycleOwner = rememberLifecycleOwner(
        maxLifecycle = if (showDetailPane && !showListDetail) Lifecycle.State.CREATED else Lifecycle.State.RESUMED,
    )

    LaunchedEffect(isLargeLandscape, navEntry) {
        if (showListDetail) detailOwnerEntryId = navEntry?.id
    }

    fun openListDetail(destination: NavDestination) {
        detailOwnerEntryId = navController.currentBackStackEntry?.id
        detailNavController.popBackStack(detailNavController.graph.startDestinationId, inclusive = false)
        navigateContent(destination, detailNavController)
    }

    fun openReadingDestination(destination: NavDestination, onlyIfReading: Boolean = false) {
        val current = if (hasOpenDetail) selectedContentDestination else navEntry.readingDestinationOrNull()
        if (current == destination || (onlyIfReading && current == null)) return
        val opensInDetail = useSecondaryContentNavigation && destination.isDetailPaneDestination()
        if (hasOpenDetail) {
            if (opensInDetail) {
                detailNavController.popBackStack()
            } else {
                detailNavController.popBackStack(detailNavController.graph.startDestinationId, inclusive = false)
            }
        } else if (current != null && !opensInDetail) {
            navController.popBackStack()
        }
        if (opensInDetail) {
            detailOwnerEntryId = navController.currentBackStackEntry?.id
            navigateContent(destination, detailNavController)
        } else {
            navigate(destination)
        }
    }
    // This flag is derived from the active adaptive host. It must not be
    // restored independently across configuration changes, otherwise a
    // restored pane can leave the bottom bar in the wrong visibility state.
    var isSinglePaneListDetailShowingDetail by remember { mutableStateOf(false) }
    var adaptiveDetailDestination by remember { mutableStateOf<NavDestination?>(null) }
    var adaptiveNavigationRequest by remember { mutableStateOf<AdaptiveNavigationRequest?>(null) }
    var adaptiveHostOwnerRouteId by remember { mutableStateOf<String?>(null) }
    var adaptiveNavigationRequestId by remember { mutableLongStateOf(0L) }

    fun requestAdaptiveNavigation(destination: NavDestination) {
        val ownerRouteId = navEntry?.id ?: return
        adaptiveNavigationRequest = AdaptiveNavigationRequest(
            id = ++adaptiveNavigationRequestId,
            destination = destination,
            ownerRouteId = ownerRouteId,
        )
    }

    // Navigation persists the back-stack entry id across configuration
    // recreation, while assigning a new id when the typed route arguments
    // change. Use it to reset pane state at real route boundaries without
    // wiping a selected pane during rotation.
    LaunchedEffect(navEntry?.id) {
        isSinglePaneListDetailShowingDetail = false
        adaptiveDetailDestination = null
        if (adaptiveHostOwnerRouteId != navEntry?.id) {
            adaptiveHostOwnerRouteId = null
        }
        adaptiveNavigationRequest = null
    }
    PlatformBackHandler(
        enabled = navEntry != null && !showMainNavigation && !showDetailPane && adaptiveDetailDestination == null,
    ) {
        navController.popBackStack()
    }
    val isOnReadingDetail = hasOpenDetail || adaptiveDetailDestination is Article ||
        adaptiveDetailDestination is Question ||
        adaptiveDetailDestination is Pin ||
        navEntry?.destination?.hasRoute<Article>() == true ||
        navEntry?.destination?.hasRoute<Question>() == true ||
        navEntry?.destination?.hasRoute<Pin>() == true
    val rootReadingDestination = when {
        navEntry?.destination?.hasRoute<Article>() == true -> runCatching {
            navEntry?.toRoute<Article>()
        }.getOrNull()
        navEntry?.destination?.hasRoute<Pin>() == true -> runCatching {
            navEntry?.toRoute<Pin>()
        }.getOrNull()
        navEntry?.destination?.hasRoute<Question>() == true -> runCatching {
            navEntry?.toRoute<Question>()
        }.getOrNull()
        else -> null
    }
    val activeReadingDestination = adaptiveDetailDestination ?: selectedContentDestination?.takeIf { hasOpenDetail } ?: rootReadingDestination
    val isReadingPlayerExpanded = readingPlayerState.hasSession &&
        (isOnReadingDetail || isReadingPlayerExpandedByUser)
    val shouldCompactPlayerOnBackgroundInteraction by rememberUpdatedState(
        isReadingPlayerExpandedByUser && !isOnReadingDetail,
    )
    val readingPlayerOverlayPadding = when {
        !readingPlayerState.hasSession -> 0.dp
        !isReadingPlayerExpanded -> 0.dp
        readingPlayerHeightPx > 0 -> with(density) { readingPlayerHeightPx.toDp() } + 16.dp
        else -> 16.dp
    }

    LaunchedEffect(readingPlayerState.hasSession) {
        if (!readingPlayerState.hasSession) {
            showReadingQueue = false
            isReadingPlayerExpandedByUser = false
            readingPlayerOverlayOffsetState.resetOffset()
        }
    }
    var previousReadingItemKey by remember { mutableStateOf(readingPlayerState.currentItem?.key) }
    LaunchedEffect(readingPlayerState.currentItem?.key) {
        val currentItem = readingPlayerState.currentItem
        val currentItemKey = currentItem?.key
        val itemChanged = previousReadingItemKey != null && previousReadingItemKey != currentItemKey
        previousReadingItemKey = currentItemKey
        if (itemChanged && currentItem != null) {
            val currentDestination = activeReadingDestination
            val destination = currentItem.toDestination(readingPlayerState.sourceId)
            if (currentDestination != null && currentDestination != destination) {
                if (enableLandscapeListDetail) {
                    openReadingDestination(destination)
                } else if (adaptiveHostOwnerRouteId == navEntry?.id) {
                    requestAdaptiveNavigation(destination)
                } else {
                    if (rootReadingDestination != null) {
                        navController.popBackStack()
                    }
                    navigate(destination)
                }
            }
        }
    }

    // 离开文章页时恢复系统状态栏（只在实际切换时触发）
    val isOnArticle = activeReadingDestination is Article
    LaunchedEffect(navEntry, isOnArticle, activeReadingDestination) {
        isReadingPlayerExpandedByUser = false
        if (!isOnArticle) readingPlayerOverlayOffsetState.clearRoute()
    }
    var wasOnArticle by remember { mutableStateOf(false) }
    if (!isOnArticle && wasOnArticle) {
        LeaveImmersiveModeCleanup()
    }
    SideEffect {
        wasOnArticle = isOnArticle
    }

    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    // 滚动时自动隐藏底部导航栏
    var isBottomBarVisible by remember { mutableStateOf(true) }
    LaunchedEffect(navEntry?.id) {
        isBottomBarVisible = true
    }
    val bottomBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                when {
                    available.y < -3f -> isBottomBarVisible = false
                    available.y > 3f -> isBottomBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val allBottomBarItems = listOf(
        Triple(Home, "主页", Icons.Filled.Home),
        Triple(Follow, "关注", Icons.Filled.Group),
        Triple(HotList, "热榜", Icons.Filled.Whatshot),
        Triple(Daily, "日报", Icons.Filled.Newspaper),
        Triple(OnlineHistory, "历史", Icons.Filled.History),
        Triple(MyCollections, "收藏夹", Icons.Filled.Bookmarks),
        Triple(Account, "账号", Icons.Filled.ManageAccounts),
    )
    val bottomBarItems = selectedBottomBarItemKeys.mapNotNull { key ->
        allBottomBarItems.firstOrNull { it.first.name == key }
    }

    val mainTabPages = remember(bottomBarItems) {
        bottomBarItems.flatMap { item ->
            when (item.first) {
                Home -> listOf(MainTabPage.HomePage)
                Follow -> listOf(MainTabPage.FollowPage)
                HotList -> listOf(MainTabPage.HotListPage)
                Daily -> listOf(MainTabPage.DailyPage)
                OnlineHistory -> listOf(MainTabPage.OnlineHistoryPage)
                MyCollections -> listOf(MainTabPage.MyCollectionsPage)
                Account -> listOf(MainTabPage.AccountPage)
                else -> emptyList()
            }
        }
    }

    fun pageIndexForDestination(destination: TopLevelDestination): Int = mainTabPages
        .indexOfFirst {
            it.bottomDestination::class == destination::class
        }.takeIf { it >= 0 } ?: mainTabPages
        .indexOfFirst {
            it.bottomDestination::class == startDestination::class
        }.takeIf { it >= 0 } ?: 0

    val mainPagerState = rememberPagerState(
        initialPage = pageIndexForDestination(startDestination),
        pageCount = { mainTabPages.size },
    )
    val coroutineScope = rememberCoroutineScope()

    var currentMainTabDestination by remember { mutableStateOf(startDestination) }

    PlatformBackHandler(enabled = showDetailPane) {
        detailNavController.popBackStack()
    }

    fun navigateTopLevel(destination: TopLevelDestination) {
        if (destination == Account && hasOpenDetail) {
            detailNavController.popBackStack(
                detailNavController.graph.startDestinationId,
                inclusive = false,
            )
        }
        val targetPage = pageIndexForDestination(destination)
        coroutineScope.launch {
            mainPagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(navController) {
        loginNavigationRequestFlow.collect {
            navController.navigate(Login) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(mainPagerState.currentPage, mainTabPages) {
        mainTabPages.getOrNull(mainPagerState.currentPage)?.bottomDestination?.let { destination ->
            currentMainTabDestination = destination
            setCurrentMainTabOpenFrom(destination.openFrom)
            currentOnMainTabDestinationChange(destination)
        }
    }

    LaunchedEffect(currentMainTabDestination, navEntry) {
        if (currentMainTabDestination == Account && hasOpenDetail) {
            // A reading detail belongs to the feed that opened it. Do not carry it into
            // the account split pane when the user switches tabs.
            detailNavController.popBackStack(
                detailNavController.graph.startDestinationId,
                inclusive = false,
            )
        }
    }

    PlatformBackHandler(
        enabled = showMainNavigation &&
            !showDetailPane && !isSinglePaneListDetailShowingDetail &&
            adaptiveDetailDestination == null &&
            mainPagerState.currentPage != 0,
    ) {
        coroutineScope.launch {
            mainPagerState.animateScrollToPage(0)
        }
    }

    LaunchedEffect(mainTabNavigationTarget, mainTabPages) {
        mainTabNavigationTarget?.let { destination ->
            // 平台适配层会把旧的顶层 route 请求映射到 MainTabs。这里消费该请求，
            // 让 deeplink 等调用方仍能选中 Home/Follow 等 tab，而不是把旧 route 压入返回栈。
            mainPagerState.scrollToPage(pageIndexForDestination(destination))
            consumeMainTabNavigationTarget(destination)
        }
    }

    LaunchedEffect(mainTabPages) {
        if (mainTabPages.isNotEmpty()) {
            val currentDestinationStillVisible = mainTabPages.any {
                it.bottomDestination::class == currentMainTabDestination::class
            }
            val targetDestination = if (currentDestinationStillVisible) {
                currentMainTabDestination
            } else {
                startDestination
            }
            val targetPage = pageIndexForDestination(targetDestination)
            if (mainPagerState.currentPage != targetPage || mainPagerState.currentPage !in mainTabPages.indices) {
                mainPagerState.scrollToPage(targetPage)
            }
        }
    }

    @Composable
    fun AdaptiveDetailContent(destination: NavDestination) {
        when (destination) {
            is Article -> articlePaneContent(destination)
            is Question -> QuestionScreen(destination)
            is Pin -> PinScreen(destination)
            is Person -> PeopleScreen(destination)
            is Account.AppearanceSettings -> AppearanceSettingsScreen(
                setting = destination.setting,
                onExit = reloadBottomBarPreferences,
            )
            is Account.RecommendSettings -> ContentFilterSettingsScreen(destination.setting)
            Account.RecommendSettings.Blocklist -> BlocklistSettingsScreen(blocklistSettingsNlpContent)
            Account.RecommendSettings.BlockedFeedHistory -> BlockedFeedHistoryScreen()
            is Account.SystemAndUpdateSettings -> SystemAndUpdateSettingsScreen(destination.setting)
            Account.DeveloperSettings -> DeveloperSettingsScreen()
            Account.DeveloperSettings.ColorScheme -> ColorSchemeScreen()
            Account.IdentityManagement -> IdentityManagementScreen()
            Account.ReadingSettings -> ReadingSettingsScreen()
            Account.SettingsSearch -> SettingsSearchScreen()
            Account.OpenSourceLicenses -> OpenSourceLicensesScreen()
            else -> {
                // The adaptive host only claims content routes that it can render in a pane.
                // Unsupported destinations are sent to the regular navigation stack by the host.
            }
        }
    }

    @Composable
    fun AdaptiveRouteContent(
        hostRouteId: String,
        listContentKey: Any? = null,
        content: @Composable () -> Unit,
    ) {
        val currentEntryId by rememberUpdatedState(navEntry?.id)
        val hostKey = AdaptiveHostKey(hostRouteId, listContentKey)
        adaptiveContentHost(
            content,
            ::AdaptiveDetailContent,
            { showingDetail ->
                if (hostRouteId == currentEntryId) {
                    isSinglePaneListDetailShowingDetail = showingDetail
                }
            },
            { destination ->
                if (hostRouteId == currentEntryId) {
                    adaptiveDetailDestination = destination
                }
            },
            { destination, openFrom ->
                if (hostRouteId == currentEntryId) {
                    onAdaptiveDestinationOpened(destination, openFrom)
                }
            },
            hostKey,
            adaptiveNavigationRequest?.takeIf {
                hostRouteId == currentEntryId && it.ownerRouteId == hostRouteId
            },
            { requestId ->
                if (hostRouteId == currentEntryId && adaptiveNavigationRequest?.id == requestId) {
                    adaptiveNavigationRequest = null
                }
            },
            { active ->
                if (hostRouteId == currentEntryId) {
                    adaptiveHostOwnerRouteId = if (active) hostRouteId else null
                }
            },
        )
    }

    val currentBottomDestination = mainTabPages
        .getOrNull(mainPagerState.targetPage)
        ?.bottomDestination
    val navigationItems = bottomBarItems.map { item ->
        MainNavigationItem(
            destination = item.first,
            label = item.second,
            icon = item.third,
        )
    }

    var listPaneRatio by remember {
        mutableFloatStateOf(
            settings.getFloat(LANDSCAPE_LIST_PANE_RATIO_KEY, DEFAULT_LIST_PANE_RATIO),
        )
    }

    @Composable
    fun DetailPane(modifier: Modifier) {
        CompositionLocalProvider(
            LocalLifecycleOwner provides detailLifecycleOwner,
            LocalArticleNavController provides detailNavController,
            LocalNavigator provides Navigator(
                onNavigate = { destination ->
                    if (
                        destination is Video || destination.isDetailPaneDestination() || destination.isAccountDetailDestination()
                    ) {
                        navigateContent(destination, detailNavController)
                    } else {
                        navigate(destination)
                    }
                },
                onNavigateBack = detailNavController::popBackStack,
                onNavigateTopLevel = ::navigateTopLevel,
            ),
            LocalSelectedContentDestination provides selectedContentDestination,
            LocalReadingPlayerOverlayPadding provides readingPlayerOverlayPadding,
            LocalReadingPlayerOverlayOffsetState provides readingPlayerOverlayOffsetState,
        ) {
            NavHost(
                navController = detailNavController,
                startDestination = EmptyDetail,
                modifier = modifier.testTag("detail_pane"),
                enterTransition = { slideInHorizontally(tween(300)) { it } },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) },
            ) {
                composable<EmptyDetail> {
                    EmptyDetailPane()
                }
                composable<Article>(
                    typeMap = mapOf(typeOf<ArticleType>() to ArticleTypeNavType),
                    enterTransition = articleEnterTransition,
                    exitTransition = articleExitTransition,
                ) { entry ->
                    articleContent(entry.toRoute(), entry)
                }
                composable<Pin> { entry ->
                    PinScreen(entry.toRoute())
                }
                accountSettings(reloadBottomBarPreferences, blocklistSettingsNlpContent)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        val listPaneWidth = if (showListDetail) {
            normalizedListPaneWidth(containerWidth, listPaneRatio)
        } else {
            containerWidth
        }
        val listPaneModifier = if (showListDetail) {
            Modifier.width(listPaneWidth).fillMaxHeight().align(Alignment.CenterStart)
        } else {
            Modifier.fillMaxSize()
        }
        LaunchedEffect(navEntry, isLargeLandscape, hasOpenSecondaryDetail, isListPaneContext) {
            val directReadingDestination = navEntry.readingDestinationOrNull()?.takeIf { it.isDetailPaneDestination() }
            if (enableLandscapeListDetail && directReadingDestination != null) {
                navController.popBackStack()
                if (navController.currentBackStackEntry == null) navController.navigate(MainTabs)
                detailOwnerEntryId = navController.currentBackStackEntry?.id
                detailNavController.popBackStack(detailNavController.graph.startDestinationId, inclusive = false)
                // 平台入口已经处理历史、评论交接等副作用，这里只转移页面归属。
                detailNavController.navigate(directReadingDestination)
            }
        }
        if (showListDetail || !showDetailPane) {
            paneStateHolder.SaveableStateProvider("list") {
        mainNavigationScaffold(
            listPaneModifier.testTag("list_pane").nestedScroll(bottomBarScrollConnection),
            isDarkTheme,
            showMainNavigationBar && navEntry != null,
            showMainNavigation,
            isSinglePaneListDetailShowingDetail,
            autoHideBottomBar,
            isBottomBarVisible,
            navigationItems,
            currentBottomDestination,
            { destination ->
                isReadingPlayerExpandedByUser = false
                if (currentBottomDestination?.let { it::class == destination::class } != true) {
                    navigateTopLevel(destination)
                } else if (tapToScrollToTopEnabled) {
                    scrollToTopTrigger++
                }
            },
            {
                AnimatedVisibility(
                    visible = isReadingPlayerExpanded && !enableLandscapeListDetail,
                    enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.92f),
                    exit = fadeOut(tween(160)) + scaleOut(tween(160), targetScale = 0.92f),
                ) {
                    ReadingPlayerBar(
                        state = readingPlayerState,
                        onPrevious = readingPlayer::playPrevious,
                        onTogglePlayPause = readingPlayer::togglePlayPause,
                        onNext = readingPlayer::playNext,
                        onStop = readingPlayer::stop,
                        onOpenQueue = { showReadingQueue = true },
                        onPlaybackSpeedChange = { speed ->
                            saveReadingPlaybackSpeed(settings, speed)
                            readingPlayer.setPlaybackSpeed(speed)
                        },
                        onBackgroundInteraction = {
                            if (!isOnReadingDetail) isReadingPlayerExpandedByUser = false
                        },
                        modifier = Modifier
                            .onSizeChanged { readingPlayerHeightPx = it.height }
                            .graphicsLayer {
                                translationY = readingPlayerOverlayOffsetState.verticalOffsetPx
                            },
                    )
                }
            },
            { innerPadding ->
                CompositionLocalProvider(
                    LocalLifecycleOwner provides listLifecycleOwner,
                    LocalSelectedContentDestination provides selectedContentDestination,
                    LocalArticleNavController provides navController,
                    LocalNavigator provides Navigator(
                        onNavigate = { destination ->
                            if (useSecondaryContentNavigation && (destination.isDetailPaneDestination() ||
                                (showListDetail && currentMainTabDestination == Account && destination.isAccountDetailDestination()))) {
                                openListDetail(destination)
                            } else {
                                navigate(destination)
                            }
                        },
                        onNavigateBack = navController::popBackStack,
                        onNavigateTopLevel = ::navigateTopLevel,
                    ),
                    LocalReadingPlayerOverlayPadding provides readingPlayerOverlayPadding,
                    LocalReadingPlayerOverlayOffsetState provides readingPlayerOverlayOffsetState,
                    LocalAdaptiveDetailBottomPadding provides (
                        innerPadding.calculateBottomPadding() - bottomPadding
                    ).coerceAtLeast(0.dp),
                    LocalAdaptiveVideoOpener provides { video, source ->
                        onAdaptiveVideoOpened(video, source)
                    },
                ) {
                    NavHost(
                        navController,
                        modifier = Modifier.pointerInput(Unit) {
                            while (true) {
                                awaitPointerEventScope {
                                    awaitFirstDown(
                                        requireUnconsumed = false,
                                        pass = PointerEventPass.Initial,
                                    )
                                    while (
                                        awaitPointerEvent(PointerEventPass.Final)
                                            .changes
                                            .any { it.pressed }
                                    ) {
                                        // 等手势完成后再重组，避免取消同一次背景点击或滚动。
                                    }
                                }
                                if (shouldCompactPlayerOnBackgroundInteraction) {
                                    delay(100)
                                    isReadingPlayerExpandedByUser = false
                                }
                            }
                        },
                        startDestination = MainTabs,
                        enterTransition = {
                            slideInHorizontally(tween(300)) { it }
                        },
                        exitTransition = {
                            ExitTransition.None
                        },
                        popEnterTransition = {
                            EnterTransition.None
                        },
                        popExitTransition = {
                            slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
                        },
                    ) {
                        composable<MainTabs> { routeEntry ->
                            AdaptiveRouteContent(
                                hostRouteId = routeEntry.id,
                                listContentKey = mainTabPages.getOrNull(mainPagerState.currentPage)?.key,
                            ) {
                                MainTabsPager(
                                    pagerState = mainPagerState,
                                    pages = mainTabPages,
                                    scrollToTopTrigger = scrollToTopTrigger,
                                    innerPadding = innerPadding,
                                    collectionDirectBrowseEnabled = collectionDirectBrowseEnabled,
                                    showHomeTopActions = showHomeTopActions,
                                )
                            }
                        }
                        composable<Login> {
                            LoginScreen(
                                onLoginComplete = { navController.popBackStack() },
                                onOpenTelemetrySettings = {
                                    navController.navigate(Account.SystemAndUpdateSettings("allowTelemetry"))
                                },
                            )
                        }
                        composable<Question> { navEntry ->
                            val question: Question = navEntry.toRoute()
                            AdaptiveRouteContent(
                                hostRouteId = navEntry.id,
                                listContentKey = "question:${question.questionId}",
                            ) {
                                QuestionScreen(question)
                            }
                        }
                        composable<Topic> { navEntry ->
                            val topic: Topic = navEntry.toRoute()
                            AdaptiveRouteContent(
                                hostRouteId = navEntry.id,
                                listContentKey = "topic:${topic.id}:${topic.section}",
                            ) {
                                TopicScreen(topic)
                            }
                        }
                        composable<WriteAnswer> { navEntry ->
                            val args: WriteAnswer = navEntry.toRoute()
                            WriteAnswerScreen(args)
                        }
                        composable<WritePin> { navEntry ->
                            WritePinScreen(navEntry.toRoute())
                        }
                        composable<Article>(
                            typeMap = mapOf(typeOf<ArticleType>() to ArticleTypeNavType),
                            enterTransition = articleEnterTransition,
                            exitTransition = articleExitTransition,
                        ) { navEntry ->
                            val article: Article = navEntry.toRoute()
                            if (!enableLandscapeListDetail) articleContent(article, navEntry)
                        }
                        composable<HotList> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "hot-list") {
                                HotListScreen(innerPadding)
                            }
                        }
                        composable<Follow> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "follow") {
                                FollowScreen(
                                    scrollToTopTrigger = scrollToTopTrigger,
                                    innerPadding = innerPadding,
                                    parentPagerState = mainPagerState,
                                )
                            }
                        }
                        composable<Daily> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "daily") {
                                DailyScreen()
                            }
                        }
                        composable<History> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "history") {
                                LegacyLocalHistoryScreen(innerPadding)
                            }
                        }
                        composable<OnlineHistory> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "online-history") {
                                OnlineHistoryScreen()
                            }
                        }
                        composable<Account> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "account") {
                                AccountSettingScreen(innerPadding)
                            }
                        }
                        composable<Search>(
                            enterTransition = {
                                if (initialState.destination.hasRoute<Search>()) {
                                    EnterTransition.None
                                } else {
                                    fadeIn(animationSpec = tween(durationMillis = 240)) +
                                        slideInVertically(animationSpec = tween(durationMillis = 280)) { it / 16 } +
                                        scaleIn(
                                            animationSpec = tween(durationMillis = 280),
                                            initialScale = 0.985f,
                                        )
                                }
                            },
                            popExitTransition = {
                                if (targetState.destination.hasRoute<Search>()) {
                                    ExitTransition.None
                                } else {
                                    fadeOut(animationSpec = tween(durationMillis = 180)) +
                                        slideOutVertically(animationSpec = tween(durationMillis = 220)) { it / 20 } +
                                        scaleOut(
                                            animationSpec = tween(durationMillis = 220),
                                            targetScale = 0.985f,
                                        )
                                }
                            },
                        ) { navEntry ->
                            val search: Search = navEntry.toRoute()
                            AdaptiveRouteContent(
                                hostRouteId = navEntry.id,
                                listContentKey = "search:${search.query}:${search.restrictedMemberHashId}",
                            ) {
                                SearchScreen(search)
                            }
                        }
                        composable<Collections> { navEntry ->
                            val data: Collections = navEntry.toRoute()
                            AdaptiveRouteContent(
                                hostRouteId = navEntry.id,
                                listContentKey = "collections:${data.userToken}",
                            ) {
                                CollectionScreen(
                                    urlToken = data.userToken,
                                    contentPadding = innerPadding,
                                )
                            }
                        }
                        composable<CollectionContent> { navEntry ->
                            val content: CollectionContent = navEntry.toRoute()
                            AdaptiveRouteContent(
                                hostRouteId = navEntry.id,
                                listContentKey = "collection-content:${content.collectionId}",
                            ) {
                                CollectionContentScreen(content.collectionId)
                            }
                        }
                        composable<Person> { navEntry ->
                            val person: Person = navEntry.toRoute()
                            AdaptiveRouteContent(
                                hostRouteId = navEntry.id,
                                listContentKey = "person:${person.id}:${person.urlToken}",
                            ) {
                                PeopleScreen(person)
                            }
                        }
                        composable<Pin> { navEntry ->
                            val pin: Pin = navEntry.toRoute()
                            if (!enableLandscapeListDetail) PinScreen(pin)
                        }
                        composable<Account.RecommendSettings.Blocklist> {
                            BlocklistSettingsScreen(blocklistSettingsNlpContent)
                        }
                        composable<Account.RecommendSettings.BlockedFeedHistory> {
                            BlockedFeedHistoryScreen()
                        }
                        composable<Notification> { navEntry ->
                            AdaptiveRouteContent(hostRouteId = navEntry.id, listContentKey = "notification") {
                                NotificationScreen()
                            }
                        }
                        composable<Notification.Entry> { navEntry ->
                            val entry: Notification.Entry = navEntry.toRoute()
                            NotificationTimelineScreen(entry.entryName, entry.title)
                        }
                        composable<Notification.Invitations> {
                            NotificationTimelineScreen("invite", "邀请回答")
                        }
                        composable<Notification.Message> { navEntry ->
                            PrivateMessageScreen(navEntry.toRoute())
                        }
                        composable<Notification.NotificationSettings> { navEntry ->
                            NotificationSettingsScreen(
                                setting = navEntry.toRoute<Notification.NotificationSettings>().setting,
                            )
                        }
                        composable<SentenceSimilarityTest> {
                            sentenceSimilarityContent()
                        }
                        composable<Account.AppearanceSettings> { navEntry ->
                            val args = navEntry.toRoute<Account.AppearanceSettings>()
                            AppearanceSettingsScreen(
                                setting = args.setting,
                                onExit = reloadBottomBarPreferences,
                            )
                        }
                        composable<Account.RecommendSettings> { navEntry ->
                            val args = navEntry.toRoute<Account.RecommendSettings>()
                            ContentFilterSettingsScreen(args.setting)
                        }
                        composable<Account.IdentityManagement> {
                            IdentityManagementScreen()
                        }
                        composable<Account.SystemAndUpdateSettings> { navEntry ->
                            SystemAndUpdateSettingsScreen(
                                setting = navEntry.toRoute<Account.SystemAndUpdateSettings>().setting,
                            )
                        }
                        composable<Account.ReadingSettings> {
                            ReadingSettingsScreen()
                        }
                        composable<Account.SettingsSearch> {
                            SettingsSearchScreen()
                        }
                        composable<Account.OpenSourceLicenses> {
                            OpenSourceLicensesScreen()
                        }
                        composable<Account.DeveloperSettings> {
                            DeveloperSettingsScreen()
                        }
                        composable<Account.DeveloperSettings.ColorScheme> {
                            ColorSchemeScreen()
                        }
                    }
                }
            },
        )

            }
        }
        if (showListDetail || showDetailPane || detailEntry == null) {
            paneStateHolder.SaveableStateProvider("detail") {
                // 尺寸变化只调整同一宿主的布局；隐藏时保存整个导航子树，独立弹窗也随页面离开组合。
                Row(if (showListDetail || showDetailPane) Modifier.fillMaxSize() else Modifier.size(0.dp)) {
                    if (showListDetail) {
                        Spacer(Modifier.width(listPaneWidth))
                        ListDetailDivider(
                            onDrag = { deltaPx ->
                                val usableWidthPx = with(density) {
                                    (containerWidth - LIST_DETAIL_DIVIDER_WIDTH).toPx()
                                }
                                if (usableWidthPx > 0f) {
                                    listPaneRatio = (listPaneRatio + deltaPx / usableWidthPx).coerceIn(0f, 1f)
                                }
                            },
                            onAdjustBy = { ratioDelta ->
                                listPaneRatio = (listPaneRatio + ratioDelta).coerceIn(0f, 1f)
                            },
                            onDragStopped = {
                                val normalizedWidth = normalizedListPaneWidth(containerWidth, listPaneRatio)
                                listPaneRatio = normalizedWidth.value /
                                    (containerWidth - LIST_DETAIL_DIVIDER_WIDTH).value
                                settings.putFloat(LANDSCAPE_LIST_PANE_RATIO_KEY, listPaneRatio)
                            },
                        )
                    }
                    DetailPane(Modifier.weight(1f).fillMaxSize())
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = if (showListDetail && showDetailPane) listPaneWidth + LIST_DETAIL_DIVIDER_WIDTH else 0.dp,
                    bottom = bottomPadding + 16.dp + if (
                        !showDetailPane && showMainNavigation && showMainNavigationBar && (!autoHideBottomBar || isBottomBarVisible)
                    ) {
                        64.dp
                    } else {
                        0.dp
                    },
                ).fillMaxWidth(),
            visible = isReadingPlayerExpanded && enableLandscapeListDetail,
            enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.92f),
            exit = fadeOut(tween(160)) + scaleOut(tween(160), targetScale = 0.92f),
        ) {
            ReadingPlayerBar(
                state = readingPlayerState,
                onPrevious = readingPlayer::playPrevious,
                onTogglePlayPause = readingPlayer::togglePlayPause,
                onNext = readingPlayer::playNext,
                onStop = readingPlayer::stop,
                onOpenQueue = { showReadingQueue = true },
                onPlaybackSpeedChange = { speed ->
                    saveReadingPlaybackSpeed(settings, speed)
                    readingPlayer.setPlaybackSpeed(speed)
                },
                onBackgroundInteraction = {
                    if (!isOnReadingDetail) isReadingPlayerExpandedByUser = false
                },
                modifier = Modifier
                    .onSizeChanged { readingPlayerHeightPx = it.height }
                    .graphicsLayer {
                        translationY = readingPlayerOverlayOffsetState.verticalOffsetPx
                    },
            )
        }

        AnimatedVisibility(
            visible = readingPlayerState.hasSession && !isReadingPlayerExpanded,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(160)),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CompactReadingPlayerButton(
                    state = readingPlayerState,
                    onExpand = { isReadingPlayerExpandedByUser = true },
                )
            }
        }
    }

    if (showReadingQueue && readingPlayerState.hasSession) {
        ReadingQueueSheet(
            state = readingPlayerState,
            onDismissRequest = {
                showReadingQueue = false
                if (!isOnReadingDetail) isReadingPlayerExpandedByUser = false
            },
            onItemClick = { index, item ->
                previousReadingItemKey = item.key
                if (index != readingPlayerState.currentIndex) {
                    readingPlayer.playAt(index)
                }
                showReadingQueue = false
                val destination = item.toDestination(readingPlayerState.sourceId)
                if (enableLandscapeListDetail) {
                    openReadingDestination(destination)
                    return@ReadingQueueSheet
                }
                if (adaptiveHostOwnerRouteId == navEntry?.id) {
                    requestAdaptiveNavigation(destination)
                    return@ReadingQueueSheet
                }
                val currentDestination = rootReadingDestination
                if (currentDestination != destination) {
                    if (currentDestination != null) {
                        navController.popBackStack()
                    }
                    navigate(destination)
                }
            },
            onOpenSettings = {
                showReadingQueue = false
                isReadingPlayerExpandedByUser = false
                if (navEntry?.destination?.hasRoute<Account.ReadingSettings>() != true) {
                    navigate(Account.ReadingSettings)
                }
            },
        )
    }
}

/**
 * 渲染可配置底部导航主壳内的页面。
 *
 * 每个页面都接收主壳给出的 [innerPadding]，保证系统栏、底部栏和子页面之间的留白一致。
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainTabsPager(
    pagerState: PagerState,
    pages: List<MainTabPage>,
    scrollToTopTrigger: Int,
    innerPadding: PaddingValues,
    collectionDirectBrowseEnabled: Boolean,
    showHomeTopActions: Boolean,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageNestedScrollConnection = NoOpPagerNestedScrollConnection,
    ) { pageIndex ->
        val page = pages.getOrNull(pageIndex) ?: return@HorizontalPager
        when (page) {
            MainTabPage.HomePage -> HomeScreen(
                scrollToTopTrigger = scrollToTopTrigger,
                innerPadding = innerPadding,
                showTopActions = showHomeTopActions,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.FollowPage -> FollowScreen(
                scrollToTopTrigger = scrollToTopTrigger,
                innerPadding = innerPadding,
                parentPagerState = pagerState,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.HotListPage -> HotListScreen(
                innerPadding = innerPadding,
                scrollToTopTrigger = scrollToTopTrigger,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.DailyPage -> DailyScreen(
                scrollToTopTrigger = scrollToTopTrigger,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.OnlineHistoryPage -> OnlineHistoryScreen(
                scrollToTopTrigger = scrollToTopTrigger,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.MyCollectionsPage -> MyCollectionsTopLevelPage(
                scrollToTopTrigger = scrollToTopTrigger,
                innerPadding = innerPadding,
                collectionDirectBrowseEnabled = collectionDirectBrowseEnabled,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.AccountPage -> AccountSettingScreen(
                innerPadding = innerPadding,
                isActive = pagerState.currentPage == pageIndex,
            )
        }
    }
}

@Composable
private fun MyCollectionsTopLevelPage(
    scrollToTopTrigger: Int,
    innerPadding: PaddingValues,
    collectionDirectBrowseEnabled: Boolean,
    isActive: Boolean,
) {
    val account = rememberAccountSettingsAccountState().value
    if (collectionDirectBrowseEnabled) {
        CollectionBrowseScreen(
            urlToken = account.urlToken,
            contentPadding = innerPadding,
            showBackButton = false,
            scrollToTopTrigger = scrollToTopTrigger,
            isActive = isActive,
        )
    } else {
        CollectionScreen(
            urlToken = account.urlToken,
            contentPadding = innerPadding,
            showBackButton = false,
            isActive = isActive,
        )
    }
}

private val TopLevelDestination.openFrom: String?
    get() = when (this) {
        Home -> ContentOpenFrom.HOME_FEED
        OnlineHistory -> ContentOpenFrom.HISTORY
        else -> null
    }

internal fun NavBackStackEntry?.hasRoute(cls: KClass<out NavDestination>): Boolean {
    val dest = this?.destination ?: return false
    return dest.hierarchy.any { it.hasRoute(cls) }
}

private fun NavBackStackEntry?.readingDestinationOrNull(): NavDestination? = when {
    this?.destination?.hasRoute<Article>() == true -> runCatching { toRoute<Article>() }.getOrNull()
    this?.destination?.hasRoute<Pin>() == true -> runCatching { toRoute<Pin>() }.getOrNull()
    this?.destination?.hasRoute<Question>() == true -> runCatching { toRoute<Question>() }.getOrNull()
    else -> null
}

private fun NavDestination.isAccountDetailDestination(): Boolean = when (this) {
    is Account.AppearanceSettings,
    Account.ReadingSettings,
    is Account.RecommendSettings,
    Account.RecommendSettings.Blocklist,
    Account.RecommendSettings.BlockedFeedHistory,
    Account.IdentityManagement,
    is Account.SystemAndUpdateSettings,
    Account.SettingsSearch,
    Account.OpenSourceLicenses,
    Account.DeveloperSettings,
    Account.DeveloperSettings.ColorScheme,
    -> true
    else -> false
}

private fun NavBackStackEntry?.isListPaneDestination(): Boolean = when {
    this == null -> false
    destination.hasRoute<MainTabs>() -> true
    destination.hasRoute<Search>() -> true
    destination.hasRoute<History>() -> true
    destination.hasRoute<Collections>() -> true
    destination.hasRoute<CollectionContent>() -> true
    destination.hasRoute<Question>() -> true
    destination.hasRoute<Person>() -> true
    destination.hasRoute<Topic>() -> true
    destination.hasRoute<Notification>() -> true
    destination.hasRoute<Notification.Entry>() -> true
    destination.hasRoute<Notification.Invitations>() -> true
    destination.hasRoute<Account.RecommendSettings.BlockedFeedHistory>() -> true
    else -> false
}

private fun NavGraphBuilder.accountSettings(
    reloadPreferences: () -> Unit,
    nlpContent: @Composable (onNavigateBack: () -> Unit) -> Unit,
) {
    composable<Account.RecommendSettings.Blocklist> {
        BlocklistSettingsScreen(nlpContent)
    }
    composable<Account.RecommendSettings.BlockedFeedHistory> {
        BlockedFeedHistoryScreen()
    }
    composable<Account.AppearanceSettings> { entry ->
        val args = entry.toRoute<Account.AppearanceSettings>()
        AppearanceSettingsScreen(
            setting = args.setting,
            onExit = reloadPreferences,
        )
    }
    composable<Account.RecommendSettings> { entry ->
        val args = entry.toRoute<Account.RecommendSettings>()
        ContentFilterSettingsScreen(args.setting)
    }
    composable<Account.IdentityManagement> {
        IdentityManagementScreen()
    }
    composable<Account.SystemAndUpdateSettings> { entry ->
        SystemAndUpdateSettingsScreen(
            setting = entry.toRoute<Account.SystemAndUpdateSettings>().setting,
        )
    }
    composable<Account.ReadingSettings> {
        ReadingSettingsScreen()
    }
    composable<Account.SettingsSearch> {
        SettingsSearchScreen()
    }
    composable<Account.OpenSourceLicenses> {
        OpenSourceLicensesScreen()
    }
    composable<Account.DeveloperSettings> {
        DeveloperSettingsScreen()
    }
    composable<Account.DeveloperSettings.ColorScheme> {
        ColorSchemeScreen()
    }
}
