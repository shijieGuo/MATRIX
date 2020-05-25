
package comcontrol;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

import Datas.DStatic;
import Events.ConnectEvent;
import Events.ReceiveCommandEvent;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.qDebug;

import static Datas.LCommand.Len_Big_Times;


public class UdpLongUtil extends BaseConnet {
    private static UdpLongUtil udpSender;
    private InetAddress remoteAddress;
    private MulticastSocket socket;
    private String curAddr="";
    public SendThread sendThread;
    public ReceiveThread receiveThread;
    public HeartTimeTask heartTimeTask;
    public Object lock;
    private boolean isChangeCMD;
    private byte[] command;
    private int otherCount,navCount,devNum;
    public boolean CONN;
    private HashMap Datas;
    private int DataNUm;
    private int DataLen;
    private UdpLongUtil() {
        sendThread = new SendThread();
        receiveThread = new ReceiveThread();
        heartTimeTask = new HeartTimeTask();
    }

    public void init(Object lock){
        try {
                this.lock = lock;
                this.remoteAddress = InetAddress.getByName(IGMP_SER_IP);
                this.Datas=new HashMap<String, byte[]>();
                socket = new MulticastSocket(IGMP_PORT);
                socket.setLoopbackMode(false);
                socket.joinGroup(remoteAddress);
                socket.setSoTimeout(9000);
                socket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
        } catch (Exception e) {
//            VDebug.println("init socket 失败");
            Log.d("longudp","init socket 失败");
            e.printStackTrace();
        }
    }

    public static UdpLongUtil getInstance() {
        if (udpSender == null)
            udpSender = new UdpLongUtil();
        return udpSender;
    }


    public void setHasChangeCommand(boolean flag) {
        isChangeCMD = flag;
    }

    public void notifySendThread(byte[] datas) {
        this.command = datas;
        setHasChangeCommand(true);
        if (lock != null) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    public void startLongConn(){
//        VDebug.println("开始UDP长链接");
        Log.d("longudp","开始UDP长链接");
        CONN = true;
        navCount = 0;
        //otherCount =0;
//        pool.getCachePool().execute(receiveThread);
//        pool.getCachePool().execute(heartTimeTask);
//        pool.getSchedulePool().execute(sendThread);

        if(sendThread!=null && (isThreadRunning(sendThread)))
            {
                isChangeCMD=true;
            }
          else
            {
                sendThread.start();
            }
        if(heartTimeTask!=null && !isThreadRunning(heartTimeTask))
        heartTimeTask.start();

        if(receiveThread!=null && !isThreadRunning(receiveThread))
        receiveThread.start();



    }

    public boolean isThreadRunning(Thread sm)
    {
     //  qDebug.qLog("williamxiax","thread is interrupt  "+sinterupt+"   thread isalive "+isalive);
      return  sm.isAlive();

    }



    class SendThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    if (CONN) {
                        if (isChangeCMD) {
                            if(command.length>470){
                                Datas.clear();
                                for(int i=0;i<=command.length/470;i++){
                                    if(MainActivity.cuurentIp.isEmpty())break;
                                    byte[] com=GetCommand(i,command.length/470);
                                    DatagramPacket out = new DatagramPacket(com, com.length,
                                            remoteAddress, IGMP_PORT);
                                    Thread.sleep(80);
                                    if(socket!=null)
                                        socket.send(out);
                                    qDebug.printBytes("sendlongbyte",com);
                                }
                                isChangeCMD = false;
                            }
                            else {
                                DatagramPacket out = new DatagramPacket(command, command.length,
                                        remoteAddress, IGMP_PORT);
                                Thread.sleep(80);
                                if(socket!=null)
                                    socket.send(out);
                                isChangeCMD = false;
                            }

                        } else {
                            synchronized (lock) {
                                //VDebug.println("obj wait..ideal");
                                Log.d("longudp","obj wait..ideal");
                                lock.wait();
                            }
                        }
                    } else {
//                        VDebug.println("no conn ,break");
                        Log.d("longudp","no conn ,break");
                        break;
                    }
                } catch (InterruptedException e) {
//                    VDebug.println("entry InterruptedException");
                    Log.d("longudp","entry InterruptedException");
                } catch (SocketException e) {
//                    VDebug.println("entry SocketException");
                    Log.d("longudp","entry SocketException");
                    CONN = false;
                    //throw  new RuntimeException(e);
                } catch (IOException e) {
//                    VDebug.println("entry IOException");
                    Log.d("longudp","entry IOException");
                }catch (Exception e) {
                    //throw new RuntimeException(e);
//                    VDebug.println("遇到重大问题退出了");
                    Log.d("longudp","遇到重大问题退出了");
                }
            }
        }
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                while (CONN) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length,remoteAddress,IGMP_PORT);
                    socket.receive(packet);
                    String recAddr = packet.getAddress().getHostAddress();
                    //gostring(packet.getData());
                    //if(curAddr.equals(recAddr)) {
                        int highLength, lowLength, packLength;
                        highLength = packet.getData()[3] * 256;
                        lowLength = (packet.getData()[4]&0xff);
                        packLength = highLength + lowLength;
                        byte[] current = new byte[packLength];
                        System.arraycopy(packet.getData(), 0, current, 0, current.length);
                        gostring(current);
                        if(current.length==500){
                            qDebug.printBytes("reveivelongbyte",current);
                            addDatas(current,recAddr);
                        }
                        else
                            EventBus.getDefault().post(new ReceiveCommandEvent(current,recAddr));
                        navCount = 0;
                        //otherCount=0;
                   // }
                    /*else{
                        if(packet.getData()[10]==ACK_TYPE) {
                            if(Const.nativeIp.equals(recAddr)){
                                navCount++;
                            }*//*else{
                                otherCount++;
                            }*//*
                            //VDebug.println("navcount==========="+navCount+"=======othercount==="+otherCount);
                            if ((navCount > (defFlag?30:getMaxCount(devNum)))) {
                                CONN = false;
                                stopConnect();
                            }
                        }
                    }*/
                }
            } catch (Exception e) {
                //throw  new RuntimeException(e);
                e.printStackTrace();
//                VDebug.println("ReceiveThread server break the SCONNect ");
                Log.d("longudp","ReceiveThread server break the SCONNect ");
                CONN = false;
            }finally {
                stopConnect();
            }
        }
    }


    class HeartTimeTask extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(HEART_CMD,HEART_CMD.length,remoteAddress,IGMP_PORT);
                    socket.send(packet);
