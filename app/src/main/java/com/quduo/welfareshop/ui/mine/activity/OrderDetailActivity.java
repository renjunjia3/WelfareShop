package com.quduo.welfareshop.ui.mine.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hss01248.dialog.StyledDialog;
import com.lzy.okgo.OkGo;
import com.quduo.welfareshop.MyApplication;
import com.quduo.welfareshop.R;
import com.quduo.welfareshop.activity.OpenPayActivity;
import com.quduo.welfareshop.base.GlideApp;
import com.quduo.welfareshop.config.AppConfig;
import com.quduo.welfareshop.event.UpdateScoreAndDiamondEvent;
import com.quduo.welfareshop.http.api.ApiUtil;
import com.quduo.welfareshop.mvp.BaseMvpActivity;
import com.quduo.welfareshop.ui.mine.adapter.OrderDetailRecommendGoodsAdapter;
import com.quduo.welfareshop.ui.mine.entity.CheckPayResultInfo;
import com.quduo.welfareshop.ui.mine.entity.OrderDetailInfo;
import com.quduo.welfareshop.ui.mine.entity.OrderDetailResultInfo;
import com.quduo.welfareshop.ui.mine.presenter.OrderDetailPresenter;
import com.quduo.welfareshop.ui.mine.view.IOrderDetailView;
import com.quduo.welfareshop.ui.shop.activity.GoodsDetailActivity;
import com.quduo.welfareshop.ui.shop.dialog.BuySuccessDialog;
import com.quduo.welfareshop.ui.shop.entity.GoodsInfo;
import com.quduo.welfareshop.ui.shop.entity.PayInfo;
import com.quduo.welfareshop.ui.shop.fragment.ServiceCenterActivity;
import com.quduo.welfareshop.widgets.CustomGridView;
import com.quduo.welfareshop.widgets.CustomListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

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
 * Time:2018/3/14 10:13
 * Description:订单详情
 * 1未支付  2已支付  3已发货  4已评价  5已取消
 */

public class OrderDetailActivity extends BaseMvpActivity<IOrderDetailView, OrderDetailPresenter> implements IOrderDetailView {
    public static final String ARG_ID = "order_id";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_image_menu)
    ImageView toolbarImageMenu;
    @BindView(R.id.status_text)
    TextView statusText;
    @BindView(R.id.status_icon)
    ImageView statusIcon;
    @BindView(R.id.receiver_name)
    TextView receiverName;
    @BindView(R.id.receiver_phone)
    TextView receiverPhone;
    @BindView(R.id.receiver_address)
    TextView receiverAddress;
    @BindView(R.id.goods_image)
    ImageView goodsImage;
    @BindView(R.id.goods_name)
    TextView goodsName;
    @BindView(R.id.goods_score)
    TextView goodsScore;
    @BindView(R.id.goods_price)
    TextView goodsPrice;
    @BindView(R.id.goods_number)
    TextView goodsNumber;
    @BindView(R.id.goods_model)
    TextView goodsModel;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.need_pay_price)
    TextView needPayPrice;
    @BindView(R.id.coupon_info)
    TextView couponInfo;
    @BindView(R.id.btn_wechat)
    TextView btnWechat;
    @BindView(R.id.btn_alipay)
    TextView btnAlipay;
    @BindView(R.id.layout_type_unpay)
    LinearLayout layoutTypeUnpay;
    @BindView(R.id.logisticsListView)
    CustomListView logisticsListView;
    @BindView(R.id.layout_type_has_send)
    LinearLayout layoutTypeHasSend;
    @BindView(R.id.order_number)
    TextView orderNumber;
    @BindView(R.id.copy_order_number)
    TextView copyOrderNumber;
    @BindView(R.id.ship_number)
    TextView shipNumber;
    @BindView(R.id.ship_company)
    TextView shipCompany;
    @BindView(R.id.copy_ship_number)
    TextView copyShipNumber;
    @BindView(R.id.layout_ship)
    LinearLayout layoutShip;
    @BindView(R.id.order_time)
    TextView orderTime;
    @BindView(R.id.goodsGridView)
    CustomGridView goodsGridView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.status_view)
    StatusViewLayout statusView;
    Unbinder unbinder;
    private int orderId;

    private List<GoodsInfo> goodsList = new ArrayList<>();
    private OrderDetailRecommendGoodsAdapter recommendGoodsAdapter;

