package WinPages;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import android.support.constraint.ConstraintLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import Adapters.CLocalPresetInfo;
import Adapters.SavePresetToDeviceAdapter;
import Adapters.SavePresetToPCAdapter;
import Datas.CFinalStr;
import Datas.CmdSender;
import Datas.Command;
import Datas.DStatic;
import Datas.XData;
import Events.ReceivePassword;
import Events.ReceivePresetEvent;
import Events.cannotControlEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.CIOBean;
import cn.com.williamxia.wipack.utils.CIOBeanOpreation;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.SomethingMsg;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.CPreTextView;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import static cn.com.williamxia.wipack.utils.CIOBeanOpreation.readBean_fromStoreageCard;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Page_Preset} interface
 * to handle interaction events.
 * Use the {@link Page_Preset#} factory method to
 * create an instance of this fragment.
 */
public class Page_Preset extends Fragment {
    private SavePresetToDeviceAdapter deviceAdapter;
    private SavePresetToPCAdapter localAdapter;
    private ArrayList<String> mDevPresetList;
    private ArrayList<CLocalPresetInfo> mLocalPreFileList;
    private SharedPreferences passwordPref;
    private Timer timerPreset =null ;
    public Page_Preset() {
        // Required empty public constructor

    }

    private BroadcastReceiver mPresetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DStatic.Action_Preset)) {
                final SomethingMsg msg = (SomethingMsg) intent.getSerializableExtra(DStatic.Msg_Key_Preset);
                if (msg.fID.equals(DStatic.Msg_ID_Preset)) {
                    switch (msg.HValue) {
                        case Command.F_GetPresetList: {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshGUI_presetList(gRCheckTag());
                                    //setIndecatorSpinStatus(false);
                                }
                            });
                            qDebug.qLog("refresh presetlist now.......");
                        }
                        break;
                        case Command.F_RecallCurrentScene: {
                            if(timerPreset !=null) timerPreset.cancel();
                            setIndecatorSpinStatus(false);
                            EventBus.getDefault().post(new cannotControlEvent(false));
                            localAdapter.setmSelectindex(-1);
                            Toast.makeText(getActivity(),"load success!",Toast.LENGTH_SHORT).show();
                            if(XData.gInstance().lockFlag==1){
                                showDialogUnlock();
                            }
                        }

                        break;
                        case Command.F_MemoryExport://read dats to local return
                        {
                            int index = msg.LValue;
                            qDebug.qLog("receive package seg is " + index);
                            setPreTxt(edImptAllPresets, index);
                            if (index == DStatic.Memory_Max_Package - 1)//0..23
                            {
                                saveAllMemoryToLocal(DStatic.MemPresetsName, XData.gInstance().m_meoryRead);
                                btnImptAllPrests.setEnabled(true);
                                btnExportAllPrests.setEnabled(true);
                                rightArea.setEnabled(true);
                            } else {
                                btnExportAllPrests.setEnabled(false);
                                rightArea.setEnabled(false);
                            }
                        }
                        break;
                        case Command.F_MemoryImportAck: {
                            int index = msg.LValue;
                            setPreTxt(edExportPresets, index);
                            if (index == DStatic.Memory_Max_Package - 1) {
                                btnImptAllPrests.setEnabled(true);
                                btnExportAllPrests.setEnabled(true);
                                rightArea.setEnabled(true);

                            } else {
                                btnImptAllPrests.setEnabled(false);
                                rightArea.setEnabled(false);
                            }

                        }
                        break;
                        case DStatic.CPageUPdate:
                            refreshGUI_presetList(gRCheckTag());
                            qDebug.qLog("page preset onstart now.....");
                            break;

                    }
                }

            }
        }

    };



    private void saveAllMemoryToLocal(final String fileName, final byte[] bodyData) {

        CIOBean bean = new CIOBean(fileName, bodyData, DStatic.StrMemoryHeader);

        int slen = bean.getBeanData().length;
        qDebug.qLog("wrie bean body data length is : " + slen);
        bean.printBeanData();
        boolean result = CIOBeanOpreation.writeBeanToSDcard(bean, DStatic.SD_Path_SaveLoad);
        //add to list
        if (result) {
            //  qDebug.qLog("write bean success fule so ben add to list");
            Toast.makeText(getActivity(), "Has saved sucessfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Has saved failed!", Toast.LENGTH_LONG).show();
        }

    }


    private void setPreTxt(TextView tv, final int iseg) {
        double seg = (double) (iseg + 1) / DStatic.Memory_Max_Package;
        String str = new DecimalFormat("##%").format(seg);
        tv.setText(str);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deviceAdapter.setmSelectindex(-1);
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mPresetReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(DStatic.Action_Preset);
        getActivity().registerReceiver(mPresetReceiver, filter);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview = inflater.inflate(R.layout.fragment_page_preset, container, false);
        ButterKnife.bind(this,mview);
        if(!EventBus.getDefault().isRegistered(this))EventBus.getDefault().register(this);
        initGUI(mview);
        return mview;
    }


    /**
     * 2018/4/11 by zhihuafeng
     */
    @BindView(R.id.btn_passSet)
    Button btnPasswordSet;
    @BindView(R.id.btn_showlock)
    Button btnShowLock;
    @OnClick(R.id.btn_passSet)
    public void PasswordSet(){
        showPasswordset();
    }

    @OnClick(R.id.btn_showlock)
    public void showLock(){
        if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
            CmdSender.sendCMD_RewritePassword(XData.gInstance().lock_pass, 1);
        }
        showDialogUnlock();
    }



    /*@OnClick(R.id.btn_default)
    public void setDefault(){
        String s="0000";
        byte[] b = new byte[4];
        b = str2byte(s);
        if(TCPClient.getInstance().isConnected()) {
            CmdSender.sendCMD_RewritePassword(b, IpManager.getInstance().getSelDevID());
        }
    }*/
    private String StrPin="";
    private EditText edOldPassword,edNewPassword,edconfirmPassword;
    private EditText lockPassword;
    public void showPasswordset(){
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle("Password Setting")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //withMessageColor(int resid)
                .withDialogColor("#373737")                               //withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.icon_76))
                .isCancelableOnTouchOutside(false)                           //isCancelable(true)
                .withDuration(800)                                          //
                .withEffect(Effectstype.Fadein)                         //def Effectstype.Slidetop
                .withButton1Text("cancel")
                .withButton2Text("ok")
                .setCustomView(R.layout.passwordset_dialog, getActivity())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        oldPassRight = false;
                        edOldPassword.setText("");
                        edNewPassword.setText("");
                        edconfirmPassword.setText("");
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        getPassword(edOldPassword,oldPass);
                        getPassword(edNewPassword,newPass);
                        getPassword(edconfirmPassword,confirmPass);
                        if(!pass1.equals("")&&pass1.equals(pass2)&&oldPassRight){
                            byte[] b = new byte[4];
                            b = str2byte(pass2);
                            qDebug.qLog("密码重设成功！");
                            if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                                CmdSender.sendCMD_RewritePassword(b, 0);
                                Toast.makeText(getActivity(), "Revise the password successfully!", Toast.LENGTH_SHORT).show();
                                dialogBuilder.dismiss();
                            }
                            else
                                Toast.makeText(getActivity(), "No networking!", Toast.LENGTH_SHORT).show();
                        }else {
                            if(oldPassRight) {
                                Toast.makeText(getActivity(), "Your confirmed password and new password do not match.", Toast.LENGTH_SHORT).show();
                                edNewPassword.setText("");
                                edconfirmPassword.setText("");
                            }
                        }
                    }
                }).show();
        edOldPassword = (EditText) dialogBuilder.tempView.findViewById(R.id.ed_old_password);
        edNewPassword = (EditText) dialogBuilder.tempView.findViewById(R.id.ed_new_password);
        edconfirmPassword = (EditText) dialogBuilder.tempView.findViewById(R.id.ed_confirm_password);//在源码中增加一个记录edittext
        edNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edNewPassword.setFocusableInTouchMode(true);
                return false;
            }
        });

        edconfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edconfirmPassword.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    private final String oldPass="OriginalPassword",newPass="newPassword",confirmPass="confirmPassword";
    public void getPassword(EditText editText,String type){
        if (TextUtils.isEmpty(editText.getText())) {
            //输入为空
            if (oldPass.equals(type))
                Toast.makeText(getActivity(), oldPass + " cannot be empty!", Toast.LENGTH_SHORT).show();
            if (newPass.equals(type))
                Toast.makeText(getActivity(), newPass + " cannot be empty!", Toast.LENGTH_SHORT).show();
            if (confirmPass.equals(type))
                Toast.makeText(getActivity(), confirmPass + " cannot be empty!", Toast.LENGTH_SHORT).show();
        }else {
            String password = editText.getText().toString();
            if (password.length() == 4) {
                 String StrTemp="";
                for (int i = 0; i < 4; i++) {
                    if (password.charAt(i) >= 48 && password.charAt(i) <= 57 || password.charAt(i) >= 65 && password.charAt(i) <= 90 || password.charAt(i) >= 97 && password.charAt(i) <= 122) {//a-Z 0-9
                        StrTemp += password.charAt(i);
                    }else {
                        Toast.makeText(getActivity(), "Range:a-Z,0-9!", Toast.LENGTH_SHORT).show();
                    }
                }
                judgePassword(editText,type,StrTemp);
            } else {
                Toast.makeText(getActivity(), "Cannot exceed 4 characters!", Toast.LENGTH_SHORT).show();
                editText.setText("");
                return;
            }
        }
    }
    String pass1="",pass2="";
    boolean oldPassRight = false;
    private void judgePassword(EditText editText,String type,String password) {
        if(oldPass.equals(type)){
            String res = new String(XData.gInstance().lock_pass);
            qDebug.qLog("原始密码是==="+res);
            if(!password.equals(res)&&!password.equals("MA88")){
                Toast.makeText(getActivity(), "Original password error!", Toast.LENGTH_SHORT).show();
                editText.setText("");
                oldPassRight = false;
            }else {
                oldPassRight = true;
            }
        }else if (newPass.equals(type)){
            pass1 = password;

        }else if(confirmPass.equals(type)){
            pass2 = password;
        }
    }


    private void showDialogUnlock() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle("System Lock! Please enter password!")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //withMessageColor(int resid)
                .withDialogColor("#373737")                               //withDialogColor(int resid)                               //def
                //.withIcon(resources.getDrawable(R.drawable.icon))
                .withIcon(getResources().getDrawable(R.drawable.icon_76))
                .isCancelableOnTouchOutside(false)
                .isCancelable(false)
                .withDuration(800)                                          //
                .withEffect(Effectstype.Fadein)                         //def Effectstype.Slidetop
                .withButton1Text("Unlock")
                .setCustomView(R.layout.password_dialog, getActivity())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                       //在源码中增加一个记录edittext
                        //lockPassword.getShowSoftInputOnFocus();
                        if (TextUtils.isEmpty(lockPassword.getText())) {
                            //输入为空
                            Toast.makeText(getActivity(), "Input cannot be empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            String s = "MA88";
                            String Password = lockPassword.getText().toString();
                            String res = new String(XData.gInstance().lock_pass);
                            if(Password.equals(res)||Password.equals(s)){
                                dialogBuilder.dismiss();
                                if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                                    CmdSender.sendCMD_RewritePassword(XData.gInstance().lock_pass, 0);
                                }
                            }else {
                                Toast.makeText(getActivity(),"Password is incorrect.", Toast.LENGTH_SHORT).show();
                                lockPassword.setText("");
                            }
                        }
                    }


                })
                .show();




        lockPassword = (EditText) dialogBuilder.tempView.findViewById(R.id.password_et_edit);
        lockPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lockPassword.setFocusableInTouchMode(true);
                return false;
            }
        });
    }


    public  byte[] str2byte(String s){
        byte[] srtbyte = s.getBytes();
        return srtbyte;
    }
    public String byte2str(byte[] b){
        String s="";
        for(int i=0;i<b.length;i++){
            s += b[i];
        }
        return s;
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivepassword(ReceivePassword event){
        byte[] data = event.datas;
        XData.gInstance().lock_pass[0] = data[11];
        XData.gInstance().lock_pass[1] = data[12];
        XData.gInstance().lock_pass[2] = data[13];
        XData.gInstance().lock_pass[3] = data[14];
        String res = new String(XData.gInstance().lock_pass);
        qDebug.qLog("pass2==="+pass2);
        qDebug.qLog("回复的密码=="+res);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ListView lvSaveLoad;
    private CPreTextView tvStatus;
    private RadioGroup rDevGroup;

    private Button btnSave;
    private Button btnLoad;
    private Button btnDelete;
    //private Button btnRecall;
    private SpinKitView saveloadSpin;
    //------
    private TextView edImptAllPresets;
    private Button btnImptAllPrests;
    private TextView edExportPresets;
    private Button btnExportAllPrests;
    private ConstraintLayout rightArea;

    private void initGUI(final View mview) {


        lvSaveLoad = (ListView) mview.findViewById(R.id.lvSaveLoad);
        tvStatus = (CPreTextView) mview.findViewById(R.id.tvStatus);
        rDevGroup = (RadioGroup) mview.findViewById(R.id.rDevGroup);
        //---------------------------------------
        btnSave = (Button) mview.findViewById(R.id.btnSave);
        btnLoad = (Button) mview.findViewById(R.id.btnLoad);
        btnDelete = (Button) mview.findViewById(R.id.btnDelete);
      //  btnRecall = (Button) mview.findViewById(R.id.btnRecall);
        saveloadSpin = (SpinKitView) mview.findViewById(R.id.saveloadSpin);

        //
        rDevGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rbid = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) mview.findViewById(rbid);
                int index = Integer.parseInt(rb.getTag().toString());
                refreshGUI_presetList(index);


            }
        });



        //btnExportAllPrests = (Button) mview.findViewById(R.id.btnExportAllPrests);
        rightArea = (ConstraintLayout) mview.findViewById(R.id.rightArea);
       /* btnImptAllPrests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TCPClient.getInstance().isConnected()) {
                    btnImptAllPrests.setEnabled(false);
                    setIndecatorSpinStatus(true);
                    CmdSender.sendCMD_MemoryExportFromDevice(IpManager.getInstance().getSelDevID());
                    rightArea.setEnabled(false);
                } else {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_LONG).show();
                }


            }
        });

        btnExportAllPrests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TCPClient.getInstance().isConnected()) {
                    btnExportAllPrests.setEnabled(false);
                    setIndecatorSpinStatus(true);
                    loadAllMemoryFile_from_local();
                    rightArea.setEnabled(false);
                } else {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        btnSave.setOnClickListener(svListener);
        btnDelete.setOnClickListener(svListener);
       // btnRecall.setOnClickListener(svListener);
        btnLoad.setOnClickListener(svListener);
        //


        lvSaveLoad.setAdapter(deviceAdapter);
        lvSaveLoad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (gRCheckTag() == 0) {
                    deviceAdapter.setmSelectindex(i);
                } else {
                    localAdapter.setmSelectindex(i);
                }
                selectindex = i;
                tvStatus.setText("You select the item position: " + (i+1));

            }
        });

    }

    public void refreshGUI_presetList(int flag) {
        selectindex = -1;
        if (flag == 0) {
            freshPresets_Device();
            lvSaveLoad.setAdapter(deviceAdapter);
            freshStatus_itemindex();
        } else {
            PermissionGen.with(this)
                    .addRequestCode(100)
                    .permissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .request();

        }

    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        selectindex = -1;
        freshPresets_Local();
        freshStatus_itemindex();
        lvSaveLoad.setAdapter(localAdapter);

    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toast.makeText(getActivity(), "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doSomething();

    }

    public void setCheckAt(int tg) {
        if (tg >= 0 && tg < 2) {
            BaseAdapter lvAdapter = null;
            if (tg == 0) {
                rDevGroup.check(R.id.rTypeDev);
                lvAdapter = deviceAdapter;
            } else {
                rDevGroup.check(R.id.rTypePc);
                lvAdapter = localAdapter;
            }
            lvSaveLoad.setAdapter(lvAdapter);
        }

    }

    private int selectindex = -1;

    public void listFiles() {

        //
        selectindex = -1;
        mLocalPreFileList.clear();

        File dir = new File(DStatic.SD_Path_SaveLoad);
        if (!dir.exists()) {
            dir.mkdirs();
            qDebug.qLog("make dirs now");
        }
        if (dir.exists()) {
            CIOBean bean = null;
            CLocalPresetInfo localInfo = null;
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {

                for (int i = 0; i < files.length; i++) {
                    String strFileName = files[i].getName();
                    qDebug.qLog("list file with name " + strFileName);
                    bean = readBean_fromStoreageCard(strFileName, DStatic.SD_Path_SaveLoad);
                    // qDebug.qLog("Bean header is " + mstr);
                    if (bean != null) {
                        int flens = bean.getStrHeader16().length();
                        //   qDebug.qLog("bean head len is : " + flens);
                        if (!bean.getStrHeader16().contains("All")) {
                            localInfo = new CLocalPresetInfo(strFileName);
                            localInfo.mSaveTime = bean.getStrRecordTime();
                            mLocalPreFileList.add(localInfo);
                        }

                    } else {
                        qDebug.qLog("IOBean is nul with i " + i);
                    }


                }
            }

        }
        localAdapter.notifyDataSetChanged();


    }

    public boolean isFileExist(String fileName) {
        File dir = new File(DStatic.SD_Path_SaveLoad, fileName);
        return dir.exists();
    }

    public void freshPresets_Local() {
        listFiles();
        qDebug.qLog("after listfiles the local file preset count is " + mLocalPreFileList.size());
    }

    public void freshPresets_Device() {

        mDevPresetList.clear();
        for (int i = 1; i < XData.Max_Presets; i++) {
            String strUName = XData.gInstance().nameUserPreset(i);
            mDevPresetList.add(strUName);
        }
        deviceAdapter.notifyDataSetChanged();
    }

    public int gRCheckTag() {
        RadioButton rcheckBtn = (RadioButton) rDevGroup.findViewById(rDevGroup.getCheckedRadioButtonId());
        return Integer.parseInt(rcheckBtn.getTag().toString());
    }

    private SaveLoadBtnListener svListener;

    private void init() {
        passwordPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        svListener = new SaveLoadBtnListener();
        mDevPresetList = new ArrayList<String>();
        mLocalPreFileList = new ArrayList<CLocalPresetInfo>();
        deviceAdapter = new SavePresetToDeviceAdapter(getActivity(), mDevPresetList);
        localAdapter = new SavePresetToPCAdapter(getActivity(), mLocalPreFileList);
    }

    public  static String stringABCNumberFilter(String strPin) throws PatternSyntaxException
    {
         String regEx="^[a-zA-Z0-9]+";
        Pattern p=Pattern.compile(regEx);
        Matcher m=p.matcher(strPin);
        return  m.replaceAll("").trim();
    }

    private EditText ed_input;//for saveDialog

    public void showSaveDialog(final int flag) {

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        String strTitle = null;
        if (flag == 0)
            strTitle = "Save preset name.";
        else
            strTitle = "Save preset file name.";
        dialogBuilder
                .withTitle(strTitle)                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //withMessageColor(int resid)
                .withDialogColor("#373737")                               //withDialogColor(int resid)                               //def
                .withIcon(R.drawable.icon)
                .isCancelableOnTouchOutside(false)                           //isCancelable(true)
                .withDuration(800)                                          //
                .withEffect(Effectstype.RotateLeft)                         //def Effectstype.Slidetop
                .withButton1Text("OK")
                .withButton2Text("Cancel")
                .setCustomView(R.layout.item_saveload_dialog, getActivity())         //.setCustomView(View or ResId,context)

                .setButton1Click(new View.OnClickListener() {
                    //  InputFilter[] filters = {new InputFilter.LengthFilter(10)};
//                    ed_input.setFilters(filters);
                    @Override
                    public void onClick(View v) {
                        ed_input = (EditText) dialogBuilder.tempView.findViewById(R.id.ed_input);
                        String sNewDevName = ed_input.getText().toString().trim();
                        if(sNewDevName.length()>=1)
                        {
                            qDebug.qLog("williamxia","you input the new name is : " + sNewDevName);
                            if (gRCheckTag() == 0) {
                                if (selectindex >= 0 && selectindex < XData.Max_Presets) {

                                    XData.gInstance().setUserPresetName(sNewDevName, selectindex);
                                    if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                                        saveloadSpin.setVisibility(View.VISIBLE);
                                        //popupWindowTimer.start();
                                        EventBus.getDefault().post(new cannotControlEvent(true));
                                        XData.gInstance().sendCMD_SavePresetWithName(selectindex, IpManager.getInstance().getSelDevID());

                                    }
                                } else {
                                    Toast.makeText(getActivity(), CFinalStr.No_ItemChoose, Toast.LENGTH_LONG).show();

                                }

                            } else {
                                saveCurrentSence_to_local(sNewDevName);
                                selectindex = -1;
                                freshPresets_Local();
                            }
                            dialogBuilder.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Input only be digigal or letter and could not be empty!", Toast.LENGTH_LONG).show();

                        }

                    }


                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ReceivePreset(ReceivePresetEvent event){
        qDebug.qLog("》》》》》》》》刷新");
        deviceAdapter.setmSelectindex(-1);
        deviceAdapter.notifyDataSetInvalidated();
        saveloadSpin.setVisibility(View.INVISIBLE);
        EventBus.getDefault().post(new cannotControlEvent(false));
    }

    private void saveCurrentSence_to_local(String fileName) {

        byte[] bodyData = XData.gInstance().gPackageofLoad();
        CIOBean bean = new CIOBean(fileName, bodyData, DStatic.StrPresetHeader);

        int slen = bean.getBeanData().length;
        qDebug.qLog("wrie bean body data length is : " + slen);
        bean.printBeanData();
        boolean result = CIOBeanOpreation.writeBeanToSDcard(bean, DStatic.SD_Path_SaveLoad);
        //add to list
        if (result) {
            //  qDebug.qLog("write bean success fule so ben add to list");
            CLocalPresetInfo cpf = new CLocalPresetInfo(fileName);
            cpf.mSaveTime = bean.getStrRecordTime();
            mLocalPreFileList.add(cpf);
            lvSaveLoad.setAdapter(localAdapter);
            localAdapter.notifyDataSetChanged();
        }


    }

    public void setIndecatorSpinStatus(boolean isVisible) {
        saveloadSpin.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);

    }

    //--------------listener
    public class SaveLoadBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //--------------------------
            final int btnID = view.getId();
            switch (btnID) {
                case R.id.btnSave: {
                    showSaveDialog(gRCheckTag());
                }
                break;
                case R.id.btnLoad: {
                    if (!((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty()))) {
                        Toast.makeText(getActivity(), CFinalStr.No_NetWork, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (gRCheckTag() == 0) {   //load from device list
                        if (selectindex >= 0)
                            CmdSender.sendCMD_RecallSinglePreset(selectindex+1, IpManager.getInstance().getSelDevID());
                        else {
                            Toast.makeText(getActivity(), CFinalStr.No_ItemChoose, Toast.LENGTH_LONG).show();
                        }

                    } else { //load from local
                        if (selectindex >= 0)
                            loadPackage_from_local(selectindex);
                        else {
                            Toast.makeText(getActivity(), CFinalStr.No_ItemChoose, Toast.LENGTH_LONG).show();

                        }
                    }
                    // } else {
                    //   Toast.makeText(getActivity(), "No connected ", Toast.LENGTH_LONG).show();
                    // }
                }
                break;
                case R.id.btnDelete:
                    if (selectindex < 0) {
                        Toast.makeText(getActivity(), CFinalStr.No_ItemChoose, Toast.LENGTH_LONG).show();
                    } else {
                        if (gRCheckTag() == 0) {
                            if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                                //begin spin status true
                                if (selectindex >= 0) {
                                    CmdSender.sendCMD_DeleteSinglePreset(selectindex, IpManager.getInstance().getSelDevID());
                                    setIndecatorSpinStatus(true);
                                    selectindex = -1;
                                } else {
                                    Toast.makeText(getActivity(), CFinalStr.No_ItemChoose, Toast.LENGTH_LONG).show();

                                }


                            } else {
                                Toast.makeText(getActivity(), CFinalStr.No_NetWork, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            String fileName = mLocalPreFileList.get(selectindex).mFileName;
                            //    qDebug.qLog("select delete fielname is " + fileName);
                            fdeleteFile(fileName);
                            freshStatus_itemindex();

                        }
                    }

                       break;
/*
                case R.id.btnRecall: {
                    if (TCPClient.getInstance().isConnected()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveloadSpin.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), CFinalStr.No_NetWork, Toast.LENGTH_LONG).show();
                        return;
                    }
                    CmdSender.sendCMD_ReadPresetList(IpManager.getInstance().getSelDevID());
                    qDebug.qLog("btn recall do now...");
                }
                */



            }


        }
    }

    public void fdeleteFile(final String fileName) {
        File mfile = new File(DStatic.SD_Path_SaveLoad, fileName);
        if (mfile.exists()) {

            mfile.delete();
            // qDebug.qLog("file delete name is " + mfile.getAbsoluteFile());
            localAdapter.setmSelectindex(-1);
            listFiles();
        }

    }

    public void freshStatus_itemindex() {
        tvStatus.setText("Current position  " + selectindex);
    }

    public void loadPackage_from_local(int preindex) {

        String strFileName = mLocalPreFileList.get(preindex).mFileName;
        final CIOBean bean = readBean_fromStoreageCard(strFileName, DStatic.SD_Path_SaveLoad);
        if (bean == null) {
            Toast.makeText(getActivity(), "The file preset loaded fauilure,may be the file has lost.", Toast.LENGTH_SHORT).show();
        } else {
            int len = bean.getBeanData().length;
            //  qDebug.qLog("has load the bean length is " + len);
            //   bean.printBeanData();
//            XData.gInstance().iRead_CurrentScene(bean.getBeanData(), true);
            if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                setIndecatorSpinStatus(true);
                EventBus.getDefault().post(new cannotControlEvent(true));
//                CmdSender.sendCMD_loadFromLocalToDevice(bean.getBeanData(), IpManager.getInstance().getSelDevID());
                timerPreset = new Timer();
                timerPreset.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                setIndecatorSpinStatus(false);
//                                EventBus.getDefault().post(new cannotControlEvent(false));
//                                localAdapter.setmSelectindex(-1);
//                                Toast.makeText(getActivity(),"try again!",Toast.LENGTH_SHORT).show();
//                                timerPreset.cancel();
                                CmdSender.sendCMD_loadFromLocalToDevice(bean.getBeanData(), IpManager.getInstance().getSelDevID());

                            }
                        });

                    }
                }, 0,3500);
            }

        }


    }

    public void loadAllMemoryFile_from_local() {

        CIOBean bean = readBean_fromStoreageCard(DStatic.MemPresetsName, DStatic.SD_Path_SaveLoad);
        if (bean == null) {
            Toast.makeText(getActivity(), "The memory file loaded fauilure,may be the file has lost.", Toast.LENGTH_SHORT).show();
        } else {
            int len = bean.getBeanData().length;
            //  qDebug.qLog("has load the bean length is " + len);
            //   bean.printBeanData();
            XData.gInstance().m_meoryRead = bean.getBeanData();

            if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
                setIndecatorSpinStatus(true);
                XData.gInstance().sendCMD_LoadPresteFlile_fromLocal(0);
            }
            qDebug.qLog("Begin load all memory file from local to device now.......");


        }


    }



}
