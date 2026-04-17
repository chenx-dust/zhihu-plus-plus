package com.github.zly2006.zhihu.ui

import androidx.compose.animation.ContentTransform
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
import com.github.zly2006.zhihu.viewmodel.ArticleViewModel

internal fun articleEnterTransition(
    direction: ArticleViewModel.AnswerTransitionDirection,
): EnterTransition = when (direction) {
    ArticleViewModel.AnswerTransitionDirection.VERTICAL_NEXT ->
        slideInVertically(tween(300)) { it } + fadeIn(tween(300))

    ArticleViewModel.AnswerTransitionDirection.VERTICAL_PREVIOUS ->
        slideInVertically(tween(300)) { -it } + fadeIn(tween(300))

    ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_NEXT ->
        slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))

    ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
        slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300))

    ArticleViewModel.AnswerTransitionDirection.DEFAULT ->
        EnterTransition.None
}

internal fun articleExitTransition(
    direction: ArticleViewModel.AnswerTransitionDirection,
): ExitTransition = when (direction) {
    ArticleViewModel.AnswerTransitionDirection.VERTICAL_NEXT ->
        slideOutVertically(tween(300)) { -it } + fadeOut(tween(300))

    ArticleViewModel.AnswerTransitionDirection.VERTICAL_PREVIOUS ->
        slideOutVertically(tween(300)) { it } + fadeOut(tween(300))

    ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_NEXT ->
        slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))

    ArticleViewModel.AnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
        slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))

    ArticleViewModel.AnswerTransitionDirection.DEFAULT ->
        ExitTransition.None
}

internal fun articleContentTransform(
    direction: ArticleViewModel.AnswerTransitionDirection,
): ContentTransform = articleEnterTransition(direction).togetherWith(articleExitTransition(direction))
