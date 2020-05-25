package cn.com.williamxia.wipack.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by williamXia on 2017/6/15.
 */

public class CLoadFirmware extends AsyncTask<Object, Object, byte[]> {
    private String fileName;
    private Context context;

    public CLoadFirmware(String strFileName, Context fcontext) {
        this.fileName = strFileName;
        context = fcontext;
    }

    @Override
    protected byte[] doInBackground(Object... objects) {
        //  return new byte[0];
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            int ireadLen = inputStream.available();
            byte[] data = new byte[ireadLen];
            inputStream.read(data);
            inputStream.close();
           // assetManager.close(); must not process or it will crash
            return data;
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return null;

    }


    @Override
    protected void onPostExecute(byte[] bytes) {
        Log.d("haha", "onpostexecute begin....");
        super.onPostExecute(bytes);
//        switch (fileName) {
//            case WiStaticComm.FW_MCU1:
//                XCoreData.getInstance().m_FMCU = bytes;
//                Log.d("haha", "mcu1 length read :" + XCoreData.getInstance().m_FMCU.length);
//                break;
//            case WiStaticComm.FW_MCU2:
//                XCoreData.getInstance().m_FKey = bytes;
//                Log.d("haha", "mcu2 length read :" + XCoreData.getInstance().m_FKey.length);
//                break;
//            case WiStaticComm.FW_DSP1:
//                XCoreData.getInstance().m_FDsp1 = bytes;
//                Log.d("haha", "dsp1 length read :" + XCoreData.getInstance().m_FDsp1.length);
//                break;
//            case WiStaticComm.FW_DSP2:
//                XCoreData.getInstance().m_FDsp2 = bytes;
//                Log.d("haha", "dsp2 length read :" + XCoreData.getInstance().m_FDsp2.length);
//                break;
//        }

    }
}
