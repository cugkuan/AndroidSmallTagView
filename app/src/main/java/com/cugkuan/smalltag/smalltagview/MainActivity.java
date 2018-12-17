package com.cugkuan.smalltag.smalltagview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cugkuan.smalltag.SmallTagView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private SmallTagView mSmallTagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSmallTagView = findViewById(R.id.tag);

        List<String>  tags = new ArrayList<>();

        tags.add("数学");
        tags.add("语文");
        tags.add("化学");

        mSmallTagView.setTags(tags);

    }
}
