package com.sam_chordas.android.stockhawk.dependencies;


import android.content.Context;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.StockNetworkService;
import com.sam_chordas.android.stockhawk.rest.HttpNetworkInterceptor;
import com.sam_chordas.android.stockhawk.utils.NetworkUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(NetworkUtils networkUtils) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new HttpNetworkInterceptor(networkUtils));
        okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);
        return okHttpClientBuilder.build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(Context context, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.improved_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    StockNetworkService providesStockNetworkService(Retrofit retrofit) {
        return retrofit.create(StockNetworkService.class);
    }

    @Provides
    @Singleton
    NetworkUtils providesNetworkUtils(Context context) {
        return new NetworkUtils(context);
    }
}
