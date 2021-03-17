package com.example.rocketreserver

import android.content.Context
import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloExperimental
import com.apollographql.apollo.network.http.ApolloHttpNetworkTransport
import com.apollographql.apollo.network.ws.ApolloWebSocketFactory
import com.apollographql.apollo.network.ws.ApolloWebSocketNetworkTransport
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

@OptIn(ApolloExperimental::class, ExperimentalCoroutinesApi::class)
private var instance: ApolloClient? = null

@OptIn(ApolloExperimental::class, ExperimentalCoroutinesApi::class)
fun apolloClient(context: Context): ApolloClient {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "Only the main thread can get the apolloClient instance"
    }

    if (instance != null) {
        return instance!!
    }


    instance = ApolloClient(
        networkTransport = ApolloHttpNetworkTransport(
            serverUrl = "https://apollo-fullstack-tutorial.herokuapp.com/graphql",
            headers = mapOf("Authorization" to (User.getToken(context) ?: ""))
        ),
        subscriptionNetworkTransport = ApolloWebSocketNetworkTransport(
            webSocketFactory = ApolloWebSocketFactory("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
        )
    )

    return instance!!
}

