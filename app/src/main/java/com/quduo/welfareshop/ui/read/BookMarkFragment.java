package com.quduo.welfareshop.ui.read;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.quduo.welfareshop.R;
import com.quduo.welfareshop.db.BookMarks;
import com.quduo.welfareshop.ui.read.adapter.MarkAdapter;
import com.quduo.welfareshop.util.PageFactory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class BookMarkFragment extends SupportFragment {
    public static final String ARGUMENT = "argument";

    @BindView(R.id.lv_bookmark)
    ListView lv_bookmark;
    Unbinder unbinder;

    private String bookpath;
    private String mArgument;
    private List<BookMarks> bookMarksList;
    private MarkAdapter markAdapter;
    private PageFactory pageFactory;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
        initListener();
    }

    private void initData() {
        pageFactory = PageFactory.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bookpath = bundle.getString(ARGUMENT);
        }
        bookMarksList = new ArrayList<>();
        bookMarksList = DataSupport.where("bookpath = ?", bookpath).find(BookMarks.class);
        markAdapter = new MarkAdapter(getActivity(), bookMarksList);
        lv_bookmark.setAdapter(markAdapter);

    }

    private void initListener() {
        markAdapter.setOnClickDeleteMarkListener(new MarkAdapter.OnClickDeleteMarkListener() {
            @Override
            public void onClickDeleteMark(final MarkAdapter.MarkViewHolder viewHolder, final int position) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("是否删除书签？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DataSupport.delete(BookMarks.class, bookMarksList.get(position).getId());
                                    bookMarksList.clear();
                                    bookMarksList.addAll(DataSupport.where("bookpath = ?", bookpath).find(BookMarks.class));
                                    markAdapter.notifyDataSetChanged();
                                    viewHolder.swipe.quickClose();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).setCancelable(true).show();
            }

            @Override
            public void onClickItemLayout(int position) {
                try {
                    pageFactory.changeChapter(bookMarksList.get(position).getBegin());
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 用于从Activity传递数据到Fragment
     *
     * @param bookpath
     * @return
     */
    public static BookMarkFragment newInstance(String bookpath) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, bookpath);
        BookMarkFragment bookMarkFragment = new BookMarkFragment();
        bookMarkFragment.setArguments(bundle);
        return bookMarkFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
