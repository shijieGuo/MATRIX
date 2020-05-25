package cn.com.williamxia.wipack.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by williamXia on 2017/5/22.
 */

public class ActivityController {
    public static List<Activity> activities=new ArrayList<>();

    public static void  addActivity(Activity activity)
    {
        if(activity!=null)
            activities.add(activity);

    }

    public  static  void  removeActivity(Activity activity)
    {
        activities.remove(activity);

    }

    public static  void finishAll()
    {
        for(Activity activity:activities)
        {
           if(! activity.isFinishing())
           {
               activity.finish();
           }

        }
        activities.clear();

    }


}
