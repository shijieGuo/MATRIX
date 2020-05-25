package cn.com.williamxia.matrixa8dante;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.FragPageAdaper;
import Datas.CmdSender;
import Datas.Command;
import Datas.DStatic;
import Datas.LCommand;
import Datas.XData;
import Events.ConnectEvent;
import Events.E_refreshChannelName;
import Events.E_refreshConnectInfo;
import Events.E_refreshWholePage;
import Events.MatrixRecall;
import Events.ReceiveCommandEvent;
import Events.ReceivePassword;
import Events.ReceivePresetEvent;
import Events.cannotControlEvent;
import Events.nothtingEvent;
import Events.recallGain;
import Events.recallMute;
import Events.showChannelPgaeEvent;
import WinPages.Page_DspChanel;
import WinPages.Page_IPConfig;
import WinPages.Page_Matrix;
import WinPages.Page_Preset;
import cn.com.williamxia.wipack.activity.BaseActivity;
import cn.com.williamxia.wipack.control.SquareLed;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.socket.TcpClientEvent;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.SomethingMsg;
import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.UdpLongUtil;

import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_End;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_H0;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_H1;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_H2;
import static cn.com.williamxia.wipack.utils.Xover.combineHLByte;


public class MainActivity extends BaseActivity implements View.OnClickListener, TcpClientEvent, View.OnTouchListener {

    public static String cuurentIp="";
    private ArrayList<Button> m_tabBtnList;
    //手势管理器
    private GestureDetector detector;

    private RelativeLayout alNoControl;

