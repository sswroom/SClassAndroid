package org.sswr.android;

import android.content.Context;
import android.view.ViewGroup;
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

}
