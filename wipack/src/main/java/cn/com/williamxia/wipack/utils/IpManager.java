package cn.com.williamxia.wipack.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IpManager {
    private static IpManager manager = null;
    private static ArrayList<String> scanedipList = null;
    private static ArrayList<String> devIPList = null;
    public static ArrayList<DevInfo> scanedIPMacDevList = null;

    private IpManager() {
        selDevIndex = -1;
    }

    public static IpManager getInstance() {
        if (manager == null) {
            manager = new IpManager();
            if (scanedipList == null)
                scanedipList = new ArrayList<String>();
            if (devIPList == null)
                devIPList = new ArrayList<String>();

            if (scanedIPMacDevList == null)
                scanedIPMacDevList = new ArrayList<DevInfo>();
        }
        return manager;
    }

    public ArrayList<DevInfo> getScanedIPMacDevList() {
        return scanedIPMacDevList;
    }

    public int getScanedIPCount() {
        if (scanedipList != null && scanedipList.size() > 0) {
            return scanedipList.size();
        } else return 0;

    }

    public int getDevMacList() {
        if (scanedIPMacDevList != null && scanedIPMacDevList.size() > 0)
            return scanedIPMacDevList.size();
        else
            return 0;

    }

    public String getSelStrIP() {
        int selindex = getSelDevIndex();
        if (selindex >= 0)
            return scanedIPMacDevList.get(selindex).devceAddr;
        else
            return "";

    }

    public int getSelDevID() {
        int selindex = getSelDevIndex();
        if (selindex >= 0)
            return scanedIPMacDevList.get(selindex).mDevID;
        else
            return -1;
    }


    private int selDevIndex;

    public int getSelDevIndex() {
        int index = -1;
        int num = scanedIPMacDevList.size();
        if (num == 0)
            return -1;
        else {
            index = Math.min(selDevIndex, num);
        }
        return index;
    }

    public void setSelDevIndex(int selDevIndex) {
        this.selDevIndex = selDevIndex;


    }

    /***************************************************************
     * add ip to ipList
     ***************************************************************/
    public void addIP_toScanList(String address) {
        boolean isExist = false;

        if (scanedipList.size() == 0) {
            scanedipList.add(address);
        } else {

            for (int i = 0; i < scanedipList.size(); i++) {
                if (scanedipList.get(i).equals(address)) {
                    isExist = true;
                    break;
                } else
                    isExist = false;
            }


            if (!isExist) {
                scanedipList.add(address);
            }
        }
    }

    //dif by different device id
    public void addIPMacDev(DevInfo ipMacDevName) {
        boolean isExist = false;

        if (scanedIPMacDevList.size() == 0) {
            scanedIPMacDevList.add(ipMacDevName);
        } else {

            for (int i = 0; i < scanedIPMacDevList.size(); i++) {
                if (scanedIPMacDevList.get(i).mDevID == ipMacDevName.mDevID) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                scanedIPMacDevList.add(ipMacDevName);
            }
        }
    }

    public boolean isThisDeviceExist(final String mAddr) {
        boolean isExist = false;

        if (scanedIPMacDevList.size() == 0) {
            isExist = false;
        } else {

            for (int i = 0; i < scanedIPMacDevList.size(); i++) {
                if (scanedIPMacDevList.get(i).devceAddr.equals(mAddr)) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }

    public void setDeviceName(final String mAddr, String devName) {

        if (scanedIPMacDevList.size() == 0 || mAddr.trim().length() < 1)
            return;
        for (int i = 0; i < scanedIPMacDevList.size(); i++) {
            if (scanedIPMacDevList.get(i).equals(mAddr)) {
                scanedIPMacDevList.get(i).setM_devName(devName);
                break;
            }
        }


    }


    public void setStrMac(final String strIP, String mac) {
        if (scanedIPMacDevList.size() == 0 || strIP.trim().length() < 1)
            return;
        for (int i = 0; i < scanedIPMacDevList.size(); i++) {
            if (scanedIPMacDevList.get(i).devceAddr.equals(strIP)) {
                scanedIPMacDevList.get(i).devMac = mac;
                break;
            }
        }


    }

    public String getDeviceName(final String strip) {
        String strDev = null;
        for (int i = 0; i < scanedIPMacDevList.size(); i++) {
            if (scanedIPMacDevList.get(i).devceAddr.equals(strip)) {
                strDev = scanedIPMacDevList.get(i).getDevName();
                break;
            }
        }
        return strDev;
    }

    public String getDeviceName(final int mdevID) {
        String strDev = null;
        for (int i = 0; i < scanedIPMacDevList.size(); i++) {
            if (scanedIPMacDevList.get(i).mDevID == mdevID) {
                strDev = scanedIPMacDevList.get(i).getDevName();
                break;
            }
        }
        return strDev;
    }


    public ArrayList<String> getDevIPList() {
        if (devIPList == null) {
            devIPList = new ArrayList<String>();
        }
        return devIPList;
    }


    public ArrayList<String> getScanedIpList() {
        if (scanedipList == null) {
            scanedipList = new ArrayList<String>();
        }
        return scanedipList;
    }


    public void clearAllList() {
        if (scanedipList != null)
            scanedipList.clear();
        if (devIPList != null)
            devIPList.clear();
        if (scanedIPMacDevList != null) {
            scanedIPMacDevList.clear();
        }
    }

    public static boolean isIPV4(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr))
            return false;

        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(rexp);
        Matcher mat = pattern.matcher(addr);
        boolean isIPAddr = mat.find();
        return isIPAddr;

    }


}
