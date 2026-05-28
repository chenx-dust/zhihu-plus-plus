package com.github.zly2006.zhihu.ui

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.zly2006.zhihu.MainActivity
import com.github.zly2006.zhihu.navigation.Account
import com.github.zly2006.zhihu.navigation.Article
import com.github.zly2006.zhihu.navigation.ArticleType
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.NavDestination
import com.github.zly2006.zhihu.navigation.Navigator
import com.github.zly2006.zhihu.navigation.Person
import com.github.zly2006.zhihu.navigation.Pin
import com.github.zly2006.zhihu.navigation.Question
import com.github.zly2006.zhihu.ui.PaneDestination.Type.Appearance
import com.github.zly2006.zhihu.ui.PaneDestination.Type.Developer
import com.github.zly2006.zhihu.ui.PaneDestination.Type.DeveloperColorScheme
import com.github.zly2006.zhihu.ui.PaneDestination.Type.Recommend
import com.github.zly2006.zhihu.ui.PaneDestination.Type.RecommendBlockedHistory
import com.github.zly2006.zhihu.ui.PaneDestination.Type.RecommendBlocklist
import com.github.zly2006.zhihu.ui.PaneDestination.Type.SystemAndUpdate
import com.github.zly2006.zhihu.ui.subscreens.AppearanceSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.BlockedFeedHistoryScreen
import com.github.zly2006.zhihu.ui.subscreens.ColorSchemeScreen
import com.github.zly2006.zhihu.ui.subscreens.ContentFilterSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.DeveloperSettingsScreen
import com.github.zly2006.zhihu.ui.subscreens.SystemAndUpdateSettingsScreen
import com.github.zly2006.zhihu.viewmodel.ArticleViewModel
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private val ContentListDetailBackBehavior = BackNavigationBehavior.PopUntilContentChange

@Parcelize
data class PaneDestination(
    val type: Type,
    val id: String,
    val articleType: String = "",
    val title: String = "",
    val urlToken: String = "",
    val jumpTo: String = "",
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
    }
}

internal fun NavDestination.toContentPaneDestination(): PaneDestination? = when (this) {
    is Article -> PaneDestination(
        type = if (type == ArticleType.Answer) PaneDestination.Type.Answer else PaneDestination.Type.Article,
        id = id.toString(),
        articleType = type.name,
        title = title,
    )

    is Question -> PaneDestination(
        type = PaneDestination.Type.Question,
        id = questionId.toString(),
        title = title,
    )

    is Pin -> PaneDestination(
        type = PaneDestination.Type.Pin,
        id = id.toString(),
    )

    is Person -> PaneDestination(
        type = PaneDestination.Type.Person,
        id = id,
        urlToken = urlToken,
        title = name,
        jumpTo = jumpTo,
    )

    is Account.AppearanceSettings -> PaneDestination(
        type = Appearance,
        id = setting,
    )

    is Account.RecommendSettings -> PaneDestination(
        type = Recommend,
        id = setting,
    )

    Account.SystemAndUpdateSettings -> PaneDestination(SystemAndUpdate, id = "")
    Account.DeveloperSettings -> PaneDestination(Developer, id = "")
    Account.DeveloperSettings.ColorScheme -> PaneDestination(DeveloperColorScheme, id = "")
    Account.RecommendSettings.Blocklist -> PaneDestination(RecommendBlocklist, id = "")
    Account.RecommendSettings.BlockedFeedHistory -> PaneDestination(RecommendBlockedHistory, id = "")

    else -> null
}

internal fun NavDestination?.matchesContentSelection(
    selectionState: ListDetailSelectionState<PaneDestination>,
): Boolean = this?.toContentPaneDestination() ==
    (selectionState as? ListDetailSelectionState.ShowSelection)?.content

