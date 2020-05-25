package WinPages;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.MutilHeadAdapter;
import Datas.CmdSender;
import Datas.DStatic;
import Datas.XData;
import Events.E_refreshConnectInfo;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.socket.TCPFastClient;
import cn.com.williamxia.wipack.socket.TFastClientEvent;
import cn.com.williamxia.wipack.socket.UDPClient;
import cn.com.williamxia.wipack.socket.UDPScanedEvent;
import cn.com.williamxia.wipack.utils.DevInfo;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.SomethingMsg;
import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.UdpLongUtil;

import static Datas.LCommand.Len_Small_Times;
import static cn.com.williamxia.wipack.activity.BaseActivity.cachedThreadPool;
import static cn.com.williamxia.wipack.utils.WiStaticComm.Len_Big_Time;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_End;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_H0;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_H1;
import static cn.com.williamxia.wipack.utils.WiStaticComm.UTRAL_H2;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Page_IPConfig# n ew Instance} factory method to
 * create an instance of this fragment.
 */
public class Page_IPConfig extends Fragment {


    public Page_IPConfig() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_listDatas = new ArrayList<DevInfo>();
        IntentFilter filter = new IntentFilter(DStatic.Action_IPConfigFrag);
        getActivity().registerReceiver(mIpConfigRecever, filter);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview = inflater.inflate(R.layout.fragment_page_ipconfig, container, false);
        initListViewHeader(mview);
        initGUI(mview);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return mview;
    }

    private TextView htv1;
    private TextView htv2;
    private TextView htv3;
    private TextView htv4;
    private TextView htv5;
    private TextView htv6;
    private ListView ipLv;
    private List<DevInfo> m_listDatas;
    private MutilHeadAdapter mAdatper;
    private int dvWd;

    private int mSelectindex = -1;


    private TextView lbSelindex;
    private TextView lbDevNum;
    private ConstraintLayout all_item_heder;

    private void initListViewHeader(View mview) {

        //  all_item_heder = (ConstraintLayout) mview.findViewById(R.id.all_item_heder);
        // all_item_heder.setBackgroundColor(Color.BLACK);

        htv1 = (TextView) mview.findViewById(R.id.htv1);
        /*htv2 = (TextView) mview.findViewById(R.id.htv2);
        htv3 = (TextView) mview.findViewById(R.id.htv3);
        htv4 = (TextView) mview.findViewById(R.id.htv4);
        htv5 = (TextView) mview.findViewById(R.id.htv5);
        htv6 = (TextView) mview.findViewById(R.id.htv6);*/
        //----htv6
        htv1.setText("Device List:");
        /*htv2.setText("Mac Addr");
        htv3.setText("Device Name");
        htv4.setText("App ID");
        htv5.setText("Device ID");
        htv6.setText("Firmware Version");*/
        //----


        ipLv = (ListView) mview.findViewById(R.id.ipLv);
        lbSelindex = (TextView) mview.findViewById(R.id.lbSelindex);
        lbDevNum = (TextView) mview.findViewById(R.id.lbDevNum);
        if (m_listDatas.size() == 0) {
            mAdatper = new MutilHeadAdapter(getActivity().getApplicationContext(), mydefualtPreset());
            ipLv.setAdapter(mAdatper);
        } else {
            mAdatper = new MutilHeadAdapter(getActivity().getApplicationContext(), m_listDatas);
            ipLv.setAdapter(mAdatper);
        }

        ipLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdatper.setmSelectindex(i);
                qDebug.qLog("item index clici with " + i);
                lbSelindex.setText("Current selected index: "+i);
                edRemoteDev.setText(m_listDatas.get(i).devceAddr);
                IpManager.getInstance().setSelDevIndex(i);
                if (i >= 0 && m_listDatas.size() > i)
                    XData.gInstance().mDeviceInf = m_listDatas.get(i);
            }
        });

        ipLv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ipLv.getViewTreeObserver().removeOnPreDrawListener(this);
                //freshLayout();
                refreshListV();
                return false;
            }
        });
    }

    public static List<DevInfo> mydefualtPreset() {
        DevInfo defaultIP = new DevInfo();
        List<DevInfo> list_defaultPresetname = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list_defaultPresetname.add(i, defaultIP);
        }
        return list_defaultPresetname;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mIpConfigRecever);

    }

    private void freshLayout() {
        dvWd = all_item_heder.getMeasuredWidth() / 14;
        htv1.setWidth(dvWd * 3);
        htv2.setWidth(dvWd * 3);
        //
        htv3.setWidth(dvWd * 3);
        htv4.setWidth(dvWd * 1);
        htv5.setWidth(dvWd * 1);
        htv6.setWidth(dvWd * 3);

        mAdatper.setFdvWd(dvWd);
        lbDevNum.setText("" + m_listDatas.size());
        refreshListV();

    }


    //..
    private int detectedCount = 0;
    private EditText edRemoteDev;
    private Button btnConnect;
    private Button btnfresh;
    private SpinKitView loadProgres;

    private List<String> m_DevIPFilterList = new ArrayList<String>();

    public void startSendUDP() {
        btnfresh.setEnabled(false);
        detectedCount = 0;
        m_DevIPFilterList.clear();
        edRemoteDev.setText("127.0.1");
        m_listDatas.clear();
        mAdatper.notifyDataSetInvalidated();
        lbDevNum.setText("" + m_listDatas.size());
        lbSelindex.setText("Current selected index: -1");
        m_checkRecord.clear();
        m_CheckIPList.clear();//clear the IP
        IpManager.getInstance().clearAllList();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //-------IP210 initial below..
                    UDPClient uHaf11 = new UDPClient(WiStaticComm.BordcastAddr, WiStaticComm.rPort_HAF11,
                            WiStaticComm.rPort_HAF11, WiStaticComm.HAF11ModuleDetect.getBytes(), MainHandler);
                    uHaf11.setOnUDPScanedEvent(new UDPScanedEvent() {
                        @Override
                        public void onUDPScanedEvent(String strScannedIP, int sport) {
                            //in fragment should be showd as this
                        }
                    });
                    Thread.sleep(100);
                    uHaf11.startDetect();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

            }
        });
        cachedThreadPool.execute(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             //-------IP210 initial below..
                                             UDPClient uIP210 = new UDPClient(WiStaticComm.BordcastAddr, 7999, 8000,
                                                     WiStaticComm.IP120ModuleDetect, MainHandler);
                                             uIP210.setOnUDPScanedEvent(new UDPScanedEvent() {
                                                 @Override
                                                 public void onUDPScanedEvent(String strScannedIP, int sport) {

                                                 }
                                             });
                                             Thread.sleep(50);
                                             uIP210.startDetect();
                                         } catch (InterruptedException e) {
                                             e.printStackTrace();
                                         }
                                     }

                                 }
        );


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshConnectInfo(E_refreshConnectInfo evo) {
        btnfresh.setEnabled(!evo.isConnect());
        //   btnConnect.setEnabled(!evo.isConnect() );

        btnConnect.setActivated(evo.isConnect());
        String strTxt = (evo.isConnect() ? "Disconnect" : "Connect");
        btnConnect.setText(strTxt);
        btnfresh.setActivated(evo.isConnect());

        qDebug.qLog("refresh connect info button now....");
    }


    private BroadcastReceiver mIpConfigRecever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DStatic.Action_IPConfigFrag)) {
                final SomethingMsg msg = (SomethingMsg) intent.getSerializableExtra(DStatic.Msg_Key_IpConfig);
                if (msg.fID.equals(DStatic.Msg_ID_IPConfig)) {
                    switch (msg.HValue) {
                        case DStatic.CPageUPdate: {
                            if(DStatic.Model){
                                String strTxt = (TCPClient.getInstance().isConnected() ? "Disconnect" : "Connect");
                                btnConnect.setText(strTxt);
                                btnfresh.setEnabled(!TCPClient.getInstance().isConnected());
                            }
                            else {
                                String strTxt = (!MainActivity.cuurentIp.isEmpty() ? "Disconnect" : "Connect");
                                btnConnect.setText(strTxt);
                                btnfresh.setEnabled(MainActivity.cuurentIp.isEmpty());
                            }
                        }
                        break;

                    }

                }


            }

        }

    };

    public void refreshListV() {
        m_listDatas.clear();
        m_listDatas.addAll(IpManager.getInstance().scanedIPMacDevList);
        ipLv.setAdapter(mAdatper);
        mAdatper.notifyDataSetInvalidated();
        loadProgres.setVisibility(View.GONE);
    }


    private boolean isCanClick = true;
    private Timer timer = null;

    private void initialTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new CLoopCheckClick(), 0, DStatic.HeartTimeDelay);

    }

    private boolean isAckSendPause = true;

    class CLoopCheckClick extends TimerTask {
        @Override
        public void run() {
            isCanClick = true;
            // qDebug.qLog("loopcheck click now.........");
            if (!isAckSendPause) {
                // CMDSender.sendCMD_Ack();
                //  XCoreData.getInstance().setAckComuteStatus();
                qDebug.qLog("sendcmd ack .......in cloop ...");
            }
        }
    }


    private void initGUI(final View mview) {

        edRemoteDev = (EditText) mview.findViewById(R.id.edRemoteDev);
        edRemoteDev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edRemoteDev.setFocusableInTouchMode(true);
                return false;
            }
        });
        btnConnect = (Button) mview.findViewById(R.id.btnConnect);
        btnfresh = (Button) mview.findViewById(R.id.btnfresh);
        loadProgres = (SpinKitView) mview.findViewById(R.id.loadProgres);

        /*loadProgres.setOnOverLoopEvent(new SpinKitView.OverLoopListener() {
            @Override
            public void onOverLoopEvent() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnfresh.setEnabled(true);
                    }
                });
            }
        });*/
        //
        btnfresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSendUDP();
                setScaningProgress(true);
                popupWindowTimer.start();
                btnConnect.setText("Connect");
                qDebug.qLog("begin scan device now.....");
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCanClick) {
                    SomethingMsg fmsg = new SomethingMsg(DStatic.Msg_ID_BeginConnect);
                    if(DStatic.Model){
                        if (TCPClient.getInstance().isConnected()) {
                            fmsg.HValue = DStatic.MDisconnect; //disconnect
                            btnConnect.setText("Connect");
                        } else if (mAdatper.getmSelectindex() >= 0) {
                            fmsg.HValue = DStatic.MConnect;//connect
                            btnConnect.setText("Disconnect");
                        }
                    }
                    else {
                        if (!MainActivity.cuurentIp.isEmpty()) {
                            fmsg.HValue = DStatic.MDisconnect; //disconnect
                            btnConnect.setText("Connect");
                        } else if (mAdatper.getmSelectindex() >= 0) {
                            fmsg.HValue = DStatic.MConnect;//connect
                            btnConnect.setText("Disconnect");
                        }
                    }
                    CmdSender.sendBroadCastMsg_NoteMainActivity(getContext(), fmsg);
                    clickConnectTimer.start();
                    isCanClick = false;
                } else {
                    Toast.makeText(getActivity(), "Cann't repeat within 3 seconds", Toast.LENGTH_LONG).show();
                }


            }
        });

        //
        //initialTimer();


    }


    public void addToDevIPList(String strIP) {

        if (IpManager.isIPV4(strIP)) {
            int i = 0;
            boolean isExist = false;
            for (i = 0; i < m_DevIPFilterList.size(); i++) {
                if (strIP.equals(m_DevIPFilterList.get(i))) {
                    isExist = true;
                    break;
                }

            }
            if (!isExist) {
                m_DevIPFilterList.add(strIP);
            }

        }

    }

    //region mainhander process
    //扫描的结果通过MainHandler进行处理
    private Handler MainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case WiStaticComm.AfterScan_toCheckIP: {
                    if(DStatic.Model){
                        detectedCount++;

                        if (IpManager.getInstance().getScanedIPCount() > 0 && detectedCount > 0) {
                            detectedCount = 0;
                            qDebug.qLog("begin checkip after scan below..."+IpManager.getInstance().getScanedIPCount()); //是一次性出现的

                            //  SomethingMsg fmsg=new SomethingMsg(DStatic.Msg_ID_BeginCheckIP);
                            //   CmdSender.sendBroadCastMsg_NoteMainActivity(getContext(),fmsg);

                            cachedThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < IpManager.getInstance().getScanedIPCount(); i++) {
                                        String tmpIP = IpManager.getInstance().getScanedIpList().get(i);
                                        if (!isCheckedIPExist(tmpIP)) {
                                            FastTCPConnect(tmpIP);
                                            qDebug.qLog("begin connet with fast way...");
                                        }
                                    }
                                }
                            });

                        } else {
                            sendEmptyMessage(WiStaticComm.NOT_FIND_IP);
                            btnfresh.setEnabled(true);
                        }
                    }
                    else {
//                        if(!UdpLongUtil.getInstance().CONN){
//                            UdpLongUtil.getInstance().init(getContext());
//                            UdpLongUtil.getInstance().startLongConn();
//                        }
                        UdpLongUtil.getInstance().notifySendThread(CmdSender.getPackage_searchDevice());





                    }
                }
                break;

                case WiStaticComm.HAS_FIND_IP: {     //has converted IP and mac here to connected now..
                    //  if (++detectedCount < 2) break;
                    DevInfo devObj = (DevInfo) msg.obj;
                    final String strIP = devObj.devceAddr;
                    if (IpManager.isIPV4(strIP) && !isNewDevIP(strIP)) {
                        addToDevIPList(strIP);
                        setLoadingProgress(false);
                        qDebug.qLog("hereGo", "has_findIP: " + devObj.devceAddr + " ; " + devObj.devMac + " devname is " + devObj.getDevName());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnfresh.setEnabled(true);
                                edRemoteDev.setText(strIP);
                                // refreshGUI_leftMenu_deviceCount(IpManager.getInstance().getDevMacList());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdatper = new MutilHeadAdapter(getActivity().getApplicationContext(), m_listDatas);
                                        ipLv.setAdapter(mAdatper);
                                        refreshListV();
                                    }
                                });

                                lbDevNum.setText("" + IpManager.getInstance().getDevMacList());

                            }
                        });
                    }

                }
                break;
                case WiStaticComm.NOT_FIND_IP: {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "No device found.", Toast.LENGTH_LONG).show();
                            setScaningProgress(false);
                            btnfresh.setEnabled(true);
                        }
                    });
                }
                break;

            }
        }
    };

    public void addDevice(final String strIP) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnfresh.setEnabled(true);
