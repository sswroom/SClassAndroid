package org.sswr.android;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class AndroidUtil
{
    public static TextView newLabel(Context ctx, String txt)
    {
        TextView tv = new TextView(ctx);
        tv.setText(txt);
        return tv;
    }

    public static TableRow newTextRow(Context ctx, String vals[])
    {
        TableRow row = new TableRow(ctx);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);
        int i = 0;
        int j = vals.length;
        while (i < j)
        {
            row.addView(AndroidUtil.newLabel(ctx, vals[i]));
            i++;
        }
        return row;
    }

    public static void setSubItem(TableLayout tableLayout, int rowIndex, int colIndex, String text)
    {
        TableRow row = (TableRow)tableLayout.getChildAt(rowIndex);
        if (row == null)
            return;
        TextView tv = (TextView)row.getChildAt(colIndex);
        if (tv == null)
            return;
        tv.setText(text);
    }

    // Requires android.permission.INTERNET
    public static InetAddress getFirstWiFiAddress()
    {
        String WIFI_INTERFACE_NAME = "^w.*[0-9]$";
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null)
                return null;
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.getName().matches(WIFI_INTERFACE_NAME)) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements())
                {
                    InetAddress address = inetAddresses.nextElement();
                    if (!address.isSiteLocalAddress())
                    {
                        continue;
                    }
                    if (address instanceof Inet4Address)
                    {
                        return address;
                    }
                }
            }
        }
        catch (SocketException ex)
        {

        }
        return null;
    }

    // Requires android.permission.ACCESS_NETWORK_STATE
    // Requires android.permission.ACCESS_WIFI_STATE
    public static DhcpInfo getWifiDhcp(Context ctx)
    {
        WifiManager wifii = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (wifii == null)
        {
            return null;
        }
        return wifii.getDhcpInfo();
    }
}
