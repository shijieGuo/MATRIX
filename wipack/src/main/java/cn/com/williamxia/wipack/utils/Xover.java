package cn.com.williamxia.wipack.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by williamXia on 2017/6/6.
 */

public final class Xover {

    /*****************************************************************
     * @author williamXia
     * created at 2017/8/23 下午4:47
     * byte range :[-128 127]
     ******************************************************************/
    public static int byteToInt(byte ko) {

        return ko & 0xff;
    }

    public static int halfSearch(double[] buf, int length, double mvalue) {

        int mid, start = 0, end = length - 1;
        while (start < end) {
            mid = (start + end) / 2;
            if (buf[mid] < mvalue) {
                start = mid + 1;
            } else if (buf[mid] > mvalue) {
                end = mid - 1;
            } else {
                return mid;
            }
        }
        return start;
    }

    public static void setEditTextStatus(TextView view,boolean sts) {
       if(view instanceof EditText)
       {
           view.setCursorVisible(sts);
           view.setFocusable(sts);
           view.setFocusableInTouchMode(sts);

       }

    }


    /*covert the string to byte array as required length*/
    public static byte[] stringToByte(String str, int maxlen) {
        if(str==null || str.isEmpty())return (new byte[]{0x20});
        String tmp = str.trim();

        if (str.length() > maxlen) {
            tmp = str.substring(0, maxlen - 1);
        }
        return tmp.getBytes(Charset.forName("US-ASCII"));
    }


    public static boolean isASCII(byte b) {
        boolean res = false;
        if (
                (b >= 48 && b <= 57) ||
                        (b >= 41 && b <= 90) ||
                        (b >= 97 && b <= 122) || (b == 46 || b == 32)


                ) {
            res = true;
        }
        return res;

    }

    public static boolean identyInputString(String data,String strRex){
        Pattern pattern = Pattern.compile(strRex);
        Matcher matcher = pattern.matcher(data);
        if(matcher.matches()) {
           return  true;
        }
        return false;
    }


    /*convert the byte to string(ASCII)*/
    public static String byteToString(byte[] srcByte, int len) {
        int lt = (srcByte.length >= len ? len : srcByte.length);
        byte[] m_temp = new byte[lt];
        Arrays.fill(m_temp, LConstaint.ZEROByte);
        System.arraycopy(srcByte, 0, m_temp, 0, lt);
        String doc;
        try {
            doc = new String(m_temp, "US-ASCII");
        } catch (UnsupportedEncodingException ue) {
            doc = "";
        }
        return doc;

    }

    public static int LimitFreq(final int freqindex) {
        int tmpf = 0;
        final int freqMin = 0;
        final int freqMax = 300;
        tmpf = Math.max(freqMin, Math.min(freqMax, freqindex));
        return tmpf;
    }

    public static int bTi(byte mt) {
        return (mt & WiStaticComm.CHByte);
    }

    public static int combineHLByte(byte sHv, byte slv, final int flag) {
        int hv = (sHv & WiStaticComm.CHByte);
        int lv = (slv & WiStaticComm.CHByte);
        int times = (flag == 0 ? WiStaticComm.Len_Small_Time : WiStaticComm.Len_Big_Time);
        return hv * times + lv;
    }


    public static String intToHex2(int a) {
        byte tmp = (byte) a;
        byte H = (byte) ((tmp & 0xf0) >> 4);
        byte L = (byte) (tmp & 0x0f);
        return String.format("%x%x", H, L);
    }

    /***************************************************************
     * judge 判断对象是否为空的函数
     ***************************************************************/
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof CharSequence) {
            return (((CharSequence) obj).length() == 0);
        }
        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            if (obj instanceof Objects[]) {
                Object[] object = (Object[]) obj;
                if (object.length == 0) return true;

                boolean empty = true;
                for (int i = 0; i < object.length; i++) {
                    if (!isNullOrEmpty(object[i])) {
                        empty = false;
                        break;
                    }
                }
                return empty;

            }
        }
        return false;


    }

    public static int measureSize(int widMeasureSpec, int defValue) {
        int result = 0;
        int specMode = View.MeasureSpec.getMode(widMeasureSpec);
        int specSize = View.MeasureSpec.getSize(widMeasureSpec);
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
            qDebug.qLog("specMode exactly ,specSize is  " + result);
        } else {
            result = defValue;
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }

        }
        return result;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static void sendBroadCastMsg(Context context, String strAction, String msgKey, Serializable msgValue) {
        if (context != null) {
            Intent intent = new Intent(strAction);
            intent.putExtra(msgKey, msgValue);
            context.sendBroadcast(intent);
        }

    }


    public static void Sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
