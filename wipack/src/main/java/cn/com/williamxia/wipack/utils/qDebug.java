package cn.com.williamxia.wipack.utils;

import android.util.Log;

import java.util.List;

/**
 * Created by williamXia on 2017/6/6.
 */

public class qDebug {

    private static final String MYLog = "William";
    private static boolean needDebug = true;

    public static void qLog(String str) {
        if (needDebug)
            Log.d(MYLog, str);
    }

    public static void qLog(String Tag, String str) {
        if (needDebug)
            Log.d(Tag, str);
    }

    public static void qLog(String Tag, String preStr, String str) {
        if (needDebug)
            qLog(Tag, preStr + " " + str);
    }

    /***************************************************************
     * print byte data to hex string
     ***************************************************************/
    public static void printBytes(String strPre, byte[] mdata) {
        if (Xover.isNullOrEmpty(mdata)) {
            qLog("the data array is null");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(strPre);
        buffer.append(" length: " + mdata.length + "  ");
        for (int i = 0; i < mdata.length; i++) {

            buffer.append(Xover.intToHex2(mdata[i]));
            if (i < mdata.length - 1)
                buffer.append("-");

        }
        qLog(buffer.toString());
    }

    /***************************************************************
     * print byte data to hex string
     ***************************************************************/
    public static void fprintBytes(String tag, String strPre, byte[] mdata) {

        if (Xover.isNullOrEmpty(mdata)) {
            qLog("the data array is null");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(strPre);
        buffer.append(" length: " + mdata.length + "  ");
        for (int i = 0; i < mdata.length; i++) {

            buffer.append(Xover.intToHex2(mdata[i]).toUpperCase());
            if (i < mdata.length - 1)
                buffer.append("-");
        }
        qLog(tag, buffer.toString());
    }

    public static void fprintBytes(String tag, String strPre, List<Byte> arrayList) {
        if (Xover.isNullOrEmpty(arrayList)) {
            qLog("the data array is null");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(strPre);
        buffer.append(" length: " + arrayList.size() + "  ");
        for (int i = 0; i < arrayList.size(); i++) {

            buffer.append(Xover.intToHex2(arrayList.get(i)).toUpperCase());
            if (i < arrayList.size() - 1)
                buffer.append("-");
        }
        qLog(tag, buffer.toString());

    }

}