//    private List<String> logisticsList = new ArrayList<>();
//    private TimeLineAdapter timeLineAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_order_detail);
        unbinder = ButterKnife.bind(this);
        orderId = getIntent().getIntExtra(ARG_ID, 0);
        initToolbar();
        initView();
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
            presenter.getData(true);
        }
    };

    private void initToolbar() {
        toolbarTitle.setText("订单详情");
        toolbarImageMenu.setImageResource(R.drawable.ic_order_detail_server_center);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedSupport();
            }
        });
    }

    @OnClick(R.id.toolbar_image_menu)
    public void onClickToolbarImageMenu() {
        try {
            startActivity(new Intent(OrderDetailActivity.this, ServiceCenterActivity.class));
            overridePendingTransition(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        MyApplication.getInstance().uploadPageInfo(AppConfig.POSITION_MINE_ORDER_DETAIL, orderId);
        initRefreshLayout();
        initRecommendGoodsGridView();
        presenter.getData(true);
    }

    private void initRefreshLayout() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getData(false);
            }
        });
    }

    private void initRecommendGoodsGridView() {
        recommendGoodsAdapter = new OrderDetailRecommendGoodsAdapter(OrderDetailActivity.this, goodsList);
        goodsGridView.setAdapter(recommendGoodsAdapter);
        goodsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toGoodsDetailActivity(goodsList.get(position).getId());
            }
        });
    }

