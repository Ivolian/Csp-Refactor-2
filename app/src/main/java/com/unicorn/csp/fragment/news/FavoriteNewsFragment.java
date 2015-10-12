package com.unicorn.csp.fragment.news;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.unicorn.csp.other.SwipeableRecyclerViewTouchListener;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;


// clear
public class FavoriteNewsFragment extends NewsFragment {

    @Override
    public String getUrl(Integer pageNo) {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/favorite?").buildUpon();
        builder.appendQueryParameter("pageNo", pageNo.toString());
        builder.appendQueryParameter("pageSize", PAGE_SIZE.toString());
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        return builder.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        addSwipeListenerForRecycleView();
        return rootView;
    }

    private void addSwipeListenerForRecycleView() {

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String newsId = newsAdapter.getNewsList().get(position).getId();
                                    showConfirmBar(newsId);
                                    newsAdapter.getNewsList().remove(position);
                                    newsAdapter.notifyItemRemoved(position);
                                }
                                newsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String newsId = newsAdapter.getNewsList().get(position).getId();
                                    showConfirmBar(newsId);
                                    newsAdapter.getNewsList().remove(position);
                                    newsAdapter.notifyItemRemoved(position);
                                }
                                newsAdapter.notifyDataSetChanged();
                            }
                        });
        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    private void showConfirmBar(final String newsId) {

        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text("确认取消关注？")
                        .actionLabel("确认")
                        .actionColor(ColorOverrider.getInstance(getActivity()).getColorAccent())
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                unfollow(newsId);
                            }
                        })
                , getActivity());
    }

    private void unfollow(String newsId) {

        MyVolley.addRequest(new StringRequest(getUnfollowUrl(newsId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ToastUtils.show("已取消关注");
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getUnfollowUrl(String newsId) {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/favorite/delete?").buildUpon();
        builder.appendQueryParameter("newsId", newsId);
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        return builder.toString();
    }

}
