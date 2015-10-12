package com.unicorn.csp.adapter.recyclerView.question;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.unicorn.csp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuestionDetailHeaderAdapter implements StickyHeadersAdapter<QuestionDetailHeaderAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_detail_header, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {

        headerViewHolder.tvTitle.setText(position == 0 ? "问题" : "回答");
    }

    @Override
    public long getHeaderId(int position) {

        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_title)
        TextView tvTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
