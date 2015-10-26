package com.unicorn.csp.adapter.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unicorn.csp.R;
import com.unicorn.csp.model.Thumb;
import com.unicorn.csp.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ThumbAdapter extends RecyclerView.Adapter<ThumbAdapter.ViewHolder> {

    private List<Thumb> thumbList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_display_name)
        TextView tvDisplayName;

        @Bind(R.id.tv_eventtime)
        TextView tvEventTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_thumb, viewGroup, false));
    }

    @Override
    public int getItemCount() {

        return thumbList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Thumb thumb = thumbList.get(position);
        viewHolder.tvDisplayName.setText(thumb.getDisplayName());
        viewHolder.tvEventTime.setText(DateUtils.getFormatDateString(thumb.getEventTime(), new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)));
    }

    public List<Thumb> getThumbList() {
        return thumbList;
    }

    public void setThumbList(List<Thumb> thumbList) {
        this.thumbList = thumbList;
    }

}
