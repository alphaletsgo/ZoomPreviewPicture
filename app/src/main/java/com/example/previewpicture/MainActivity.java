package com.example.previewpicture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.imgpre.Action;
import com.imgpre.PhotoActivity;
import com.imgpre.bean.PageBean;
import com.imgpre.bean.ThumbViewInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private ArrayList<PageBean> pageBeans = new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //初始化数据和控件
    private void init() {
        //准备数据
        List<String> urls = ImageUrlConfig.getUrls();
        for (int i = 0; i < urls.size(); i++) {
            PageBean p = new PageBean("this is content,this is content,this is content,this is content,this is content,this is content,this is content,this is content,this is content,this is content" +
                    "this is contentthis is contentthis is contentthis is contentthis is contentthis is contentthis is contentthis is content" + i, i, new ThumbViewInfo(urls.get(i)));
            pageBeans.add(p);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        BaseQuickAdapter adapter = new BaseQuickAdapter<PageBean>(R.layout.item_image, pageBeans) {
            @Override
            protected void convert(final BaseViewHolder holder, final PageBean pageBean) {
                final ImageView thumbView = holder.getView(R.id.iv);
                Glide.with(MainActivity.this)
                        .load(pageBean.thumbImage.getUrl())
                        .into(thumbView);
                holder.getView(R.id.iv).setTag(R.id.iv, pageBean.thumbImage.getUrl());
            }
        };
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                assembleDataList();
                PhotoActivity.startActivity(MainActivity.this, pageBeans, position, new PhotoActivity.OnElementClick() {
                    @Override
                    public void onElementClick(Context context,int position, View view, Action action) {
                        switch (action.getAction()){
                            case Action.AC_COMMENT:
                                Toast.makeText(context,"comment被点击了",Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                });
            }
        });
    }

    /**
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
    private void computeBoundsBackward(int firstCompletelyVisiblePos) {
        for (int i = firstCompletelyVisiblePos; i < pageBeans.size(); i++) {
            View itemView = mGridLayoutManager.findViewByPosition(i);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv);
                thumbView.getGlobalVisibleRect(bounds);
            }
            pageBeans.get(i).thumbImage.setBounds(bounds);
        }

    }

    private void assembleDataList() {
        computeBoundsBackward(mGridLayoutManager.findFirstVisibleItemPosition());

    }
}
