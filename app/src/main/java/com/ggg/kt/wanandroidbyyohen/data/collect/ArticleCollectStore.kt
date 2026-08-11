package com.ggg.kt.wanandroidbyyohen.data.collect

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArticleCollectStore {
    private val lock = Any()

    private val _states =
        MutableStateFlow<Map<Int, ArticleCollectState>>(emptyMap())
    val states: StateFlow<Map<Int, ArticleCollectState>> =
        _states.asStateFlow()

    private val rollbackValues = mutableMapOf<Int, Boolean>()

    fun seed(serverStates: Map<Int, Boolean>) {
        synchronized(lock) {
            val updatedStates = _states.value.toMutableMap()
            serverStates.forEach { (articleId, isCollected) ->
                val currentState = updatedStates[articleId]
                // 网络请求期间，不允许列表刷新覆盖乐观更新状态
                if (currentState?.isPending != true) {
                    updatedStates[articleId] = ArticleCollectState(
                        isCollected = isCollected
                    )
                }
            }
            _states.value = updatedStates
        }
    }

    fun beginToggle(
        articleId: Int,
        fallbackCollected: Boolean
    ): Boolean? {
        synchronized(lock) {
            val currentState = _states.value[articleId]
                ?: ArticleCollectState(isCollected = fallbackCollected)

            // 同一文章已有请求时，拒绝重复操作
            if (currentState.isPending) {
                return null
            }

            rollbackValues[articleId] = currentState.isCollected

            val targetCollected = !currentState.isCollected

            _states.value += (articleId to ArticleCollectState(
                isCollected = targetCollected,
                isPending = true
            ))

            return targetCollected
        }
    }

    fun confirm(articleId: Int) {
        synchronized(lock) {
            val currentState = _states.value[articleId] ?: return

            rollbackValues.remove(articleId)

            _states.value += (articleId to currentState.copy(isPending = false))
        }
    }

    fun rollback(articleId: Int) {
        synchronized(lock) {
            val previousCollected =
                rollbackValues.remove(articleId) ?: return

            _states.value += (articleId to ArticleCollectState(
                isCollected = previousCollected,
                isPending = false
            ))
        }
    }

    fun clear() {
        synchronized(lock) {
            rollbackValues.clear()
            _states.value = emptyMap()
        }
    }
}
