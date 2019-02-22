package com.mars.alien.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.mars.alien.utils.LogUtil;
import com.mars.alien.utils.ScreenUtil;
import com.mars.alien.utils.ViewUtil;
import com.mars.alien.R;

public class SwipeWindowHelper extends Handler {

    private static final String CURRENT_POINT_X = "currentPointX"; //点击事件

    private static final int MSG_ACTION_DOWN = 1;            //点击事件
    private static final int MSG_ACTION_MOVE = 2;            //滑动事件
    private static final int MSG_ACTION_UP = 3;                //点击结束
    private static final int MSG_SLIDE_CANCEL = 4;            //开始滑动，不返回前一个页面
    private static final int MSG_SLIDE_CANCELED = 5;        //结束滑动，不返回前一个页面
    private static final int MSG_SLIDE_PROCEED = 6;            //开始滑动，返回前一个页面
    private static final int MSG_SLIDE_FINISHED = 7;        //结束滑动，返回前一个页面
    private static final int MARGIN_THRESHOLD = 40;        //px 默认拦截手势区间 0~40
    private static final int SHADOW_WIDTH = 50;                //px 阴影宽度

    private boolean isSliding;                    //是否正在滑动
    private float distanceX;                    //px 当前滑动距离 （正数或0）
    private int marginThreshold;                //px 拦截手势区间
    private float lastPointX;                    //记录手势在屏幕上的X轴坐标
    //	private float lastPointY;                    //记录手势在屏幕上的Y轴坐标
    private float actionDownPointX = -1;        //记录手势在屏幕上按下的X轴坐标
    private float actionDownPointY;                //记录手势在屏幕上按下的Y轴坐标
    private float actionMoveMaxDistanceY;        //记录手势在屏幕上按下的Y轴坐标
    private long actionDownPointTime = -1;        //记录手势在屏幕上按下的时间
    private boolean isSlideAnimPlaying;            //滑动动画展示过程中
    private boolean isSupportSlideBack;
    private final Activity currentActivity;
    public boolean isSlideFinish = true;

    @NonNull
    private Window currentWindow;
    private ViewManager viewManager;
    private final FrameLayout currentContentView;

    private int firstCurrentActivityColor;

    public SwipeWindowHelper(@NonNull Activity currentActivity) {
        this(currentActivity, true);
    }

