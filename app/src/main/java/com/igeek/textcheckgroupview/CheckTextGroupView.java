package com.igeek.textcheckgroupview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class CheckTextGroupView extends View implements View.OnTouchListener{

    private static final String TAG=CheckTextGroupView.class.getSimpleName();

    private List<CheckText> checkTexts =new ArrayList<CheckText>();
    private SparseArray<CheckText> checkeds=new SparseArray<CheckText>();

    //文本字体大小
    private int textSize;
    //未选中状态文本的颜色(默认)
    private int checkedTextColor;
    //选中状态文本的颜色
    private int unCheckedTextColor;
    //选中边框填充颜色
    private int checkedStrokeColor;
    //未选中边框填充颜色(默认)
    private int unCheckedStrokeColor;
    //文本之间的间隔距离
    private int textGapWidth;
    //换行的行高间距
    private int lineHeight;
    //边框宽度
    private int strokeWidth;
    //图标的宽度
    private int drawableWidth;
    //图标的高度
    private int drawableHeight;
    //图标与文本之间的间距
    private int drawTextGapWidth;
    //圆角半径
    private int tagRadius;

    //文本距离边框的填充间距
    private int textPadding;
    private int textPaddingLeft;
    private int textPaddingTop;
    private int textPaddingRight;
    private int textPaddingButtom;

    //选中的图标
    private Drawable checkedDrawable;
    //默认的图标
    private Drawable unCheckedDrawable;

    //默认和选中都显示边框
    private static final int STROKE=1;
    //默认隐藏边框,选中显示边框
    private static final int GONE_STROKE=2;
    //默认显示边框,选中显示填充和边框
    private static final int STROKE_FILL=3;
    //默认和选中都显示填充和边框
    private static final int FILL_FILL=4;
    //默认和选中都隐藏边框
    private static final int GONE=5;

    //单选
    private static final int SIMPLE=6;

    //多选
    private static final int MULTI=7;

    //边框显示模式
    private int strokeModel;

    //选中模式
    private int checkModel;


    private Paint textPaint;
    private Paint strokePaint;

    private int lasterDownX;
    private int lasterDownY;

    private int downCheckedIndex;

    private CheckTextCheckedChangeListener listener;


    public CheckTextGroupView(Context context) {
        this(context,null);
    }

    public CheckTextGroupView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckTextGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context,attrs);
    }

    public void initConfig(Context context, AttributeSet attrs){

        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.CheckTextGroupView);

        textPadding=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textPadding,0);

        textPaddingLeft=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textPaddingLeft,-1);
        textPaddingTop=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textPaddingTop,-1);
        textPaddingRight=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textPaddingRight,-1);
        textPaddingButtom=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textPaddingButtom,-1);

        if(textPaddingLeft==-1){
            textPaddingLeft=textPadding;
        }

        if(textPaddingTop==-1){
            textPaddingTop=textPadding;
        }

        if(textPaddingRight==-1){
            textPaddingRight=textPadding;
        }

        if(textPaddingButtom==-1){
            textPaddingButtom=textPadding;
        }

        textSize =ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textSize,14);
        tagRadius =ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_tagRadius,10);
        textGapWidth =ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_textGapWidth,0);
        lineHeight=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_lineHeight,0);
        strokeWidth=ta.getDimensionPixelSize(R.styleable.CheckTextGroupView_strokeWidth,0);
        drawableWidth=ta.getDimensionPixelOffset(R.styleable.CheckTextGroupView_drawableWidth,0);
        drawableHeight=ta.getDimensionPixelOffset(R.styleable.CheckTextGroupView_drawableHeight,0);
        drawTextGapWidth=ta.getDimensionPixelOffset(R.styleable.CheckTextGroupView_drawTextGapWidth,0);
        checkedTextColor=ta.getColor(R.styleable.CheckTextGroupView_checkedTextColor, Color.GREEN);
        unCheckedTextColor=ta.getColor(R.styleable.CheckTextGroupView_unCheckedTextColor,Color.GRAY);
        checkedStrokeColor=ta.getColor(R.styleable.CheckTextGroupView_checkedStrokeColor,Color.RED);
        unCheckedStrokeColor=ta.getColor(R.styleable.CheckTextGroupView_unCheckedStrokeColor,Color.GRAY);
        checkModel=ta.getInt(R.styleable.CheckTextGroupView_checkModel,SIMPLE);
        strokeModel=ta.getInt(R.styleable.CheckTextGroupView_strokeModel,GONE);
        checkedDrawable=ta.getDrawable(R.styleable.CheckTextGroupView_checkedDrawable);
        unCheckedDrawable=ta.getDrawable(R.styleable.CheckTextGroupView_unCheckedDrawable);

        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(unCheckedTextColor);
        textPaint.setStyle(Paint.Style.STROKE);

        strokePaint=new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setTextSize(strokeWidth);
        strokePaint.setColor(unCheckedStrokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);

        ta.recycle();

        setOnTouchListener(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightModel=MeasureSpec.getMode(heightMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        if(heightModel==MeasureSpec.AT_MOST){
            heightMeasureSpec=MeasureSpec.makeMeasureSpec(mesureHeightByWithLayout(width),MeasureSpec.AT_MOST);
        }
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mesureHeightByWithLayout(w);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        lasterDownX= (int) event.getX();
        lasterDownY= (int) event.getY();
        if(isEnabled())
            updateTextChecked(lasterDownX,lasterDownY,event.getAction());
        return checkTexts.size()>0&&isEnabled();
    }

    /**
     * 更新选择的状态
     * @param touchX 触摸的X坐标
     * @param touchY 触摸的Y坐标
     */
    public synchronized boolean updateTextChecked(int touchX,int touchY,int action){
        CheckText text=null;
        boolean hasExchange=false;
        for(int index=0;index<checkTexts.size();index++){
            text=checkTexts.get(index);
            if(text.inRange(touchX,touchY)){
                switch (action){
                    case  MotionEvent.ACTION_DOWN:
                        if(checkeds.get(index)==null){
                            text.setChecked(true);
                            if(checkModel==SIMPLE)
                                cleanRadioChecked();
                            checkeds.put(index,text);
                            hasExchange=true;
                            downCheckedIndex=-1;
                        }else{
                            downCheckedIndex=index;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(checkeds.get(index)==null){
                            text.setChecked(true);
                            if(checkModel==SIMPLE)
                                cleanRadioChecked();
                            checkeds.put(index,text);
                            hasExchange=true;
                        }
                        break;
                    case  MotionEvent.ACTION_UP:
                        if(downCheckedIndex==index){
                            if(checkeds.get(index)!=null){
                                text.setChecked(false);
                                checkeds.delete(index);
                                hasExchange=true;
                            }
                        }
                        break;
                }
            }
        }
        if(hasExchange){
            requestInvalidate();
        }

        if(listener!=null&&hasExchange)
            listener.onCheckedChange(this,text);
        return hasExchange;
    }


    public void cleanRadioChecked(){

        for(int index=0;index<checkeds.size();index++){
            CheckText text=checkeds.get(checkeds.keyAt(index));
            text.setChecked(false);
        }
        checkeds.clear();
    }

    /**
     * 重新计算每个文本的位置
     */
    public int mesureHeightByWithLayout(int width){

        //上一次换行停留的位置index
        int priorColIndex=0;
        //当前最新的行数
        int curRow=0;
        //开始计算每个文本的位置和宽高
        for(int index = 0; index< checkTexts.size(); index++){
            CheckText text= checkTexts.get(index);
            Rect rect=new Rect();
            textPaint.getTextBounds(text.getText(),0,text.getText().length(),rect);
            text.setWidth(rect.width()+textPaddingLeft+textPaddingRight+drawableWidth+drawTextGapWidth);
            if(drawableHeight<rect.height()+textPaddingButtom+textPaddingTop)
                text.setHeight(rect.height()+textPaddingButtom+textPaddingTop);
            else
                text.setHeight(drawableHeight);
            //判断总长度是否超过了view的宽度,超过则自动换行
            int colWidth=curTextColWidth(index,priorColIndex);
            if(colWidth>width-getPaddingRight()){
                curRow++;
                colWidth=text.getWidth()+getPaddingLeft();
                priorColIndex=index;
            }
            computerPosition(curRow,colWidth,text);
        }

        if(checkTexts.size()==0){
            return 0;
        }else{
            CheckText checkText=checkTexts.get(checkTexts.size()-1);
            return checkText.getCenterY()+checkText.getHeight()/2+getPaddingTop()+getPaddingBottom();
        }
    }

    /**
     * 计算当前列的总文本长度
     * @param targetIndex 目标的位置
     * @param priorColIndex 上一次换行停留的位置index
     * @return
     */
    public int curTextColWidth(int targetIndex,int priorColIndex){
        int colWidth=0;
        for(int index=priorColIndex;index<=targetIndex;index++){
            colWidth+= checkTexts.get(index).getWidth();
        }
        return colWidth+getPaddingLeft()+(targetIndex-priorColIndex)* textGapWidth;
    }

    /**
     * 重置每个文本在view当中的位置的和宽高
     * @param rowIndex 所在的行位置
     * @param text 文本对象
     */
    public void computerPosition(int rowIndex,int colWidth,CheckText text){
        text.setCenterX(getPaddingLeft()+colWidth-text.getWidth()/2);
        text.setCenterY(getPaddingTop()+rowIndex*lineHeight+rowIndex*text.getHeight()+text.getHeight()/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for(CheckText text: checkTexts){
            drawTextBg(canvas,text);
            drawText(canvas,text);
            drawIcon(canvas,text);
        }
    }


    /**
     * 绘制文本的背景
     * @param canvas
     * @param text
     */
    public void drawTextBg(Canvas canvas,CheckText text){

        RectF strokeRectf=new RectF();
        strokeRectf.left=text.getCenterX()-text.getWidth()/2;
        strokeRectf.top=text.getCenterY()-text.getHeight()/2;
        strokeRectf.right=text.getCenterX()+text.getWidth()/2;
        strokeRectf.bottom=text.getCenterY()+text.getHeight()/2;

        //检查是否画边框
        if(strokeModel==STROKE){
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(text.isChecked()?checkedStrokeColor:unCheckedStrokeColor);
            canvas.drawRoundRect(strokeRectf,tagRadius,tagRadius,strokePaint);
        }else if(strokeModel==GONE_STROKE){
            if(text.isChecked()){
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setColor(checkedStrokeColor);
                canvas.drawRoundRect(strokeRectf,tagRadius,tagRadius,strokePaint);
            }
        }else if(strokeModel==STROKE_FILL){
            if(!text.isChecked()){
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setColor(unCheckedStrokeColor);
                canvas.drawRoundRect(strokeRectf,tagRadius,tagRadius,strokePaint);
            }else{
                strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                strokePaint.setColor(checkedStrokeColor);
                canvas.drawRoundRect(strokeRectf,tagRadius,tagRadius,strokePaint);
            }
        }else if(strokeModel==FILL_FILL){
            strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            if(!text.isChecked()){
                strokePaint.setColor(unCheckedStrokeColor);
                canvas.drawRoundRect(strokeRectf,tagRadius,tagRadius,strokePaint);
            }else{
                strokePaint.setColor(checkedStrokeColor);
                canvas.drawRoundRect(strokeRectf,tagRadius,tagRadius,strokePaint);
            }
        }
    }

    /**
     * 绘制文本
     * @param canvas
     * @param text
     */
    public void drawText(Canvas canvas,CheckText text){
        Rect targetRect=new Rect();
        targetRect.left=text.getCenterX()-text.getWidth()/2+drawableWidth+drawTextGapWidth+textPaddingLeft;
        targetRect.top=text.getCenterY()-text.getHeight()/2+textPaddingTop;
        targetRect.right=text.getCenterX()+text.getWidth()/2-textPaddingRight;
        targetRect.bottom=text.getCenterY()+text.getHeight()/2-textPaddingButtom;

        textPaint.setColor(text.isChecked()?checkedTextColor:unCheckedTextColor);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 实现水平居中，drawText对应改为传入targetRect.centerX(),也可以不设置，默认为left,自己计算
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text.getText(), targetRect.centerX(), baseline, textPaint);
    }


    public void drawIcon(Canvas canvas,CheckText text){
        if(checkedDrawable!=null&&unCheckedDrawable!=null){
            Drawable drawable = text.isChecked() ? checkedDrawable : unCheckedDrawable;
            Bitmap bitmap = drawabletoZoomBitmap(drawable, drawableWidth, drawableHeight);
            canvas.drawBitmap(bitmap,text.getCenterX()-text.getWidth()/2+textPaddingLeft,text.getCenterY()-drawableHeight/2,null);
        }
    }

    public void updateCheckTexts(List<CheckText> checkTexts) {
        this.checkTexts = checkTexts;
        if(checkTexts!=null&&checkTexts.size()>0){
            /**
             * mesure()-->onmesure()-->layout()-->onlayout()-->dispatchDraw()-->Draw()-->onDraw();
             * 重新计算视图的宽高和绘制
             */
            requestLayout();
        }
    }

    /**
     * dispatchDraw()-->Draw()-->onDraw();
     * 重新绘制
     */
    public void requestInvalidate(){
        if(Thread.currentThread().getId()== Looper.getMainLooper().getThread().getId())
            invalidate();
        else
            postInvalidate();
    }

    static class CheckText {
        //位置
        private int index;
        //中心X坐标
        private int centerX;
        //中心Y坐标
        private int centerY;
        //文本的宽度
        private int width;
        //文本的高度
        private int height;
        //文本信息
        private String text;
        //文本字体大小
        private int textSize;
        //是否被选中
        private boolean isChecked;


        public int getCenterX() {
            return centerX;
        }

        public void setCenterX(int centerX) {
            this.centerX = centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        public void setCenterY(int centerY) {
            this.centerY = centerY;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public boolean inRange(int touchX, int touchY){
            boolean inX=(touchX>=centerX-width/2)&&(touchX<=centerX+width/2);
            boolean inY=(touchY>=centerY-height/2)&&(touchY<=centerY+height/2);
            return inX&&inY;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

    }

    public CheckTextCheckedChangeListener getListener() {
        return listener;
    }

    public void setListener(CheckTextCheckedChangeListener listener) {
        this.listener = listener;
    }

    static interface CheckTextCheckedChangeListener{
        void onCheckedChange(CheckTextGroupView view, CheckText radioText);
    }

    /**
     * drawlable 缩放
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Bitmap drawabletoZoomBitmap(Drawable drawable, int w, int h) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawabletoBitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }

    /**
     * Drawable转换成Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawabletoBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config=drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width,height,config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
