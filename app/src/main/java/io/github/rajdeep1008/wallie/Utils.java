package io.github.rajdeep1008.wallie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class Utils
{
    public static final LayoutParams adjustParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

    public static String formatDecimal(float valueToFormat, int numberOfDecimalPlaces, int scaleFactor, boolean useGroupings)
    {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        // Remove grouping if commas cause errors when parsing to double/float
        numberFormat.setGroupingUsed(useGroupings);

        if (scaleFactor != 0)
            valueToFormat *= Math.pow(1000, scaleFactor);

        // If too large, remove a digits behind decimal
        float tempAmount = valueToFormat;
        while (tempAmount >= 1000 && numberOfDecimalPlaces >= 0)
        {
            numberOfDecimalPlaces--;
            tempAmount /= 10;
        }

        numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
        numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);

        try
        {
            return numberFormat.format(valueToFormat);
        }
        catch (Exception e)
        {
            return "N/A";
        }
    }

    public static String formatWidgetMoney(float amount, CurrencyPair pair, boolean includeCurrencyCode, boolean displayInMilliBtc)
    {
        int numOfDecimals = 3;
        int unitIndex = 0;
        String currencyCode = (includeCurrencyCode) ? " " + pair.counter.getCurrencyCode() : "";

        // If BTC and user wants price in mBTC
        boolean isBTC = pair.base.equals(Currency.BTC);
        if (displayInMilliBtc && isBTC)
        {
            amount /= 1000;

            // adjust altcoin units
            // at least one digit on the left side of decimal point
        }
        else if (!isBTC && amount < 1)
        {
            unitIndex = getUnitIndex(amount);
            if (!includeCurrencyCode) numOfDecimals = 2;
            currencyCode = currencyCode.replace(" ", " " + Constants.METRIC_UNITS[unitIndex]);
            unitIndex++;
        }
        else
        {
            numOfDecimals = 2;
        }

        if (amount >= 1000 && !includeCurrencyCode) numOfDecimals = 0;

        return CurrencyUtils.getSymbol(pair.counter.getCurrencyCode()) + formatDecimal(amount, numOfDecimals, unitIndex, false) + currencyCode;
    }

    // returns the index for the proper units in Contants.METRIC_UNITS
    // is also used to scale the value to match units
    public static int getUnitIndex(float price)
    {
        int unitIndex = -1;
        while (price < 0.5 && unitIndex < 4)
        {
            price *= 1000;
            unitIndex++;
        }
        return unitIndex;
    }

    public static boolean isBetween(float value, float min, float max)
    {
        return ((value >= min) && (value <= max));
    }

    public static String getCurrentTime(Context context)
    {
        Date time = new Date();

        return DateFormat.format("E", time) + " " + DateFormat.getTimeFormat(context).format(time);
    }

    public static String dateFormat(Context ctxt, long date)
    {
        Date dateFormatted = new Date(date);

        return DateFormat.format("MMM dd", dateFormatted) + " @ " + DateFormat.getTimeFormat(ctxt).format(dateFormatted);
    }

    public static void setTextViewParams(TextView tv, BigDecimal value)
    {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

        tv.setText(Utils.formatDecimal(value.floatValue(), 2, 0, true));
        tv.setLayoutParams(params);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public static String formatHashrate(float hashRate)
    {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (hashRate >= 1000000)
            return df.format((hashRate / 1000000)) + " TH/s";
        else if (hashRate >= 1000)
            return df.format((hashRate / 1000)) + " GH/s";
        else
            return df.format((hashRate)) + " MH/s";
    }

    public static Dialog errorDialog(Context context, String msg, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();

        return builder.create();
    }

    public static Dialog errorDialog(Context context, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();

        return builder.create();
    }

    public static boolean isConnected(Context context, boolean bWifiOnly)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(bWifiOnly){
            return (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected());
        } else
            return activeNetwork != null && activeNetwork.isConnected();
    }

    public static void copyDonationAddressToClipboard(Context context, String donationAddress)
    {
        ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText(context.getString(R.string.donationAddress), donationAddress));

        Toast.makeText(context, context.getString(R.string.msg_copiedClipboard), Toast.LENGTH_SHORT).show();
    }
}
