package com.unicorn.csp.adapter.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.setting.PersonalInfoActivity;
import com.unicorn.csp.model.Comment;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.DateUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.NetworkCircleImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.nciv_avatar)
        NetworkCircleImageView ncivAvatar;

        @Bind(R.id.tv_display_name)
        TextView tvDisplayName;

        @Bind(R.id.tv_eventtime)
        TextView tvEventTime;

        @Bind(R.id.tv_content)
        TextView tvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.nciv_avatar)
        public void startPersonalInfoActivity(){
            Activity activity = (Activity)ncivAvatar.getContext();
            Intent intent = new Intent(activity, PersonalInfoActivity.class);
            intent.putExtra("userId",commentList.get(getAdapterPosition()).getUserId());
            activity.startActivity(intent);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false));
    }

    @Override
    public int getItemCount() {

        return commentList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Comment comment = commentList.get(position);
        viewHolder.ncivAvatar.setImageUrl(ConfigUtils.getBaseUrl()+comment.getAvatar(), MyVolley.getImageLoader());
        viewHolder.ncivAvatar.setDefaultImageResId(R.drawable.profile);
        viewHolder.tvDisplayName.setText(comment.getDisplayName());
        viewHolder.tvEventTime.setText(DateUtils.getFormatDateString(new Date(comment.getEventTime()), new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)));
        viewHolder.tvContent.setText(comment.getContent());
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

}
