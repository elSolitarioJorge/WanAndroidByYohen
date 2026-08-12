package com.ggg.kt.wanandroidbyyohen.data.collect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleCollectStoreTest {

    @Test
    fun seedPublishesServerState() {
        val store = ArticleCollectStore()

        store.seed(
            mapOf(
                1 to true,
                2 to false
            )
        )

        assertTrue(store.states.value.getValue(1).isCollected)
        assertFalse(store.states.value.getValue(2).isCollected)
    }

    @Test
    fun beginTogglePublishesOptimisticState() {
        val store = ArticleCollectStore()

        val targetCollected = store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )

        assertEquals(true, targetCollected)
        assertTrue(store.states.value.getValue(1).isCollected)
        assertTrue(store.states.value.getValue(1).isPending)
    }

    @Test
    fun duplicateToggleIsRejectedWhileRequestIsPending() {
        val store = ArticleCollectStore()

        store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )

        val secondTarget = store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )

        assertNull(secondTarget)
    }

    @Test
    fun confirmKeepsOptimisticValueAndClearsPendingState() {
        val store = ArticleCollectStore()

        store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )

        store.confirm(articleId = 1)

        val state = store.states.value.getValue(1)

        assertTrue(state.isCollected)
        assertFalse(state.isPending)
    }

    @Test
    fun rollbackRestoresPreviousValue() {
        val store = ArticleCollectStore()

        store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )

        store.rollback(articleId = 1)

        val state = store.states.value.getValue(1)

        assertFalse(state.isCollected)
        assertFalse(state.isPending)
    }

    @Test
    fun seedDoesNotOverwritePendingOptimisticState() {
        val store = ArticleCollectStore()

        store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )

        store.seed(mapOf(1 to false))

        val state = store.states.value.getValue(1)

        assertTrue(state.isCollected)
        assertTrue(state.isPending)
    }

    @Test
    fun clearRemovesAllStates() {
        val store = ArticleCollectStore()

        store.seed(mapOf(1 to true))
        store.clear()

        assertTrue(store.states.value.isEmpty())
    }

    @Test
    fun beginSetPublishesExplicitTargetState() {
        val store = ArticleCollectStore()

        val accepted = store.beginSet(
            articleId = 1,
            fallbackCollected = true,
            targetCollected = false
        )

        assertTrue(accepted)
        assertFalse(
            store.states.value.getValue(1).isCollected
        )
        assertTrue(
            store.states.value.getValue(1).isPending
        )
    }

    @Test
    fun resetForSignedOutUserKeepsIdsAndMarksAllUncollected() {
        val store = ArticleCollectStore()

        store.seed(
            mapOf(
                1 to true,
                2 to false
            )
        )

        store.beginToggle(
            articleId = 2,
            fallbackCollected = false
        )

        store.resetForSignedOutUser()

        assertEquals(
            setOf(1, 2),
            store.states.value.keys
        )

        store.states.value.values.forEach { state ->
            assertFalse(state.isCollected)
            assertFalse(state.isPending)
        }

        // 新账号的服务器状态必须能覆盖旧账号重置状态
        store.seed(mapOf(1 to true))

        assertTrue(
            store.states.value.getValue(1).isCollected
        )
    }

    @Test
    fun staleSeedDoesNotOverwriteConfirmedLocalMutation() {
        val store = ArticleCollectStore()

        store.seed(mapOf(1 to false))

        store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )
        store.confirm(articleId = 1)

        // 模拟较早发出的列表请求延迟返回旧状态
        store.seed(mapOf(1 to false))

        assertTrue(
            store.states.value.getValue(1).isCollected
        )
        assertFalse(
            store.states.value.getValue(1).isPending
        )
    }

    @Test
    fun matchingServerSeedReleasesLocalProtection() {
        val store = ArticleCollectStore()

        store.seed(mapOf(1 to false))

        store.beginToggle(
            articleId = 1,
            fallbackCollected = false
        )
        store.confirm(articleId = 1)

        // 服务器与本地状态一致，同步完成
        store.seed(mapOf(1 to true))

        // 解除保护后，后续服务器新状态可以正常生效
        store.seed(mapOf(1 to false))

        assertFalse(
            store.states.value.getValue(1).isCollected
        )
    }

    @Test
    fun resetForAuthenticatedUserPublishesExactAccountSnapshot() {
        val store = ArticleCollectStore()

        // 登录前页面已经见过文章 1、2
        store.seed(
            mapOf(
                1 to true,
                2 to false
            )
        )

        // 新账号收藏了文章 2、3
        store.resetForAuthenticatedUser(
            collectedArticleIds = listOf(2, 3)
        )

        assertEquals(
            setOf(1, 2, 3),
            store.states.value.keys
        )

        assertFalse(
            store.states.value.getValue(1).isCollected
        )

        assertTrue(
            store.states.value.getValue(2).isCollected
        )

        assertTrue(
            store.states.value.getValue(3).isCollected
        )

        store.states.value.values.forEach { state ->
            assertFalse(state.isPending)
        }
    }

    @Test
    fun resetForAuthenticatedUserWithNoCollectionsMarksKnownArticlesUncollected() {
        val store = ArticleCollectStore()

        store.seed(
            mapOf(
                1 to true,
                2 to true
            )
        )

        store.resetForAuthenticatedUser(
            collectedArticleIds = emptyList()
        )

        assertEquals(
            setOf(1, 2),
            store.states.value.keys
        )

        store.states.value.values.forEach { state ->
            assertFalse(state.isCollected)
            assertFalse(state.isPending)
        }
    }
}