    public SwipeWindowHelper(@NonNull Activity currentActivity, boolean isSupportSlideBack) {
        this.currentActivity = currentActivity;
        currentWindow = currentActivity.getWindow();
        this.isSupportSlideBack = isSupportSlideBack;
        currentContentView = getContentView(currentWindow);
        viewManager = new ViewManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            firstCurrentActivityColor = this.currentActivity.getWindow().getStatusBarColor();
        }
    }

    public boolean processTouchEvent(MotionEvent ev) {
        if (!isSupportSlideBack) { //不支持滑动返回，则手势事件交给View处理
            return false;
        }
        if (isSlideAnimPlaying) {
            return true;
        }
        if (marginThreshold == 0) {
            marginThreshold = MARGIN_THRESHOLD;
        }

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int actionIndex = ev.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                actionDownPointX = lastPointX = ev.getRawX();
                actionDownPointY = ev.getRawY();
                actionDownPointTime = System.currentTimeMillis();
                actionMoveMaxDistanceY = 0;
                isSliding = false;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (isSliding) {  //有第二个手势事件加入，而且正在滑动事件中，则直接消费事件
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isSliding && isLateralMovement(ev)) {
                    isSliding = true;
                    lastPointX = ev.getRawX();
                    hideInputMethod();
                    viewManager.addViewFromPreviousActivity();
                    sendEmptyMessage(MSG_ACTION_DOWN);
                    // TODO: 2019/2/12
                }

                if ((isSliding) && actionIndex == 0) { //开始滑动
                    Message message = obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putFloat(CURRENT_POINT_X, ev.getRawX());
                    message.what = MSG_ACTION_MOVE;
                    message.setData(bundle);
                    sendMessage(message);
                    isSlideFinish = false;
                }
                if (isSliding) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isLateralMovement(ev)) {
                    // TODO: 2019/2/12
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_OUTSIDE:
                if (isSliding && actionIndex == 0) { // 取消滑动 或 手势抬起 ，而且手势事件是第一手势，开始滑动动画
                    isSliding = false;
                    sendEmptyMessage(MSG_ACTION_UP);
                    return true;
                }
                break;
            default:
                isSliding = false;
                break;
        }
        return false;
    }

    public boolean isSliding() {
        return isSliding;
    }

    public boolean isSlideFinish() {
        return isSlideFinish;
    }

    private boolean isLateralMovement(MotionEvent ev) {
        if (getContext() instanceof BaseActivity && !((BaseActivity) getContext()).isMovementForBackPressed(ev)) {
            return false;
        }
        if (actionDownPointY <= ScreenUtil.getStatusBarHeight() + ViewUtil.dip2px(48)) {
            return false;
        }
        float distanceY = Math.abs(actionDownPointY - ev.getRawY());
        if (distanceY > actionMoveMaxDistanceY) {
            actionMoveMaxDistanceY = distanceY;
        }
        return actionMoveMaxDistanceY < ViewUtil.dip2px(20) && actionMoveMaxDistanceY <= Math.abs(actionDownPointX - ev.getRawX()) && (ev.getRawX() - actionDownPointX) > ViewUtil.dip2px(5);
    }

    public Context getContext() {
        return currentWindow.getContext();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_ACTION_DOWN:
                if (currentContentView.getChildCount() >= 3) {
                    View curView = viewManager.getDisplayView();
                    if (curView.getBackground() == null) {
                        int color = getWindowBackgroundColor();
                        curView.setBackgroundColor(color);
                    }
                }
                break;

            case MSG_ACTION_MOVE:
                final float curPointX = msg.getData().getFloat(CURRENT_POINT_X);
                onSliding(curPointX);
                break;
            case MSG_ACTION_UP:
                if (distanceX == 0) {
                    if (currentContentView.getChildCount() >= 3) {
                        viewManager.removeShadowView();
                        viewManager.resetPreviousView();
                        isSlideFinish = true;
                    }
                    resetStatusBarColor();
                } else if (isProceedForActionUp()) {
                    sendEmptyMessage(MSG_SLIDE_PROCEED);
                } else {
                    sendEmptyMessage(MSG_SLIDE_CANCEL);
                }
                break;

            case MSG_SLIDE_CANCEL:
                startSlideAnim(true);
                break;

            case MSG_SLIDE_CANCELED:
                distanceX = 0;
                isSliding = false;
                viewManager.removeShadowView();
                viewManager.resetPreviousView();
                isSlideFinish = true;
                break;

            case MSG_SLIDE_PROCEED:
                startSlideAnim(false);
                break;

            case MSG_SLIDE_FINISHED:
                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();
                    activity.finish();
                    activity.overridePendingTransition(R.anim.swip_window_enter, R.anim.swip_window_exit);
                } else if (getContext() instanceof ContextWrapper) {
                    Context baseContext = ((ContextWrapper) getContext()).getBaseContext();
                    if (baseContext instanceof Activity) {
                        Activity activity = (Activity) baseContext;
                        activity.finish();
                        activity.overridePendingTransition(0, 0);
                    }
                }
                isSlideFinish = true;
                break;

            default:
                break;
        }
    }

    public void hideInputMethod() {
        InputMethodManager inputMethod = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = currentWindow.getCurrentFocus();
        if (view != null) {
            inputMethod.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isProceedForActionUp() {
        final int width = getContext().getResources().getDisplayMetrics().widthPixels;
        return distanceX > width / 4 ||
                ((System.currentTimeMillis() - actionDownPointTime) < 200 && actionDownPointX < ViewUtil.dip2px(15) && distanceX > ViewUtil.dip2px(10)); //最左边右划返回处理200毫秒内快速短距离滑动返回
    }

    /**
     * 手动处理滑动事件
     */
    private synchronized void onSliding(float curPointX) {
        final int width = getContext().getResources().getDisplayMetrics().widthPixels;
        View previewActivityContentView = viewManager.previousContentView;
        View shadowView = viewManager.mShadowView;
        View currentActivityContentView = viewManager.getDisplayView();

        if (previewActivityContentView == null || currentActivityContentView == null || shadowView == null) {
            sendEmptyMessage(MSG_SLIDE_CANCELED);
            return;
        }

        final float localDistanceX = curPointX - lastPointX;
        lastPointX = curPointX;
        distanceX = distanceX + localDistanceX;
        if (distanceX < 0) {
            distanceX = 0;
        }

        previewActivityContentView.setX(-width / 3 + distanceX / 3);
        shadowView.setX(distanceX - SHADOW_WIDTH);
        currentActivityContentView.setX(distanceX);
    }

    private int getWindowBackgroundColor() {
        TypedArray array = null;
        try {
            array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
            return array.getColor(0, ContextCompat.getColor(getContext(), android.R.color.transparent));
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }

    /**
     * 开始自动滑动动画
     *
     * @param slideCanceled 是不是要返回（true则不关闭当前页面）
     */
    private void startSlideAnim(final boolean slideCanceled) {
        final View previewView = viewManager.previousContentView;
        final View shadowView = viewManager.mShadowView;
        final View currentView = viewManager.getDisplayView();

        if (previewView == null || currentView == null) {
            return;
        }

        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        Interpolator interpolator = new DecelerateInterpolator(2f);

        // preview activity's animation
        ObjectAnimator previewViewAnim = new ObjectAnimator();
        previewViewAnim.setInterpolator(interpolator);
        previewViewAnim.setProperty(View.TRANSLATION_X);
        float preViewStart = distanceX / 3 - width / 3;
        float preViewStop = slideCanceled ? -width / 3 : 0;
        previewViewAnim.setFloatValues(preViewStart, preViewStop);
        previewViewAnim.setTarget(previewView);

        // shadow view's animation
        ObjectAnimator shadowViewAnim = new ObjectAnimator();
        shadowViewAnim.setInterpolator(interpolator);
        shadowViewAnim.setProperty(View.TRANSLATION_X);
        float shadowViewStart = distanceX - SHADOW_WIDTH;
        float shadowViewEnd = slideCanceled ? SHADOW_WIDTH : width + SHADOW_WIDTH;
        shadowViewAnim.setFloatValues(shadowViewStart, shadowViewEnd);
        shadowViewAnim.setTarget(shadowView);

        // current view's animation
        ObjectAnimator currentViewAnim = new ObjectAnimator();
        currentViewAnim.setInterpolator(interpolator);
        currentViewAnim.setProperty(View.TRANSLATION_X);
        float curViewStart = distanceX;
        float curViewStop = slideCanceled ? 0 : width;
        currentViewAnim.setFloatValues(curViewStart, curViewStop);
        currentViewAnim.setTarget(currentView);

        // play animation together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(slideCanceled ? 150 : 300);
        animatorSet.playTogether(previewViewAnim, shadowViewAnim, currentViewAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (slideCanceled) {
                    isSlideAnimPlaying = false;
                    previewView.setX(0);
                    shadowView.setX(-SHADOW_WIDTH);
                    currentView.setX(0);
                    sendEmptyMessage(MSG_SLIDE_CANCELED);
                } else {
                    viewManager.removeShadowView();
                    sendEmptyMessage(MSG_SLIDE_FINISHED);
                }
            }
        });
        animatorSet.start();
        isSlideAnimPlaying = true;

        if (slideCanceled) {
            resetStatusBarColor();
        }
    }

    private void resetStatusBarColor() {
        MApplication application = (MApplication) currentWindow.getContext().getApplicationContext();
        setCurrentActivityStatusBarTranslucent(application.getActivityLifecycleHelper().getCurrentActivity().getWindow(), false);
    }

    private FrameLayout getContentView(Window window) {
        if (window == null) return null;
        return (FrameLayout) window.findViewById(Window.ID_ANDROID_CONTENT);
    }

    class ViewManager {
        private Activity previousActivity;
        private View previousContentView;
        private View mShadowView;

        private boolean addViewFromPreviousActivity() {
            if (currentContentView.getChildCount() == 0) {
                previousContentView = null;
                return false;
            }

            MApplication application = (MApplication) currentWindow.getContext().getApplicationContext();
            previousActivity = application.getActivityLifecycleHelper().getPreActivity();
            if (previousActivity == null) {
                previousContentView = null;
                return false;
            }

            try {
                ViewGroup previousActivityContainer = getContentView(previousActivity.getWindow());
                if (previousActivityContainer == null || previousActivityContainer.getChildCount() == 0) {
                    previousContentView = null;
                    previousActivity = null;
                    return false;
                }


                Bitmap bm = convertViewToBitmap(previousActivityContainer.getChildAt(0));
                previousContentView = new View(currentContentView.getContext());
                previousContentView.setBackgroundDrawable(new BitmapDrawable(bm));

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.topMargin = 0;
                setCurrentActivityStatusBarTranslucent(application.getActivityLifecycleHelper().getCurrentActivity().getWindow(), true);
                currentContentView.addView(previousContentView, 0, params);
                currentContentView.setBackgroundColor(ContextCompat.getColor(currentContentView.getContext(), R.color.navigation_bar_bg));
                addShadowView();
                viewManager.removeCurrentViewEvent();
                return true;
            } catch (Exception e) {
                LogUtil.d(e.toString());
            }
            return false;
        }


        Bitmap convertViewToBitmap(View view) {
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }

        /**
         * Remove the PreviousContentView at current Activity and put it into previous Activity.
         */
        private void resetPreviousView() {
            if (previousContentView == null || currentContentView == null) {
                return;
            }
            previousContentView.setX(0);
            currentContentView.removeView(previousContentView);
            previousContentView = null;
            previousActivity = null;
        }

        /**
         * add shadow view on the left of content view
         */
        private void addShadowView() {
            try {
                if (mShadowView == null) {
                    mShadowView = new ShadowView(getContext());
                    mShadowView.setX(-SHADOW_WIDTH);
                }
                final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        SHADOW_WIDTH, FrameLayout.LayoutParams.MATCH_PARENT);
                final FrameLayout contentView = currentContentView;
                contentView.removeView(mShadowView);
                contentView.addView(mShadowView, 1, layoutParams);
            } catch (Exception e) {
                LogUtil.i("avoid add view if exist");
            }
        }

        private void removeShadowView() {
            if (mShadowView == null) return;
            final FrameLayout contentView = getContentView(currentWindow);
            contentView.removeView(mShadowView);
            mShadowView = null;
        }

        private View getDisplayView() {
            int index = 0;
            if (viewManager.previousContentView != null) {
                index = index + 1;
            }

            if (viewManager.mShadowView != null) {
                index = index + 1;
            }
            return currentContentView.getChildAt(index);
        }

        public void removeCurrentViewEvent() {
            View currentView = getDisplayView();
            if (currentView == null || !(currentView instanceof ViewGroup)) {
                return;
            }
            currentChildCount = 0;
            removeViewEnent((ViewGroup) currentView);
        }

        private int currentChildCount = 0;

        private void removeViewEnent(ViewGroup groupView) {
            int childCount = groupView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = groupView.getChildAt(i);
                currentChildCount++;
                if (currentChildCount > 500) {
                    return;
                }
                if (view.getVisibility() == View.VISIBLE) {
                    view.cancelLongPress();
                    view.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    if (view instanceof ViewGroup) {
                        removeViewEnent((ViewGroup) view);
                    }
                }
            }
        }
    }

    private void setCurrentActivityStatusBarTranslucent(Window window, boolean translucent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (translucent) {
                int color = firstCurrentActivityColor;
                int red = (color & 0xff0000) >> 16;
                int green = (color & 0x00ff00) >> 8;
                int blue = (color & 0x0000ff);
                int alphaColor = Color.argb(0, red, green, blue);
                window.setStatusBarColor(alphaColor);
            } else {
                window.setStatusBarColor(firstCurrentActivityColor);
            }

        }
    }
}

class ShadowView extends View {
    private Drawable mDrawable;

    public ShadowView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable == null) {
            int colors[] = {0x00000000, 0x17000000, 0x43000000};//分别为开始颜色，中间夜色，结束颜色
            mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        }
        mDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mDrawable.draw(canvas);
    }
}
