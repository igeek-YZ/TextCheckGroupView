# 帮朋友写的一个类似于radioGroup和checkGroup 管理的组件
自定义继承View,可直接放置到listview等的子类视图中  

主要特点：
* 可用作显示给用户单选或者多选；  

* 可自定义显示的模式：无边框，有边框，有图标和隐藏图标几种组合模式  

* 流动布局，每一行的显示个数可根据显示的数据和屏幕的宽度自适应换行  

代码和效果说明：mainActivity  
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
  

#### 无边框单选模式

	<com.igeek.textcheckgroupview.CheckTextGroupView
        android:id="@+id/checkTextGroupView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:checkedTextColor="#009EF2"
        app:textPaddingLeft="16dp"
        app:textPaddingRight="16dp"
        app:textPaddingButtom="12dp"
        app:textPaddingTop="12dp"
        app:lineHeight="12dp"
        app:strokeModel="GONE"
        app:checkModel="SIMPLE"
        app:strokeWidth="1px"
        app:textGapWidth="10dp"
        app:textSize="16sp"
        app:unCheckedTextColor="@color/black_overlay" />




**多选模式**只需要修改***app:checkModel="SIMPLE"***为***app:checkModel="MULTI"*** 即可  

#### 选中边框单选模式(多选修改*app:checkModel="MULTI"*)  

	<com.igeek.textcheckgroupview.CheckTextGroupView
        android:id="@+id/checkTextGroupView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:checkedStrokeColor="@color/colorPrimary"
        app:checkedTextColor="#009EF2"
        app:textPaddingLeft="16dp"
        app:textPaddingRight="16dp"
        app:textPaddingButtom="12dp"
        app:textPaddingTop="12dp"
        app:lineHeight="12dp"
        app:strokeModel="GONE_STROKE"
        app:checkModel="SIMPLE"
        app:strokeWidth="1px"
        app:textGapWidth="10dp"
        app:textSize="16sp"
        app:unCheckedStrokeColor="@color/gray_efeff4"
        app:unCheckedTextColor="@color/black_overlay" />


#### 选中边框带图标单选模式(多选修改*app:checkModel="MULTI"*) 

	<com.igeek.textcheckgroupview.CheckTextGroupView
        android:id="@+id/checkTextGroupView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:checkedStrokeColor="@color/colorPrimary"
        app:checkedTextColor="#009EF2"
        app:textPaddingLeft="16dp"
        app:textPaddingRight="16dp"
        app:textPaddingButtom="12dp"
        app:textPaddingTop="12dp"
        app:lineHeight="12dp"
        app:strokeModel="GONE_STROKE"
        app:checkModel="SIMPLE"
        app:strokeWidth="1px"
        app:textGapWidth="10dp"
        app:textSize="16sp"
        app:drawTextGapWidth="8dp"
        app:drawableWidth="20dp"
        app:drawableHeight="20dp"
        app:unCheckedDrawable="@mipmap/ic_radio_btn_unchecked_black_24dp"
        app:checkedDrawable="@mipmap/ic_radio_btn_checked_black_24dp"
        app:unCheckedStrokeColor="@color/gray_efeff4"
        app:unCheckedTextColor="@color/black_overlay" />

