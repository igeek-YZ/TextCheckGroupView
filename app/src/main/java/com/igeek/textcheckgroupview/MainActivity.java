package com.igeek.textcheckgroupview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    CheckTextGroupView checkTextGroupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkTextGroupView= (CheckTextGroupView) findViewById(R.id.checkTextGroupView);
        checkTextGroupView.updateCheckTexts(initList());
    }

    public List<CheckTextGroupView.CheckText> initList(){

        List<CheckTextGroupView.CheckText> list=new ArrayList<CheckTextGroupView.CheckText>();
        for(int index=0;index<10;index++){
            CheckTextGroupView.CheckText checkText=new CheckTextGroupView.CheckText();
            checkText.setText("中文"+index);
            list.add(checkText);
        }

        return list;
    }
}
