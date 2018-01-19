package footer.android;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import footer.android.adapter.RecyclerWrapAdapter;
import footer.android.tools.AnimationRotate;


/**
 * Created by private on 2016-11-23.
 */
public class FooterRecyclerView extends RecyclerView {
    private View v;
    private Context context;
    private ArrayList<View> mHeaderViews = new ArrayList<>();

    private ArrayList<View> mFooterViews = new ArrayList<>();
    private LayoutInflater layout;
    private Adapter mAdapter;
    //底部的View
    private View endFooterView;
    private TextView end_tv;
    private ImageView loading_iv;
    //是否允许上拉加载
    private boolean endFooter = false;
    /**
     * 松开刷新
     */
    private final static int RELEASE_TO_REFRESH = 0;
    /**
     * 下拉刷新
     */
    private final static int PULL_TO_REFRESH = 1;
    /**
     * 正在刷新
     */
    private final static int REFRESHING = 2;
    /**
     * 刷新完成   or 什么都没做，恢复原状态。
     */
    private final static int DONE = 3;
    /**
     * <strong>加载更多FootView（EndView）的实时状态flag</strong>
     * <p/>
     * <p> 0 : 完成/等待刷新 ;
     * <p> 1 : 加载中
     */
    private int mEndState;
    private int mHeadState;
    /**
     * 可以自动加载更多吗？（注意，先判断是否有加载更多，如果没有，这个flag也没有意义）
     */
    private boolean mIsAutoLoadMore = true;
    /**
     * 加载中
     */
    private final static int ENDINT_LOADING = 1;
    /**
     * 手动完成刷新
     */
    private final static int ENDINT_MANUAL_LOAD_DONE = 2;
    /**
     * 自动完成刷新
     */
    private final static int ENDINT_AUTO_LOAD_DONE = 3;
    /**
     * 可以下拉刷新？
     */
    private boolean mCanRefresh = true;
    //加载更多监听接口
    private OnLoadMoreListener mLoadMoreListener;

    public FooterRecyclerView(Context pContext) {
        super(pContext);
        init(pContext);
    }

    public FooterRecyclerView(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
        init(pContext);
    }

