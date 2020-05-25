package newfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import Datas.DStatic;

/**

 */
public class commDspFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    public commDspFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void refreshPage() {

        refreshChanelMute();
        refreshVolumeFader();
        refreshChannelName();
        closeFoucse();

    }

    public void refreshChannelName() {


    }
    public void refreshSingleSlider(int index) {


    }
    public void refreshVolumeFader() {


    }

    public void refreshChanelMute() {


    }
    public  void closeFoucse()
    {


    }


}