private fun PaneDestination.toNavDestination(): NavDestination? = when (type) {
    PaneDestination.Type.Answer -> Article(
        type = ArticleType.Answer,
        id = id.toLongOrNull() ?: return null,
        title = title,
    )

    PaneDestination.Type.Article -> Article(
        type = ArticleType.Article,
        id = id.toLongOrNull() ?: return null,
        title = title,
    )

    PaneDestination.Type.Question -> Question(
        questionId = id.toLongOrNull() ?: return null,
        title = title,
    )

    PaneDestination.Type.Pin -> Pin(id = id.toLongOrNull() ?: return null)

    PaneDestination.Type.Person -> Person(
        id = id,
        urlToken = urlToken,
        name = title,
        jumpTo = jumpTo,
    )

    Appearance -> Account.AppearanceSettings(setting = id)
    Recommend -> Account.RecommendSettings(setting = id)
    SystemAndUpdate -> Account.SystemAndUpdateSettings
    Developer -> Account.DeveloperSettings
    DeveloperColorScheme -> Account.DeveloperSettings.ColorScheme
    RecommendBlocklist -> Account.RecommendSettings.Blocklist
    RecommendBlockedHistory -> Account.RecommendSettings.BlockedFeedHistory
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ContentListDetailScreen(
    onSinglePaneDetailChanged: (Boolean) -> Unit = {},
    onExit: () -> Unit = {},
    listPane: @Composable (Navigator, ListDetailSelectionState<PaneDestination>) -> Unit,
) {
    val activity = androidx.activity.compose.LocalActivity.current as MainActivity
    val parentNavigator = LocalNavigator.current

    BaseListDetailScreen(
        backBehavior = ContentListDetailBackBehavior,
        toPaneDestination = { it.toContentPaneDestination() },
        emptyPane = {
            ListDetailEmptyPane(
                text = "请选择内容",
                icon = Icons.AutoMirrored.Outlined.Article,
            )
        },
        onSinglePaneDetailChanged = onSinglePaneDetailChanged,
        listPane = { navigator, selectionState ->
            listPane(navigator, selectionState)
        },
        detailPane = { paneDestination, paneNavigator ->
            val destination = paneDestination.toNavDestination()
            if (destination == null) {
                ListDetailEmptyPane(
                    text = "暂不支持在详情窗格中打开该内容",
                    icon = Icons.AutoMirrored.Outlined.Article,
                )
                return@BaseListDetailScreen
            }
            val sharedData: ArticleViewModel.ArticlesSharedData = viewModel(viewModelStoreOwner = activity)
            AnimatedContent(
                targetState = destination,
                transitionSpec = {
                    if (initialState is Article || targetState is Article) {
                        articleContentTransform(sharedData.answerTransitionDirection)
                    } else {
                        EnterTransition.None togetherWith ExitTransition.None
                    }
                },
                label = "content-list-detail-pane",
            ) { currentDestination ->
                when (currentDestination) {
                    is Article -> {
                        val viewModel: ArticleViewModel = viewModel(key = "article-${currentDestination.id}") {
                            ArticleViewModel(currentDestination, activity.httpClient, null)
                        }
                        ArticleScreen(
                            article = currentDestination,
                            viewModel = viewModel,
                            paneNavigator = paneNavigator,
                            parentNavigator = parentNavigator,
                        )
                    }

                    is Question -> {
                        QuestionScreen(
                            question = currentDestination,
                        )
                    }

                    is Pin -> {
                        PinScreen(
                            pin = currentDestination,
                            parentNavigator = parentNavigator,
                        )
                    }

                    is Person -> {
                        PeopleScreen(
                            person = currentDestination,
                        )
                    }

                    is Account.AppearanceSettings -> {
                        AppearanceSettingsScreen(
                            setting = currentDestination.setting,
                            onExit = onExit,
                        )
                    }

                    is Account.RecommendSettings -> {
                        ContentFilterSettingsScreen(
                            setting = currentDestination.setting,
                        )
                    }

                    is Account.SystemAndUpdateSettings -> {
                        SystemAndUpdateSettingsScreen()
                    }

                    is Account.DeveloperSettings -> {
                        DeveloperSettingsScreen()
                    }

                    is Account.DeveloperSettings.ColorScheme -> {
                        ColorSchemeScreen()
                    }

                    is Account.RecommendSettings.Blocklist -> {
                        BlocklistSettingsScreen()
                    }

                    is Account.RecommendSettings.BlockedFeedHistory -> {
                        BlockedFeedHistoryScreen()
                    }

                    else -> {
                        ListDetailEmptyPane(
                            text = "暂不支持在详情窗格中打开该内容",
                            icon = Icons.AutoMirrored.Outlined.Article,
                        )
                    }
                }
            }
        },
    )
}
