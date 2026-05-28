/*
 * Zhihu++ - Free & Ad-Free Zhihu client for Android.
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

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalNavigationBarOverride
import androidx.compose.material3.LocalNavigationRailOverride
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarOverride
import androidx.compose.material3.NavigationBarOverrideScope
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.NavigationRailOverride
import androidx.compose.material3.NavigationRailOverrideScope
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.zly2006.zhihu.MainActivity
import com.github.zly2006.zhihu.navigation.Account
import com.github.zly2006.zhihu.navigation.Article
import com.github.zly2006.zhihu.navigation.CollectionContent
import com.github.zly2006.zhihu.navigation.Collections
import com.github.zly2006.zhihu.navigation.Daily
import com.github.zly2006.zhihu.navigation.Follow
import com.github.zly2006.zhihu.navigation.History
import com.github.zly2006.zhihu.navigation.Home
import com.github.zly2006.zhihu.navigation.HotList
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.MainTabs
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
import com.github.zly2006.zhihu.theme.ThemeManager
import com.github.zly2006.zhihu.theme.ZhihuTheme
import com.github.zly2006.zhihu.ui.subscreens.AppearanceSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.BOTTOM_BAR_ITEMS_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.subscreens.BlockedFeedHistoryScreen
import com.github.zly2006.zhihu.ui.subscreens.ColorSchemeScreen
import com.github.zly2006.zhihu.ui.subscreens.ContentFilterSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.DeveloperSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.OpenSourceLicensesScreen
import com.github.zly2006.zhihu.ui.subscreens.START_DESTINATION_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.subscreens.SystemAndUpdateSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.defaultBottomBarSelectionKeys
import com.github.zly2006.zhihu.ui.subscreens.navDestinationFromName
import com.github.zly2006.zhihu.ui.subscreens.normalizeBottomBarSelection
import com.github.zly2006.zhihu.ui.subscreens.resolveValidStartDestinationKey
import com.github.zly2006.zhihu.viewmodel.ArticleViewModel
import com.github.zly2006.zhihu.viewmodel.filter.ContentOpenFrom
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import com.github.zly2006.zhihu.ui.NavHost as MyNavHost

const val SURVEY_URL = "https://v.wjx.cn/vm/Ppfw2R4.aspx#"

private sealed class MainTabPage(
    val bottomDestination: TopLevelDestination,
    val key: String,
) {
    data object HomePage : MainTabPage(Home, "home")

    data object FollowRecommendPage : MainTabPage(Follow, "follow_recommend")

    data object FollowDynamicPage : MainTabPage(Follow, "follow_dynamic")

    data object HotListPage : MainTabPage(HotList, "hotlist")

    data object DailyPage : MainTabPage(Daily, "daily")

    data object OnlineHistoryPage : MainTabPage(OnlineHistory, "online_history")

    data object AccountPage : MainTabPage(Account, "account")
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ComponentOverrideApi::class)
@SuppressLint("RestrictedApi")
@Composable
fun ZhihuMain(modifier: Modifier = Modifier, navController: NavHostController) {
    val bottomPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues().calculateBottomPadding()
    val activity = LocalActivity.current as MainActivity
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences(PREFERENCE_NAME, android.content.Context.MODE_PRIVATE) }

    // 底部导航栏功能
    var duo3HomeAccount by remember { mutableStateOf(preferences.getBoolean("duo3_home_account", false)) }
    var duo3NavStyle by remember { mutableStateOf(preferences.getBoolean("duo3_nav_style", false)) }
    var tapToScrollToTopEnabled by remember { mutableStateOf(preferences.getBoolean("bottomBarTapScrollToTop", true)) }
    var autoHideBottomBar by remember { mutableStateOf(preferences.getBoolean("autoHideBottomBar", false)) }
    val allBottomBarItemKeys = remember {
        listOf(Home.name, Follow.name, HotList.name, Daily.name, OnlineHistory.name, Account.name)
    }

    fun computeSelectedKeys(isDuo3HomeAccount: Boolean) = normalizeBottomBarSelection(
        preferences
            .getStringSet(BOTTOM_BAR_ITEMS_PREFERENCE_KEY, defaultBottomBarSelectionKeys(isDuo3HomeAccount))
            ?.toSet() ?: defaultBottomBarSelectionKeys(isDuo3HomeAccount),
        isDuo3HomeAccount,
        enforceMinimumSelection = true,
    )

    fun computeStartDestination(selectedKeys: Set<String>) = navDestinationFromName(
        resolveValidStartDestinationKey(
            preferences.getString(START_DESTINATION_PREFERENCE_KEY, Home.name),
            allBottomBarItemKeys.filter { it in selectedKeys },
        ),
    )

    var selectedBottomBarItemKeys by remember { mutableStateOf(computeSelectedKeys(duo3HomeAccount)) }
    var startDestination by remember { mutableStateOf(computeStartDestination(selectedBottomBarItemKeys)) }

    val reloadBottomBarPreferences = {
        val updatedDuo3HomeAccount = preferences.getBoolean("duo3_home_account", false)
        val updatedSelectedBottomBarItemKeys = computeSelectedKeys(updatedDuo3HomeAccount)
        duo3HomeAccount = updatedDuo3HomeAccount
        duo3NavStyle = preferences.getBoolean("duo3_nav_style", false)
        tapToScrollToTopEnabled = preferences.getBoolean("bottomBarTapScrollToTop", true)
        autoHideBottomBar = preferences.getBoolean("autoHideBottomBar", false)
        selectedBottomBarItemKeys = updatedSelectedBottomBarItemKeys
        startDestination = computeStartDestination(updatedSelectedBottomBarItemKeys)
    }

    val navEntry by navController.currentBackStackEntryAsState()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val paneDirective = calculatePaneScaffoldDirective(adaptiveInfo)
    val navSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    val navigationSuiteState = rememberSaveable(saver = MyNavigationSuiteScaffoldStateImpl.saver()) {
        MyNavigationSuiteScaffoldStateImpl(initialValue = NavigationSuiteScaffoldValue.Visible)
    }
    val isSinglePaneWindow = paneDirective.maxHorizontalPartitions == 1
    var isSinglePaneListDetailShowingDetail by rememberSaveable { mutableStateOf(false) }

    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    // 滚动时自动隐藏底部导航栏
    var isBottomBarVisible by remember { mutableStateOf(true) }
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

    LaunchedEffect(
        isSinglePaneWindow,
        navSuiteType,
        isSinglePaneListDetailShowingDetail,
        autoHideBottomBar,
        isBottomBarVisible,
        navEntry,
    ) {
        val shouldHideNavigationSuite = navSuiteType.isNavigationBar() &&
            (
                isSinglePaneWindow && isSinglePaneListDetailShowingDetail ||
                    (autoHideBottomBar && !isBottomBarVisible && isTopLevelDest(navEntry))
            )
        navigationSuiteState.snapTo(
            if (shouldHideNavigationSuite) {
                NavigationSuiteScaffoldValue.Hidden
            } else {
                NavigationSuiteScaffoldValue.Visible
            },
        )
    }

    val allBottomBarItems = listOf(
        Triple(Home, "主页", Icons.Filled.Home),
        Triple(Follow, "关注", if (duo3NavStyle) Icons.Filled.Group else Icons.Filled.PersonAddAlt1),
        Triple(HotList, "热榜", Icons.Filled.Whatshot),
        Triple(Daily, "日报", Icons.Filled.Newspaper),
        Triple(OnlineHistory, "历史", Icons.Filled.History),
        Triple(Account, "账号", Icons.Filled.ManageAccounts),
    )
    val bottomBarItems = allBottomBarItems.filter { it.first.name in selectedBottomBarItemKeys }

    val mainTabPages = remember(bottomBarItems) {
        bottomBarItems.flatMap { item ->
            when (item.first) {
                Home -> listOf(MainTabPage.HomePage)
                Follow -> listOf(MainTabPage.FollowRecommendPage, MainTabPage.FollowDynamicPage)
                HotList -> listOf(MainTabPage.HotListPage)
                Daily -> listOf(MainTabPage.DailyPage)
                OnlineHistory -> listOf(MainTabPage.OnlineHistoryPage)
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

    var lastFollowPageKey by rememberSaveable { mutableStateOf(MainTabPage.FollowRecommendPage.key) }
    val mainPagerState = rememberPagerState(
        initialPage = pageIndexForDestination(startDestination),
        pageCount = { mainTabPages.size },
    )
    val coroutineScope = rememberCoroutineScope()

    fun currentMainTabPage(): MainTabPage? = mainTabPages.getOrNull(mainPagerState.currentPage)
    var currentMainTabDestination by remember { mutableStateOf(startDestination) }

    fun pageIndexForBottomDestination(destination: TopLevelDestination): Int {
        if (destination == Follow) {
            val rememberedFollowPage = mainTabPages.indexOfFirst { it.key == lastFollowPageKey }
            if (rememberedFollowPage >= 0) return rememberedFollowPage
        }
        return pageIndexForDestination(destination)
    }

    fun navigateTopLevel(destination: TopLevelDestination) {
        val targetPage = pageIndexForBottomDestination(destination)
        coroutineScope.launch {
            mainPagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(mainPagerState.currentPage, mainTabPages) {
        when (val page = currentMainTabPage()) {
            MainTabPage.FollowRecommendPage, MainTabPage.FollowDynamicPage -> lastFollowPageKey = page.key
            else -> {}
        }
        currentMainTabPage()?.bottomDestination?.let { destination ->
            currentMainTabDestination = destination
            activity.setCurrentMainTabOpenFrom(destination.openFrom)
        }
    }

    val mainTabNavigationTarget = activity.mainTabNavigationTarget
    LaunchedEffect(mainTabNavigationTarget, mainTabPages) {
        mainTabNavigationTarget?.let { destination ->
            // MainActivity maps legacy top-level route requests onto MainTabs. Consume that request
            // here so callers such as deeplinks can still select Home/Follow/etc. without pushing
            // those old routes onto the back stack.
            mainPagerState.scrollToPage(pageIndexForBottomDestination(destination))
            activity.consumeMainTabNavigationTarget(destination)
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

    val itemColors =
        if (duo3NavStyle) {
            if (!ThemeManager.isDarkTheme()) {
                NavigationSuiteItemColors(
                    navigationBarItemColors = NavigationBarItemDefaults.colors().copy(
                        selectedIndicatorColor =
                            MaterialTheme.colorScheme.secondaryContainer
                                .copy(alpha = 0.92f)
                                .compositeOver(MaterialTheme.colorScheme.secondary),
                    ),
                    navigationRailItemColors = NavigationRailItemDefaults.colors().copy(
                        selectedIndicatorColor =
                            MaterialTheme.colorScheme.secondaryContainer
                                .copy(alpha = 0.92f)
                                .compositeOver(MaterialTheme.colorScheme.secondary),
                    ),
                    navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
                )
            } else {
                null
            }
        } else {
            NavigationSuiteItemColors(
                navigationBarItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xff66ccff),
                    indicatorColor = Color.Transparent,
                ),
                navigationRailItemColors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xff66ccff),
                    indicatorColor = Color.Transparent,
                ),
                navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
                    selectedIconColor = Color(0xff66ccff),
                ),
            )
        }

    @OptIn(ExperimentalMaterial3ComponentOverrideApi::class)
    val myCustomOverride = object : NavigationBarOverride, NavigationRailOverride {
        @Composable
        override fun NavigationBarOverrideScope.NavigationBar() {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = contentColor,
                tonalElevation = tonalElevation,
                modifier = this@NavigationBar.modifier.height(
                    (if (duo3NavStyle) 64.dp else 56.dp) + bottomPadding,
                ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(windowInsets)
                            .padding(top = (if (duo3NavStyle) 4.dp else 0.dp))
                            .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
            }
        }

        @Composable
        override fun NavigationRailOverrideScope.NavigationRail() {
            Surface(
                color = containerColor,
                contentColor = contentColor,
                modifier = this@NavigationRail.modifier.width(
                    (if (duo3NavStyle) 68.dp else 60.dp),
                ),
            ) {
                Column(
                    Modifier.fillMaxHeight()
                        .windowInsetsPadding(windowInsets)
                        .widthIn(min = 80.dp)
                        .padding(vertical = (if (duo3NavStyle) 14.dp else 0.dp))
                        .padding(start = 8.dp)
                        .selectableGroup()
                        .semantics { isTraversalGroup = true },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val header = header
                    if (header != null) {
                        header()
                        Spacer(Modifier.height(8.dp))
                    }
                    content()
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalNavigationBarOverride provides myCustomOverride,
        LocalNavigationRailOverride provides myCustomOverride,
    ) {
        val currentBottomDestination = mainTabPages
            .getOrNull(mainPagerState.targetPage)
            ?.bottomDestination
        val innerPadding = PaddingValues(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
        )
        NavigationSuiteScaffold(
            modifier = modifier
                .nestedScroll(bottomBarScrollConnection)
                .semantics { testTagsAsResourceId = true },
            state = navigationSuiteState,
            navigationSuiteItems = {
                bottomBarItems.forEach { item ->
                    val destination = item.first
                    val label = item.second
                    val icon = item.third
                    val tag = "nav_tab_${destination.name.lowercase()}"
                    item(
                        currentBottomDestination?.let { it::class == destination::class } == true,
                        onClick = {
                            if (currentBottomDestination?.let { it::class == destination::class } != true) {
                                navigateTopLevel(destination)
                            } else if (tapToScrollToTopEnabled) {
                                scrollToTopTrigger++
                            }
                        },
                        label = {
                            if (duo3NavStyle) {
                                Text(label)
                            } else {
                                Text(
                                    label,
                                    style = TextStyle(
                                        fontSize = 9.sp,
                                        color = LocalContentColor.current.copy(alpha = 0.6f),
                                    ),
                                )
                            }
                        },
                        alwaysShowLabel = duo3NavStyle,
                        colors = itemColors,
                        icon = {
                            Icon(icon, contentDescription = label)
                        },
                        modifier = (if (duo3NavStyle) Modifier.padding(top = 4.dp) else Modifier).testTag(tag),
                    )
                }
            },
        ) {
            CompositionLocalProvider(
                LocalNavigator provides Navigator(
                    onNavigate = { destination ->
                        activity.navigate(destination)
                    },
                    onNavigateBack = navController::popBackStack,
                ),
            ) {
                MyNavHost(
                    navController,
                    modifier = Modifier,
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
                    composable<MainTabs> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            MainTabsPager(
                                pagerState = mainPagerState,
                                pages = mainTabPages,
                                scrollToTopTrigger = scrollToTopTrigger,
                                innerPadding = innerPadding,
                                onFollowTabSelected = { followTabIndex ->
                                    val page = if (followTabIndex == 0) {
                                        MainTabPage.FollowRecommendPage
                                    } else {
                                        MainTabPage.FollowDynamicPage
                                    }
                                    val index = mainTabPages.indexOfFirst { it.key == page.key }
                                    if (index >= 0) {
                                        coroutineScope.launch {
                                            mainPagerState.animateScrollToPage(index)
                                        }
                                    }
                                },
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Question> { navEntry ->
                        val question: Question = navEntry.toRoute()
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            QuestionScreen(
                                question = question,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Article>(
                        enterTransition = {
                            val sharedData = try {
                                ViewModelProvider(activity)[ArticleViewModel.ArticlesSharedData::class.java]
                            } catch (_: Exception) {
                                null
                            }
                            when (sharedData?.answerTransitionDirection) {
                                ArticleViewModel.AnswerTransitionDirection.VERTICAL_NEXT ->
                                    slideInVertically(tween(300)) { it } + fadeIn(tween(300))
                                ArticleViewModel.AnswerTransitionDirection.VERTICAL_PREVIOUS ->
                                    slideInVertically(tween(300)) { -it } + fadeIn(tween(300))
                                ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_NEXT ->
                                    slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
                                ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
                                    slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300))
                                else -> slideInHorizontally(tween(300)) { it }
                            }
                        },
                        exitTransition = {
                            val sharedData = try {
                                (activity as? androidx.activity.ComponentActivity)
                                    ?.let { ViewModelProvider(it)[ArticleViewModel.ArticlesSharedData::class.java] }
                            } catch (_: Exception) {
                                null
                            }
                            when (sharedData?.answerTransitionDirection) {
                                ArticleViewModel.AnswerTransitionDirection.VERTICAL_NEXT ->
                                    slideOutVertically(tween(300)) { -it } + fadeOut(tween(300))
                                ArticleViewModel.AnswerTransitionDirection.VERTICAL_PREVIOUS ->
                                    slideOutVertically(tween(300)) { it } + fadeOut(tween(300))
                                ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_NEXT ->
                                    slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))
                                ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
                                    slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
                                else -> ExitTransition.None
                            }
                        },
                    ) { navEntry ->
                        val article: Article = navEntry.toRoute()
                        val viewModel: ArticleViewModel = viewModel(navEntry) {
                            ArticleViewModel(article, activity.httpClient, navEntry)
                        }
                        ArticleScreen(article, viewModel)
                    }
                    composable<HotList> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            HotListScreen(
                                innerPadding = innerPadding,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Follow> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            FollowScreen(
                                scrollToTopTrigger = scrollToTopTrigger,
                                innerPadding = innerPadding,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Daily> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, _ ->
                            DailyScreen()
                        }
                    }
                    composable<History> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            LegacyLocalHistoryScreen(
                                innerPadding,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<OnlineHistory> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            OnlineHistoryScreen(
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Account> {
                        SettingsListDetailScreen(
                            innerPadding = innerPadding,
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                            onExit = reloadBottomBarPreferences,
                        )
                    }
                    composable<Search> { navEntry ->
                        val search: Search = navEntry.toRoute()
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            SearchScreen(
                                search = search,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Collections> {
                        val data: Collections = it.toRoute()
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, _ ->
                            CollectionScreen(data.userToken)
                        }
                    }
                    composable<CollectionContent> {
                        val content: CollectionContent = it.toRoute()
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            CollectionContentScreen(
                                collectionId = content.collectionId,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Person> {
                        val person: Person = it.toRoute()
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, selectionState ->
                            PeopleScreen(
                                person = person,
                                selectionState = selectionState,
                            )
                        }
                    }
                    composable<Pin> {
                        val pin = it.toRoute<Pin>()
                        PinScreen(pin)
                    }
                    composable<Account.RecommendSettings.Blocklist> {
                        BlocklistSettingsScreen()
                    }
                    composable<Account.RecommendSettings.BlockedFeedHistory> {
                        BlockedFeedHistoryScreen()
                    }
                    composable<Notification> {
                        ContentListDetailScreen(
                            onSinglePaneDetailChanged = { isSinglePaneListDetailShowingDetail = it },
                        ) { _, _ ->
                            NotificationScreen()
                        }
                    }
                    composable<Notification.NotificationSettings> {
                        NotificationSettingsScreen()
                    }
                    composable<SentenceSimilarityTest> {
                        SentenceSimilarityTestScreen()
                    }
                    composable<Account.AppearanceSettings> {
                        val args = it.toRoute<Account.AppearanceSettings>()
                        AppearanceSettingsScreen(
                            setting = args.setting,
                            onExit = reloadBottomBarPreferences,
                        )
                    }
                    composable<Account.RecommendSettings> {
                        val args = it.toRoute<Account.RecommendSettings>()
                        ContentFilterSettingsScreen(args.setting)
                    }
                    composable<Account.SystemAndUpdateSettings> {
                        SystemAndUpdateSettingsScreen()
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
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainTabsPager(
    pagerState: PagerState,
    pages: List<MainTabPage>,
    scrollToTopTrigger: Int,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onFollowTabSelected: (Int) -> Unit,
    selectionState: ListDetailSelectionState<ContentPaneDestination> = ListDetailSelectionState.NoSelection,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { pageIndex ->
        val page = pages.getOrNull(pageIndex) ?: return@HorizontalPager
        when (page) {
            MainTabPage.HomePage -> HomeScreen(
                scrollToTopTrigger = scrollToTopTrigger,
                innerPadding = innerPadding,
                selectionState = selectionState,
            )
            MainTabPage.FollowRecommendPage -> FollowTopLevelPage(
                selectedTabIndex = 0,
                onTabSelected = onFollowTabSelected,
                selectionState = selectionState,
                scrollToTopTrigger = scrollToTopTrigger,
                innerPadding = innerPadding,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.FollowDynamicPage -> FollowTopLevelPage(
                selectedTabIndex = 1,
                onTabSelected = onFollowTabSelected,
                selectionState = selectionState,
                scrollToTopTrigger = scrollToTopTrigger,
                innerPadding = innerPadding,
                isActive = pagerState.currentPage == pageIndex,
            )
            MainTabPage.HotListPage -> HotListScreen(innerPadding, selectionState)
            MainTabPage.DailyPage -> DailyScreen()
            MainTabPage.OnlineHistoryPage -> OnlineHistoryScreen()
            MainTabPage.AccountPage -> AccountSettingScreen(innerPadding)
        }
    }
}

private fun isTopLevelDest(navEntry: NavBackStackEntry?): Boolean = navEntry.hasRoute(MainTabs::class)

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

private fun NavigationSuiteType.isNavigationBar(): Boolean =
    this == NavigationSuiteType.ShortNavigationBarCompact ||
            this == NavigationSuiteType.ShortNavigationBarMedium ||
            this == NavigationSuiteType.NavigationBar

// 覆盖原有动画
internal class MyNavigationSuiteScaffoldStateImpl(
    var initialValue: NavigationSuiteScaffoldValue,
) : NavigationSuiteScaffoldState {
    private val internalValue: Float = if (initialValue == NavigationSuiteScaffoldValue.Visible) VISIBLE else HIDDEN
    private val internalState = Animatable(internalValue, Float.VectorConverter)
    private val currentValueState = derivedStateOf {
        if (internalState.value == VISIBLE) {
            NavigationSuiteScaffoldValue.Visible
        } else {
            NavigationSuiteScaffoldValue.Hidden
        }
    }

    override val isAnimating: Boolean
        get() = internalState.isRunning

    override val targetValue: NavigationSuiteScaffoldValue
        get() =
            if (internalState.targetValue == VISIBLE) {
                NavigationSuiteScaffoldValue.Visible
            } else {
                NavigationSuiteScaffoldValue.Hidden
            }

    override val currentValue: NavigationSuiteScaffoldValue
        get() = currentValueState.value

    override suspend fun hide() {
        internalState.animateTo(targetValue = HIDDEN, animationSpec = tween(200))
    }

    override suspend fun show() {
        internalState.animateTo(targetValue = VISIBLE, animationSpec = tween(200))
    }

    override suspend fun toggle() {
        internalState.animateTo(
            targetValue = if (targetValue == NavigationSuiteScaffoldValue.Visible) HIDDEN else VISIBLE,
            animationSpec = tween(200),
        )
    }

    override suspend fun snapTo(targetValue: NavigationSuiteScaffoldValue) {
        val target = if (targetValue == NavigationSuiteScaffoldValue.Visible) VISIBLE else HIDDEN
        internalState.snapTo(target)
    }

    companion object {
        private const val HIDDEN = 0f
        private const val VISIBLE = 1f

        fun saver() =
            androidx.compose.runtime.saveable.Saver<NavigationSuiteScaffoldState, NavigationSuiteScaffoldValue>(
                save = { it.targetValue },
                restore = { MyNavigationSuiteScaffoldStateImpl(it) },
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    val navController = rememberNavController()
    ZhihuTheme {
        ZhihuMain(navController = navController)
    }
}
