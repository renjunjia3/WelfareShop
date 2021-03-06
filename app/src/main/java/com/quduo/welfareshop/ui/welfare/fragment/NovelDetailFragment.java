package com.quduo.welfareshop.ui.welfare.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.ornolfr.ratingview.RatingView;
import com.hss01248.dialog.StyledDialog;
import com.lzy.okgo.OkGo;
import com.quduo.welfareshop.MyApplication;
import com.quduo.welfareshop.R;
import com.quduo.welfareshop.activity.ReadActivity;
import com.quduo.welfareshop.base.GlideApp;
import com.quduo.welfareshop.base.UnlockLisenter;
import com.quduo.welfareshop.config.AppConfig;
import com.quduo.welfareshop.db.BookList;
import com.quduo.welfareshop.event.UpdateScoreAndDiamondEvent;
import com.quduo.welfareshop.http.api.ApiUtil;
import com.quduo.welfareshop.mvp.BaseBackMvpFragment;
import com.quduo.welfareshop.ui.read.listener.OnSaveData2DBListener;
import com.quduo.welfareshop.ui.welfare.adapter.NovelDetailAdapter;
import com.quduo.welfareshop.ui.welfare.entity.NovelChapterInfo;
import com.quduo.welfareshop.ui.welfare.entity.NovelDetailInfo;
import com.quduo.welfareshop.ui.welfare.entity.NovelDetailResultInfo;
import com.quduo.welfareshop.ui.welfare.presenter.NovelDetailPresenter;
import com.quduo.welfareshop.ui.welfare.view.INovelDetailView;
import com.quduo.welfareshop.util.DialogUtils;
import com.quduo.welfareshop.util.ReaderUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.File;
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
 * Time:2018/2/26 16:31
 * Description:小说详情
 */

