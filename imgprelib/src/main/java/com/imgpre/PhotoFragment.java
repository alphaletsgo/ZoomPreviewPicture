package com.imgpre;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoFragment extends LazyFragment {
    //上下文对象
    private PhotoActivity context;
    /**
     * 预览图片 类型
     */
    public static final String KEY_START_BOUND = "startBounds";
    public static final String KEY_TRANS_PHOTO = "is_trans_photo";
    public static final String KEY_PATH = "key_path";
    //图片地址
    private String imgUrl;
    // 是否是以动画进入的Fragment
    private boolean isTransPhoto = false;
    //图片
    private SmoothImageView photoView;
    //图片的外部控件
    private View rootView;
    //进度条
    private ProgressBar loading;
    private PhotoViewAttacher.OnPhotoTapListener  onPhotoTapListener = null;
    private SmoothImageView.OnTransformingListener onTransformingListener = null;

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_photo_layout, container, false);
        context = (PhotoActivity) getActivity();
        initView(view);
        initDate();

        return view;
    }

    /**
     * 初始化控件
     */
    private void initView(View view) {
        loading = (ProgressBar) view.findViewById(R.id.loading);
        photoView = (SmoothImageView) view.findViewById(R.id.photoView);
        rootView = view.findViewById(R.id.rootView);
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            //地址
            imgUrl = bundle.getString(KEY_PATH);
            //位置
            Rect startBounds = bundle.getParcelable(KEY_START_BOUND);
            if (startBounds!=null) {
                photoView.setThumbRect(startBounds);
            }
            //是否展示动画
            isTransPhoto = bundle.getBoolean(KEY_TRANS_PHOTO, false);
            //加载缩略图
            //加载原图
            Glide.with(this)
                    .load(imgUrl)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_iamge_zhanwei)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            photoView.setImageBitmap(resource);
                            loading.setVisibility(View.GONE);
                        }
                    });
        }
        // 非动画进入的Fragment，默认背景为黑色
        if (!isTransPhoto) {
            rootView.setBackgroundColor(Color.BLACK);
        }
        photoView.setMinimumScale(1f);
        if (onPhotoTapListener!=null){
            photoView.setOnPhotoTapListener(onPhotoTapListener);
        }

        photoView.setAlphaChangeListener(new SmoothImageView.OnAlphaChangeListener() {
            @Override
            public void onAlphaChange(int alpha) {
                rootView.setBackgroundColor(getColorWithAlpha(alpha / 255f, Color.BLACK));
            }
        });

        photoView.setTransformOutListener(new SmoothImageView.OnTransformOutListener() {
            @Override
            public void onTransformOut() {
                if (photoView.checkMinScale()) {
                    ((PhotoActivity) getActivity()).transformOut();
                }
            }
        });
        if (onTransformingListener!=null){
            photoView.setOnTransformingListener(onTransformingListener);
        }
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    private boolean isLoaded = false;
    private boolean isLoadFinish = false;

    @Override
    protected void onLazy() {
        if (isLoaded) {
            return;
        }
        isLoaded = true;
        //加载原图
        Glide.with(this)
                .load(imgUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_iamge_zhanwei)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        isLoadFinish = true;
                        loading.setVisibility(View.GONE);
                        photoView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (!isLoadFinish) {
                            loading.setVisibility(View.VISIBLE);
                        } else {
                            loading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        loading.setVisibility(View.GONE);
                    }


                });
    }

    public void transformIn() {
        photoView.transformIn(new SmoothImageView.onTransformListener() {
            @Override
            public void onTransformCompleted(SmoothImageView.Status status) {
                rootView.setBackgroundColor(Color.BLACK);
            }
        });
    }

    public void transformOut(SmoothImageView.onTransformListener listener) {
        photoView.transformOut(listener);
    }

    public void changeBg(int color) {
        rootView.setBackgroundColor(color);
    }

    /**
     *  设置图片点击回调
     *
     * @param onPhotoTapListener 这是一个回调对象
     */
    public void registerPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener){
        this.onPhotoTapListener = onPhotoTapListener;
        if (photoView!=null){
            photoView.setOnPhotoTapListener(onPhotoTapListener);
        }
    }
    public void registerOnTransform(SmoothImageView.OnTransformingListener onTransformingListener){
        this.onTransformingListener = onTransformingListener;
        if (photoView!=null){
            photoView.setOnTransformingListener(onTransformingListener);
        }
    }
}
