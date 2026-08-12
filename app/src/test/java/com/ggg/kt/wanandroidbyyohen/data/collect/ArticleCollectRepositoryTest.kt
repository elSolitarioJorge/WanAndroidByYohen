package com.ggg.kt.wanandroidbyyohen.data.collect

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleCollectRepositoryTest {
    @Test
    fun successfulRequestConfirmsOptimisticState() = runTest {
        val store = ArticleCollectStore()
        val remote = FakeCollectRemoteDataSource()
        val repository = ArticleCollectRepository(store, remote)

        val request = async {
            repository.toggle(
                articleId = 1,
                fallbackCollected = false
            )
        }

        runCurrent()

        assertEquals(
            ArticleCollectState(
                isCollected = true,
                isPending = true
            ),
            store.states.value.getValue(1)
        )

        remote.complete(UiState.Success(Any()))

        assertEquals(UiState.Success(true), request.await())
        assertTrue(store.states.value.getValue(1).isCollected)
        assertFalse(store.states.value.getValue(1).isPending)
    }

    @Test
    fun failedRequestRollsBackOptimisticState() = runTest {
        val store = ArticleCollectStore()
        val remote = FakeCollectRemoteDataSource()
        val repository = ArticleCollectRepository(store, remote)

        val request = async {
            repository.toggle(
                articleId = 1,
                fallbackCollected = false
            )
        }

        runCurrent()
        remote.complete(UiState.Error("网络异常"))

        assertEquals(UiState.Error("网络异常"), request.await())
        assertFalse(store.states.value.getValue(1).isCollected)
        assertFalse(store.states.value.getValue(1).isPending)
    }

    @Test
    fun duplicateRequestForSameArticleIsIgnored() = runTest {
        val store = ArticleCollectStore()
        val remote = FakeCollectRemoteDataSource()
        val repository = ArticleCollectRepository(store, remote)

        val firstRequest = async {
            repository.toggle(
                articleId = 1,
                fallbackCollected = false
            )
        }

        runCurrent()

        val duplicateResult = repository.toggle(
            articleId = 1,
            fallbackCollected = false
        )

        assertNull(duplicateResult)
        assertEquals(
            listOf(1 to true),
            remote.requests
        )

        remote.complete(UiState.Success(Any()))
        firstRequest.await()
    }

    @Test
    fun cancelledRequestRollsBackOptimisticState() = runTest {
        val store = ArticleCollectStore()
        val remote = FakeCollectRemoteDataSource()
        val repository = ArticleCollectRepository(store, remote)

        val request = async {
            repository.toggle(
                articleId = 1,
                fallbackCollected = false
            )
        }

        runCurrent()
        request.cancelAndJoin()

        assertFalse(store.states.value.getValue(1).isCollected)
        assertFalse(store.states.value.getValue(1).isPending)
    }

    private class FakeCollectRemoteDataSource :
        CollectRemoteDataSource {

        val requests = mutableListOf<Pair<Int, Boolean>>()

        private val response =
            CompletableDeferred<UiState<Any>>()

        override suspend fun setCollected(
            articleId: Int,
            isCollected: Boolean
        ): UiState<Any> {
            requests += articleId to isCollected
            return response.await()
        }

        fun complete(result: UiState<Any>) {
            response.complete(result)
        }
    }
}
