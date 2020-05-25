package cn.com.williamxia.wipack.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by williamXia on 2017/6/22.
 */

public class CIOBeanOpreation {


    public static boolean isSDCardExist() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static CIOBean readBean_fromStoreageCard(String filename, String sd_path) {
        boolean res = true;
        CIOBean bean = null;
        if (isSDCardExist()) {
            String sdcard = sd_path;
            File saveDir = new File(sdcard);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            if (!saveDir.exists()) {
                return null;
            }
            File targetFile = new File(saveDir, filename);
            try {
                FileInputStream input = new FileInputStream(targetFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(input);
                bean = (CIOBean) objectInputStream.readObject();
                objectInputStream.close();

            } catch (ClassNotFoundException ce) {
                ce.printStackTrace();
            } catch (IOException io) {

                io.printStackTrace();
            }
            return bean;

        } else {
            return null;
        }

    }

    //FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
    public static boolean writeBeanToSDcard(CIOBean bean, String sd_path) {
        if (isSDCardExist()) {
            // qDebug.qLog("SDCard exists really..");
            String sdCardDir = sd_path;
            String filename = bean.getFileName();
            File savedir = new File(sdCardDir);
            if (!savedir.exists()) {
                //qDebug.qLog("file sdk write", "sdkdir is error  " + sdCardDir);
                savedir.mkdirs();
            }

            if (!savedir.exists()) {
                qDebug.qLog("file path does'nt exist..");
                return false;
            }

            File targetFile = new File(savedir, filename);
            //
            try {
                FileOutputStream out = new FileOutputStream(targetFile);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                //------------------------------
                if (out != null) {
                    objectOutputStream.writeObject(bean);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    qDebug.qLog("write bean object true..");
                }

            } catch (FileNotFoundException fe) {
                //TODO Auto-generated catch block
                fe.printStackTrace();
                return false;


            } catch (IOException e) {
                // TODO: 2017/6/22
                e.printStackTrace();
                return false;
            }
            return true;

        } else {
            qDebug.qLog("the device has not sdk.");
            return false;
        }

    }

    public static byte[] readByte_fromStoreageCard(String filename, String sd_path) {
        boolean res = true;
        if (isSDCardExist()) {
            String sdcard = sd_path;
            File saveDir = new File(sdcard);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            if (!saveDir.exists()) {
                return null;
            }
            File targetFile = new File(saveDir, filename);
            try {
                FileInputStream input = new FileInputStream(targetFile);
                try {

                    int len = input.available();
                    byte[] mbuffer = new byte[len];
                    input.read(mbuffer);
                    input.close();
                    return mbuffer;
                } catch (IOException ie) {
                    ie.printStackTrace();
                    return null;
                }


            } catch (FileNotFoundException fe) {
                fe.printStackTrace();
                return null;
            }


        } else {
            return null;
        }

    }


    //FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
    public static boolean writeByteToSDcard(CIOBean bean, String sd_path) {
        if (isSDCardExist()) {
            String sdCardDir = sd_path;
            String filename = bean.getFileName();
            File savedir = new File(sdCardDir);
            if (!savedir.exists()) {
                // Log.d("file sdk write", "sdkdir is error  " + sdCardDir);
                savedir.mkdirs();
            }
            if (!savedir.exists())
                return false;

            File targetFile = new File(savedir, filename);
            //
            try {
                FileOutputStream out = new FileOutputStream(targetFile);
                //------------------------------
                if (out != null) {
                    if (bean.getIsNeedHead())
                        out.write(bean.getByteHeader());
                    out.write(bean.getBeanData());
                    out.flush();
                    out.close();
                }
                return true;
            } catch (FileNotFoundException fe) {
                //TODO Auto-generated catch block
                fe.printStackTrace();
                return false;

            } catch (IOException e) {
                // TODO: 2017/6/22
                e.printStackTrace();
                return false;
            }


        } else {
            return false;
        }

    }

    public static boolean writeListBeanToSDcard(String filename, String sheader, List<CIOBean> beans, String sd_path) {
        if (isSDCardExist()) {
            String sdCardDir = sd_path;
            File savedir = new File(sdCardDir);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
            if (!savedir.exists())
                return false;
            File targetFile = new File(savedir, filename);
            //
            try {
                FileOutputStream out = new FileOutputStream(targetFile);
                if (out != null) {

                    byte[] bheader = Xover.stringToByte(sheader, 16);
                    out.write(bheader);
                    for (int i = 0; i < beans.size(); i++) {
                        out.write(beans.get(i).getBeanData());
                    }
                    out.flush();
                    out.close();
                }
                return true;
            } catch (FileNotFoundException fe) {
                //TODO Auto-generated catch block
                fe.printStackTrace();
                return false;

            } catch (IOException e) {
                // TODO: 2017/6/22
                e.printStackTrace();
                return false;
            }


        } else {
            return false;
        }

    }
}
