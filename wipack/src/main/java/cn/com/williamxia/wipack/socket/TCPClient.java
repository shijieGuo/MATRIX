package cn.com.williamxia.wipack.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.williamxia.wipack.utils.LConstaint;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/6/5.
 */

public class TCPClient {

    private static final String logTag = "TCPClient";
    private static TCPClient _tcpClient = null;

    private Socket _cSock;
    /******
     * Listener event below
     ******/

    private TcpClientEvent tcpClientEventEvent;
    public static final int DefaultConPort = 5000;

    public boolean isSendReady = false;

    private Object parentContextObj;
    public static int clientTag = 0;


    public void setTcpClientEventEvent(TcpClientEvent tcpClientEventEvent) {
        this.tcpClientEventEvent = tcpClientEventEvent;
    }

    /***************************************************************
     *
     ***************************************************************/
    public TCPClient setupTCPClient(Socket sock, Object obj) {
        try {
            _cSock = sock;
            _cSock.setReuseAddress(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        isErrorInternet = false;
        isKeepConnect = false;
        parentContextObj = obj;
        return this;

    }

    public String gConAddr() {
        if (_cSock != null) {
            String strIP = _cSock.getInetAddress().toString();
            return strIP.substring(1);
        } else return null;

    }

    public TCPClient() {

    }

    /*
    public TCPClient(String conIP, int conPort) {
        try {
            isErrorInternet = false;
            _cSock = new Socket(conIP, conPort);
            _cSock.setSoTimeout(10000);//set readtimeout
            _cSock.setKeepAlive(true);
            _conIP = conIP;
            _conPort = conPort;
            isKeepConnect = true;

        } catch (IOException e) {

            isKeepConnect = false;
            isErrorInternet = true;
            qDebug.qLog("cocoSock", e.toString());
        }
    }
    */


    public boolean isConnected() {
        // return (!(!isKeepConnect || !isSockConnect()));
        return (!(!isKeepConnect || !isSockConnect()));
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
        socketClose(_cSock);
        if (_cSock != null)
            _cSock = null;

    }

    private boolean isSockConnect() {
        return (_cSock != null && _cSock.isConnected() && !_cSock.isClosed());
    }

    public static boolean isKeepConnect = false;

    public void socketClose(Socket socket) {
        try {
            isErrorInternet = true;
            isKeepConnect = false;
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        } catch (IOException ioe) {
            isKeepConnect = false;
        } catch (Exception e) {
            qDebug.qLog("Close socket Exception.");
        }
    }


    public boolean isErrorInternet = false;

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
                if (tcpClientEventEvent != null && len > 0) {
                    tcpClientEventEvent.onTcpReceive(_cSock, buffer, len, clientTag);
                }
                // qDebug.qLog("cocoSock", "recev byte here......loopr........");
            }
        } catch (SocketTimeoutException ste) {
            qDebug.qLog("socket timeout execption..");
            ste.printStackTrace();
            isErrorInternet = true;
            //  _cSock.

        } catch (SocketException e) {
            e.printStackTrace();
            isErrorInternet = true;
        } catch (Exception e) {
            qDebug.qLog("read tcp con byte error ,so stop..");
            e.printStackTrace();
            //  isErrorInternet = true;
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

    /***************************************************************
     * export to dipatch function below................
     ***************************************************************/
    public static TCPClient getInstance() {
        if (_tcpClient == null) {
            _tcpClient = new TCPClient();
        }
        return _tcpClient;
    }

    /*
      * write byte with socket with return value type
      * */
    private void writeBytes(byte[] mdata) {
        if (!isConnected()) return;
        if (mdata != null) {
            try {
                DataOutputStream dOut = new DataOutputStream(_cSock.getOutputStream());
                dOut.write(mdata);
                dOut.flush();
                // if(mdata[10]!=0x52) //no-ack
               // qDebug.printBytes("socket send data: ", mdata);
            } catch (SocketException e) {
                isErrorInternet = true;
            } catch (IOException e) {
                 isErrorInternet = true;
            }
        }

    }

    public void sendBytes(final byte[] sdata) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                qDebug.qLog("cocosock");
                writeBytes(sdata);

            }
        }).start();

    }

    /***************************************************************
     * start Receive
     ***************************************************************/
    public void startListener() {

        if (tcpClientEventEvent != null) {
            tcpClientEventEvent.onTcpConnected(_cSock, this.isConnected(), clientTag);
        }
        new Thread(new ReceiveThread()).start();//begin run Receive thread

    }

    /***************************************************************
     * stop socket connect event
     ***************************************************************/
    public void stopConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                socketClose(_cSock);
                stopTimer();
                isExitThread = true;

                if (tcpClientEventEvent != null) {
                    //  _cSock.getRemoteSocketAddress().toString()

                    tcpClientEventEvent.onTcpDisconnected(_cSock, false, clientTag);
                }

            }
        }).start();

    }

    private byte[] command;

    public byte[] getCommand() {
        return command;
    }

    /***************************************************************
     *
     ***************************************************************/
    private byte[] commandACK;

    public byte[] getCommandACK() {
        return commandACK;
    }

    public void setCommandACK(byte[] mackPack) {
        this.commandACK = mackPack;
    }


    public void notifySendCommand(byte[] fdata) {
        if (fdata == null || !isConnected()) return;
        this.command = fdata;
        isSendReady = true; //set is true for ready to send data
        if (parentContextObj != null) {
            synchronized (parentContextObj) {
                parentContextObj.notify();
            }
        } else {
            qDebug.qLog("you dispatch notify ,bu the parencontexobj is nulll");
        }

    }

    private Timer timer;

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            qDebug.qLog("stop timer over.........");
        }
    }

    public void initialTimer() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TaskForStopConnectWhenError(), 0, LConstaint.HeartTimeDelay);

    }

    public void runWithThread_Send() {
        initialTimer();
        //
        isExitThread = false;
        isErrorInternet = false;
        //
        new Thread(new SendThread()).start();
         new Thread(new HearTimeTask()).start();
    }


    class TaskForStopConnectWhenError extends TimerTask {
        @Override
        public void run() {
            if (isErrorInternet) {
                qDebug.qLog("check the internet error, so run the thread to stop connection by timer task");
                stopTimer();
                stopConnect();
                isExitThread = true;//exit all thread loop
            }

            //  qDebug.qLog("xtime", "here,timer task run.....");

        }
    }

    /***************************************************************
     * for heart loop check remote device is on or not connection
     ***************************************************************/
    class HearTimeTask implements Runnable {
        @Override
        public void run() {
            while (!isExitThread) {
                try {
                    if (!isErrorInternet) {
                        if (!Xover.isNullOrEmpty(getCommandACK())) {
                            writeBytes(getCommandACK());
                            Thread.sleep(LConstaint.HeartTimeDelay);
                        }


                    }
                } catch (Exception e) {
                    isKeepConnect = false;
                    isExitThread = true;
                    isErrorInternet = true;
                }

            }
        }
    }

    public static boolean isExitThread = false;
    public static final int SendDelay = 80;

    class SendThread implements Runnable {
        @Override
        public void run() {
            while (!isExitThread) {
                try {
                    if (isConnected() && !isErrorInternet) {
                        if (isSendReady) {
                            byte[] data = getCommand();
                            if (data != null)
                                writeBytes(data);

                            isSendReady = false;
                            Thread.sleep(SendDelay);
                        } else {
                            synchronized (parentContextObj) {
                                parentContextObj.wait();
                            }
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    qDebug.qLog("interrupted exception when paraent contexobj wait");
                }

            }
        }
    }


}
