package com.lbl.networkframe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lbl.networkframe.bean.LoopBean;
import com.lbl.networkframe.bean.SearchDataList;
import com.lbl.networkframe.network.NetWorkUtil;
import com.lbl.networkframe.network.nethelper.RetrofitHelper;
import com.lbl.networkframe.network.netservice.ApiService;
import com.lbl.networkframe.view.viewpager.GlideManager;
import com.lbl.networkframe.view.viewpager.anim.MzTransformer;
import com.lbl.networkframe.view.viewpager.bean.PageBean;
import com.lbl.networkframe.view.viewpager.callback.PageHelperListener;
import com.lbl.networkframe.view.viewpager.indicator.ZoomIndicator;
import com.lbl.networkframe.view.viewpager.view.ArcImageView;
import com.lbl.networkframe.view.viewpager.view.BannerViewPager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

//import rx.Observable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    BannerViewPager viewPager;
    ZoomIndicator indicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initListener();
    }

    private void initview() {
        viewPager = findViewById(R.id.loop_viewpager);
        indicator = findViewById(R.id.bottom_indicator);
        viewPager.setPageTransformer(false, new MzTransformer());
    }

    private void initListener() {
        findViewById(R.id.text_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_tv:
                getData();
                break;
        }
    }

    public void getData() {
        //简单好用，没有写界面，看log就明白了
        Observable<SearchDataList.DataBean> searchVideo = RetrofitHelper.getService(ApiService.class).getSearchVideo("秋冬编发大全", "0", "20");
        //这个我返回来的是一个object 可以直接返回来数据对象
        NetWorkUtil.requestGet(searchVideo, new NetWorkUtil.OnResultListener() {
            @Override
            public void onSuccess(Object o) {
                SearchDataList.DataBean bean = (SearchDataList.DataBean) o;
                setdata(bean);
            }

            @Override
            public void onError(String msg) {

            }
        });

    }

    private void setdata(SearchDataList.DataBean beans) {
        List<LoopBean> loopBeens = new ArrayList<>();
        if (beans.content.size() > 0)
            for (SearchDataList.DataBean.ContentBean bean : beans.content) {
                LoopBean bean1 = new LoopBean();
                if (bean.getType().endsWith("video")) {
                    bean1.url = bean.getVideo().getCover_url();
                    bean1.text = bean.getVideo().getTitle();
                } else {
                    bean1.url = bean.getSharebuy().getCover_url();
                    bean1.text = bean.getSharebuy().getTitle();
                }
                loopBeens.add(bean1);
            }
        List<LoopBean> loopBeans = loopBeens.subList(0, 5);
        PageBean arcbean = new PageBean.Builder<LoopBean>()
                .setDataObjects(loopBeens)
                .setIndicator(indicator)
                .builder();
        viewPager.setPageListener(arcbean, R.layout.arc_loop_layout, new PageHelperListener() {
            @Override
            public void getItemView(View view, Object data) {
                ArcImageView imageView = view.findViewById(R.id.arc_icon);
                LoopBean bean = (LoopBean) data;
                new GlideManager.Builder()
                        .setContext(MainActivity.this)
                        .setImgSource(bean.url)
                        .setLoadingBitmap(R.mipmap.ic_launcher)
                        .setImageView(imageView)
                        .builder();
            }
        });

    }


}
