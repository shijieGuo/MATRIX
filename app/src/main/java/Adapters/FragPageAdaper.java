package Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by williamXia on 2017/11/17.
 */

public class FragPageAdaper extends FragmentPagerAdapter {

    private List<Fragment> m_FragList;

    public FragPageAdaper(FragmentManager fm,List<Fragment> fraglist) {
        super(fm);
        m_FragList=fraglist;
    }

    @Override
    public int getCount() {
        return m_FragList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return m_FragList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


}
