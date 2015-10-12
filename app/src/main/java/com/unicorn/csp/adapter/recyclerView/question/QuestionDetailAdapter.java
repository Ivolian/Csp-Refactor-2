package com.unicorn.csp.adapter.recyclerView.question;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unicorn.csp.R;
import com.unicorn.csp.model.Answer;
import com.unicorn.csp.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class QuestionDetailAdapter extends RecyclerView.Adapter<QuestionDetailAdapter.ViewHolder> {

    public QuestionDetailAdapter() {
        setHasStableIds(true);
    }

    private List<Answer> answerList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_content)
        TextView tvContent;

        @Bind(R.id.tv_event_time)
        TextView tvEventTime;

        @Bind(R.id.tv_display_name)
        TextView tvDisplayName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_question_detail, viewGroup, false));
    }

    @Override
    public long getItemId(int position) {
        return answerList.get(position).hashCode();
    }

    @Override
    public int getItemCount() {

        return answerList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Answer answer = answerList.get(position);
        viewHolder.tvContent.setText(answer.getContent());
        viewHolder.tvEventTime.setText(DateUtils.getFormatDateString(answer.getEventTime(), new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)));
        viewHolder.tvDisplayName.setText(answer.getDisplayName());
    }

    public List<Answer> getAnswerList() {

        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {

        this.answerList = answerList;
    }

}