    private Date acktime=null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                checkWifiState();
                sendEmptyMessageDelayed(0, 2000);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerMainReceiver();
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initGUI();
        XData.gInstance();
        //创建手势管理器
        detector = new GestureDetector(new gestureListener());
        mHandler.sendEmptyMessageDelayed(0, 2000);
        getMutilLock();
        showModelDialog();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void nothing(nothtingEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CanNotControlView(cannotControlEvent event) {
        qDebug.qLog("can not control show.." + event.isShow());
        alNoControl.setVisibility(event.isShow() ? View.VISIBLE : View.INVISIBLE);
    }


    private Button tabIpconfig;
    private Button tabChanel;
    private Button tabMatrix;
    private Button tabPreset;
    private ViewPager tabPager;

    private FragPageAdaper mainAdpter;
    private List<Fragment> fragmentList;
    private SquareLed conLed;


    private void initGUI() {
        alNoControl = (RelativeLayout) findViewById(R.id.ar_cannotControl);
        alNoControl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        m_tabBtnList = new ArrayList<Button>();
        fragmentList = new ArrayList<Fragment>();
        tabIpconfig = (Button) findViewById(R.id.tab_ipconfig);
        tabChanel = (Button) findViewById(R.id.tab_chanel);
        tabMatrix = (Button) findViewById(R.id.tab_matrix);
        tabPreset = (Button) findViewById(R.id.tab_Preset);
        tabPager = (ViewPager) findViewById(R.id.tabPager);

        m_tabBtnList.add(tabIpconfig);
        m_tabBtnList.add(tabChanel);
        m_tabBtnList.add(tabMatrix);
        m_tabBtnList.add(tabPreset);


        //---
        fragmentList.add(new Page_IPConfig());
        fragmentList.add(new Page_DspChanel());
        fragmentList.add(new Page_Matrix());
        fragmentList.add(new Page_Preset());
        //-
        mainAdpter = new FragPageAdaper(getSupportFragmentManager(), fragmentList);
        tabPager.setAdapter(mainAdpter);
        //
        tabPager.setOffscreenPageLimit(3);
        tabIpconfig.setOnClickListener(this);
        tabChanel.setOnClickListener(this);
        tabMatrix.setOnClickListener(this);
        tabPreset.setOnClickListener(this);
        //
        conLed = (SquareLed) findViewById(R.id.conLed);
        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                qDebug.qLog("on paged scrolled index is " + position);
                SomethingMsg msg = null;
                switch (position) {
                    case 0:
                        msg = new SomethingMsg(DStatic.Msg_ID_IPConfig);
                        msg.HValue = DStatic.CPageUPdate;
                        CmdSender.sendBroadCastMsg_NoteIPChanelFrag(getApplicationContext(), msg);
                        break;

                    case 1://channelIO
                        msg = new SomethingMsg(DStatic.Msg_ID_DspChanel);
                        msg.HValue = DStatic.CPageUPdate;
                        CmdSender.sendBroadCastMsg_NoteChanelFrag(getApplicationContext(), msg);
                        EventBus.getDefault().post(new recallMute());
                        EventBus.getDefault().post(new recallGain());
                        break;
                    case 2://matrix
                        msg = new SomethingMsg(DStatic.Msg_ID_Matrix);
                        msg.HValue = DStatic.CPageUPdate;
                        //EventBus.getDefault().post(new MatrixRecall(0,0));
                        CmdSender.sendBroadCastMsg_NoteMatrixFrag(getApplicationContext(), msg);

                        break;
                    case 3:
                        msg = new SomethingMsg(DStatic.Msg_ID_Preset);
                        msg.HValue = DStatic.CPageUPdate;
                        CmdSender.sendBroadCastMsg_NotePresetsFrag(getApplicationContext(), msg);
                        break;
                }


            }

            @Override
            public void onPageSelected(int position) {
                //   qDebug.qLog("on paged scrolled selectd is "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private final int Num_Tabs = 4;

    public void switchTab(final int tindex) {
        Button btn = null;
        int tmpx = 0;
        if (tindex < 0 || tindex >= Num_Tabs) return;
        for (int i = 0; i < Num_Tabs; i++) {
            btn = (Button) m_tabBtnList.get(i);
            tmpx = Integer.parseInt(btn.getTag().toString());
            if (tmpx == tindex) {
                btn.setActivated(true);
            } else {
                btn.setActivated(false);
            }
        }
         if(tindex==2)
        {
            //update routing page...
            EventBus.getDefault().post(new MatrixRecall(XData.gInstance().mRoutChanel));
            qDebug.qLog("begin update to refresh routing page now....");
        }


    }


    @Override
    public void onClick(View view) {
        //------
        String strTag = (String) view.getTag();
        int index = Integer.parseInt(strTag);
        //
        qDebug.qLog("click index is   " + index);
        tabPager.setCurrentItem(index, false);
        switchTab(index);
        EventBus.getDefault().post(new showChannelPgaeEvent(XData.gInstance()._dspPageIndex,0));
    }


    private List<Byte> m_Record = new ArrayList<Byte>();
    private List<String> m_DevIPList = new ArrayList<String>();


    private Socket clientSock;

    public void StartConnect(final String strip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    clientSock = new Socket(strip, WiStaticComm.rDevPort);
                    clientSock.setSoTimeout(0);
                    clientSock.setKeepAlive(true);
                    TCPClient.getInstance().setupTCPClient(clientSock, MainActivity.this);
                    TCPClient.getInstance().setTcpClientEventEvent(MainActivity.this);
                    TCPClient.getInstance().isErrorInternet = false;
                    TCPClient.getInstance().isKeepConnect = true;

                      TCPClient.getInstance().setCommandACK(CmdSender.getPackage_ack());

                } catch (IOException e) {
                    TCPClient.getInstance().isErrorInternet = true;
                    TCPClient.getInstance().isKeepConnect = false;
                }
                TCPClient.getInstance().startListener();
                TCPClient.getInstance().runWithThread_Send();

            }
        }).start();

    }


    private boolean isAckSendPause = false;

    private void initReadBytes() {
        if (m_Record == null)
            m_Record = new ArrayList<Byte>();

        if (m_DevIPList == null)
            m_DevIPList = new ArrayList<String>();
    }

    private void iReadTcpByte(Object sender, final byte ch, final int conflag) {
        if (m_Record.size() == 0 && ch != UTRAL_H0) {
            m_Record.clear();
            return;
        }
        if (m_Record.size() == 1 && ch != UTRAL_H1) {
            m_Record.clear();
            return;
        }
        if (m_Record.size() == 2 && ch != UTRAL_H2) {
            m_Record.clear();
            return;
        }
        m_Record.add(ch);

        if (m_Record.size() >= DStatic.PKLen_ACK) {
            int rlen = Xover.combineHLByte(m_Record.get(3), m_Record.get(4), WiStaticComm.BOne);
            if (m_Record.size() == rlen && m_Record.get(rlen - 1) == UTRAL_End) {
                qDebug.fprintBytes("william", "---iReadbytestcp------", m_Record);
                byte[] tmp = new byte[rlen];
                for (int i = 0; i < rlen; i++)
                    tmp[i] = m_Record.get(i);
                procesReceivePack_normalResponsed(tmp);
                m_Record.clear();

            }
        }

    }


    private void procesReceivePack_normalResponsed(final byte[] datas) {

        // XData.getInstance().m_commuStatus.resetCommunitStatus();
        if (datas.length < DStatic.PKLen_ACK) return;

        int cmd = combineHLByte(datas[9], datas[10], WiStaticComm.BOne);

        switch (cmd) {
//            case Command.F_LoadFromPC:
//                qDebug.qLog("receive Command.F_LoadFromPC now...");
//                break;
            case Command.F_Ack:
                  qDebug.qLog("receive cmd_ack now...");
                acktime = new Date();
                  break;
            case Command.F_InpuGain:
            case Command.F_OutputGain: {
                qDebug.qLog("receive input,output gain ...........");
                XData.gInstance().iRead_chanelGain(datas);
                EventBus.getDefault().post(new recallGain());
                SomethingMsg msg = new SomethingMsg(DStatic.Msg_ID_DspChanel);
                msg.HValue = cmd;
                CmdSender.sendBroadCastMsg_NoteChanelFrag(getApplicationContext(), msg);
            }
            break;
            case Command.F_Mute: {
                qDebug.qLog("receive channel inver now...return.");
                qDebug.printBytes("receive channel inver now...return", datas);
                XData.gInstance().iRead_chanelMute(datas);
                SomethingMsg msg = new SomethingMsg(DStatic.Msg_ID_DspChanel);
                msg.HValue = cmd;
                EventBus.getDefault().post(new recallMute());
                CmdSender.sendBroadCastMsg_NoteChanelFrag(getApplicationContext(), msg);
            }
            break;
            case Command.F_MatrixMixer: {

                XData.gInstance().iRead_matrixSingleChanel(datas);
                int chindex = Xover.bTi(datas[11]) - 1;
                qDebug.qLog("receive matrix single output chindex is... "+ chindex);
                EventBus.getDefault().post(new MatrixRecall(cmd, chindex));
                /*
                SomethingMsg msg = new SomethingMsg(DStatic.Msg_ID_Matrix);
                msg.HValue = cmd;
                msg.LValue = chindex;
                CmdSender.sendBroadCastMsg_NoteMatrixFrag(getApplicationContext(), msg);
                */
            }
            break;
            case Command.F_ReChName: {
                XData.gInstance().iRead_chanelName(datas);
                int chindex = Xover.bTi(datas[11]) - 1;
                qDebug.qLog("receve chname change notion now......");
                int flag = Xover.bTi(datas[12]);
                if (flag == 1)
                    chindex += DStatic.ChanlMax;
                EventBus.getDefault().post(new E_refreshChannelName(chindex)); //[0..11]
            }
            break;
            case Command.F_RecallCurrentScene: {
                qDebug.qLog("receive curernt sence...." + datas.length);
                qDebug.printBytes("receive curernt sence....", datas);
                XData.gInstance().iRead_CurrentScene(datas, false);
                if (XData.gInstance().lockFlag == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tabPager.setCurrentItem(3, false);
                            switchTab(3);
                            EventBus.getDefault().post(new showChannelPgaeEvent(XData.gInstance()._dspPageIndex, 0));
                        }
                    });
                }
                SomethingMsg msg = new SomethingMsg(DStatic.Msg_ID_Preset);
                msg.HValue = cmd;
                CmdSender.sendBroadCastMsg_NotePresetsFrag(getApplicationContext(), msg);
                //
                msg = new SomethingMsg(DStatic.Msg_ID_DspChanel);
                msg.HValue = cmd;
                CmdSender.sendBroadCastMsg_NoteChanelFrag(getApplicationContext(), msg);
                EventBus.getDefault().post(new E_refreshWholePage());
                EventBus.getDefault().post(new MatrixRecall(XData.gInstance().mRoutChanel));//20180613
            }
                break;
            case Command.F_RecallSinglePreset:
                qDebug.qLog("singlePreset Recall>>>>>>>>>>>>");
                break;
            case Command.F_GetPresetList:
                qDebug.qLog("F_GetPresetList Recall>>>>>>>>>>>>");
                qDebug.printBytes("F_GetPresetList Recall>>>>>>>>>>>>",datas);
                XData.gInstance().iRead_allPresetsList(datas);
                SomethingMsg fmsg = new SomethingMsg(DStatic.Msg_ID_Preset);
                fmsg.HValue = cmd;
                CmdSender.sendBroadCastMsg_NotePresetsFrag(getApplicationContext(), fmsg);
                EventBus.getDefault().post(new ReceivePresetEvent());
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);

                        }catch (Exception e){

                        }
                    }
                }).start();*/

                break;
            case Command.F_MemoryExport: { //after sendcmd from device to mobile
                int index = Xover.bTi(datas[11]);//0..24
                XData.gInstance().pushMemoryData_toExport(datas);
                SomethingMsg tmsg = new SomethingMsg(DStatic.Msg_ID_Preset);
                tmsg.HValue = cmd;
                tmsg.LValue = index;
                CmdSender.sendBroadCastMsg_NotePresetsFrag(getApplicationContext(), tmsg);

            }
            break;
            case Command.F_MemoryImportAck: {
                int index = Xover.bTi(datas[11]);//0..241
                if (index < DStatic.Memory_Max_Package - 1)
                    XData.gInstance().sendCMD_LoadPresteFlile_fromLocal(index + 1);
                SomethingMsg tmsg = new SomethingMsg(DStatic.Msg_ID_Preset);
                tmsg.HValue = cmd;
                tmsg.LValue = index;
                CmdSender.sendBroadCastMsg_NotePresetsFrag(getApplicationContext(), tmsg);

            }
            case Command.F_ReadLockPWD:
                EventBus.getDefault().post(new ReceivePassword(datas));
                break;
        }


    }


    @Override
    public void onTcpReceive(Object sender, byte[] mdata, int length, int conFlag) {
        for (int i = 0; i < length; i++)
            iReadTcpByte(sender, mdata[i], conFlag);
    }

    @Override
    public void onTcpDisconnected(Object sender, boolean staus, int conFlag) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conLed.setMisActive(false);
                EventBus.getDefault().post(new E_refreshConnectInfo(false));
            }
        });
    }

    @Override
    public void onTcpConnected(Object sender, boolean conStatus, int conFlag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conLed.setMisActive(true);
                EventBus.getDefault().post(new E_refreshConnectInfo(true));
            }
        });
        int idevid = IpManager.getInstance().getSelDevID();
        if (idevid >= 0) {
            CmdSender.sendCMD_RecallCurrentScene(idevid);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterMainReceiver();
        if (TCPClient.getInstance().isConnected())
            TCPClient.getInstance().stopConnect();
        EventBus.getDefault().unregister(this);
    }

    //region resister about broadcast

    private void registerMainReceiver() {
        IntentFilter filter = new IntentFilter(DStatic.Action_MainActivity);
        registerReceiver(mainReceiver, filter);

    }

    private void unRegisterMainReceiver() {
        unregisterReceiver(mainReceiver);
    }

    private BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DStatic.Action_MainActivity)) {
                final SomethingMsg msg = (SomethingMsg) intent.getSerializableExtra(DStatic.Msg_Key_MainActivity);
                final String msgID = msg.fID;
                if (msgID == null) return;
                //
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (msgID) {
                            case DStatic.Msg_ID_BeginConnect: {
                                if(DStatic.Model){
                                    if (msg.HValue == DStatic.MConnect) {
                                        //选择连接
                                        String strIP = IpManager.getInstance().getSelStrIP();
                                        if (strIP.length() > 1) {
                                            StartConnect(strIP);
                                            qDebug.qLog("beging connect with ip now.." + strIP);
                                            final Timer timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if(acktime==null)return;
                                                    Date time = new Date();
                                                    long time_diff=time.getTime()-acktime.getTime();
                                                    if(time_diff>15000){
                                                        TCPClient.getInstance().stopConnect();
                                                        acktime=null;
                                                        timer.cancel();
                                                    }
                                                }
                                            }, 0,3000);
                                        }
                                    } else {
                                        //断开连接
                                        TCPClient.getInstance().stopConnect();
                                        qDebug.qLog("disconnnect now");

                                    }
                                }
                                else {
                                    if (msg.HValue == DStatic.MConnect) {
                                        MainActivity.cuurentIp=IpManager.getInstance().getSelStrIP();
                                        int idevid = IpManager.getInstance().getSelDevID();
                                        if (idevid >= 0) {
                                            CmdSender.sendCMD_RecallCurrentScene(idevid);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                conLed.setMisActive(true);
                                                EventBus.getDefault().post(new E_refreshConnectInfo(true));

                                                final Timer timer = new Timer();
                                                timer.schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        if(acktime==null)return;
                                                        Date time = new Date();
                                                        long time_diff=time.getTime()-acktime.getTime();
                                                        if(time_diff>15000){
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    conLed.setMisActive(false);
                                                                }
                                                            });
                                                            EventBus.getDefault().post(new E_refreshConnectInfo(false));
                                                            MainActivity.cuurentIp="";
                                                            acktime=null;
                                                            timer.cancel();
                                                        }
                                                    }
                                                }, 0,3000);
                                            }
                                        });
                                        //选择连接
                                    } else {
                                        MainActivity.cuurentIp="";
//                                        UdpLongUtil.getInstance().stopConnect();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                conLed.setMisActive(false);
                                                EventBus.getDefault().post(new E_refreshConnectInfo(false));
                                            }
                                        });
                                    }
                                }

                            }
                            break;

                        }
                    }
                }).start();
            }
        }
    };

    //endregion


    @Override
    protected void onStart() {
        super.onStart();
        int index = tabPager.getCurrentItem();
        switchTab(index);


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (detector.onTouchEvent(ev)) {
            return detector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    int count = 0;

    private class gestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

           /*
            if (fragmentList.get(1).isVisible()) {
                if (e1.getX() - e2.getX() > 100) {
                    //下标++

                    XData.gInstance()._dspPageIndex++;
                    if (XData.gInstance()._dspPageIndex <= 3) {
                        //切换到下一张
                        EventBus.getDefault().post(new showChannelPgaeEvent(XData.gInstance()._dspPageIndex, 0));
                    } else {
                        XData.gInstance()._dspPageIndex = 3;
                        //滑动到最后一张，该方向不会再滑动，就停留在最后一张
                    }
                }
                //从左往右移动
                if (e1.getX() - e2.getX() < -100) {
                    XData.gInstance()._dspPageIndex--;
                    if (XData.gInstance()._dspPageIndex >= 0) {
                        EventBus.getDefault().post(new showChannelPgaeEvent(XData.gInstance()._dspPageIndex, 1));
                    } else {
                        XData.gInstance()._dspPageIndex = 0;
                    }
                }
            }
            */
            return false;
        }
    }


    public  static  String intIP2String(int ip)
    {
       return  (ip &0xFF)+"."+((ip>>8)&0xFF)+"."+((ip>>16)&0xFF)+"."+(ip>>24 &0xFF);

    }
    /**
     * 检查wifi强弱
     */
    public void checkWifiState() {
        if (isWifiConnect()) {
            WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi = mWifiInfo.getRssi();//获取wifi信号强度
            String LocalIP=intIP2String( mWifiInfo.getIpAddress());
            if (wifi > -50 && wifi < 0) {//最强

            } else if (wifi > -70 && wifi < -50) {//较强

            } else if (wifi > -80 && wifi < -70) {//较弱

            } else if (wifi > -100 && wifi < -80) {//微弱
                if (TCPClient.getInstance().isConnected())
                    TCPClient.getInstance().stopConnect();
            }
        } else {
            //无连接
            Toast.makeText(this, "No connection to WLAN!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查wifi是否处开连接状态
     *
     * @return
     */
    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }
    public void getMutilLock(){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiManager
                .createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();
    }
    public void showModelDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Model Type Choose")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //withMessageColor(int resid)
                .withDialogColor("#373737")                               //withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.icon_76))
                .isCancelableOnTouchOutside(false)                           //isCancelable(true)
                .withDuration(800)                                          //
                .withEffect(Effectstype.Fadein)                         //def Effectstype.Slidetop
                .withButton1Text("TCP")
                .withButton2Text("Dante")
                .setCustomView(R.layout.choose_model, this)         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DStatic.Model=true;
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        DStatic.Model=false;//UDP model
                        UdpLongUtil.getInstance().init(getApplicationContext());
                        UdpLongUtil.getInstance().startLongConn();
                        dialogBuilder.dismiss();
                    }
                }).show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ReceiveCommandEvent(ReceiveCommandEvent event) {
        byte[] datas = event.datas;
        String recAddr=event.recAddr;
        int cmd = combineHLByte(datas[9], datas[10], WiStaticComm.BOne);
//        int length=(datas[3]&0xff) * 256+(datas[4]&0xff);

        if(MainActivity.cuurentIp.equals(recAddr))
            procesReceivePack_normalResponsed(datas);
        if(!MainActivity.cuurentIp.equals(recAddr)&&cmd==3){
            Page_IPConfig page_ipConfig= (Page_IPConfig) fragmentList.get(0);
            page_ipConfig.procesReceivePack_ScanedResponsedCheck(recAddr,datas);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ConnectEvent(ConnectEvent event){
        boolean con=event.con;
        if(!con){
            qDebug.qLog("udp break the connect");
            DStatic.Model=false;
            UdpLongUtil.getInstance().init(getApplicationContext());
            UdpLongUtil.getInstance().startLongConn();
        }
    }

}
