package newfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import Datas.DStatic;
import Datas.XData;
import Events.E_closeAllFocuse;
import Events.E_refreshWholePage;
import Events.recallGain;
import Events.recallMute;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.control.CSlider;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.CButton;

import static newfragment.InputPage1.mPage_dspChanel;

/**
 * Created by zhihuafeng on 3/23/2018.
 */

public class OutputPage1 extends commDspFragment {
    public static OutputPage1 newInstance() {
        Bundle args = new Bundle();
        OutputPage1 fragment = new OutputPage1();
        fragment.setArguments(args);
        return fragment;
    }

    View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.channel_output_page1, container, false);
        ButterKnife.bind(this, contentView);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initLayout();
        init();
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshPage();
        refreshSingleSlider(DStatic.aChindex);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPage(E_refreshWholePage event) {
        refreshPage();
        refreshSingleSlider(DStatic.aChindex);
    }


    @BindView(R.id.ar_output_page1)
    ConstraintLayout arInputLayout;

    ConstraintLayout[] itemLayouts = new ConstraintLayout[6];

    private void initLayout() {
        itemLayouts[0] = ButterKnife.findById(arInputLayout, R.id.al_input1);
        itemLayouts[1] = ButterKnife.findById(arInputLayout, R.id.al_input2);
        itemLayouts[2] = ButterKnife.findById(arInputLayout, R.id.al_input3);
        itemLayouts[3] = ButterKnife.findById(arInputLayout, R.id.al_input4);
        itemLayouts[4] = ButterKnife.findById(arInputLayout, R.id.al_input5);
        itemLayouts[5] = ButterKnife.findById(arInputLayout, R.id.al_input6);
    }

    CButton[] btn = new CButton[6];
    EditText[] tvValue = new EditText[6];
    CSlider[] sbGain = new CSlider[6];
    CButton[] tvChannel = new CButton[6];
    TextView[] tv_old = new TextView[6];
    @Override
    public void refreshSingleSlider(int index) {
        for (int i = 0; i < sbGain.length; i++) {
            sbGain[i].setLighted(false);
        }
        if (index < sbGain.length && index >= 0)
            sbGain[index].setLighted(true);

    }
    private void init() {
        for (int i = 0; i < 6; i++) {
            btn[i] = ButterKnife.findById(itemLayouts[i], R.id.btn_mute);
            btn[i].setiTag(i + DStatic.ChanlMax);//12-17
            tvValue[i] = ButterKnife.findById(itemLayouts[i], R.id.tv_vdB);
            sbGain[i] = ButterKnife.findById(itemLayouts[i], R.id.seekBar_x1);
            sbGain[i].setiTag(i + DStatic.ChanlMax);//12-17
            tvChannel[i] = ButterKnife.findById(itemLayouts[i], R.id.tv_channel);
            tvChannel[i].setText(DStatic.StrChanelList1[i]);
            tv_old[i] = ButterKnife.findById(itemLayouts[i], R.id.tv_oldChanel);
            tv_old[i].setText(DStatic.StrChanelList1[i]);


            tvValue[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText edTxt = (EditText) view;
                    Xover.setEditTextStatus(edTxt, true);
                    edTxt.requestFocus();
                    edTxt.findFocus();
                    qDebug.qLog("input channel name editor click now");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edTxt, InputMethodManager.SHOW_FORCED);
                }
            });

            final int finaI = i;
            tvValue[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    EditText text = (EditText) view;
                    if (b) {
                        tvValue[finaI].setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                        text.setText("");
                        // qDebug.qLog("edit text on foucuse now.....");
                    } else {
                        // qDebug.qLog("edit text lost foucuse now.....");
                        tvValue[finaI].setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                        if (TextUtils.isEmpty(text.getText()))
                            text.setText(XData.gInstance().strIGain(finaI + 12));
                        Xover.setEditTextStatus(text, false);
                        DStatic.updateGainEdit(tvValue[finaI], sbGain[finaI], finaI + 12);
                    }

                }
            });

            tvValue[finaI].setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View view, int i, KeyEvent keyEvent) {

                                                    if (i == KeyEvent.KEYCODE_ENTER) {
                                                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        if (imm.isActive()) {
                                                            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                                                            if (tvValue[finaI].isFocused()) {
                                                                DStatic.updateGainEdit(tvValue[finaI], sbGain[finaI], finaI + 12);

                                                            }
                                                            view.clearFocus();
                                                            return true;
                                                        }
                                                    }
                                                    return false;
                                                }
                                            }


            );


            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CButton btn = (CButton) v;
                    //int index = btn.getiTag();
                    int chindex = btn.getiTag();//12-17
                    byte tmp;
                    if (btn.isActivated()) {
                        tmp = 0;
                    } else {
                        tmp = 1;
                    }
                    //-----
                    XData.gInstance().m_chEdit[chindex].chMute = tmp;
                    if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                        XData.gInstance().sendCMD_ChanlMute(IpManager.getInstance().getSelDevID());
                    } else {
                        refreshChanelMute();
                    }
                }
            });

            sbGain[i].setChangeListenner(new CSlider.SliderChangeListenner() {
                @Override
                public void onSliderTouchDown(Object sender) {
                    CSlider mder = (CSlider) sender;
                   // qDebug.qLog("mmf", "output slider tag is : " + mder.getiTag());
                    DStatic.aChindex = mder.getiTag()-12;
                    refreshSingleSlider(mder.getiTag()-12);
                }

                @Override
                public void onSliderChange(Object sender, int pvalue) {
                    CSlider sb = (CSlider) sender;
                    int channel = sb.getiTag();
                    XData.gInstance().m_chEdit[channel].chGain = (int) pvalue;
                    if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                        XData.gInstance().sendCMD_FaderGain(channel, IpManager.getInstance().getSelDevID());
                    } else {
                        refreshFader(channel, true);
                    }
                }
            });
            tvChannel[i].setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //  qDebug.qLog("channName text click now");
                                                    mPage_dspChanel.showChangeChanelName(finaI + 12);

                                                }
                                            }

            );

        }
    }

    @Override
    public void closeFoucse() {
        for (int i = 0; i < 6; i++) {
            Xover.setEditTextStatus(tvValue[i], false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCloseAllFocus(E_closeAllFocuse event) {
        closeFoucse();
    }

    @Override
    public void refreshChannelName() {
        super.refreshChannelName();
        for (int i = 12; i < 18; i++)
            tvChannel[i - 12].setText(XData.gInstance().getChanelName(i));
    }


    @Override
    public void refreshVolumeFader() {

        for (int i = 12; i < 18; i++) {
            refreshFader(i, true);
        }
    }

    @Override
    public void refreshChanelMute() {

// btnIMute
        for (int i = 0; i < 6; i++) {
            btn[i].setActivated(XData.gInstance().iMute(i + DStatic.channelGap * 2) > 0);
        }
    }


    /**
     * 刷新控制条
     *
     * @param channel        0-5
     * @param isNeedSetFader 是否需要给SeekBar赋值
     *                       12..17]
     */
    public void refreshFader(int channel, boolean isNeedSetFader) {
        if (isNeedSetFader) {
            int oGain = XData.gInstance().iGain(channel);
            sbGain[channel - 12].setValue(oGain);
        }
        //
        tvValue[channel - 12].setText(XData.gInstance().strIGain(channel));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recallGain(recallGain event) {
        refreshVolumeFader();
        qDebug.qLog("mvc","outputpage1 refresh chindex is "+DStatic.aChindex);
        refreshSingleSlider(DStatic.aChindex);
        closeFoucse();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recallMute(recallMute event) {
        refreshChanelMute();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < 6; i++) {
            sbGain[i].releaseBitmapEx();
        }
        EventBus.getDefault().unregister(this);
    }
}