//                edRemoteDev.setText(strIP);
                // refreshGUI_leftMenu_deviceCount(IpManager.getInstance().getDevMacList());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdatper = new MutilHeadAdapter(getActivity().getApplicationContext(), m_listDatas);
                        ipLv.setAdapter(mAdatper);
                        refreshListV();
                    }
                });

                lbDevNum.setText("" + IpManager.getInstance().getDevMacList());

            }
        });
    }

    private List<String> m_CheckIPList = new ArrayList<String>();

    public boolean isCheckedIPExist(String strIP) {

        if (IpManager.isIPV4(strIP)) {
            int i = 0;
            boolean isExist = false;
            for (i = 0; i < m_CheckIPList.size(); i++) {
                if (strIP.equals(m_CheckIPList.get(i))) {
                    isExist = true;
                    break;
                }

            }
            if (!isExist) {
                m_CheckIPList.add(strIP);
            }
            return isExist;
        } else
            return false;


    }

    //endregion
    public boolean isNewDevIP(String strIP) {

        int i = 0;
        boolean isExist = false;
        for (i = 0; i < m_DevIPFilterList.size(); i++) {
            if (strIP.equals(m_DevIPFilterList.get(i))) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }


    public void setLoadingProgress(final boolean sts) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //Thread.sleep(2000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setScaningProgress(sts);

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setScaningProgress(boolean isVisible) {
        if (isVisible) {
            loadProgres.setVisibility(View.VISIBLE);
            btnfresh.setEnabled(false);
        } else {
            loadProgres.setVisibility(View.INVISIBLE);
            btnfresh.setEnabled(true);
        }

    }

    public void FastTCPConnect(final String ip) {

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    qDebug.qLog("Enter into fast connect now with str ip " + ip);
                    Socket socket = new Socket(ip, WiStaticComm.rDevPort);
                    socket.setSoTimeout(3000);
                    byte[] m_data = CmdSender.getPackage_searchDevice();
                    qDebug.fprintBytes("william","sendcmd-search device",m_data);
                    TCPFastClient fastClient = new TCPFastClient(socket, m_data);
                    fastClient.setFastClientEvent(new TFastClientEvent() {
                        @Override
                        public void onFastConnected(Object sender, boolean conStatus, int conFlag) {

                        }

                        @Override
                        public void onFastReceive(Object sender, byte[] mdata, int length, int conFlag) {
                            for (int i = 0; i < length; i++)
                                iReadFastByte(sender, mdata[i], conFlag);
                        }
                    });
                    fastClient.startListener();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }


    private List<Byte> m_checkRecord = new ArrayList<Byte>();

    private final int MinPackLen = 13;

    private void iReadFastByte(Object sender, final byte ch, final int conflag) {
        if (m_checkRecord.size() == 0 && ch != UTRAL_H0) {
            m_checkRecord.clear();
            return;
        }
        if (m_checkRecord.size() == 1 && ch != UTRAL_H1) {
            m_checkRecord.clear();
            return;
        }
        if (m_checkRecord.size() == 2 && ch != UTRAL_H2) {
            m_checkRecord.clear();
            return;
        }
        m_checkRecord.add((byte) (ch&0xff));
        //01-20-03-00-28-ff-ff-ff-ff-00-03-00-06-10-00-4d-61-74-72-69-78-20-41-38-20-42-00-00-00-00-00-15-12-ff-ff-ff-ff-ff-a7-40
          qDebug.fprintBytes("william", "---iReadbytes------", m_checkRecord);
        if (m_checkRecord.size() >= MinPackLen) {
            int rlen = m_checkRecord.get(3) * Len_Big_Time + m_checkRecord.get(4)&0xff;
//            if(rlen<m_checkRecord.size()&&m_checkRecord.get(m_checkRecord.size()-1)==40){
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startSendUDP();
//                        setScaningProgress(true);
//                        popupWindowTimer.start();
//                        btnConnect.setText("Connect");
//                    }
//                });
//            }


            if (m_checkRecord.size() == rlen && m_checkRecord.get(rlen - 1) == UTRAL_End) {

                qDebug.fprintBytes("william", "---iReadbytes on fastr connect------", m_checkRecord);
                byte[] tmp = new byte[rlen];
                int appID = Xover.combineHLByte(m_checkRecord.get(5), m_checkRecord.get(6), WiStaticComm.BOne);                //----------
                int devID = Xover.combineHLByte(m_checkRecord.get(7), m_checkRecord.get(8), WiStaticComm.BOne);
//                if (appID == DStatic.SCAN_ALL_APID && m_checkRecord.size() == 40) {

                    int subApID = m_checkRecord.get(11) * 256 + m_checkRecord.get(12);
//                    if (subApID == DStatic.AP_MA8) //filter only for matrix A8
//                    {
                        Socket msock = (Socket) sender;
                        String mip = msock.getInetAddress().toString().substring(1);
                        //
                        //  qDebug.qLog("mained ip checking now.... ");
                        byte[] tmpB = new byte[rlen];
                        // qDebug.qLog("receivebytes here conflag is " + conflag + "  comand " + m_checkRecord.get(10));
                        for (int i = 0; i < rlen; i++)
                            tmpB[i] = m_checkRecord.get(i);
                        if(tmpB[12]==0x06&&tmpB[13]==0x01)
                            procesReceivePack_ScanedResponsedCheck(mip, tmpB);

//                        m_checkRecord.clear();
//                    }

//                }
                m_checkRecord.clear();

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().post(new E_refreshConnectInfo(TCPClient.getInstance().isConnected()));
        qDebug.qLog("onstart now......");

    }

    public void procesReceivePack_ScanedResponsedCheck(final String strIP, final byte[] fdatas) {
        //1
        if (fdatas.length < MinPackLen + 7) return;
        int cmd = (byte) (fdatas[9] * Len_Small_Times + fdatas[10]);
        int appID = Xover.combineHLByte(fdatas[11], fdatas[12], WiStaticComm.BOne);
        int devID = Xover.combineHLByte(fdatas[13], fdatas[14], WiStaticComm.BOne);

        switch (cmd) {

            case 3: {
                if (!IpManager.getInstance().isThisDeviceExist(strIP)) {
                    DevInfo mdevInfo = new DevInfo();
                    mdevInfo.devceAddr = strIP;
                    mdevInfo.mAppID = appID;
                    mdevInfo.mDevID = devID;
                    mdevInfo.Hver = fdatas[30];
                    mdevInfo.lVer = fdatas[30];

                    //deviceName
                    for (int i = 0; i < DStatic.Len_DevName; i++) {
                        mdevInfo.m_devName[i] = fdatas[i + 15];
                    }
                    IpManager.getInstance().addIPMacDev(mdevInfo);


                    qDebug.qLog("response check readdevice name..........." + mdevInfo.getDevName() + " with count is " + IpManager.getInstance().getScanedIPCount()
                            + " scan devlist count " + IpManager.getInstance().scanedIPMacDevList.size()

                    );
                    //  Looper.prepare();
                    addDevice(strIP);

                }


            }
            break;
        }


    }


    CountDownTimer popupWindowTimer = new CountDownTimer(7500, 10) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            loadProgres.setVisibility(View.INVISIBLE);
            btnfresh.setEnabled(true);
        }
    };

    CountDownTimer clickConnectTimer = new CountDownTimer(3500, 10) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            isCanClick = true;
        }
    };
}
