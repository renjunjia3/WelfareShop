package com.quduo.welfareshop.ui.friend.adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.quduo.welfareshop.MyApplication;
import com.quduo.welfareshop.R;
import com.quduo.welfareshop.activity.PreviewImageActivity;
import com.quduo.welfareshop.base.GlideApp;
import com.quduo.welfareshop.ui.friend.entity.DynamicInfo;
import com.quduo.welfareshop.widgets.CustomListView;
import com.quduo.welfareshop.widgets.RatioImageView;
import com.w4lle.library.NineGridlayout;

import java.util.ArrayList;
import java.util.List;

//个人主页的动态
public class HomePageDynamicAdapter extends BaseQuickAdapter<DynamicInfo, BaseViewHolder> {
    public HomePageDynamicAdapter(@Nullable List<DynamicInfo> data) {
        super(R.layout.fragment_friend_others_home_page_dynamic_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DynamicInfo item) {
        ImageView avatar = helper.getView(R.id.avatar);
        GlideApp.with(mContext)
                .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + item.getAvatar())
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_avatar)
                .into(avatar);
        helper.setText(R.id.nickname, item.getNickname());
        helper.setImageResource(R.id.sex, item.getSex() == 1 ? R.drawable.ic_near_sex_male_white : R.drawable.ic_near_sex_female_white);
        helper.setBackgroundRes(R.id.layout_sex_and_age, item.getSex() == 1 ? R.drawable.bg_sex_male : R.drawable.bg_sex_female);
        helper.setText(R.id.age, item.getAge() + "岁");
        helper.setText(R.id.total_follow_number, item.getFavor_times() + "人关注了TA");
        helper.setText(R.id.content, item.getContent());

        final ArrayList<String> imageList = new ArrayList<>();
        if (item.getImages() != null && item.getImages().size() > 0) {
            imageList.add(MyApplication.getInstance().getConfigInfo().getFile_domain() + item.getImages().get(0));
        }
        NineGridImageAdapter imageAdapter = new NineGridImageAdapter(mContext, imageList);
        NineGridlayout imageGridLayout = helper.getView(R.id.image_gridLayout);
        imageGridLayout.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, PreviewImageActivity.class);
                intent.putExtra(PreviewImageActivity.ARG_URLS, imageList);
                intent.putExtra(PreviewImageActivity.ARG_POSITION, position);
                mContext.startActivity(intent);
            }
        });
        imageGridLayout.setAdapter(imageAdapter);
        imageGridLayout.setVisibility(imageList.size() > 0 ? View.VISIBLE : View.GONE);
        if (StringUtils.isTrimEmpty(item.getUrl())) {
            helper.setGone(R.id.video_layout, false);
        } else {
            helper.setGone(R.id.video_layout, true);
            RatioImageView videoThumb = helper.getView(R.id.video_thumb);
            GlideApp.with(mContext)
                    .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + item.getAvatar())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_default_video)
                    .into(videoThumb);
        }

        LinearLayout commentLayout = helper.getView(R.id.comment_layout);
        commentLayout.removeAllViews();
        for (int i = 0; i < item.getAvatars().size(); i++) {
            if (i < 3) {
                ImageView imageView = new ImageView(mContext);
                ViewGroup.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMarginEnd(SizeUtils.dp2px(5));
                layoutParams.width = SizeUtils.dp2px(20);
                layoutParams.height = SizeUtils.dp2px(20);
                imageView.setLayoutParams(layoutParams);
                GlideApp.with(mContext)
                        .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + item.getAvatars().get(i))
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(imageView);
                commentLayout.addView(imageView);
            }
        }

        helper.setText(R.id.total_comment_number, item.getGood() + "人点赞");

        CustomListView commentListView = helper.getView(R.id.comment_listView);
        HomePageDynamicCommentAdapter commentAdapter = new HomePageDynamicCommentAdapter(mContext, item.getComments(), item.isShowAll());
        commentListView.setAdapter(commentAdapter);
        helper.setGone(R.id.show_all, item.getComments().size() > 2);

        if (item.isShowAll()) {
            helper.setText(R.id.show_all_text, "收起");
            helper.setImageResource(R.id.show_all_image, R.drawable.ic_dynamic_close);
        } else {
            helper.setText(R.id.show_all_text, "展开");
            helper.setImageResource(R.id.show_all_image, R.drawable.ic_dynamic_open);
        }
        helper.addOnClickListener(R.id.show_all);
        helper.addOnClickListener(R.id.video_layout);
        helper.addOnClickListener(R.id.btn_good);
        helper.addOnClickListener(R.id.btn_comment);

        helper.setImageResource(R.id.btn_good, item.isIs_good() ? R.drawable.ic_dynamic_zan_s : R.drawable.ic_dynamic_zan_d);
    }

}