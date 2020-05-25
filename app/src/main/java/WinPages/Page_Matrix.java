package WinPages;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import Adapters.CMatrixDataAdapter;
import Adapters.ChannelListSelecterAdapter;
import Datas.DStatic;
import Datas.XData;
import Events.E_refreshChannelName;
import Events.MatrixRecall;
import Events.refreshMatrix;
import Interfaces.CMatrixStatusListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.CButton;
import comcontrol.CPreTextView;

import static Datas.XData.gInstance;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Page_Matrix#} factory method to
 * create an instance of this fragment.
 */
public class Page_Matrix extends Fragment {

    private int totalChannel = 20;
    @BindView(R.id.al_output_select)
    ConstraintLayout alOutputSelect;
    @BindView(R.id.btn_output_last)
    Button btnLast;
    @BindView(R.id.btn_output_next)
    Button btnNext;
    @BindView(R.id.lv_matrix)
    ListView m_matrixView;


    @BindView(R.id.tv_output_index)
    TextView tvOutIndex;
    private PopupWindow popupWindow;
    public static final int Max_singleNum = 20;
    matrixOnOffListener mOnOffListener;

    private ChannelListSelecterAdapter OutputChListAdapter;
    private CMatrixDataAdapter m_matrixAdapter;


    public Page_Matrix() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //inputBtns.clear();
        //getActivity().unregisterReceiver(mMatrixRecever);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //IntentFilter filter = new IntentFilter(DStatic.Action_MatrixFrag);
        //getActivity().registerReceiver(mMatrixRecever, filter);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_page_matrix, container, false);
        //initGUI(mview);
        ButterKnife.bind(this, contentView);
        qDebug.qLog("page matrix ...createview");
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        setViews();
        initPopwindow();
        setListener();
        // initControls();
        initOutputList();
        Log.d("listview1",m_matrixView+"");
        return contentView;

    }

    @Override
    public void onStart() {
        super.onStart();
        refreshAll_buttonState(gInstance().mRoutChanel,true);
        refreshInputNames();
    }

    @BindView(R.id.al_Matrix_parent)
    ConstraintLayout alParent;
    @BindView(R.id.lv_output)
    ListView lv_output;

    private void initOutputList() {
        OutputChListAdapter = new ChannelListSelecterAdapter(getActivity());
        lv_output.setAdapter(OutputChListAdapter);
        lv_output.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gInstance().mRoutChanel = position;// 当前选中channnel
                qDebug.qLog("checkedId=====" + position);
                qDebug.qLog("XData.gInstance().mRoutChanel====" + gInstance().mRoutChanel);
                tvOutIndex.setText("" + gInstance().getShowNameOut(gInstance().mRoutChanel));
                EventBus.getDefault().post(new refreshMatrix());
                OutputChListAdapter.setmSelectindex(position);

                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

    }

    //List<CButton> inputBtns = new ArrayList<CButton>();

    private List<CButton> m_inputBtnList = new ArrayList<CButton>();
    private List<CPreTextView> m_chviewList = new ArrayList<CPreTextView>();


    private void initControls() {
        if (mOnOffListener == null)
            mOnOffListener = new matrixOnOffListener();
        if (m_inputBtnList == null)
            m_inputBtnList = new ArrayList<CButton>();
        if (m_chviewList == null)
            m_chviewList = new ArrayList<CPreTextView>();
        //--------
        for (int i = 0; i < 20; i++) {

            // final matrixBean MatrixData = listMatrixs.get(i);
            LinearLayout llInputItem = new LinearLayout(getActivity());
            llInputItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            llInputItem = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
                    R.layout.wr_area_item, null);

            CButton btnOnOff = (CButton) llInputItem.findViewById(R.id.btn_item_ONOFF);
            CPreTextView tvNum = (CPreTextView) llInputItem.findViewById(R.id.tv_item_num);
            CPreTextView tvChannel = (CPreTextView) llInputItem.findViewById(R.id.tv_item_channel);
            btnOnOff.setiTag(i);//0-19
            //   btnOnOff.setActivated(MatrixData.isAct());
            //inputBtns.add(btnOnOff);
            //    tvNum.setText("" + MatrixData.No);
            //  qDebug.qLog("xxx", "matrix channel name is " + MatrixData.getChannelName());
            //    tvChannel.setText("" + MatrixData.getChannelName());
            //    alTableArea.addView(llInputItem);
            m_inputBtnList.add(btnOnOff);
            m_chviewList.add(tvChannel);
            btnOnOff.setOnClickListener(mOnOffListener);
        }


    }

    private void showData() {
        //--------


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshChNames(E_refreshChannelName event) {
        refreshInputNames();
        qDebug.qLog("change name note event.....");

    }

    public class matrixOnOffListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CButton btn = (CButton) v;
            btn.setActivated(!btn.isActivated());
            int inputChannel = btn.getiTag();
            byte tmp = 0;
            if (btn.isActivated()) {
                tmp = 1;
            } else {
                tmp = 0;
            }
            btn.setText(btn.isActivated() ? "ON" : "OFF");
            DStatic.m_vrValue[inputChannel] = tmp;
            qDebug.qLog("mRoutChanel================" + gInstance().mRoutChanel+"     "+"inputChannel================" + inputChannel);
            gInstance().m_matrixAry[gInstance().mRoutChanel][inputChannel] = DStatic.m_vrValue[inputChannel];
            if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                gInstance().sendCMD_matrixWithChanel(gInstance().mRoutChanel, IpManager.getInstance().getSelDevID());
            }
        }
    }


    private void setListener() {
        alOutputSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrClosePopMenu(view);
            }
        });

        btnLast.setOnClickListener(new View.OnClickListener() {// 上一层
            @Override
            public void onClick(View v) {
                if (gInstance().mRoutChanel > 0) {
                    --gInstance().mRoutChanel;
                    if (gInstance().mRoutChanel < 0) gInstance().mRoutChanel = 0;
                    qDebug.qLog("XData.gInstance().mRoutChanel======" + gInstance().mRoutChanel);
                    tvOutIndex.setText(gInstance().getShowNameOut(gInstance().mRoutChanel));
                    EventBus.getDefault().post(new refreshMatrix());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {// 下一层
            @Override
            public void onClick(View arg0) {
                if (gInstance().mRoutChanel < totalChannel) {
                    ++gInstance().mRoutChanel;
                    if (gInstance().mRoutChanel > 19) gInstance().mRoutChanel = 19;
                    qDebug.qLog("XData.gInstance().mRoutChanel======" + gInstance().mRoutChanel);
                    tvOutIndex.setText(gInstance().getShowNameOut(gInstance().mRoutChanel));

                    EventBus.getDefault().post(new refreshMatrix());
                }
            }
        });

    }

    private void initPopwindow() {  //

        popupWindow = new PopupWindow(lv_output, DStatic.Pb_width_lstV, DStatic.Pb_height_lstV);
        initPopWnd(popupWindow);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMatrixRefresh(refreshMatrix event) {
        refreshAll_buttonState(gInstance().mRoutChanel,false);
        qDebug.qLog("matrix refresh all by thresd mode with chindiex is  " + gInstance().mRoutChanel);

    }

    /**
     * 弹出菜单
     *
     * @param view
     */
    public void openOrClosePopMenu(View view) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            lv_output.setVisibility(View.INVISIBLE);
        } else {
            alParent.removeView(lv_output);
            popupWindow.showAsDropDown(view, 0, 0);
            popupWindow.setWidth(alOutputSelect.getWidth());

            lv_output.setVisibility(View.VISIBLE);
        }
    }


    private void initPopWnd(PopupWindow popWd) {

        if (popWd != null) {
            popWd.setOutsideTouchable(true);
            popWd.setAnimationStyle(android.R.style.Animation_Activity);
            popWd.update();
            popWd.setTouchable(true);
            popWd.setFocusable(true);
        }
    }


    public void refreshInputNames() {

        tvOutIndex.setText("" + gInstance().getShowNameOut(gInstance().mRoutChanel));
        if (OutputChListAdapter != null)
            OutputChListAdapter.notifyDataSetInvalidated();
     //   if (m_matrixAdapter != null)
        //    m_matrixAdapter.notifyDataSetInvalidated();
    }


    public void refreshData(boolean isAll) {

//        if (m_matrixAdapter == null) {
//            m_matrixAdapter = new CMatrixDataAdapter(getActivity(), gInstance().m_beanList,m_matrixView);
//            m_matrixView.setAdapter(m_matrixAdapter);
//        }

        // m_matrixAdapter.m_beanList(gInstance().m_beanList);
       // m_matrixView.setAdapter(m_matrixAdapter);
        qDebug.qLog("xmm", "adpter refresh now..", "d " + gInstance().m_beanList.get(0).isAct());
        if (isAll)
            m_matrixAdapter.notifyDataSetInvalidated();
        else
            m_matrixAdapter.updataView(m_matrixView);
//        m_matrixView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE){
//                    return true;
//                }
//                return false;
//            }
//        });
    }
    private void setViews() {
        tvOutIndex.setText(gInstance().getShowNameOut(gInstance().mRoutChanel));
        if (m_matrixAdapter == null) {
            m_matrixAdapter = new CMatrixDataAdapter(getActivity(), gInstance().m_beanList,m_matrixView);
            m_matrixAdapter.mMatrixStatusListener = new CMatrixStatusListener() {
                @Override
                public void OnMatrixStateClickEvent(View view) {
                    CButton btn = (CButton) view;

                    int inputChannel = btn.getiTag();

                    qDebug.qLog("mm", "holder btn off click now...outside " + inputChannel);
                    byte tmp = 0;
                    if (btn.isActivated()) {
                        tmp = 0;
                    } else {
                        tmp = 1;
                    }
                    //     btn.setText(btn.isActivated() ? "ON" : "OFF");
                    DStatic.m_vrValue[inputChannel] = tmp;
                    qDebug.qLog("mRoutChanel================" + gInstance().mRoutChanel+"     "+"inputChannel================" + inputChannel);
                    gInstance().m_matrixAry[gInstance().mRoutChanel][inputChannel] = DStatic.m_vrValue[inputChannel];
                    if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                        gInstance().sendCMD_matrixWithChanel(gInstance().mRoutChanel, IpManager.getInstance().getSelDevID());
                    } else {
                        refreshAll_buttonState(gInstance().mRoutChanel,false);

                    }


                }
            };

            m_matrixView.setAdapter(m_matrixAdapter);
        }

    }


    public void refreshAll_buttonState(int channel, boolean isAll) {
        System.arraycopy(gInstance().m_matrixAry[channel], 0, DStatic.m_vrValue, 0, 20);
        if (gInstance().m_beanList == null) return;
        for (int i = 0; i < 20; i++) {
            //  qDebug.qLog("m_vrValue[i]======="+i+" "+DStatic.m_vrValue[i]);
            //  m_inputBtnList.get(i).setActivated((DStatic.m_vrValue[i] == 1));
            //  m_inputBtnList.get(i).setText((DStatic.m_vrValue[i] == 1) ? "ON" : "OFF");
            if (i == 0)
                qDebug.qLog("matrix value is " + DStatic.m_vrValue[i]);
            gInstance().m_beanList.get(i).setAct(DStatic.m_vrValue[i]);

        }
        qDebug.qLog("fresh buton state all now.....");
        refreshData(isAll);
    }
   /* public void refreshSingleButtonStatus(int index){
        inputBtns.get(index).setActivated((m_vrValue[index]>0));
        inputBtns.get(index).setText((m_vrValue[index]>0)?"ON":"OFF");
    }*/


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ReceiveMatrix(MatrixRecall event) {
        gInstance().mRoutChanel = event.getChannel();
        qDebug.qLog("channel====" + gInstance().mRoutChanel + " begin refresh  recall ");
        qDebug.qLog("gain====" , XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][0]+"");
        tvOutIndex.setText(gInstance().getShowNameOut(gInstance().mRoutChanel));
        refreshAll_buttonState(gInstance().mRoutChanel,false);
        refreshInputNames();
    }


    /*
    public void updateSingleMatrix(final int chindex) {
        if (chindex >= 0 && chindex < mgroupList.size()) {
            mgroupList.get(chindex).refreshAll_buttonState();

        }


    }

    private BroadcastReceiver mMatrixRecever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DStatic.Action_MatrixFrag)) {
                final SomethingMsg msg = (SomethingMsg) intent.getSerializableExtra(DStatic.Msg_Key_Matrix);
                if (msg.fID.equals(DStatic.Msg_ID_Matrix)) {
                    switch (msg.HValue) {
                        case Command.F_MatrixMixer:
                            //begin update matrix
                        {
                            final int chindex = msg.LValue;
                            qDebug.qLog("matrix recievc chindex is :" + chindex);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateSingleMatrix(chindex);
                                }
                            });

                        }
                        break;
                        case DStatic.CPageUPdate:
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshWholePage();
                                }
                            });

                            break;
                    }
                }
            }

        }

    };




    @Override
    public void onStart() {
        super.onStart();
        refreshWholePage();
    }

    ScrollViewX mScrolv;
    HScrollViewX mHScrolv;
    private HScrollViewX topLbScrol;
    private ScrollViewX leftLbScroll;

    private void initGUI(View mv) {
        topLbScrol = (HScrollViewX) mv.findViewById(R.id.topLbScrol);
        leftLbScroll = (ScrollViewX) mv.findViewById(R.id.leftLbScroll);

        mScrolv = (ScrollViewX) mv.findViewById(mMatrixScrV);
        mScrolv.setScrollListener(new ScrollListener() {
            @Override
            public void onScrollChanged(int nx, int ny, int oldX, int oldY) {

                leftLbScroll.setScrollY(ny);
            }

            @Override
            public void onScrollStopped() {

            }

            @Override
            public void onScrolling() {
                //  qDebug.qLog("scrollview is scrolling.....");
            }
        });


        mHScrolv = (HScrollViewX) mv.findViewById(hrScroll);
        mHScrolv.setScrollListener(new ScrollListener() {
            @Override
            public void onScrollChanged(int nx, int ny, int oldX, int oldY) {
                //  qDebug.qLog("horizontal scrollview is changeed  with nx :  " + nx + "   ny " + ny);
                topLbScrol.setScrollX(nx);
            }

            @Override
            public void onScrollStopped() {

            }

            @Override
            public void onScrolling() {
                // qDebug.qLog("scrollview is scrolling.....");
            }
        });
        //  qDebug.qLog("begin initial in created view in fragment...");
        initSingleGroups(mv);
    }

    public void refreshWholePage() {
        CSingleVrButonGroup mgroup = null;
        for (int i = 0; i < 20; i++) {
            mgroup = mgroupList.get(i);
           System.arraycopy( XData.gInstance().m_matrixAry[i],0,mgroup.m_vrValue,0,20);
            mgroup.refreshAll_buttonState();
        }

    }


    private CSingleVrButonGroup singleGroup0;
    private CSingleVrButonGroup singleGroup1;
    private CSingleVrButonGroup singleGroup2;
    private CSingleVrButonGroup singleGroup3;
    private CSingleVrButonGroup singleGroup4;
    private CSingleVrButonGroup singleGroup5;
    private CSingleVrButonGroup singleGroup6;
    private CSingleVrButonGroup singleGroup7;
    private CSingleVrButonGroup singleGroup8;
    private CSingleVrButonGroup singleGroup9;
    private CSingleVrButonGroup singleGroup10;
    private CSingleVrButonGroup singleGroup11;
    private CSingleVrButonGroup singleGroup12;
    private CSingleVrButonGroup singleGroup13;
    private CSingleVrButonGroup singleGroup14;
    private CSingleVrButonGroup singleGroup15;
    private CSingleVrButonGroup singleGroup16;
    private CSingleVrButonGroup singleGroup17;
    private CSingleVrButonGroup singleGroup18;
    private CSingleVrButonGroup singleGroup19;

    private ArrayList<CSingleVrButonGroup> mgroupList = new ArrayList<CSingleVrButonGroup>();

    private void initSingleGroups(View mv) {
        if (mgroupList == null)
            mgroupList = new ArrayList<CSingleVrButonGroup>();
        else
            mgroupList.clear();
        singleGroup0 = (CSingleVrButonGroup) mv.findViewById(R.id.singleGroup_0);

//
        mgroupList.add(singleGroup0);
        mgroupList.add(singleGroup1);
        mgroupList.add(singleGroup2);
        mgroupList.add(singleGroup3);
        mgroupList.add(singleGroup4);
        //
        mgroupList.add(singleGroup5);
        mgroupList.add(singleGroup6);
        mgroupList.add(singleGroup7);
        mgroupList.add(singleGroup8);
        mgroupList.add(singleGroup9);//
        //
        mgroupList.add(singleGroup10);
        mgroupList.add(singleGroup11);
        mgroupList.add(singleGroup12);
        mgroupList.add(singleGroup13);
        mgroupList.add(singleGroup14);
        //
        mgroupList.add(singleGroup15);
        mgroupList.add(singleGroup16);
        mgroupList.add(singleGroup17);
        mgroupList.add(singleGroup18);
        mgroupList.add(singleGroup19);//
        CSingleVrButonGroup mgroup = null;
        for (int i = 0; i < 20; i++) {
            mgroup = mgroupList.get(i);
            mgroup.setSingleMatrixClickListener(this);
        }

    }

    @Override
    public void OnSingleMatrixClick(CSingleVrButonGroup sender, int col, int row) {
        //----
        qDebug.qLog("on slingle matrix clik col " + col + "  row is " + row);
        gInstance().m_matrixAry[row][col] = sender.m_vrValue[col];
        if (TCPClient.getInstance().isConnected()) {
            gInstance().sendCMD_matrixWithChanel(row, IpManager.getInstance().getSelDevID());
        } else {
            sender.refreshAll_buttonState();

        }

    }*/

    public void setm_matrixView(boolean enable){
        m_matrixView.setScrollingCacheEnabled(enable);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.d("listview",m_matrixView+"");
        }
    }
}
