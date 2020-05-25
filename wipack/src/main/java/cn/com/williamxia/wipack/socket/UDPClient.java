package cn.com.williamxia.wipack.socket;

import android.os.Handler;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.qDebug;

import static android.content.ContentValues.TAG;

/**
 * Created by williamXia on 2017/6/5.
 */

public class UDPClient {
    public static final String LOOP_IP = "127.0.0.1";

    private static final String myTag = "udpclient";
    //------------------------------------------------
    private int srcPort, remotePort;
    //
    private String strBordercastAddress;
    private byte[] m_UDPCommand;
    private DatagramSocket udpSocket;
    private String LocalNetIP;
    private Handler mHandler;
    public String strWifiIP = "";
    public final static int UDPTimeout = 3000;

    public UDPClient(String targetAddress, int msrcPort, int mremotePort, byte[] m_Command, Handler handler) {
        //----
        srcPort = msrcPort;
        remotePort = mremotePort;
        m_UDPCommand = m_Command;
        mHandler = handler;
        strBordercastAddress = targetAddress;
        strWifiIP=getNavWifiIp();
        try {
            udpSocket = new DatagramSocket(null);
            udpSocket.setReuseAddress(true);
            udpSocket.setSoTimeout(UDPTimeout);
            udpSocket.bind(new InetSocketAddress(srcPort));
        } catch (BindException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void startDetect() {
        new Thread(new SendUdp()).start();
        new Thread(new ReceiveUdp()).start();
    }

    public static final int SleepTime = 80;

    class SendUdp implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(SleepTime);
                qDebug.qLog(myTag, "Send thread started.....");
                InetAddress sendAddress = InetAddress.getByName(strBordercastAddress);
                DatagramPacket out = new DatagramPacket(m_UDPCommand, m_UDPCommand.length,
                        sendAddress, remotePort);
                udpSocket.send(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static final String IPLocal = "127.0.0.1";

    public UDPScanedEvent onUDPScanedEvent;

    public void setOnUDPScanedEvent(UDPScanedEvent udpScanedEvent) {
        onUDPScanedEvent = udpScanedEvent;
    }
    /*****************************************************************
     * @author williamXia
     *         created at 2017/8/21 上午9:18
     *         sendBordcast with command(step1)
     *         receivUDP:
     *         1:enumerate local network IP
     *         2:found ip to ipmanager which is not localIP in loop
     *         until the udpsocket timeout event happened
     ******************************************************************/
    class ReceiveUdp implements Runnable {

        @Override
        public void run() {
            try {
                enumerateNetWork_toFindLocalIP();
                if (LocalNetIP.equals(IPLocal)) {
                    Collection<InetAddress> colInetAddress = enumerateNetWork_getnetAddressCollection();
                    for (InetAddress address : colInetAddress) {
                        if (!address.isLoopbackAddress())
                            LocalNetIP = address.getHostAddress();
                    }
                }

                if (LocalNetIP.equals(IPLocal)) return;
                // qDebug.qLog(myTag, "---两次结果是===" + LocalNetIP);
                String strRemoteIP = "";
                while (true) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    udpSocket.receive(packet);
                    byte[] current = packet.getData();
                    int len = packet.getLength();//66
                  //  qDebug.fprintBytes(myTag, "udp socket receive data: " + len, current);
                    strRemoteIP = packet.getAddress().getHostAddress();
                    int remotedPort = packet.getPort();
                    qDebug.qLog(myTag, "成功获取到了服务器返回的IP" + strRemoteIP + "--端口是--" + remotedPort);
                    qDebug.qLog(myTag, "本地地址是LocalNet=====" + LocalNetIP);
                    if (!strWifiIP.equals(strRemoteIP)) {
                        IpManager.getInstance().addIP_toScanList(strRemoteIP);
                     //   if (onUDPScanedEvent != null) {
                      //      onUDPScanedEvent.onUDPScanedEvent(strRemoteIP, remotedPort);
                      //  }

                    }
                    // qDebug.qLog(myTag, "ipList---size", " " + IpManager.getInstance().getIpList().size());
                }
            } catch (SocketTimeoutException e) {
                qDebug.qLog(myTag, "已经超时....");
                //VDebug.println("成功把ip存入到了IpManager里面" + IpManager.getInstance().getAddress().size())
            } catch (Exception e) {
                qDebug.qLog(myTag, "UDP扫描出现了异常情况....");
            } finally {
                qDebug.qLog(myTag, "已经跳出接收循环----");
               // enumerateNetWork_toFindLocalIP();
                if (udpSocket != null) {
                    udpSocket.close();
                }
                // qDebug.qLog("timeOutUDP", "sendmessage start find");
                mHandler.sendEmptyMessage(WiStaticComm.AfterScan_toCheckIP);
            }
        }
    }

    private final void enumerateNetWork_toFindLocalIP() {
        qDebug.qLog(myTag, "------------进入了printIP");
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
                // qDebug.qLog(myTag, item.toString());
                // qDebug.qLog(myTag, item.getMTU() + " " + item.isLoopback() + " " + item.isPointToPoint() + " " + item.isUp() + " " + item.isVirtual());
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (address.getAddress() instanceof Inet4Address) {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();

                        LocalNetIP = inet4Address.getHostAddress();
                        qDebug.qLog(myTag, "本机地址是--------------" + LocalNetIP);
                        return;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }


    private Collection<InetAddress> enumerateNetWork_getnetAddressCollection() {
        qDebug.qLog(TAG, "------------进入了printIP2");
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Collection<InetAddress> addresses = new ArrayList<InetAddress>();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    addresses.add(inetAddress);
                }
            }
            return addresses;
        } catch (SocketException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    protected String getNavWifiIp() {
        String ip="";
        Collection<InetAddress> addresses = new ArrayList<InetAddress>();
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
//                VDebug.println(item.toString());
//                VDebug.println(item.getMTU() + " " + item.isLoopback() + " " + item.isPointToPoint() + " " + item.isUp() + " " + item.isVirtual());
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (address.getAddress() instanceof Inet4Address) {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();
//                        VDebug.println("本机地址是--------------" + inet4Address.getHostAddress());
                        ip = inet4Address.getHostAddress();
                    }
                }
            }
            if(ip.equals(LOOP_IP)){
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        addresses.add(inetAddress);
                    }
                }
                for (InetAddress address : addresses) {
                    if ((address.getHostAddress().length()>6&&address.getHostAddress().length()<20)&&!address.isLoopbackAddress()) {
                        ip = address.getHostAddress();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
//            VDebug.println("获取本地wifi地址异常错误！");
        }
        return ip;
    }


}
