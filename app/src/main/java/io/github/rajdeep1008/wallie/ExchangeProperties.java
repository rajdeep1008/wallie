package io.github.rajdeep1008.wallie;

import android.content.Context;
import android.util.Log;

public class ExchangeProperties
{
    private final String exchange_name;
    private final String class_name;
    private final String main_currency;
    private String identifier;
    private final String shortName;
    private final boolean supportsTicker;
    private final boolean supportsOrderbook;
    private final boolean supportsTrades;
    private Context context = null;

    public ExchangeProperties(Context context, String exchangeName)
    {
        // ToLower and Remove Exchange to keep compatibility with previous indexing system
        identifier = exchangeName.toLowerCase().replace("exchange", "").replaceAll("[ .-]", "");
        int resId = context.getResources().getIdentifier(identifier, "array", context.getPackageName());
        String[] exchangeProperties = context.getResources().getStringArray(resId);

        this.context = context;
        exchange_name = exchangeProperties[ItemType.EXCHANGE_NAME];
        class_name = exchangeProperties[ItemType.CLASS_NAME];
        main_currency = exchangeProperties[ItemType.DEFAULT_CURRENCY_PAIR];
        shortName = exchangeProperties[ItemType.SHORT_NAME];
        supportsTicker = exchangeProperties[ItemType.TICKER_ENABLED].equals("1");
        supportsOrderbook = exchangeProperties[ItemType.ORDERBOOK_ENABLED].equals("1");
        supportsTrades = exchangeProperties[ItemType.TRADES_ENABLED].equals("1");
    }

    public String getExchangeName()
    {
        return exchange_name;
    }

    public String getClassName()
    {
        return class_name;
    }

    public String getDefaultCurrency()
    {
        return main_currency;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getShortName()
    {
        return shortName;
    }

    public Boolean supportsTicker()
    {
        return supportsTicker;
    }

    public Boolean supportsTrades()
    {
        return supportsTrades;
    }

    public Boolean supportsOrderbook()
    {
        return supportsOrderbook;
    }

    public Boolean supportsServiceType(int serviceType){

        if(serviceType == ItemType.TICKER_ENABLED)
            return supportsTicker();
        else if (serviceType == ItemType.ORDERBOOK_ENABLED)
            return supportsOrderbook();
        else if(serviceType == ItemType.TRADES_ENABLED)
            return supportsTrades();

        return false;
    }

    public String[] getCurrencies()
    {
        int resId = context.getResources().getIdentifier(identifier + "_currencies", "array", context.getPackageName());

        return (resId != 0) ? context.getResources().getStringArray(resId) : null;
    }

    public static class ItemType
    {
        public static final int EXCHANGE_NAME = 0;
        public static final int CLASS_NAME = 1;
        public static final int DEFAULT_CURRENCY_PAIR = 2;
        public static final int SHORT_NAME = 3;
        public static final int TICKER_ENABLED = 4;
        public static final int ORDERBOOK_ENABLED = 5;
        public static final int TRADES_ENABLED = 6;
        public static final int IDENTIFIER = 7;
    }
}