//                    VDebug.println("发送ack---------------");
                    Log.d("longudp","发送ack---------------");
                    Thread.sleep(3000);
                } catch (Exception e) {
//                    VDebug.println("HeartTimeTask server break the SCONNect ");
                    Log.d("longudp","HeartTimeTask server break the SCONNect ");
                    CONN = false;
                    stopConnect();
                    return;
                }
            }
        }
    }



    public void stopConnect() {
        try {
            if (socket != null) {
                socket.close();
            }
            //stopTimer();
            //ackNum=0;
            //EventBus.getDefault().post(new IpAdapterEvent());
//            VDebug.println("socket close normal");
            Log.d("longudp","socket close normal");
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
//            VDebug.println("stopConnect close socket Exception");
            Log.d("longudp","stopConnect close socket Exception");
        } finally {
//            pool.end();
            EventBus.getDefault().post(new ConnectEvent(false));
        }
    }

    public void setCurAddr(String addr){
        this.curAddr = addr;
    }
    public void setDeviceNum(int num){
        this.devNum = num;
    }

//    public int getMaxCount(int devNum){
//        return Dconst.DEV_COUNT[devNum>Dconst.DEV_COUNT.length?50:Dconst.DEV_COUNT[devNum]];
//    }

    public void addDatas(byte[] data,String recAddr){
        DataNUm=data[15];
        DataLen=(data[11]&0xff)*256+(data[12]&0xff);
        Datas.put(data[16]+"",data);
        if(Datas.size()==DataNUm){
            byte[] command=new byte[DataLen];
            for(int i=0;i<DataNUm;i++){
                byte[] current=(byte[]) Datas.get(i+"");
                System.arraycopy(current, 25, command, i*470, (current[13]&0xff)*256+(current[14]&0xff));
            }
            DataNUm=0;
            DataLen=0;
            Datas.clear();
            EventBus.getDefault().post(new ReceiveCommandEvent(command,recAddr));
        }
    }

    public byte[] GetCommand(int index,int num){
        byte[] com=new byte[500];
        Arrays.fill(com, WiStaticComm.BZero);
        com[0] = WiStaticComm.UTRAL_H0;
        com[1] = WiStaticComm.UTRAL_H1;
        com[2] = WiStaticComm.UTRAL_H2;
        com[3] = 0x01;
        com[4] = (byte) 0xf4;
        com[5] = (byte) (DStatic.AP_MA8 / Len_Big_Times);
        com[6] = (byte) (DStatic.AP_MA8 % Len_Big_Times);

        com[7] = (byte) (IpManager.getInstance().getSelDevID() / Len_Big_Times);
        com[8] = (byte) (IpManager.getInstance().getSelDevID() % Len_Big_Times);

        com[9] =command[9];
        com[10] =command[10];

        com[11] = (byte) (command.length/ Len_Big_Times);
        com[12] =(byte) (command.length% Len_Big_Times);

        com[13]= (byte) (num-index==0?command.length%470/256:1);

        com[14]=(byte) (num-index==0?command.length%470%256:214);
        com[15]= (byte) (num+1);
        com[16]= (byte) index;

//        ip
        qDebug.qLog(MainActivity.cuurentIp);
        String[] ip = MainActivity.cuurentIp.split("\\.");
        com[17]=(byte) Integer.parseInt( ip[0] );
        com[18]= (byte) Integer.parseInt( ip[1] );
        com[19]= (byte) Integer.parseInt( ip[2] );
        com[20]= (byte) Integer.parseInt( ip[3] );
        System.arraycopy(command, index*470, com, 25, num-index==0?command.length%470:470);
        com[496]=0x10;
        com[497]=0x00;
        com[498]=0x55;
        com[499]=0x40;

        byte checkSum = com[0];
        for (int i = 1; i < 500; i++) {
            if (i != 500 - 5)
                checkSum ^= com[i];

        }
        com[500 - 5] = checkSum;
        return com;
    }
}






