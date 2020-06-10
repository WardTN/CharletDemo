package com.example.charletdemo.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.charletdemo.R;
import com.example.charletdemo.bean.ChartletBean;
import com.example.charletdemo.view.ChartletView;
import com.example.charletdemo.view.NavigationLineView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChartletFragment extends Fragment {

    private View mContentView;
    private ChartletView mChartletView;
    private NavigationLineView mLineView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = View.inflate(getContext(), R.layout.fragment_chartle,null);
        init();
        return mContentView;
    }

    private void init() {

        List<Bitmap> imageList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Bitmap bitmap = Bitmap.createBitmap(ChartletView.IMAGE_WIDTH, ChartletView.IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Random random = new Random();
            int a = 80;
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            ColorDrawable colorDrawable = new ColorDrawable(Color.argb(a, r, g, b));
            colorDrawable.setBounds(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            colorDrawable.draw(canvas);

            imageList.add(bitmap);
        }
        mChartletView = (ChartletView) mContentView.findViewById(R.id.chartletView);

        mLineView = mContentView.findViewById(R.id.navigationLineView);;
        mLineView.setImageList(imageList);
        mChartletView.setOnChartletChangeListener(list -> {
            mLineView.notificationChartletChange(list);
        });

        mLineView.post(() -> {
            int measuredWidth = mLineView.getMeasuredWidth();
            mChartletView.setMinimumWidth(measuredWidth);
        });

    }

    public void addData(ChartletBean bean) {
        mChartletView.addData(bean);
    }


    public void delete() {
        mChartletView.delete();
    }
}
