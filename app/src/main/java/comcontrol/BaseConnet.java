package comcontrol;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import cn.com.williamxia.wipack.utils.WiStaticComm;

public class BaseConnet {
    protected String LocalNet = "";
    public final static int IGMP_PORT = 9000;
    public static String IGMP_SER_IP = "239.254.50.123";
    public static final String LOOP_IP = "127.0.0.1";
    public byte[] HEART_CMD;
//    protected ThreadPool pool;
//    protected SendCommand sc;
    BaseConnet(){
//        pool = ThreadPool.getInstance();
//        sc = SendCommand.getInstance();
        HEART_CMD = new byte[]{

        WiStaticComm.UTRAL_H0,
        WiStaticComm.UTRAL_H1,
        WiStaticComm.UTRAL_H2,
        0x00,
        0x13,
        0x00,
        0x06,
        0x01,
        0x00,
        0x00,
        0x52,
        0x00,
        0x00,
        0x00,
        0x00,
        0x0A,
        (byte) 0xF4,
        (byte) 0xAA,
        0x40
        };
        byte checkSum = HEART_CMD[0];
        for (int i = 1; i < 19; i++)
        {
            if (i != 19 - 5)
                checkSum ^= HEART_CMD[i];

        }
        HEART_CMD[19 - 5] = checkSum;
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

    public static void gostring(byte[] chars) {
        String string = "";
        for (int i = 0; i < chars.length; i++) {
            String hex = Integer.toHexString(chars[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hex = hex + " ";
            string = string + hex;
        }
//        VDebug.println("命令：" + string);
    }
}
