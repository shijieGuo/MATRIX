package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.williamxia.matrixa8dante.R;
import cn.com.williamxia.wipack.utils.DevInfo;
import cn.com.williamxia.wipack.utils.qDebug;

/**
 * Created by williamXia on 2017/9/11.
 */

public class MutilHeadAdapter extends BaseAdapter {


    public static final String StrColI = "ColI";
    public static final String StrColII = "ColII";
    public static final String StrColIII = "ColIII";
    public static final String StrColIV = "ColIV";
    public static final String StrColV = "ColV";
    public static final String StrColVI = "ColVI";
    //--------------------------------------


    private Context mcontext;
    private int mSelectindex = -1;
    private List<DevInfo> m_listData;
    private DevInfo datas;
    private int fdvWd;

    public MutilHeadAdapter(Context mcontext, List<DevInfo> m_listData) {
        this.mcontext = mcontext;
        this.m_listData = m_listData;
        this.notifyDataSetChanged();
    }

    public int getmSelectindex() {
        return mSelectindex;
    }

    public void setmSelectindex(int mSelectindex) {
        this.mSelectindex = mSelectindex;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return m_listData.size();
    }

    @Override
    public Object getItem(int i) {
        return m_listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public void setFdvWd(final int swd) {
        fdvWd = swd;
        notifyDataSetInvalidated();
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MutilColumnHolder holder = null;

        if (view == null) {
            view = LayoutInflater.from(mcontext).inflate(R.layout.ip_list_item, null);
            view.setMinimumHeight(60);
            holder = new MutilColumnHolder();
            holder.tvH1 = (TextView) view.findViewById(R.id.tv_ip_address_name1);
            /*holder.tvH2 = (TextView) view.findViewById(R.id.htv2);
            holder.tvH3 = (TextView) view.findViewById(R.id.htv3);

            holder.tvH4 = (TextView) view.findViewById(R.id.htv4);
            holder.tvH5 = (TextView) view.findViewById(R.id.htv5);
            holder.tvH6 = (TextView) view.findViewById(R.id.htv6);*/
            //
            view.setTag(holder);

        } else {
            holder = (MutilColumnHolder) view.getTag();

        }
        if(!m_listData.get(i).devceAddr.equals("")) {
            datas = m_listData.get(i);
            holder.tvH1.setText("" + datas.devceAddr + "(" + datas.getDevName() + ")");
            qDebug.qLog("okf","add devname now....");
        }
        else
            holder.tvH1.setText("");
       /* holder.tvH2.setText("" + datas.devMac);
        holder.tvH3.setText("" + datas.getDevName());
        holder.tvH4.setText("" + datas.getHexAppID());
        holder.tvH5.setText("" + datas.getHexDevID());
        holder.tvH6.setText("" + datas.getStrVer());*/

        /*holder.tvH1.setWidth(fdvWd * 3);
        holder.tvH1.setTextColor(Color.BLACK);*/
        /*holder.tvH2.setWidth(fdvWd * 3);
        holder.tvH3.setWidth(fdvWd * 3);
        holder.tvH4.setWidth(fdvWd * 1);
        holder.tvH5.setWidth(fdvWd * 1);
        holder.tvH6.setWidth(fdvWd * 3);


        holder.tvH2.setTextColor(Color.BLACK);
        holder.tvH3.setTextColor(Color.BLACK);
        holder.tvH4.setTextColor(Color.BLACK);
        holder.tvH5.setTextColor(Color.BLACK);
        holder.tvH6.setTextColor(Color.BLACK);*/
       // qDebug.qLog("textview column width set now......" + fdvWd);

        if (mSelectindex == i) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

    private class MutilColumnHolder {
        TextView tvH1;
    /*    TextView tvH2;
        TextView tvH3;
        TextView tvH4;
        TextView tvH5;
        TextView tvH6;*/
    }

}
