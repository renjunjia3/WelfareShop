package com.quduo.welfareshop.ui.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.quduo.welfareshop.MyApplication;
import com.quduo.welfareshop.R;
import com.quduo.welfareshop.config.AppConfig;
import com.quduo.welfareshop.http.api.ApiUtil;
import com.quduo.welfareshop.mvp.BaseMvpActivity;
import com.quduo.welfareshop.ui.shop.adapter.GoodsCommentAdapter;
import com.quduo.welfareshop.ui.shop.dialog.ChooseGoodsTypeDialog;
import com.quduo.welfareshop.ui.shop.entity.GoodsCommentInfo;
import com.quduo.welfareshop.ui.shop.entity.GoodsCommentResultInfo;
import com.quduo.welfareshop.ui.shop.entity.GoodsDetailInfo;
import com.quduo.welfareshop.ui.shop.presenter.GoodsCommentPresenter;
import com.quduo.welfareshop.ui.shop.view.IGoodsCommentView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wiki.scene.loadmore.StatusViewLayout;

/**
 * Author:scene
 * Time:2018/2/28 15:43
 * Description:买家秀
 */

public class GoodsCommentActivity extends BaseMvpActivity<IGoodsCommentView, GoodsCommentPresenter> implements IGoodsCommentView {
    public static final String ARG_GOODSINFO = "goods_info";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.status_view)
    StatusViewLayout statusView;
    @BindView(R.id.buy_now)
    TextView buyNow;
    Unbinder unbinder;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.goods_price)
    TextView goodsPrice;
    @BindView(R.id.goods_old_price)
    TextView goodsOldPrice;

    private List<GoodsCommentInfo> list = new ArrayList<>();
    private GoodsCommentAdapter adapter;
    private int page = 1;

    private GoodsDetailInfo detailInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_comment);
        unbinder = ButterKnife.bind(this);
        detailInfo = (GoodsDetailInfo) getIntent().getSerializableExtra(ARG_GOODSINFO);
        initToolbar();
        initView();
    }

    private void initView() {
        MyApplication.getInstance().uploadPageInfo(AppConfig.POSITION_SHOP_COMMENT, detailInfo.getId());
        initRecyclerView();
        presenter.getData(1, true);
        goodsPrice.setText(MessageFormat.format("￥{0}", detailInfo.getPrice()));
        SpannableStringBuilder stringBuilder = new SpanUtils().append("原价:￥" + detailInfo.getOld_price()).setStrikethrough().create();
        goodsOldPrice.setText(stringBuilder);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedSupport();
            }
        });
    }

    private void initRecyclerView() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getData(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                presenter.getData(page + 1, false);
            }
        });
        adapter = new GoodsCommentAdapter(GoodsCommentActivity.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(GoodsCommentActivity.this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoadingPage() {
        try {
            statusView.showLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showContentPage() {
        try {
            statusView.showContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showErrorPage() {
        try {
            statusView.showFailed(retryListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener retryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.getData(1, true);
        }
    };

    @Override
    public GoodsCommentPresenter initPresenter() {
        return new GoodsCommentPresenter(this);
    }

    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(ApiUtil.GOODS_COMMENT_TAG);
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void bindData(GoodsCommentResultInfo data) {
        try {
            page = data.getCurrent_page();
            if (page == 1) {
                list.clear();
            }
            list.addAll(data.getData());
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessage(String message) {
        try {
            ToastUtils.showShort(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshFinish() {
        try {
            refreshLayout.finishRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadmoreFinish() {
        try {
            refreshLayout.finishLoadMore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hasLoadmore(boolean hasMore) {
        try {
            refreshLayout.setEnableLoadMore(hasMore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getGoodsId() {
        return detailInfo.getId();
    }

    @OnClick(R.id.buy_now)
    public void onClickBuyNow() {
        try {
            Intent intent = new Intent(GoodsCommentActivity.this, ChooseGoodsTypeDialog.class);
            intent.putExtra(ChooseGoodsTypeDialog.ARG_GOODSINFO, detailInfo);
            startActivity(intent);
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
