package com.ggg.kt.wanandroidbyyohen.common.network

import android.content.Context
import androidx.core.content.edit
import com.ggg.kt.wanandroidbyyohen.app.AppContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar : CookieJar {
    private val sharedPreferences by lazy {
        AppContext.application.getSharedPreferences(
            "cookie_store",
            Context.MODE_PRIVATE
        )
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val oldCookies = loadForRequest(url).toMutableList()

        cookies.forEach { newCookie ->
            oldCookies.removeAll { oldCookie ->
                oldCookie.name == newCookie.name &&
                        oldCookie.domain == newCookie.domain &&
                        oldCookie.path == newCookie.path
            }
            oldCookies.add(newCookie)
        }

        saveCookies(url, oldCookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStrings = sharedPreferences
            .getStringSet(url.host, emptySet())
            .orEmpty()
        val validCookies = cookieStrings.mapNotNull { cookieString ->
            Cookie.parse(url, cookieString)
        }.filter { cookie ->
            cookie.expiresAt > System.currentTimeMillis()
        }

        saveCookies(url, validCookies)

        return validCookies
    }

    fun clear() {
        sharedPreferences.edit() {
            clear()
        }
    }

    private fun saveCookies(url: HttpUrl, cookies: List<Cookie>) {
        val cookieStrings = cookies.map { cookie ->
            cookie.toString()
        }.toSet()

        sharedPreferences.edit() {
            putStringSet(url.host, cookieStrings)
        }
    }
}