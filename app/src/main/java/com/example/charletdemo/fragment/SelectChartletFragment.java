package com.example.charletdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.charletdemo.R;
import com.example.charletdemo.adapter.RecyclerViewAdapter;
import com.example.charletdemo.adapter.SpaceItemDecoration;
import com.example.charletdemo.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择贴图Fragment
 */
public class SelectChartletFragment extends Fragment {

    private View mContentView;
    private String mSelectivePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = View.inflate(getContext(), R.layout.fragment_select_chartle, null);
        init();
        return mContentView;

    }

    private void init() {
        List<String> list = new ArrayList<>(63);

        for (int i = 1; i <= 21; i++) {
            String index = i < 10 ? "0" + i : String.valueOf(i);
            String path = "emoji_celebration_00" + index + ".png";
            list.add(path);
        }
        for (int i = 1; i <= 56; i++) {
            String index = i < 10 ? "0" + i : String.valueOf(i);
            String path = "emoji_emotion_00" + index + ".png";
            list.add(path);
        }
        for (int i = 1; i <= 28; i++) {
            String index = i < 10 ? "0" + i : String.valueOf(i);
            String path = "emoji_gesture_00" + index + ".png";
            list.add(path);
        }
        for (int i = 1; i <= 63; i++) {
            String index = i < 10 ? "0" + i : String.valueOf(i);
            String path = "emoji_smileys_00" + index + ".png";
            list.add(path);
        }


        RecyclerViewAdapter<String> adapter = new RecyclerViewAdapter<String>(list, R.layout.adapter_select_chartle) {
            @Override
            protected void setData(RecyclerViewHolder holder, String bean, int position) {
                holder.getIV(R.id.iv).setImageBitmap(CommonUtils.getAssetsBitmap(holder.context, bean));
                holder.getIV(R.id.iv).setOnClickListener(v -> {
                    mSelectivePath = bean;
                });
            }
        };

        RecyclerView recyclerView = mContentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.addItemDecoration(new SpaceItemDecoration(0, 0, 0, 10));
        recyclerView.setAdapter(adapter);
    }

    public String getSelectivePath() {
        String selectivePath = mSelectivePath;
        mSelectivePath = null;
        return selectivePath;
    }


}
