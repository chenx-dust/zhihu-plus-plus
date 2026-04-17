package com.github.zly2006.zhihu.ui

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.zly2006.zhihu.MainActivity
import com.github.zly2006.zhihu.navigation.Article
import com.github.zly2006.zhihu.navigation.ArticleType
import com.github.zly2006.zhihu.navigation.NavDestination
import com.github.zly2006.zhihu.navigation.Navigator
import com.github.zly2006.zhihu.navigation.Person
import com.github.zly2006.zhihu.navigation.Pin
import com.github.zly2006.zhihu.navigation.Question
import com.github.zly2006.zhihu.viewmodel.ArticleViewModel
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private val ContentListDetailBackBehavior = BackNavigationBehavior.PopUntilContentChange

@Parcelize
data class ContentPaneDestination(
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
    }
}

internal fun NavDestination.toContentPaneDestination(): ContentPaneDestination? = when (this) {
    is Article -> ContentPaneDestination(
        type = if (type == ArticleType.Answer) ContentPaneDestination.Type.Answer else ContentPaneDestination.Type.Article,
        id = id.toString(),
        articleType = type.name,
        title = title,
    )

    is Question -> ContentPaneDestination(
        type = ContentPaneDestination.Type.Question,
        id = questionId.toString(),
        title = title,
    )

    is Pin -> ContentPaneDestination(
        type = ContentPaneDestination.Type.Pin,
        id = id.toString(),
    )

    is Person -> ContentPaneDestination(
        type = ContentPaneDestination.Type.Person,
        id = id,
        urlToken = urlToken,
        title = name,
        jumpTo = jumpTo,
    )

    else -> null
}

internal fun NavDestination?.matchesContentSelection(
    selectionState: ListDetailSelectionState<ContentPaneDestination>,
): Boolean = this?.toContentPaneDestination() ==
    (selectionState as? ListDetailSelectionState.ShowSelection)?.content

private fun ContentPaneDestination.toNavDestination(): NavDestination? = when (type) {
    ContentPaneDestination.Type.Answer -> Article(
        type = ArticleType.Answer,
        id = id.toLongOrNull() ?: return null,
        title = title,
    )

    ContentPaneDestination.Type.Article -> Article(
        type = ArticleType.Article,
        id = id.toLongOrNull() ?: return null,
        title = title,
    )

    ContentPaneDestination.Type.Question -> Question(
        questionId = id.toLongOrNull() ?: return null,
        title = title,
    )

    ContentPaneDestination.Type.Pin -> Pin(id = id.toLongOrNull() ?: return null)

    ContentPaneDestination.Type.Person -> Person(
        id = id,
        urlToken = urlToken,
        name = title,
        jumpTo = jumpTo,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ContentListDetailScreen(
    innerPadding: PaddingValues,
    onSinglePaneDetailChanged: (Boolean) -> Unit = {},
    listPane: @Composable (Navigator, ListDetailSelectionState<ContentPaneDestination>) -> Unit,
) {
    val activity = androidx.activity.compose.LocalActivity.current as MainActivity
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
                            innerPadding = innerPadding,
                            paneNavigator = paneNavigator,
                        )
                    }

                    is Question -> {
                        QuestionScreen(
                            question = currentDestination,
                        )
                    }

                    is Pin -> {
                        PinScreen(
                            innerPadding = innerPadding,
                            pin = currentDestination,
                        )
                    }

                    is Person -> {
                        PeopleScreen(
                            innerPadding = innerPadding,
                            person = currentDestination,
                        )
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
