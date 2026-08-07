package com.ggg.kt.wanandroidbyyohen.common.network

import android.content.Context
import androidx.core.content.edit
import com.ggg.kt.wanandroidbyyohen.app.AppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar : CookieJar {
    private val lock = Any()
    private val sharedPreferences by lazy {
        AppContext.application.getSharedPreferences(
            "cookie_store",
            Context.MODE_PRIVATE
        )
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(lock) {
            val oldCookies = loadUnexpiredCookies(url).toMutableList()
            val now = System.currentTimeMillis()
            cookies.forEach { newCookie ->
                oldCookies.removeAll { oldCookie ->
                    oldCookie.name == newCookie.name &&
                            oldCookie.domain == newCookie.domain &&
                            oldCookie.path == newCookie.path
                }
                // 服务端可能通过一个已过期 Cookie 表示删除，
                // 此时只移除旧 Cookie，不再保存新 Cookie
                if (newCookie.expiresAt > now) {
                    oldCookies.add(newCookie)
                }
            }

            saveCookies(url, oldCookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return synchronized(lock) {
            loadUnexpiredCookies(url).filter { cookie ->
                cookie.matches(url)
            }
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            synchronized(lock) {
                sharedPreferences.edit(commit = true) {
                    clear()
                }
            }
        }
    }

    private fun loadUnexpiredCookies(url: HttpUrl): List<Cookie> {
        val cookieStrings = sharedPreferences
            .getStringSet(url.host, emptySet())
            .orEmpty()
            .toSet()

        val parsedCookies = cookieStrings.mapNotNull { cookieString ->
            Cookie.parse(url, cookieString)
        }

        val now = System.currentTimeMillis()
        val unexpiredCookies = parsedCookies.filter { cookie ->
            cookie.expiresAt > now
        }

        // 只有存在无法解析或已经过期的 Cookie 时才重新写入。
        if (unexpiredCookies.size != cookieStrings.size) {
            saveCookies(url, unexpiredCookies)
        }

        return unexpiredCookies
    }

    private fun saveCookies(url: HttpUrl, cookies: List<Cookie>) {
        val cookieStrings = cookies
            .map { cookie -> cookie.toString() }
            .toSet()

        /*
         * saveCookies 只会由 OkHttp 的 CookieJar 回调间接调用，
         * 当前执行线程是 OkHttp 工作线程。
         */
        sharedPreferences.edit(commit = true) {
            putStringSet(url.host, cookieStrings)
        }
    }
}