//    private void initLogisticsListView() {
//        logisticsList.add("");
//        logisticsList.add("");
//        logisticsList.add("");
//        logisticsList.add("");
//        logisticsList.add("");
//        logisticsList.add("");
//        logisticsList.add("");
//        timeLineAdapter = new TimeLineAdapter(getContext(), logisticsList);
//        logisticsListView.setAdapter(timeLineAdapter);
//    }

    private void showType(int orderStatus) {
        if (orderStatus == 1) {
            layoutTypeUnpay.setVisibility(View.VISIBLE);
            layoutTypeHasSend.setVisibility(View.GONE);
        } else if (orderStatus == 2) {
            layoutTypeUnpay.setVisibility(View.GONE);
            layoutTypeHasSend.setVisibility(View.GONE);
        } else if (orderStatus == 3) {
            layoutTypeUnpay.setVisibility(View.GONE);
            layoutTypeHasSend.setVisibility(View.VISIBLE);
            //initLogisticsListView();
        }
    }

    @Override
    public OrderDetailPresenter initPresenter() {
        return new OrderDetailPresenter(this);
    }

    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(ApiUtil.ORDER_DETAIL_TAG);
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public int getOrderId() {
        return orderId;
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
    public void bindData(OrderDetailResultInfo data) {
        try {
            showType(data.getData().getStatus());
            bindOrderData(data.getData());
            bindRecommendGoods(data.getProducts());
            btnAlipay.setVisibility(data.isAli_pay_enable() ? View.VISIBLE : View.GONE);
            btnWechat.setVisibility(data.isWx_pay_enable() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoadingDialog() {
        try {
            StyledDialog.buildLoading().setActivity(OrderDetailActivity.this).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideLoadingDialog() {
        try {
            StyledDialog.dismissLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void alert(String message) {
        try {
            StyledDialog
                    .buildIosAlert("提示", message, null).setActivity(OrderDetailActivity.this)
                    .setBtnText("确定")
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int newOrderId;

    @Override
    public void repayOrderSuccess(PayInfo payInfo) {
        try {
            newOrderId = payInfo.getOrder_id();
            Intent intent = new Intent(OrderDetailActivity.this, OpenPayActivity.class);
            intent.putExtra(OpenPayActivity.ARG_URL, payInfo.getUrl());
            if (payInfo.getPay_type() == 1 || payInfo.getPay_type() == 3) {
                intent.putExtra(OpenPayActivity.ARG_TYPE, 1);
            } else {
                intent.putExtra(OpenPayActivity.ARG_TYPE, 2);
            }
            startActivityForResult(intent, 40001);
            OrderDetailActivity.this.overridePendingTransition(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paySuccess(CheckPayResultInfo data) {
        try {
            showBuySuccessDialog();
            MyApplication.getInstance().getUserInfo().setScore(data.getScore());
            MyApplication.getInstance().getUserInfo().setDiamond(data.getDiamond());
            EventBus.getDefault().post(new UpdateScoreAndDiamondEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindOrderData(OrderDetailInfo orderDetailInfo) {
        try {
            if (orderDetailInfo.getStatus() == 1) {
                statusText.setText("等待买家付款");
                statusIcon.setImageResource(R.drawable.ic_order_detail_unpay);
                needPayPrice.setText(MessageFormat.format("￥{0}", orderDetailInfo.getActual_pay()));
                if (orderDetailInfo.getCoupon_id() == 0) {
                    couponInfo.setVisibility(View.GONE);
                } else {
                    couponInfo.setVisibility(View.VISIBLE);
                    couponInfo.setText(MessageFormat.format("(代金券已抵扣{0}元)", orderDetailInfo.getCoupon_money()));
                }
            } else if (orderDetailInfo.getStatus() == 2) {
                statusText.setText("买家已付款等待卖家发货");
                statusIcon.setImageResource(R.drawable.ic_order_detail_unsend);
            } else {
                statusText.setText("卖家已发货");
                statusIcon.setImageResource(R.drawable.ic_order_detail_unreveiver);
            }
            receiverName.setText(orderDetailInfo.getName());
            receiverPhone.setText(orderDetailInfo.getMobile());
            receiverAddress.setText(orderDetailInfo.getAddress());
            GlideApp.with(this)
                    .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + orderDetailInfo.getThumb())
                    .placeholder(R.drawable.ic_default_shop)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(goodsImage);
            goodsName.setText(orderDetailInfo.getProduct_name());
            goodsPrice.setText(MessageFormat.format("￥{0}", orderDetailInfo.getPrice()));
            goodsNumber.setText(MessageFormat.format("x{0}", orderDetailInfo.getNumber()));
            totalPrice.setText(MessageFormat.format("合计：￥{0}", orderDetailInfo.getActual_pay()));
            goodsModel.setText(orderDetailInfo.getModel());
            Number num = Float.parseFloat(orderDetailInfo.getPrice()) * 100;
            int giveNum = num.intValue() / 100 * orderDetailInfo.getNumber();
            goodsScore.setText(MessageFormat.format("赠送{0}钻石+{1}积分", giveNum, giveNum));
            orderNumber.setText(orderDetailInfo.getOrder_id());
            DateTime dateTime = new DateTime(orderDetailInfo.getCreate_time() * 1000);
            orderTime.setText(dateTime.toString("yyyy-MM-dd HH:mm:ss"));

            if (StringUtils.isTrimEmpty(orderDetailInfo.getExpress_number()) || orderDetailInfo.getExpress_number().equals("null")) {
                layoutShip.setVisibility(View.GONE);
            } else {
                layoutShip.setVisibility(View.VISIBLE);
                shipNumber.setText(orderDetailInfo.getExpress_number());
                shipCompany.setText(MessageFormat.format("({0})", orderDetailInfo.getExpress_company()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindRecommendGoods(List<GoodsInfo> data) {
        try {
            goodsList.clear();
            goodsList.addAll(data);
            recommendGoodsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toGoodsDetailActivity(int goodsId) {
        try {
            Intent intent = new Intent(OrderDetailActivity.this, GoodsDetailActivity.class);
            intent.putExtra(GoodsDetailActivity.ARG_ID, goodsId);
            startActivity(intent);
            OrderDetailActivity.this.overridePendingTransition(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.copy_ship_number)
    public void onClickCopyShipNumber() {
        try {
            ClipboardManager cm = (ClipboardManager) OrderDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setText(shipNumber.getText().toString());
                showMessage("物流编号已复制到剪切板");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.copy_order_number)
    public void onClickCopyOrderNumber() {
        try {
            ClipboardManager cm = (ClipboardManager) OrderDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setText(orderNumber.getText().toString());
                showMessage("物流编号已复制到剪切板");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_wechat)
    public void onClickBtnWechat() {
        presenter.rePayOrder(orderId, 1);
    }

    @OnClick(R.id.btn_alipay)
    public void onClickBtnAlipay() {
        presenter.rePayOrder(orderId, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("requestCode=====>" + requestCode);
        if (requestCode == 40001) {
            presenter.checkPayResult(newOrderId);
        }
    }

    private BuySuccessDialog buySuccessDialog;

    private void showBuySuccessDialog() {
        try {
            if (buySuccessDialog == null) {
                buySuccessDialog = new BuySuccessDialog(OrderDetailActivity.this);
            }
            buySuccessDialog.show();
            Number num = Float.parseFloat(totalPrice.getText().toString().substring(4)) * 100;
            int giveNum = num.intValue() / 100;
            buySuccessDialog.setNumber(giveNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
