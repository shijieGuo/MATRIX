package cn.com.williamxia.wipack.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPMacPingGetter {
    /***
     * 执行命令行工具类
     */
    private String TAG = "IPAssistant";
    private String COMMAND_SH = "sh";
    private String COMMAND_LINE_END = "\n";
    private String COMMAND_EXIT = "exit\n";
    private boolean ISDEBUG = false;
    private HashMap<String, String> datas, resultDatas;
    private String regexIp = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
    private String regexMac = "\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}";
    private int RUNTIME_PING = 3;
    private int NORMAL_SERCH = 4;
    private List<String> results;
    private String IP = "";
    private Handler hander;
    private Pattern patternIp, patternMac;

    public IPMacPingGetter(Handler handler) {
        this.hander = handler;
        patternIp = Pattern.compile(regexIp);
        patternMac = Pattern.compile(regexMac);
        datas = new HashMap<String, String>();
        resultDatas = new HashMap<String, String>();
    }

    /**
     * 执行单条命令
     *
     * @param command
     * @return
     */
    private void execute(String command, boolean isPing) {
        execute(new String[]{command}, isPing);
    }

    /**
     * 可执行多行命令（bat）
     *
     * @param commands
     * @return
     */
    private void execute(final String[] commands, final boolean isPing) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //System.out.println("the count="+(++count)+"entry ping command");
                if (results != null && results.size() > 0) {
                    results.clear();
                } else {
                    results = new ArrayList<String>();
                }
                int status = -1;
                if (commands == null || commands.length == 0) {
                    return;
                }
                debug("execute command start : " + commands);
                Process process = null;
                BufferedReader successReader = null;
                BufferedReader errorReader = null;
                StringBuilder errorMsg = null;

                DataOutputStream dos = null;
                try {
                    // TODO
                    process = Runtime.getRuntime().exec(COMMAND_SH);
                    dos = new DataOutputStream(process.getOutputStream());
                    for (String command : commands) {
                        if (command == null) {
                            continue;
                        }
                        dos.write(command.getBytes());
                        dos.writeBytes(COMMAND_LINE_END);
                        dos.flush();
                    }
                    dos.writeBytes(COMMAND_EXIT);
                    dos.flush();
                    status = process.waitFor();
                    errorMsg = new StringBuilder();
                    successReader = new BufferedReader(new InputStreamReader(
                            process.getInputStream()));
                    errorReader = new BufferedReader(new InputStreamReader(
                            process.getErrorStream()));
                    String lineStr;
                    while ((lineStr = successReader.readLine()) != null) {
                        results.add(lineStr);
                        debug(" command line item : " + lineStr);
                    }
                    debug(" command line item result.size: " + results.size());
                    while ((lineStr = errorReader.readLine()) != null) {
                        errorMsg.append(lineStr);
                    }
                    if (successReader != null && isPing) {
                        mHandler.sendEmptyMessage(RUNTIME_PING);
                    }
                    if (successReader != null && !isPing) {
                        mHandler.sendEmptyMessage(NORMAL_SERCH);
                    }
                    Looper.loop();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (dos != null) {
                            dos.close();
                        }
                        if (successReader != null) {
                            successReader.close();
                        }
                        if (errorReader != null) {
                            errorReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (process != null) {
                        process.destroy();
                    }
                }
                debug(String.format(Locale.CHINA,
                        "execute command end,errorMsg:%s,and status %d: ", errorMsg,
                        status));
            }
        }).start();

    }


    public void pingCommand(String ip) {
        resultDatas.clear();
        IP = ip;
        System.out.println(IP);
        execute("ping -w 3 " + ip, true);
    }

    private void getMac(Handler handler) {
        for (int i = 0; i < results.size(); i++) { //convert all ip matcher to datas map
            String temp = results.get(i);
            //temp is like :mactemp string is  192.168.1.1      0x1         0x2         50:fa:84:33:69:24     *        wlan0
            qDebug.qLog("ipmacii", temp);
            //-------
            ISDEBUG = true;
            Matcher matchIp = patternIp.matcher(temp);
            Matcher matchMac = patternMac.matcher(temp);
            if (matchIp.find() && matchMac.find()) {
                datas.put(matchIp.group(0), matchMac.group(0).toUpperCase());
                // qDebug.qLog("maps", temp);
            }
        }

        if (datas.size() > 0) {
            String needMac = datas.get(IP);
            //  IpManager.getInstance().addIP(IP);
            // IpManager.getInstance().addMac(needMac);
            // resultDatas.put(IP, needMac);
            qDebug.qLog("ipmac", "datas.size is " + datas.size() + " will send strIP is " + IP);
            DevInfo ipMacDevName = new DevInfo();
            ipMacDevName.devceAddr = IP;
            ipMacDevName.devMac = needMac;
            IpManager.getInstance().setStrMac(IP, needMac);
            ipMacDevName.setM_devName( IpManager.getInstance().getDeviceName(IP));
            Message message = new Message();
            message.what = WiStaticComm.HAS_FIND_IP;
            message.obj = ipMacDevName; //send ipmaDevName object
           // Looper.prepare();
            handler.sendMessage(message);
        } else {
           // Looper.prepare();
            handler.sendEmptyMessage(WiStaticComm.NOT_FIND_IP);
        }
    }

    /**
     * DEBUG LOG
     *
     * @param message
     */
    public void debug(String message) {
        if (ISDEBUG) {
            Log.d(TAG, message);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RUNTIME_PING) {
                // System.out.println("the count="+count+"RUNTIME_PING");
                System.out.println("msg.what==RUNTIME_PING");
                execute("cat /proc/net/arp", false);
            } else if (msg.what == NORMAL_SERCH) {
                //System.out.println("the count="+count+"NORMAL_SERCH");
                System.out.println("msg.what ==NORMAL_SERCH");
                getMac(hander);
            }
        }
    };

}
