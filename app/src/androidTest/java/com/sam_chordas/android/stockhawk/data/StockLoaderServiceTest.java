package com.sam_chordas.android.stockhawk.data;

import android.content.ContentValues;
import android.content.Context;
import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static com.sam_chordas.android.stockhawk.data.StockLoaderService.QuoteSymbolLoaderCallback;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class StockLoaderServiceTest extends AndroidTestCase {
    @Before
    public void setup() {
        resetDatabase();
    }

    @After
    public void tearDown() {
        resetDatabase();
    }

    @Test
    public void shouldNotLoadQuoteNameForStockWhichDoesNotExistLocally() throws InterruptedException {
        StockLoaderService stockLoaderService = new StockLoaderService(getContext());

        QuoteSymbolLoaderCallback quoteSymbolLoaderCallback = Mockito.mock(QuoteSymbolLoaderCallback.class);
        stockLoaderService.loadQuoteSymbolForQuoteId(1, quoteSymbolLoaderCallback);
        Thread.sleep(1000);
        verify(quoteSymbolLoaderCallback).onQuoteSymbolLoadFailed();
        verifyNoMoreInteractions(quoteSymbolLoaderCallback);
    }

    @Test
    public void shouldLoadQuoteSymbolWhenCorrespondingQuoteExistsLocally() throws InterruptedException {
        StockLoaderService stockLoaderService = new StockLoaderService(getContext());
        ContentValues quoteContentValues = new ContentValues();
        quoteContentValues.put(QuoteColumns.SYMBOL, "YHOO");
        quoteContentValues.put(QuoteColumns.PERCENT_CHANGE, "20");
        quoteContentValues.put(QuoteColumns.CHANGE, "10");
        quoteContentValues.put(QuoteColumns.BIDPRICE, "10");
        quoteContentValues.put(QuoteColumns.CREATED, "20-10-2016");
        quoteContentValues.put(QuoteColumns.ISUP, 1);
        quoteContentValues.put(QuoteColumns.ISCURRENT, 1);
        getContext().getContentResolver().insert(QuoteProvider.Quotes.CONTENT_URI, quoteContentValues);

        QuoteSymbolLoaderCallback quoteSymbolLoaderCallback = Mockito.mock(QuoteSymbolLoaderCallback.class);
        stockLoaderService.loadQuoteSymbolForQuoteId(1, quoteSymbolLoaderCallback);
        Thread.sleep(1000);
        verify(quoteSymbolLoaderCallback).onQuoteSymbolLoaded("YHOO");
        verifyNoMoreInteractions(quoteSymbolLoaderCallback);
    }

    private void resetDatabase() {
        getContext().deleteDatabase("quoteDatabase.db");
        getContext().openOrCreateDatabase("quoteDatabase.db", Context.MODE_PRIVATE, null);
    }
}
