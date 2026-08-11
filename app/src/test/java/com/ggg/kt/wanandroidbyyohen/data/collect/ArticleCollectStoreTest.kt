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
}
