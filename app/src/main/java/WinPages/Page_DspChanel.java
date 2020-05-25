package WinPages;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import Datas.DStatic;
import Events.E_closeAllFocuse;
import Events.E_refreshChannelName;
import Events.E_refreshWholePage;
import Events.recallGain;
import Events.showChannelPgaeEvent;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.qDebug;
import newfragment.InputPage1;
import newfragment.InputPage2;
import newfragment.OutputPage1;
import newfragment.OutputPage2;
import newfragment.commDspFragment;

import static Datas.XData.gInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Page_DspChanel#} factory method to
 * create an instance of this fragment.
 */
public class Page_DspChanel extends Fragment {
    public static final int First = 0, Second = 1, Third = 2, Fourth = 3;
    commDspFragment[] mFragments = new commDspFragment[4];
    int count = 0;
    private Fragment mContent;
    private Button[] tabBtns = new Button[4];

    Unregistrar mkeybordUnregistar;

    public Page_DspChanel() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //registerDspReceiver();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview = inflater.inflate(R.layout.fragment_page_dsp_chanel, container, false);
        //initGUI(mview);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initChildFragment();
        initTabBtn(mview);
        initDefault();

        mkeybordUnregistar = KeyboardVisibilityEvent.registerEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (!isOpen) {

                    EventBus.getDefault().post(new recallGain());

                    qDebug.qLog("keybor is " + (isOpen ? "is open" : "hiden"));
                }


            }
        });
        switchFragment();
        return mview;
    }

    private EditText ed_newChName;
    private TextView lb_oldChName;


    /*****************************************************************
     * @author williamXia
     * created at 2018/4/28 11:26
     * chindex:[0..23]
     ******************************************************************/
    public void showChangeChanelName(final int chindex) {
        EventBus.getDefault().post(new E_closeAllFocuse());
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle("Change Channel Name")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //withMessageColor(int resid)
                .withDialogColor("#373737")                               //withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.icon_76))
                .isCancelableOnTouchOutside(false)                           //isCancelable(true)
                .withDuration(800)                                          //
                .withEffect(Effectstype.Fadein)                         //def Effectstype.Slidetop
                .withButton1Text("Cancel")
                .withButton2Text("Ok")
                .setCustomView(R.layout.change_channelname_dialog, getActivity())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() { //cancel button click
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        ed_newChName.setText("");

                    }
                })
                .setButton2Click(new View.OnClickListener() {  //tijiao
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {  //OK button click
                        dialogBuilder.dismiss();
                        String strName = ed_newChName.getText().toString();
                        qDebug.qLog("you input the new channel name is  with chindex is   " + strName + "    " + chindex);
                        gInstance().setChanelName(chindex, strName);
                        ed_newChName.setText("");
                        if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                            gInstance().sendCMD_SaveChannelName(chindex);
                        } else {

                            int flag = chindex / 6;
                            mFragments[flag].refreshChannelName();

                        }
                        EventBus.getDefault().post(new E_closeAllFocuse());


                    }

                }).show();
        ed_newChName = (EditText) dialogBuilder.tempView.findViewById(R.id.ed_new_chanelName);
        lb_oldChName = (TextView) dialogBuilder.tempView.findViewById(R.id.tv_oldChanelName);

        ed_newChName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ed_newChName.setFocusableInTouchMode(true);
                return false;
            }
        });
        String tmpStr = "";
        if (chindex < 12)
            tmpStr = String.format("Input Channel: %s", gInstance().getChanelName(chindex));
        else
            tmpStr = String.format("Output Channel: %s", gInstance().getChanelName(chindex));
        lb_oldChName.setText(tmpStr);

    }


    private void initDefault() {
        onTabBtnAct(gInstance()._dspPageIndex);
    }

    private void initTabBtn(View v) {
        tabBtns[0] = (Button) v.findViewById(R.id.btn_tab_input1);
        tabBtns[1] = (Button) v.findViewById(R.id.btn_tab_input2);
        tabBtns[2] = (Button) v.findViewById(R.id.btn_tab_output1);
        tabBtns[3] = (Button) v.findViewById(R.id.btn_tab_output2);
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            tabBtns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new showChannelPgaeEvent(finalI, 0));
                    EventBus.getDefault().post(new recallGain());

                }
            });
        }
    }

    private void onTabBtnAct(int page) {
        for (int i = 0; i < 4; i++) {
            if (page == i) {
                tabBtns[i].setActivated(true);
            } else {
                tabBtns[i].setActivated(false);
            }
        }
    }

    private void switchFragment() {

        FragmentManager fmanger = getActivity().getSupportFragmentManager();
        FragmentTransaction beginTransaction = fmanger.beginTransaction();
        beginTransaction
                .add(R.id.al_channel_content_area, mFragments[First]);
        beginTransaction.commitAllowingStateLoss();
        mContent = mFragments[First];
    }


    private void initChildFragment() {
        mFragments[First] = InputPage1.newInstance(this);
        mFragments[Second] = InputPage2.newInstance();
        mFragments[Third] = OutputPage1.newInstance();
        mFragments[Fourth] = OutputPage2.newInstance();

    }

    int enterType = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPage(showChannelPgaeEvent event) {
        enterType = event.getType();
        int page = event.getPage();

        gInstance()._dspPageIndex = page;
        qDebug.qLog("switch dsp pageindex is  " + page);
        onTabBtnAct(gInstance()._dspPageIndex);
        if (enterType == 0) {
            //右往左
            qDebug.qLog("game","from right to left slidernow.......");
            showContentInFromRight(mFragments[page]);
        } else {
            //左往右
            showContentInFromLeft(mFragments[page]);
            qDebug.qLog("game","from left to right slidernow.......");
        }
        EventBus.getDefault().post(new E_refreshWholePage());//更新界面


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDspPageChannelName(E_refreshChannelName event) {
        int pgindex = event.getPageindex();
        mFragments[pgindex].refreshChannelName();
        qDebug.qLog("update by event bus now........");
    }


    /**
     * 修改显示的内容 不会重新加载
     **/
    public void showContentInFromLeft(Fragment to) {
        try {
            if (mContent != to) {

                FragmentManager fmanger = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fmanger.beginTransaction();
                // transaction.setCustomAnimations(R.anim.in_from_left, R.anim.out_from_right);
                if (!to.isAdded()) { // 先判断是否被add过
                    transaction.hide(mContent)
                            .add(R.id.al_channel_content_area, to)
                            .commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
                } else {
                    transaction.hide(mContent).show(to)
                            .commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
                }
                mContent = to;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void showContentInFromRight(Fragment to) {
        try {
            if (mContent != to) {
                FragmentManager fmanger = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fmanger.beginTransaction();
                //transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_from_left);
                // transaction.setCustomAnimations()
                if (!to.isAdded()) { // 先判断是否被add过
                    transaction.hide(mContent)
                            .add(R.id.al_channel_content_area, to)
                            .commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
                } else {
                    transaction.hide(mContent).show(to)
                            .commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
                }
                mContent = to;
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mkeybordUnregistar.unregister();
    }

}
