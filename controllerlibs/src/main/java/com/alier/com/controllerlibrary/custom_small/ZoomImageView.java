package com.alier.com.controllerlibrary.custom_small;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener,View.OnTouchListener{
    private boolean mOnce=false;//第一次执行
    private float mInitScale;//初始化缩放的值（缩小时的最小值）
    private float mMidScale;//双击放大时，到达的值
    private float mMaxScale;//放大的最大值
    private Matrix mScaleMatrix;//缩放的Matrix
    private ScaleGestureDetector mScaleGestureDetector;//多指触摸监听
    /***自由移动***/
    private int mLastPointerCount;//得到上次多点触控的数量，需要在触控手机数量发生变化时，做相应的处理，防止图片方式跳跃式的改变
    private int mTouchSlop;//系统默认表示触发移动事件的值
    private boolean isCanDrag=false;//是否可移动
    private float mLastX=0;//上次触摸的X坐标
    private  float mLastY=0;//上次触摸的Y坐标
    private boolean isCheckLeftAndRight;//检测水平方向
    private boolean isCheckTopAndBottom;//检测垂直方向
    /**双击放大缩小**/
    private GestureDetector mGestureDetector;//双击触发事件的手势对象
    private boolean isAutoScale;//是否正在缩放
    public ZoomImageView(Context context) {
       this(context, null);
    }
    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix=new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector=new ScaleGestureDetector(context,this);
        setOnTouchListener(this);//设置触摸事件监听
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();//得到系统触发移动事件的值
        mGestureDetector=new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDoubleTap(MotionEvent e) {//双击事件
                if(isAutoScale)
                    return true;
                float x=e.getX();
                float y=e.getY();//双击的中心点
                if(getScale()<mMidScale&&getScale()>=mInitScale){//想放大到mMidScale
                    postDelayed(new AutoScaleRunnable(mMidScale +0.01f, x, y), 16);
                }else if(getScale()<mMaxScale&&getScale()>mMidScale){//想放大到mMaxScale
                    postDelayed(new AutoScaleRunnable(mMaxScale , x, y), 16);
                }else if (getScale() == mMaxScale) {//缩小到mMidScale
                   postDelayed(new AutoScaleRunnable(mMidScale , x, y), 16);
                }else if(getScale()==mMidScale){//想缩小到mMinScale
                   postDelayed(new AutoScaleRunnable(mInitScale,x,y),16);
                }
                isAutoScale=true;
                return true;
            }
        });
    }
    /**进行缓慢的缩放*/
    public class AutoScaleRunnable implements  Runnable{
        private float x;//中心点位置
        private float y;
        private float mTagScale;//最终缩放值
        private final float BIGING=1.07f;//正在放大
        private final float SMILE=0.93f;//正在缩小
        private float mScale;//当前缩放值
        public AutoScaleRunnable(float mTagScale,float x,float y){
            this.mTagScale=mTagScale;
            this.x=x;
            this.y=y;
            if(getScale()<mTagScale){
                mScale=BIGING;
            }else{
                mScale=SMILE;
            }
        }
        @Override
        public void run() {
            mScaleMatrix.postScale(mScale,mScale,x,y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            if((mScale==BIGING&&getScale()<mTagScale)||(mScale==SMILE&&getScale()>mTagScale)){
                //再次进行缩放
                postDelayed(this,16);
            }else{//设置目标的缩放值
                mScaleMatrix.postScale(mTagScale/getScale(),mTagScale/getScale(),x,y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale=false;//缩放结束
            }
        }
    }
    /**
     *当View控件attach到window时会执行这个方法
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    /**
     *当View控件从window上移除时会执行这个方法
     */
    @SuppressLint("NewApi")
	@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if(!mOnce){
            //得到控件的宽和高
            int width=getWidth();
            int height=getHeight();
            //得到图片的宽和高
             Drawable d=getDrawable();
            if(d==null){
                return;
            }
            int dw=d.getIntrinsicWidth();
            int dh=d.getIntrinsicHeight();
             float scale=1.0f;//缩放值
            if(dw>width&&dh<height){//图片宽度大于控件宽度，图片高度小于控件高度
                scale=width*1.0f/dw;//以宽度缩放为主
            }
            if(dh>height&&dw<width){//图片宽度小于控件宽度，图片高度大于控件高度
                scale=height*1.0f/dh;//以高度缩放为主
            }
            if(dw>width&&dh>height){//图片宽高都大于控件的宽高
                scale=Math.min(width*1.0f/dw,height*1.0f/dh);//去最小值
            }
            if(dw<width&&dh<height){//图片宽高都小于控件宽高，图片应该放大
                scale=Math.min(width*1.0f/dw,height*1.0f/dh);
            }
            //得到初始化时缩放比例
            mInitScale=scale;
            mMaxScale=mInitScale*4;
            mMidScale=mInitScale*2;
            //将图片移动到控件的中心点
            float dx= getWidth()*1.0f/2-dw*1.0f/2;
            float dy= getHeight()*1.0f/2-dh*1.0f/2;
            mScaleMatrix.postTranslate(dx,dy);
            mScaleMatrix.postScale(mInitScale,mInitScale,width/2,height/2);
            setImageMatrix(mScaleMatrix);
            mOnce=true;
        }
    }
    /**
     *得到当前的缩放值
     */
    public float getScale(){
        float vaules[]=new float[9];
        mScaleMatrix.getValues(vaules);
        return vaules[Matrix.MSCALE_X];//X,Y方向的缩放情况都一样
    }
    @Override
    public boolean onScale(ScaleGestureDetector detector) {//缩放进行中
        if(getDrawable()==null){
            return true;
        }
        float scaleFactor=detector.getScaleFactor();//得到当前手指操作需要缩放的值
        float scale=getScale();
        if(scaleFactor>1.0f){//当前为正在放大
            if(scaleFactor*scale>=mMaxScale){//放大后超过最大放大量
                scaleFactor=mMaxScale/scale;
            }
        }else{//当前为正在缩小
            if(scaleFactor*scale<mInitScale){//缩小时小于最初最小的缩放值
                scaleFactor=mInitScale/scale;
            }
        }
        //缩放
        mScaleMatrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusY());
        checkBorderAndCenterWhenScale();
        setImageMatrix(mScaleMatrix);
        return true;//这里需要返回true，保证事件能够继续执行
    }

    /**
     *得到图片放大以后的宽和高
     */
    private RectF getMatrixRectf(){
        Matrix matrix=mScaleMatrix;
         RectF rectF =new RectF();
        Drawable d=getDrawable();
        if(d!=null){
         rectF.set(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            matrix.mapRect(rectF);//得到放大缩小以后的图片宽和高，left ,right,top, bottom
        }
        return rectF;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {//缩放开始
        return true;//这里需要返回true，保证事件能够继续执行
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {//缩放结束
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x=0;
        float y=0;
        int pointerCount=event.getPointerCount();//得到多点触控数量
        for(int i=0;i<pointerCount;i++){
            x+=event.getX(i);
            y+=event.getY(i);
        }
        x/=pointerCount;//触摸中心点X坐标
        y/=pointerCount;//触摸中心点Y坐标

        if(mLastPointerCount!=pointerCount){//数量发生改变时
            mLastX=x;
            mLastY=y;
            isCanDrag=false;
        }
        mLastPointerCount=pointerCount;
        RectF rectF=getMatrixRectf();//获得图片缩放后的宽高
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
                if(rectF.height()>getHeight()+0.01f||rectF.width()>getWidth()+0.01f){
                    getParent().requestDisallowInterceptTouchEvent(true);//通知父布局事件不被拦截，由当前控件进行事件处理
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(rectF.height()>getHeight()+0.01f||rectF.width()>getWidth()+0.01f){
                    getParent().requestDisallowInterceptTouchEvent(true);//通知父布局事件不被拦截，由当前控件进行事件处理
                }
                float dx=x-mLastX;
                float dy=y-mLastY;
                if(!isCanDrag){
                    isCanDrag=isMoveAction(dx,dy);
                }
                if(isCanDrag){
                    isCheckLeftAndRight=isCheckTopAndBottom=true;//移动时，默认都开启检测
                    if(getDrawable()!=null){
                        //如果当前宽度小于控件的宽度，不允许水平移动
                        if(rectF.width()<=getWidth()){
                            isCheckLeftAndRight=false;
                            dx=0;
                        }
                        //如果当前高度小于控件的高度，不允许纵向移动
                        if(rectF.height()<=getHeight()){
                            isCheckTopAndBottom=false;//为了当高度小于控件高度时，此时的空白不算是白边，避免出现边界检测
                            dy=0;
                        }
                        mScaleMatrix.postTranslate(dx,dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX=x;
                mLastY=y;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
               mLastPointerCount=0;
                break;
        }
        return true;
    }
    /**
     * 判断是否足以触发move事件
     */
    private boolean isMoveAction(float dx,float dy){
        return Math.sqrt(dx*dx+dy*dy)>=mTouchSlop?true:false;
    }
    /**
     *在缩放的时候进行边界控制，并居中，防止缩放的时候出现白边
     */
    private void checkBorderAndCenterWhenScale(){
        RectF rectF=getMatrixRectf();
        float dx=0;//水平方向需要调整的偏移量
        float dy=0;//垂直方向需要调整的偏移量
        int width=getWidth();//控件宽度
        int height=getHeight();//控件高度
        if(rectF.width()>width){//放大图片大于控件宽度
            if(rectF.left>0){//右边偏移，需要向左移动
                dx=-rectF.left;
            }
            if(rectF.right<width){//左边偏移，需要向右移动
                dx=width-rectF.right;
            }
        }
        if(rectF.height()>height){//放大图片大于控高度
            if(rectF.top>0){//向下偏离，需要向上移动
                dy=-rectF.top;
            }
            if(rectF.bottom<height){//向上偏移，需要向下移动
                dy=height-rectF.bottom;
            }
        }
        if(rectF.width()<width||rectF.height()<height){//如果宽高小于控件的宽高时，需要居中显示
            dx=width*1.0f/2-rectF.width()/2-rectF.left;
            dy=height*1.0f/2-rectF.height()/2-rectF.top;
        }
        mScaleMatrix.postTranslate(dx, dy);
    }
    /**
     *当移动时，检测边界
     */
    private void checkBorderWhenTranslate(){
        RectF rectF=getMatrixRectf();
        float dx=0;
        float dy=0;
        float width=getWidth();
        float height=getHeight();
         if(rectF.top>0&&isCheckTopAndBottom){
             dy=-rectF.top;
         }
        if(rectF.bottom<height&&isCheckTopAndBottom){
            dy=height-rectF.bottom;
        }
        if(rectF.left>0&&isCheckLeftAndRight){
            dx=-rectF.left;
        }
        if(rectF.right<width&&isCheckLeftAndRight){
            dx=width-rectF.right;
        }
        mScaleMatrix.postTranslate(dx,dy);
    }
}