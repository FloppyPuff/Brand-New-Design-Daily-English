package me.fujunhao.engstudy.data.remote

import me.fujunhao.engstudy.data.local.SessionStore
import java.io.IOException
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

const val MOBILE_CLIENT = "daily-english-mobile"

class TokenInterceptor(private val session: SessionStore) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val token = runBlocking { session.token() }
    val request = chain.request().newBuilder()
      .header("x-client", MOBILE_CLIENT)
      .header("accept", "application/json")
      .apply { if (!token.isNullOrBlank()) header("Authorization", "Bearer $token") }
      .build()
    return chain.proceed(request)
  }
}
