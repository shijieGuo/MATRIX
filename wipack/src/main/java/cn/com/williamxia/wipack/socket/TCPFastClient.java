package cn.com.williamxia.wipack.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.williamxia.wipack.utils.LConstaint;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/6/10.
 * 思路：每隔2秒自动断开一次，同时也关闭掉socket，等待下一次的socket来进行，而每次设置最大的读延时为3秒，分别通过线程的方式进行处理每一个tcp连接，
 * 读完之后
 * 虽然是在线程中进行，但是由于也是每次连接在循环中，但关键是socket是内建的
 */

public class TCPFastClient {

    private static final String logTag = "TCPClient";
    private static TCPFastClient _tcpFClient = null;

    public boolean isErrorInternet = false;
    private Socket _cSock;
    /******
     * Listener event below
     ******/
    private TFastClientEvent fastClientEvent;

    public byte[] m_ackCommand;
    public int TCPFlastclientTag = 99;
    private Timer taskTimer = new Timer();


    /***************************************************************
     *
     ***************************************************************/


    public void setCommandACK(byte[] mackPack) {
        this.m_ackCommand = mackPack;
    }


    public void setFastClientEvent(TFastClientEvent fastClientEvent) {
        this.fastClientEvent = fastClientEvent;
    }


    /***************************************************************
     *
     ***************************************************************/
    public TCPFastClient(Socket sock, byte[] command) {
        try {
            isErrorInternet = false;
            _cSock = sock;
            _cSock.setReuseAddress(true);
            m_ackCommand = command;


        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    class MyTask extends TimerTask {
        @Override
        public void run() {
            isErrorInternet = true;
        }
    }

    public void initialTimer() {

        taskTimer = new Timer();
        taskTimer.schedule(new MyTask(), 3000);

    }

    public void stopTimer() {
        if (taskTimer != null) {
            taskTimer.cancel();
            taskTimer = null;
            qDebug.qLog("stop timer over.........");
        }
    }


    /*
     set socket read timeout time  miliseconds
    * */
    public void setSockReadTimeout(int timeout) {
        try {
            _cSock.setSoTimeout(timeout);
        } catch (Exception e) {

        }
    }

    public void clearSock() {
        if (_cSock != null)
            _cSock = null;

    }

    private boolean isSockConnect() {
        return (_cSock != null && _cSock.isConnected() && !_cSock.isClosed());
    }


    /******
     * 接受数据部分
     ******/
    private void readBytes() {
        int len = 0;
        byte[] buffer = new byte[8192];
        try {
            while (!isErrorInternet) {

                DataInputStream inputStream = new DataInputStream(_cSock.getInputStream());
                Arrays.fill(buffer, LConstaint.ZEROByte);
                len = inputStream.read(buffer);
                //qDebug.qLog("cocoSock", "tcpfast readbyte len " + len);
                if (fastClientEvent != null && len > 0) {
                    fastClientEvent.onFastReceive(_cSock, buffer, len, TCPFlastclientTag);
                }
                // qDebug.qLog("cocoSock", "recev byte here..tcpfast....loopr........");
            }
        } catch (Exception e) { //java.net.SocketTimeoutException
            isErrorInternet = true;
//            qDebug.qLog("cocoSock", e.toString());
        } finally {
            isErrorInternet = true;
           // qDebug.qLog("cocoSock", "tcpfast go to finally at readbyte fun");
            stopTimer();
            if (_cSock != null) {
                try {
                    _cSock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /******
     * receive thread runnable
     ******/
    class ReceiveThread implements Runnable {
        @Override
        public void run() {
            readBytes();
        }
    }

    /*
      * write byte with socket with return value type
      * */
    private void writeBytes(byte[] mdata) {
        if (mdata != null) {
            try {
                DataOutputStream dOut = new DataOutputStream(_cSock.getOutputStream());
                dOut.write(mdata);
                dOut.flush();
               // qDebug.printBytes("fastc tcp socket send data: ", mdata);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // isErrorInternet = true;
            }
        }

    }

    class SendBytesThread implements Runnable {

        @Override
        public void run() {

            writeBytes(m_ackCommand);
            // qDebug.fprintBytes("cocosock","tcpfast send checkdata ",m_ackCommand);

        }
    }

    public boolean isConnected() {
        // return (!(!isKeepConnect || !isSockConnect()));
        return (!(isErrorInternet || !isSockConnect()));
    }

    /***************************************************************
     * start Receive,after set the listenner for this
     ***************************************************************/
    public void startListener() {

        isErrorInternet = false;
        if (fastClientEvent != null) {
            fastClientEvent.onFastConnected(_cSock, this.isConnected(), TCPFlastclientTag);
        }
        new Thread(new ReceiveThread()).start();//begin run Receive thread
        new Thread(new SendBytesThread()).start();
        initialTimer();

    }

}