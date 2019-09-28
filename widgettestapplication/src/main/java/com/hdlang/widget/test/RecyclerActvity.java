package com.hdlang.widget.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.hlibrary.util.Logger;
import com.hlibrary.widget.design.RecyclerView;

public final class RecyclerActvity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < 20; i++)
            adapter.mDatas.add("item " + i);
        adapter.notifyDataSetChanged();
        recyclerView.setOnBottomCallback(new RecyclerView.OnBottomCallback() {
            @Override
            public void onBottom() {
                Logger.getInstance().defaultTagD(" == onBottom ==");
            }
        });
    }

}
