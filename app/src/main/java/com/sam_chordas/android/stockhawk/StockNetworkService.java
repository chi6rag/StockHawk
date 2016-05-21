package com.sam_chordas.android.stockhawk;

import com.sam_chordas.android.stockhawk.data.models.HistoricalQuotesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockNetworkService {
    @GET("yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20=%20%22YHOO%22%20and%20startDate%20=%20%222016-03-11%22%20and%20endDate%20=%20%222016-05-15%22&format=json&env=http://datatables.org/alltables.env")
    Call<HistoricalQuotesResponse> getHistoricalQuotes(@Query("symbol") String symbol);
}
