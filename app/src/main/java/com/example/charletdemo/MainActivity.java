package com.example.charletdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.charletdemo.bean.ChartletBean;
import com.example.charletdemo.fragment.ChartletFragment;
import com.example.charletdemo.fragment.SelectChartletFragment;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {


    private SelectChartletFragment mSelectFragment;
    private ChartletFragment mChartletFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mChartletFragment = new ChartletFragment();
        mSelectFragment = new SelectChartletFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, mChartletFragment)
                .add(R.id.frameLayout, mSelectFragment)
                .hide(mSelectFragment)
                .commitAllowingStateLoss();

    }

    public void click(View view){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mChartletFragment)
                .show(mSelectFragment)
                .commitAllowingStateLoss();
    }


    public void addText(View view){
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入文字")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    mChartletFragment.addData(new ChartletBean(et.getText()));
                }).setNegativeButton("取消",null).show();
    }

    public void textAndImage(View view){
        mChartletFragment.addData(new ChartletBean("emoji_gesture_0001.png", "的地方水电费第三方但是"));
    }
    public void delete(View view){
        mChartletFragment.delete();
    }

    public void confirm(View view){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mSelectFragment)
                .show(mChartletFragment)
                .commitAllowingStateLoss();

        String selectivePath = mSelectFragment.getSelectivePath();
        if (selectivePath != null){
            mChartletFragment.addData(new ChartletBean(selectivePath));
        }

    }
}
