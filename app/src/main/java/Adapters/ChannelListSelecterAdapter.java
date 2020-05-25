package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import Datas.DStatic;
import Datas.XData;
import cn.com.williamxia.matrixa8dante.R;

/**
 * Created by zhihuafeng on 4/10/2018.
 */

public class ChannelListSelecterAdapter extends BaseAdapter {
    private Context mContext;
    private int mSelectindex;

    public void setmSelectindex(int selindex) {
        mSelectindex = selindex;
        this.notifyDataSetInvalidated();
    }

    public int getmSelectindex() {
        return mSelectindex;
    }


    public ChannelListSelecterAdapter(Context context) {
        mContext = context;
    }


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
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.channel_list_item, null);
            holder = new ViewHolder();
            holder.item = (TextView) view.findViewById(R.id.list_selectItem);
            view.setTag(holder);
            //对于listview，注意添加这一行，即可在item上使用高度

        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (mSelectindex == i) {
            holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
        } else {
            holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.bg_color));
        }
        holder.item.setText(XData.gInstance().getShowNameOut(i));
        return view;
    }

    public class ViewHolder {
        TextView item;
    }

}
