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
    private val locallyModifiedIds = mutableSetOf<Int>()

    fun seed(serverStates: Map<Int, Boolean>) {
        synchronized(lock) {
            val updatedStates = _states.value.toMutableMap()
            serverStates.forEach { (articleId, isCollected) ->
                val currentState = updatedStates[articleId]
                when {
                    currentState?.isPending == true -> Unit

                    articleId in locallyModifiedIds -> {
                        // 服务器已经返回与本地操作一致的结果，
                        // 说明同步完成，可以解除本地保护
                        if (currentState?.isCollected == isCollected) {
                            locallyModifiedIds.remove(articleId)

                            updatedStates[articleId] = ArticleCollectState(isCollected = isCollected)
                        }
                    }

                    else -> {
                        updatedStates[articleId] = ArticleCollectState(isCollected = isCollected)
                    }
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
            locallyModifiedIds += articleId

            val targetCollected = !currentState.isCollected

            _states.value += (articleId to ArticleCollectState(
                isCollected = targetCollected,
                isPending = true
            ))

            return targetCollected
        }
    }

    fun beginSet(
        articleId: Int,
        fallbackCollected: Boolean,
        targetCollected: Boolean
    ): Boolean {
        synchronized(lock) {
            val currentState = _states.value[articleId]
                ?: ArticleCollectState(isCollected = fallbackCollected)

            if (currentState.isPending) {
                return false
            }

            rollbackValues[articleId] = currentState.isCollected
            locallyModifiedIds += articleId

            _states.value += (articleId to ArticleCollectState(
                isCollected = targetCollected,
                isPending = true
            ))
            return true
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
            locallyModifiedIds.remove(articleId)
            val previousCollected =
                rollbackValues.remove(articleId) ?: return

            _states.value += (articleId to ArticleCollectState(
                isCollected = previousCollected,
                isPending = false
            ))
        }
    }

    fun resetForSignedOutUser() {
        synchronized(lock) {
            rollbackValues.clear()
            locallyModifiedIds.clear()

            _states.value = _states.value.mapValues {
                ArticleCollectState(
                    isCollected = false,
                    isPending = false
                )
            }
        }
    }

    fun resetForAuthenticatedUser(
        collectedArticleIds: Collection<Int>
    ) {
        synchronized(lock) {
            rollbackValues.clear()
            locallyModifiedIds.clear()

            val collectedIdSet = collectedArticleIds.toSet()

            val allArticleIds = _states.value.keys + collectedIdSet

            _states.value =
                allArticleIds.associateWith { articleId ->
                    ArticleCollectState(
                        isCollected = articleId in collectedIdSet,
                        isPending = false
                    )
                }
        }
    }

    fun clear() {
        synchronized(lock) {
            rollbackValues.clear()
            locallyModifiedIds.clear()
            _states.value = emptyMap()
        }
    }
}
