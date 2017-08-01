package com.imgpre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imgpre.bean.PageBean;

import java.util.ArrayList;
import java.util.List;

import cn.isif.alibs.utils.log.ALog;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.imgpre.Action.AC_COMMENT;
import static com.imgpre.Action.AC_OTHER;

public class PhotoActivity extends FragmentActivity implements PhotoViewAttacher.OnPhotoTapListener, View.OnClickListener, SmoothImageView.OnTransformingListener {

    private boolean isTransformOut = false;
    //图片的地址
    private ArrayList<PageBean> pageBean;
    //当前图片的位置
    private int currentIndex;
    //图片的展示的Fragment
    private List<PhotoFragment> fragments = new ArrayList<>();
    //展示图片的viewPager
    private PhotoViewPager viewPager;
    //显示图片数
    private TextView ltAddDot;
    //底部信息显示
    private LinearLayout boardSummary;
    //summary
    private TextView summaryText;
    //评论
    private TextView commentText;

    private static OnElementClick onElementClick = null;


    /***
     * 启动预览
     *
     * @param activity     活动对象
     * @param tempData     图片集合
     * @param currentIndex 当前索引坐标
     ***/
    public static void startActivity(Activity activity, ArrayList<PageBean> tempData, int currentIndex, OnElementClick onElementClick) {
        PhotoActivity.onElementClick = onElementClick;
        // 图片的地址
        //获取图片的bitmap
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putParcelableArrayListExtra("imagePaths", tempData);
        intent.putExtra("position", currentIndex);
        activity.startActivity(intent);
    }

    /***
     * 启动预览
     *
     * @param activity     活动对象
     * @param tempData     图片集合
     * @param currentIndex 当前索引坐标
     ***/
    public static void startActivity(Activity activity, ArrayList<PageBean> tempData, int currentIndex) {
        // 图片的地址
        //获取图片的bitmap
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putParcelableArrayListExtra("imagePaths", tempData);
        intent.putExtra("position", currentIndex);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imge_preview_photo);
        initDate(savedInstanceState);
        initView();
    }

    /**
     * 初始化数据
     */
    private void initDate(Bundle savedInstanceState) {
        pageBean = getIntent().getParcelableArrayListExtra("imagePaths");
        currentIndex = getIntent().getIntExtra("position", -1);
        if (pageBean != null) {
            for (int i = 0; i < pageBean.size(); i++) {
                PhotoFragment fragment = new PhotoFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(PhotoFragment.KEY_PATH, pageBean.get(i).thumbImage.getUrl());
                bundle.putParcelable(PhotoFragment.KEY_START_BOUND, pageBean.get(i).thumbImage.getBounds());
                bundle.putBoolean(PhotoFragment.KEY_TRANS_PHOTO, currentIndex == i);
                fragment.setArguments(bundle);
                fragment.registerPhotoTapListener(this);
                fragment.registerOnTransform(this);
                fragments.add(fragment);
            }
        } else {
            finish();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        viewPager = (PhotoViewPager) findViewById(R.id.viewPager);
        ltAddDot = (TextView) findViewById(R.id.ltAddDot);
        ltAddDot.setText(currentIndex + 1 + "/" + pageBean.size());
        boardSummary = (LinearLayout) this.findViewById(R.id.board_summary);
        boardSummary.setVisibility(View.VISIBLE);
        summaryText = (TextView) this.findViewById(R.id.tv_summary);
        ReadMoreUtil.limitStringTo140(PhotoActivity.this, pageBean.get(currentIndex).subContent, summaryText, 3, new SummeryTextClickListener());
        commentText = (TextView) this.findViewById(R.id.tv_comment);
        commentText.setOnClickListener(this);
        //viewPager的适配器
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当被选中的时候设置小圆点和当前位置
                if (ltAddDot != null) {
                    ltAddDot.setText((position + 1) + "/" + pageBean.size());
                }
                currentIndex = position;
                viewPager.setCurrentItem(currentIndex, true);
                //设置summer
                ReadMoreUtil.limitStringTo140(PhotoActivity.this, pageBean.get(position).subContent, summaryText, 3, new SummeryTextClickListener());
                //设置评论
                commentText.setText(String.valueOf(pageBean.get(position).commentCount));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final PhotoFragment fragment = fragments.get(currentIndex);
                fragment.transformIn();
            }
        });

    }

    //退出预览的动画
    public void transformOut() {
        if (isTransformOut) {
            return;
        }
        isTransformOut = true;
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < pageBean.size()) {
            PhotoFragment fragment = fragments.get(currentItem);
            ltAddDot.setVisibility(View.GONE);
            fragment.changeBg(Color.TRANSPARENT);
            fragment.transformOut(new SmoothImageView.onTransformListener() {
                @Override
                public void onTransformCompleted(SmoothImageView.Status status) {
                    exit();
                }
            });
        } else {
            exit();
        }
    }

    /**
     * 关闭页面
     */
    private void exit() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        transformOut();
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        boardHideOrShow();
    }

    private void boardHideOrShow() {
        setBoardVisible(!boardVisible);
        //当且仅当用户主动点击显示或隐藏式起作用
        canVisible = boardVisible;
    }

    private boolean boardVisible = true;

    @Override
    public void onClick(View view) {
        if (onElementClick == null) return;

        if (view.getId() == R.id.tv_comment) {
            onElementClick.onElementClick(this, currentIndex, view, new Action(AC_COMMENT));
        } else {
            onElementClick.onElementClick(this, currentIndex, view, new Action(AC_OTHER));
        }
    }

    private boolean canVisible = true;//控制移动结束后是否显示board 默认显示

    @Override
    public void onTransforming(float offsetX, float offsetY) {
        setBoardVisible(false);
    }

    @Override
    public void onTransFormFinish(float offsetX, float offsetY) {
        setBoardVisible(canVisible);
    }

    private boolean setBoardVisible(boolean boardVisible) {
        boardSummary.setVisibility(boardVisible ? View.VISIBLE : View.GONE);
        this.boardVisible = boardVisible;
        return boardVisible;
    }

    /**
     * pager的适配器
     */
    private class PhotoPagerAdapter extends FragmentPagerAdapter {

        PhotoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    public interface OnElementClick {
        /**
         * 处理界面元素回调函数
         *
         * @param view   被点击的view
         * @param action 与view对应的元素标记
         */
        void onElementClick(Context context, int position, View view, Action action);
    }

    @Override
    protected void onDestroy() {
        PhotoActivity.onElementClick = null;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //当配置发生变化时（横竖屏切换等原因）
        ALog.d(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE?"横屏":"竖屏");
        ReadMoreUtil.limitStringTo140(PhotoActivity.this, pageBean.get(currentIndex).subContent, summaryText, 3, new SummeryTextClickListener());
        super.onConfigurationChanged(newConfig);
    }

    class SummeryTextClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ReadMoreUtil.limitStringTo140(PhotoActivity.this, pageBean.get(currentIndex).subContent, summaryText, 3, this);
        }
    }

}
