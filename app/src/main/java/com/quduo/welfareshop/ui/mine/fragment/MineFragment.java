package com.quduo.welfareshop.ui.mine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hss01248.dialog.StyledDialog;
import com.quduo.welfareshop.MyApplication;
import com.quduo.welfareshop.R;
import com.quduo.welfareshop.activity.RechargeActivity;
import com.quduo.welfareshop.base.GlideApp;
import com.quduo.welfareshop.config.AppConfig;
import com.quduo.welfareshop.event.StartBrotherEvent;
import com.quduo.welfareshop.event.UpdateAvatarEvent;
import com.quduo.welfareshop.event.UpdateMyInfoSuccessEvent;
import com.quduo.welfareshop.event.UpdateScoreAndDiamondEvent;
import com.quduo.welfareshop.mvp.BaseMainMvpFragment;
import com.quduo.welfareshop.ui.mine.activity.MyReceiverActivity;
import com.quduo.welfareshop.ui.mine.activity.UserAgreementActivity;
import com.quduo.welfareshop.ui.mine.presenter.MinePresenter;
import com.quduo.welfareshop.ui.mine.view.IMineView;
import com.quduo.welfareshop.widgets.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 我的主界面
 * Created by scene on 2018/1/25.
 */

public class MineFragment extends BaseMainMvpFragment<IMineView, MinePresenter> implements IMineView {
    Unbinder unbinder;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.score_number)
    TextView scoreNumber;
    @BindView(R.id.diamonds_number)
    TextView diamondsNumber;
    @BindView(R.id.show_id)
    TextView showId;
    @BindView(R.id.follow_number)
    TextView followNumber;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initView() {
        MyApplication.getInstance().uploadPageInfo(AppConfig.POSITION_MINE, 0);
        GlideApp.with(this)
                .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + MyApplication.getInstance().getUserInfo().getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(avatar);
        nickname.setText(StringUtils.isTrimEmpty(MyApplication.getInstance().getUserInfo().getNickname()) ? "游客" : MyApplication.getInstance().getUserInfo().getNickname());
        scoreNumber.setText(String.valueOf(MyApplication.getInstance().getUserInfo().getScore()));
        diamondsNumber.setText(String.valueOf(MyApplication.getInstance().getUserInfo().getDiamond()));
        showId.setText(String.valueOf(MyApplication.getInstance().getUserInfo().getId()));
        followNumber.setText(String.valueOf(MyApplication.getInstance().getUserInfo().getSubscribe()));
    }

    @Override
    public void showLoadingPage() {

    }

    @Override
    public void showContentPage() {

    }

    @Override
    public void showErrorPage() {

    }

    @Override
    public MinePresenter initPresenter() {
        return new MinePresenter(this);
    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.toolbar_back)
    public void onClickToolbarBack() {
        _mActivity.onBackPressed();
    }

    @OnClick(R.id.avatar)
    public void onClickAvatar() {
        EventBus.getDefault().post(new StartBrotherEvent(MyInfoFragment.newInstance()));
    }

    @OnClick(R.id.my_order)
    public void onClickMyOrder() {
        EventBus.getDefault().post(new StartBrotherEvent(MyOrderFragment.newInstance()));
    }

    @OnClick(R.id.my_follow_goods)
    public void onClickMyFollowGoods() {
        EventBus.getDefault().post(new StartBrotherEvent(MyGoodsFragment.newInstance()));
    }

    @OnClick(R.id.my_video)
    public void onClickMyVideo() {
        EventBus.getDefault().post(new StartBrotherEvent(MyFollowFragment.newInstance(0)));
    }

    @OnClick(R.id.my_novel)
    public void onClickMyNovel() {
        EventBus.getDefault().post(new StartBrotherEvent(MyFollowFragment.newInstance(1)));
    }

    @OnClick(R.id.my_photo)
    public void onClickMyPhoto() {
        EventBus.getDefault().post(new StartBrotherEvent(MyFollowFragment.newInstance(2)));
    }

    @OnClick(R.id.my_coupon)
    public void onClickMyCoupon() {
        EventBus.getDefault().post(new StartBrotherEvent(MyCouponFragment.newInstance()));
    }

    @OnClick(R.id.my_receiver)
    public void onClickMyReceiver() {
        startActivity(new Intent(_mActivity, MyReceiverActivity.class));
        _mActivity.overridePendingTransition(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
    }

    @OnClick(R.id.user_agreement)
    public void onClickUserAgreement() {
        startActivity(new Intent(_mActivity, UserAgreementActivity.class));
        _mActivity.overridePendingTransition(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
    }

    @OnClick(R.id.to_recharge)
    public void onClickToRecharge() {
        Intent intent = new Intent(getContext(), RechargeActivity.class);
        intent.putExtra(RechargeActivity.ARG_FROM_POSITION, AppConfig.POSITION_MINE);
        startActivity(intent);
        _mActivity.overridePendingTransition(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
    }

    @Subscribe
    public void uploadAvatar(UpdateAvatarEvent event) {
        try {
            MyApplication.getInstance().getUserInfo().setAvatar(event.getAvatarPath());
            GlideApp.with(this)
                    .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + MyApplication.getInstance().getUserInfo().getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(avatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void updateMyInfoSuccess(UpdateMyInfoSuccessEvent event) {
        try {
            nickname.setText(StringUtils.isTrimEmpty(MyApplication.getInstance().getUserInfo().getNickname()) ? "游客" : MyApplication.getInstance().getUserInfo().getNickname());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.clear_cache)
    public void onClickClearCache() {
        try {
            StyledDialog.buildLoading("正在清理缓存请稍候").setActivity(_mActivity).show();
            avatar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StyledDialog.dismissLoading();
                    ToastUtils.showShort("缓存清理完成");
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void changeScoreAndDiamond(UpdateScoreAndDiamondEvent event) {
        try {
            scoreNumber.setText(String.valueOf(MyApplication.getInstance().getUserInfo().getScore()));
            diamondsNumber.setText(String.valueOf(MyApplication.getInstance().getUserInfo().getDiamond()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