public class NovelDetailFragment extends BaseBackMvpFragment<INovelDetailView, NovelDetailPresenter> implements INovelDetailView {
    private static final String ARG_NOVEL_ID = "ARG_NOVEL_ID";
    @BindView(R.id.toolbar_layout)
    AppBarLayout toolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.read_now)
    TextView readNow;
    @BindView(R.id.follow)
    TextView follow;
    Unbinder unbinder;
    @BindView(R.id.status_view)
    StatusViewLayout statusView;

    private ImageView coverImage;
    private TextView novelTitle;
    private TextView readTimes;
    private RatingView ratingView;
    private TextView score;
    private TextView des;

    private int novelId;
    private List<NovelChapterInfo> list;
    private NovelDetailAdapter adapter;

    private String fileUrl;
    private String fileName;
    private int followId = 0;
    private NovelDetailResultInfo resultInfo;

    public static NovelDetailFragment newInstance(int novelId) {
        Bundle args = new Bundle();
        args.putInt(ARG_NOVEL_ID, novelId);
        NovelDetailFragment fragment = new NovelDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            novelId = getArguments().getInt(ARG_NOVEL_ID, 0);
        }
        if (novelId == 0) {
            _mActivity.onBackPressed();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welfare_novel_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return attachToSwipeBack(view);
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
            presenter.getNovelDetailData(true);
        }
    };

    @Override
    public void initToolbar() {
        toolbarTitle.setText("");
        initToolbarNav(toolbar);
        toolbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void initView() {
        MyApplication.getInstance().uploadPageInfo(AppConfig.POSITION_NOVEL_DETAIL, novelId);
        initRecyclerView();
        initHeaderView();
        presenter.getNovelDetailData(true);
    }

    private void initRecyclerView() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getNovelDetailData(false);
            }
        });
        toolbarLayout.setVisibility(View.GONE);
        list = new ArrayList<>();
        adapter = new NovelDetailAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onClickReadNow();
            }
        });
    }

    private void initHeaderView() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_welfare_novel_detail_header, null);
        coverImage = headerView.findViewById(R.id.cover_image);
        novelTitle = headerView.findViewById(R.id.novel_title);
        readTimes = headerView.findViewById(R.id.read_times);
        ratingView = headerView.findViewById(R.id.ratingView);
        score = headerView.findViewById(R.id.score);
        des = headerView.findViewById(R.id.des);
        headerView.findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
        adapter.addHeaderView(headerView);

        coverImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        coverImage.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        int height = coverImage.getHeight(); // 获取高度
                        ViewGroup.LayoutParams layoutParams = coverImage.getLayoutParams();
                        layoutParams.width = (int) (243f * height / 325f);
                        coverImage.setLayoutParams(layoutParams);
                    }
                });
    }

    private void bindHeaderView(NovelDetailInfo detailInfo) {
        try {
            novelTitle.setText(detailInfo.getTitle());
            readTimes.setText(MessageFormat.format("{0}人读过", detailInfo.getView_times()));
            des.setText(detailInfo.getDescription());
            ratingView.setRating((float) (detailInfo.getScore() / 2));
            score.setText(MessageFormat.format("评分：{0}分", detailInfo.getScore()));
            GlideApp.with(this)
                    .load(MyApplication.getInstance().getConfigInfo().getFile_domain() + detailInfo.getThumb_shu())
                    .placeholder(R.drawable.ic_default_image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(coverImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public NovelDetailPresenter initPresenter() {
        return new NovelDetailPresenter(this);
    }

    @OnClick(R.id.read_now)
    public void onClickReadNow() {
        try {
            presenter.novelSee(novelId);
            if (resultInfo.getData().isPayed()) {
                onClickRead();
            } else {
                DialogUtils.getInstance().showNeedUnlockDialog(_mActivity, resultInfo.getData().getPrice(), MyApplication.getInstance().getUserInfo().getScore(),AppConfig.POSITION_NOVEL_DETAIL, new UnlockLisenter() {
                    @Override
                    public void unlock() {
                        presenter.unlockNovel();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        OkGo.getInstance().cancelTag(ApiUtil.NOVEL_DETAIL_TAG);
        OkGo.getInstance().cancelTag(ApiUtil.CANCEL_FOLLOW_TAG);
        OkGo.getInstance().cancelTag(ApiUtil.FOLLOW_NOVEL_TAG);
        super.onDestroyView();
        unbinder.unbind();
    }

    private void onClickRead() {
        try {
            MyApplication.getInstance().uploadPageInfo(AppConfig.POSITION_NOVEL_READ, novelId);
            String fileUrl = AppConfig.NOVEL_DIR + novelTitle.getText().toString();
            //判断小说是否在本地
            if (com.blankj.utilcode.util.FileUtils.isFileExists(fileUrl)) {
                //判断小说是否导入了
                List<BookList> bookLists = DataSupport.findAll(BookList.class);
                boolean flag = false;
                if (null != bookLists && bookLists.size() > 0) {
                    for (BookList bookList : bookLists) {
                        if (bookList.getNovelId() == novelId) {
                            flag = true;
                            openNovel(bookList);
                        }
                    }
                    if (!flag) {
                        initNovel(fileUrl);
                    }

                } else {
                    initNovel(AppConfig.NOVEL_DIR + novelTitle.getText().toString());
                }
            } else {
                presenter.downloadNovel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getNovelId() {
        return novelId;
    }

    @Override
    public void showMessage(String msg) {
        try {
            if (msg.equals("积分不足")) {
                DialogUtils.getInstance().showNeedRechargeScoreDialog(_mActivity, resultInfo.getData().getPrice(), MyApplication.getInstance().getUserInfo().getScore(),AppConfig.POSITION_NOVEL_DETAIL);
                return;
            }
            ToastUtils.showShort(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bindData(NovelDetailResultInfo detailInfo) {
        try {
            resultInfo = detailInfo;
            bindHeaderView(detailInfo.getData());
            list.clear();
            list.addAll(detailInfo.getChapters());
            adapter.notifyDataSetChanged();
            setFileName(detailInfo.getData().getTitle());
            setFileUrl(detailInfo.getData().getTxt_url());
            setFollowId(detailInfo.getData().getFavor_id());
            follow.setText(getFollowId() != 0 ? "已收藏" : "加入收藏");
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
    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void downloadSuccess(String url) {
        initNovel(url);
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
    public void showLoadingDialog(String message) {
        try {
            if (StringUtils.isEmpty(message)) {
                StyledDialog.buildLoading().show();
            } else {
                StyledDialog.buildLoading(message).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initNovel(String url) {
        final BookList bookList = new BookList();
        bookList.setBookname(getFileName());
        bookList.setBookpath(url);
        bookList.setNovelId(novelId);
        ReaderUtil.addBook2DB(bookList, new OnSaveData2DBListener() {
            @Override
            public void onSaveSuccess() {
                openNovel(bookList);
            }

            @Override
            public void onSaveFail() {
                ToastUtils.showShort("由于一些原因添加书本失败");
            }

            @Override
            public void onSaveRepeat() {
                openNovel(bookList);
            }
        });
    }

    @Override
    public void openNovel(BookList bookList) {
        final String path = bookList.getBookpath();
        File file = new File(path);
        if (!file.exists()) {
            new AlertDialog.Builder(_mActivity)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(path + "文件不存在,是否删除该书本？")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //数据库删除书籍
                            DataSupport.deleteAll(BookList.class, "bookpath = ?", path);

                        }
                    }).setCancelable(true).show();
            return;
        }
        bookList.setBookpath(path);
        bookList.setBookname(getFileName());
        ReadActivity.openBook(bookList, _mActivity);
    }

    @Override
    public void showHasFollow(int followId) {
        try {
            follow.setText("已收藏");
            setFollowId(followId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoFollow() {
        try {
            follow.setText("加入收藏");
            setFollowId(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getFollowId() {
        return followId;
    }

    @Override
    public void unlockSuccess(int currentScore) {
        try {
            resultInfo.getData().setPayed(true);
            MyApplication.getInstance().getUserInfo().setScore(currentScore);
            EventBus.getDefault().post(new UpdateScoreAndDiamondEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFollowId(int followId) {
        this.followId = followId;
    }

    private void setFileUrl(String url) {
        fileUrl = MyApplication.getInstance().getConfigInfo().getFile_domain() + url;
    }

    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @OnClick(R.id.follow)
    public void onClickFollow() {
        if (follow.getText().toString().equals("已收藏")) {
            presenter.cancelFollowNovel();
        } else {
            presenter.followNovel();
        }
    }

}
