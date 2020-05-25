package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import Datas.DStatic;
import Datas.XData;
import Datas.matrixBean;
import Interfaces.CMatrixStatusListener;
import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.CButton;
import comcontrol.CPreTextView;
import comcontrol.MyGainBar;

/**
 * Created by zhihuafeng on 4/10/2018.
 */

public class CMatrixDataAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private int mSelectindex;
    private List<matrixBean> m_beanList;
    private ListView listView;
    private ArrayList<MyGainBar> myGainBarList;

    public void setmSelectindex(int selindex) {
        mSelectindex = selindex;
        this.notifyDataSetInvalidated();
    }

    public int getmSelectindex() {
        return mSelectindex;
    }

    public List<matrixBean> getM_beanList() {
        return m_beanList;
    }

    public void setM_beanList(List<matrixBean> m_beanList) {
        this.m_beanList = m_beanList;
    }

    public CMatrixDataAdapter(Context context, List<matrixBean> mbean,ListView listView) {
        mContext = context;
        m_beanList = mbean;
        this.listView=listView;
    }

    public CMatrixStatusListener mMatrixStatusListener;

    @Override
    public int getCount() {
        return DStatic.ChannelOutName.length;
    }

    @Override
    public Object getItem(int i) {
        return DStatic.ChannelOutName[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_item_ONOFF:
                CButton btn = (CButton) v;
                if (mMatrixStatusListener != null) {
                    qDebug.qLog("mm", "holder btn off click now...index " + btn.getiTag());
                    mMatrixStatusListener.OnMatrixStateClickEvent(v);

                }
                break;


        }
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.wr_area_item, null);
            ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,120);//设置宽度和高度
            view.setLayoutParams(params);
            holder = new ViewHolder();
            holder.m_btnOnOF = (CButton) view.findViewById(R.id.btn_item_ONOFF);


            holder.m_bar=(MyGainBar)view.findViewById(R.id.tv_item_bar);
            holder.item = (CPreTextView) view.findViewById(R.id.tv_item_num);
            holder.m_chname = (CPreTextView) view.findViewById(R.id.tv_item_channel);
            view.setTag(holder);
            //对于listview，注意添加这一行，即可在item上使用高度

        } else {
            holder = (ViewHolder) view.getTag();
        }
//        if(XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][i]==0){
//            holder.m_btnOnOF.setText(m_beanList.get(i).getStrAct() +":"+"MUTE");
//        }
//        else {
//            Double gain=new Double( XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][i]-160) /2;
//            holder.m_btnOnOF.setText(m_beanList.get(i).getStrAct() +":"+String.format("%.1f", gain)+"db");
//        }
        holder.m_btnOnOF.setiTag(i);
        holder.m_btnOnOF.setOnClickListener(this);
        holder.m_btnOnOF.setActivated(m_beanList.get(i).isAct()>0);
        holder.m_btnOnOF.setText(m_beanList.get(i).getStrAct());
        qDebug.qLog("xmm","inner pring "+m_beanList.get(0).isAct());
        Log.d("m_bar",holder.m_bar+"");
        holder.m_bar.setParent(listView);
        holder.m_bar.setiTag(i);
        holder.m_bar.setProgress(XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][i]);
        holder.item.setText(m_beanList.get(i).getNo()+"");
        holder.m_chname.setText(XData.gInstance().getShowNameInput(i));

        return view;
    }

    public class ViewHolder {
        CButton m_btnOnOF;
        CPreTextView item;
        CPreTextView m_chname;
        MyGainBar m_bar;
    }
    public void updataView( ListView listView) {

        for (int i=0;i<20;i++){
            View view = listView.getChildAt(i);
            ViewHolder holder = null;
            int index;
            if(view!=null){
                holder = (ViewHolder) view.getTag();
                index=holder.m_bar.getiTag();
            }
            else break;
            holder.m_bar.setProgress(XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][index]);
//            if(XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][index]==0){
//                holder.m_btnOnOF.setText(m_beanList.get(index).getStrAct() +":"+"MUTE");
//            }
//            else {
//                Double gain=new Double( XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][index]-160) /2;
//                holder.m_btnOnOF.setText(m_beanList.get(index).getStrAct() +":"+String.format("%.1f", gain)+"db");
//            }
            holder.m_btnOnOF.setActivated(m_beanList.get(index).isAct()>0);
            holder.m_btnOnOF.setText(m_beanList.get(index).getStrAct());
        }

    }
}
