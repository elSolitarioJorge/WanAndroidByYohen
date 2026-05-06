package com.ggg.kt.wanandroidbyyohen.data.local

import android.content.Context
import androidx.core.content.edit
import com.ggg.kt.wanandroidbyyohen.app.AppContext
import com.ggg.kt.wanandroidbyyohen.data.model.CoinInfo
import com.ggg.kt.wanandroidbyyohen.data.model.User
import com.ggg.kt.wanandroidbyyohen.data.model.UserInfoData

object UserStore {
    private const val PREF_NAME = "user_store"
    private const val KEY_IS_LOGIN = "is_login"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_COIN_COUNT = "coin_count"
    private const val KEY_LEVEL = "level"
    private const val KEY_RANK = "rank"

    private val sharedPreferences by lazy {
        AppContext.application.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun saveUserInfo(userInfoData: UserInfoData) {
        val user = userInfoData.userInfo
        val coin = userInfoData.coinInfo

        sharedPreferences.edit {
            putBoolean(KEY_IS_LOGIN, true)
            putInt(KEY_USER_ID, user?.id ?: 0)
            putString(KEY_USERNAME, user?.username.orEmpty())
            putString(KEY_NICKNAME, user?.nickname.orEmpty())
            putInt(KEY_COIN_COUNT, coin?.coinCount ?: 0)
            putInt(KEY_LEVEL, coin?.level ?: 0)
            putString(KEY_RANK, coin?.rank.orEmpty())
        }
    }

    fun saveLoginUser(user: User){
        sharedPreferences.edit {
            putBoolean(KEY_IS_LOGIN, true)
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USERNAME, user.username)
            putString(KEY_NICKNAME, user.nickname.orEmpty())
        }
    }

    fun isLogin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGIN, false)
    }

    fun getLocalUserInfo(): UserInfoData? {
        if (!isLogin()) return null

        val userId = sharedPreferences.getInt(KEY_USER_ID, 0)
        val username = sharedPreferences.getString(KEY_USERNAME, "").orEmpty()
        val nickname = sharedPreferences.getString(KEY_NICKNAME, "").orEmpty()
        val coinCount = sharedPreferences.getInt(KEY_COIN_COUNT, 0)
        val level = sharedPreferences.getInt(KEY_LEVEL, 0)
        val rank = sharedPreferences.getString(KEY_RANK, "").orEmpty()

        return UserInfoData(
            userInfo = User(
                id = userId,
                username = username,
                nickname = nickname
            ),
            coinInfo = CoinInfo(
                coinCount = coinCount,
                level = level,
                rank = rank,
                username = username
            )
        )
    }

    fun clear() {
        sharedPreferences.edit(){
            clear()
        }
    }
}
