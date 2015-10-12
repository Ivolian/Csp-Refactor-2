package com.unicorn.csp.adapter.recyclerView;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.etiennelawlor.trestle.library.Span;
import com.etiennelawlor.trestle.library.Trestle;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.news.NewsDetailActivity;
import com.unicorn.csp.model.News;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.DateUtils;
import com.unicorn.csp.volley.MyVolley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> newsList = new ArrayList<>();

    private String keyword = "";

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_title)
        TextView tvTitle;

        @Bind(R.id.tv_time)
        TextView tvTime;

        @Bind(R.id.tv_comment_count)
        TextView tvCommentCount;

        @Bind(R.id.tv_thumb_count)
        TextView tvThumbCount;

        @Bind(R.id.niv_picture)
        NetworkImageView nivPicture;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Intent intent = new Intent(itemView.getContext(), NewsDetailActivity.class);
                    intent.putExtra("news", newsList.get(getAdapterPosition()));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public int getItemCount() {

        return newsList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        News news = newsList.get(position);
        viewHolder.tvTitle.setText(news.getTitle());
        viewHolder.tvTime.setText(DateUtils.getFormatDateString(news.getTime(), new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)));
        viewHolder.tvCommentCount.setText("评论 " + news.getCommentCount());
        viewHolder.tvThumbCount.setText("点赞 " + news.getThumbCount());
        viewHolder.nivPicture.setDefaultImageResId(R.drawable.default_news);
        viewHolder.nivPicture.setImageUrl(ConfigUtils.getBaseUrl() + news.getPicture(), MyVolley.getImageLoader());
        emphasizeTitle(news.getTitle(), viewHolder.tvTitle);
    }

    public void emphasizeTitle(String title, TextView tvTitle) {

        if (!keyword.equals("")) {
            String[] arr = title.split(keyword);
            List<Span> spans = new ArrayList<>();
            if (arr.length == 2) {
                spans.add(new Span.Builder(arr[0])
                        .foregroundColor(MyApplication.getInstance().getResources().getColor(android.R.color.black))
                        .build());
                spans.add(new Span.Builder(keyword)
                        .foregroundColor(ColorOverrider.getInstance(MyApplication.getInstance()).getColorAccent())
                        .build());
                spans.add(new Span.Builder(arr[1])
                        .foregroundColor(MyApplication.getInstance().getResources().getColor(android.R.color.black))
                        .build());
                tvTitle.setText(Trestle.getFormattedText(spans));
            }
        }
    }

    public List<News> getNewsList() {

        return newsList;
    }

    public void setNewsList(List<News> newsList) {

        this.newsList = newsList;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
