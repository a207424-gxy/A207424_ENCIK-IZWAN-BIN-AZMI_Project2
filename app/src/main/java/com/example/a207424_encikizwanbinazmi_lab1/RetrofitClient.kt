package com.example.a207424_encikizwanbinazmi_lab1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. API 响应的数据模型（用于解析服务器返回的 JSON 数据）
data class EcoNotice(
    val title: String,
    val description: String
)

// 2. 定义请求接口
interface EcoApiService {
    // 💡 答辩技巧：如果老师让你修改 API 的路由/终点（Endpoint），就是修改下面 @GET 括号里的字符串
    @GET("v3/b/example-placeholder")
    suspend fun getLatestEcoTip(): List<EcoNotice>
}

// 3. 创建单例客户端
object RetrofitClient {
    // 💡 答辩技巧：如果老师让你修改服务器的主机地址（Base URL），就是修改这里
    private const val BASE_URL = "https://api.jsonbin.io/"

    val instance: EcoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EcoApiService::class.java)
    }
}