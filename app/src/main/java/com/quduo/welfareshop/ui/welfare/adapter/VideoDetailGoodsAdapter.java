package com.quduo.welfareshop.ui.welfare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quduo.welfareshop.MyApplication;
import com.quduo.welfareshop.R;
import com.quduo.welfareshop.base.GlideApp;
import com.quduo.welfareshop.ui.shop.entity.GoodsInfo;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author:scene
 * Time:2018/2/27 13:34
 * Description:视频详情--商品推荐
 */

public class VideoDetailGoodsAdapter extends BaseAdapter {
    private Context context;
    private List<GoodsInfo> list;
    private LayoutInflater inflater;

    public VideoDetailGoodsAdapter(Context context, List<GoodsInfo> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        VideoDetailGoodsViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_shop_item, parent, false);
            holder = new VideoDetailGoodsViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (VideoDetailGoodsViewHolder) convertView.getTag();
        }
        GoodsInfo info = list.get(position);
        GlideApp.with(context)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + info.getThumb())
                .into(holder.image);
        holder.name.setText(info.getName());
        holder.price.setText(MessageFormat.format("￥{0}", info.getPrice()));
        holder.buyNumber.setText(MessageFormat.format("{0}人付款", info.getSales()));
        return convertView;
    }

    static class VideoDetailGoodsViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.buy_number)
        TextView buyNumber;
        @BindView(R.id.btn_buy)
        TextView btnBuy;
        @BindView(R.id.name)
        TextView name;

        VideoDetailGoodsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
