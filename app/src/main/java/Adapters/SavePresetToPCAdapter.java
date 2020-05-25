package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.williamxia.matrixa8dante.R;


/**
 * Created by williamXia on 2017/8/25.
 */
public class SavePresetToPCAdapter extends BaseAdapter {

    private List<CLocalPresetInfo> mLocal_PresetList;

    private Context mcontext;
    private int mSelectindex;

    public int getmSelectindex() {
        return mSelectindex;
    }

    public void setmSelectindex(int mSelectindex) {
        this.mSelectindex = mSelectindex;
        this.notifyDataSetInvalidated();
    }

    public SavePresetToPCAdapter(Context context, ArrayList<CLocalPresetInfo> localPresetList) {
        mcontext = context;
        mLocal_PresetList = localPresetList;
        mSelectindex = -1;
    }

    @Override
    public int getCount() {
        return mLocal_PresetList.size();
    }

    @Override
    public Object getItem(int i) {
        return mLocal_PresetList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null) {
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_list_savepreset_local, null);//R.layout.item_list_savepreset_local
            holder = new ViewHolder();


            holder.lb_fileName = (TextView) view.findViewById(R.id.lbFileName);
            holder.lb_time = (TextView) view.findViewById(R.id.lbFileTime);
            view.setTag(holder);
            //add this line below,so we can set the item of listview height

        } else {

            holder = (ViewHolder) view.getTag();
        }

        if (mSelectindex == i) {
            holder.lb_fileName.setBackgroundColor(mcontext.getResources().getColor(R.color.red));
        } else {
            holder.lb_fileName.setBackgroundColor(mcontext.getResources().getColor(R.color.bg_color));

        }
        holder.lb_fileName.setText(mLocal_PresetList.get(i).mFileName);
        holder.lb_time.setText(mLocal_PresetList.get(i).mSaveTime);

        return view;
    }

    public class ViewHolder {
        TextView lb_fileName;
        TextView lb_time;
    }

}