    public FooterRecyclerView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        init(pContext);
    }

    /**
     * 初始化操作
     */
    private void init(Context pContext) {
        this.context = pContext;
        layout = LayoutInflater.from(context);
        this.setOnScrollListener(new RVOnScrollListener());

    }

    public void setmIsAutoLoadMore(boolean mIsAutoLoadMore) {
        this.mIsAutoLoadMore = mIsAutoLoadMore;
    }

    /**
     * 可以加载更多？
     */
    public void EndFooter(boolean flags) {
        endFooter = flags;
        if (endFooter&&endFooterView==null) {
            initFooterView();
        }
    }

    /**
     * 可以下拉刷新?
     */
    public void setCanRefresh(boolean pCanRefresh) {
        mCanRefresh = pCanRefresh;
    }

    //添加头部View
    private void addHeaderView(View view) {
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFooterViews, mAdapter);
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //添加加载更多View
    private void addFooterView(View view) {
        mFooterViews.clear();
        mFooterViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFooterViews, mAdapter);
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void initFooterView() {
        endFooterView = layout.inflate(R.layout.pull_to_refresh_load_more, null);
        endFooterView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        endFooterView.setLayoutParams(layoutParams);
        end_tv = (TextView) endFooterView.findViewById(R.id.load_more);
        loading_iv = (ImageView) endFooterView.findViewById(R.id.pull_to_refresh_progress);

        endFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endFooter) {
                    if (mCanRefresh) {
                        // 当可以下拉刷新时，如果FootView没有正在加载，并且HeadView没有正在刷新，才可以点击加载更多。
                        if (mEndState != ENDINT_LOADING && mHeadState != REFRESHING) {
                            mEndState = ENDINT_LOADING;
                            onLoadMore();
                        }
                    } else if (mEndState != ENDINT_LOADING) {
                        // 当不能下拉刷新时，FootView不正在加载时，才可以点击加载更多。
                        mEndState = ENDINT_LOADING;
                        onLoadMore();
                    }
                }
            }
        });
        if (mIsAutoLoadMore) {
            mEndState = ENDINT_AUTO_LOAD_DONE;
        } else {
            mEndState = ENDINT_MANUAL_LOAD_DONE;
        }
        addFooterView(endFooterView);
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if (mHeaderViews.isEmpty() && mFooterViews.isEmpty()) {
            super.setAdapter(adapter);
        } else {
            adapter = new RecyclerWrapAdapter(mHeaderViews, mFooterViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }


    public class RVOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // 当不滚动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //获取最后一个完全显示的ItemPosition
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = manager.getItemCount();

                // 判断是否滚动到底部，并且是向右滚动
                if (lastVisibleItem == (totalItemCount - 1)) {
                    //加载更多功能的代码
                    /** 是否存在加载更多 */
                    if (endFooter) {
                        startImageView();
                        if (mEndState != ENDINT_LOADING) {
                            if (mIsAutoLoadMore) {
                                if (mCanRefresh) {
                                    // 存在下拉刷新并且HeadView没有正在刷新时，FootView可以自动加载更多。
                                    if (mHeadState != REFRESHING) {
                                        // FootView显示 : 更    多  ---> 加载中...
                                        mEndState = ENDINT_LOADING;
                                        onLoadMore();
                                        changeEndViewByState();
                                    }
                                } else {
                                    mEndState = ENDINT_LOADING;
                                    onLoadMore();
                                    changeEndViewByState();
                                }
                            } else {
                                mEndState = ENDINT_MANUAL_LOAD_DONE;
                                changeEndViewByState();

                            }
                        }
                    }
                }
            }
        }
    }

    private void onLoadMore() {
        if (mLoadMoreListener != null) {
            end_tv.setText("加载中.....");
            AnimationRotate.rotatebolowImage(loading_iv);
            mLoadMoreListener.onLoadMore();
        }
    }

    private void startImageView() {
        loading_iv.clearAnimation();
        AnimationRotate.rotatebolowImage(loading_iv);
    }

    private void changeEndViewByState() {
        if (endFooter) {
            switch (mEndState) {
                case ENDINT_LOADING://刷新中
                    if ("加载中.....".equals(end_tv.getText().toString())) {
                        break;
                    }
                    end_tv.setText("加载中.....");
                    AnimationRotate.rotatebolowImage(loading_iv);
                    break;
                case ENDINT_MANUAL_LOAD_DONE://手动刷新完成
                    end_tv.setText("点击加载");
                    loading_iv.clearAnimation();
                    loading_iv.setVisibility(View.INVISIBLE);
                    endFooterView.setVisibility(View.VISIBLE);
                    break;
                case ENDINT_AUTO_LOAD_DONE://自动刷新完成
                    // 更    多
                    end_tv.setText(" ");
                    end_tv.setVisibility(View.VISIBLE);
                    loading_iv.clearAnimation();
                    loading_iv.setVisibility(View.INVISIBLE);
                    endFooterView.setVisibility(View.VISIBLE);

            }
        }
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    //继承接口默认开启上拉加载
    public void setOnLoadListener(OnLoadMoreListener pLoadMoreListener) {
        if (pLoadMoreListener != null) {
            endFooter = true;
            mLoadMoreListener = pLoadMoreListener;
            if(endFooter && endFooterView==null){
                initFooterView();
            }
        }
    }

    /**
     * 加载更多完成
     *
     * @date 2013-11-11 下午10:21:38
     * @change JohnWatson
     * @version 1.0
     */
    public void onLoadMoreComplete() {
        if (mIsAutoLoadMore) {
            mEndState = ENDINT_AUTO_LOAD_DONE;
        } else {
            mEndState = ENDINT_MANUAL_LOAD_DONE;
        }
        changeEndViewByState();
    }
 /**  设置没有更多数据了并且取消加载更多,*/
    public void closeFooter(String info){
        endFooter=false;
        end_tv.setText(info);
        loading_iv.clearAnimation();
        loading_iv.setVisibility(View.GONE);
    }
}
