package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.robinhood.spark.SparkView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.StockDetailView;
import com.sam_chordas.android.stockhawk.StockHawkApp;
import com.sam_chordas.android.stockhawk.StockService;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.StockProviderService;
import com.sam_chordas.android.stockhawk.data.models.HistoricalQuote;
import com.sam_chordas.android.stockhawk.data.models.HistoricalQuoteDate;
import com.sam_chordas.android.stockhawk.data.models.Quote;

import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

public class StockDetailActivity extends AppCompatActivity implements StockDetailView {
    @Inject
    public Context context;
    @Inject
    public StockProviderService stockProviderService;
    @Inject
    public StockService stockService;

    private StockDetailPresenter stockDetailPresenter;
    private StockGraphAdapter stockGraphAdapter;
    private TextView textError;
    private SparkView sparkGraphView;
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);
        ((StockHawkApp) getApplication()).getStockHawkDependencies().inject(this);
        stockDetailPresenter = new StockDetailPresenter(this, stockProviderService, stockService);
        setTitle(R.string.stock_detail);
        long quoteId = getIntent().getLongExtra(QuoteColumns._ID, 0);
        sparkGraphView = (SparkView) findViewById(R.id.sparkview);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        textError = (TextView) findViewById(R.id.text_error);
        stockGraphAdapter = new StockGraphAdapter(null);
        sparkGraphView.setAdapter(stockGraphAdapter);
        stockDetailPresenter.loadQuoteSymbolForQuoteId(quoteId);
    }

    @Override
    public void beforeLoad() {
        progressWheel.spin();
    }

    @Override
    public void afterLoad() {
        progressWheel.stopSpinning();
    }

    @Override
    public void onQuoteLoaded(Quote quote) throws ParseException {
        stockDetailPresenter.loadOneMonthsHistoricalQuotes(quote.symbol, HistoricalQuoteDate.fromMilliseconds(System.currentTimeMillis()));
    }

    @Override
    public void onQuoteLoadFailed() {
    }

    @Override
    public void onOneMonthsHistoricalQuotesLoaded(List<HistoricalQuote> historicalQuotes) {
        sparkGraphView.setVisibility(SparkView.VISIBLE);
        textError.setVisibility(TextView.GONE);
        stockGraphAdapter.populate(historicalQuotes);
    }

    @Override
    public void onOneMonthsHistoricalQuotesLoadFailure(String error) {
        sparkGraphView.setVisibility(SparkView.GONE);
        textError.setVisibility(TextView.VISIBLE);
        textError.setText(error);
    }
}
