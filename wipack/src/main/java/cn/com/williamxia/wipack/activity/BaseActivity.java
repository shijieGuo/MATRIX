package cn.com.williamxia.wipack.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import me.yokeyword.fragmentation.SupportActivity;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class BaseActivity extends AppCompatActivity {

    public static ExecutorService cachedThreadPool;//williamxia 20190715
    static {

        cachedThreadPool = Executors.newFixedThreadPool(5);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //must put after setcontentview
        initWindow();
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);

    }

    public void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
    }


}
