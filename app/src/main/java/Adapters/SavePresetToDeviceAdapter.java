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

public class SavePresetToDeviceAdapter extends BaseAdapter {

    private List<String> mDevice_presetList;
    private Context mcontext;

    private int mSelectindex;

    public int getmSelectindex() {
        return mSelectindex;
    }

    public void setmSelectindex(int mSelectindex) {
        this.mSelectindex = mSelectindex;
        this.notifyDataSetInvalidated();
    }

    public SavePresetToDeviceAdapter(Context context, ArrayList<String> presetsList) {

        mcontext = context;
        mDevice_presetList = presetsList;
        //---
        mSelectindex = -1;
    }

    @Override
    public int getCount() {
        return mDevice_presetList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDevice_presetList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null) {
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_list_savepreset_device, null);
            holder =new  ViewHolder();
            holder.lbPresetName = (TextView) view.findViewById(R.id.lbPresetName);
            view.setTag(holder);
            //add this line below,so we can set the item of listview height

        } else {

            holder = (ViewHolder) view.getTag();
        }

        if (mSelectindex == i) {
            view.setBackgroundColor(mcontext.getResources().getColor(R.color.gray));
        } else {
            view.setBackgroundColor(mcontext.getResources().getColor(R.color.bg_color));

        }
        holder.lbPresetName.setText(mDevice_presetList.get(i));

        return view;
    }

    public class ViewHolder {
        TextView lbPresetName;

    }
}
