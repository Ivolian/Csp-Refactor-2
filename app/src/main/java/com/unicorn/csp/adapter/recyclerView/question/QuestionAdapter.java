package com.unicorn.csp.adapter.recyclerView.question;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.question.QuestionDetailActivity;
import com.unicorn.csp.model.Question;
import com.unicorn.csp.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


// clear
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questionList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_content)
        TextView tvContent;

        @Bind(R.id.tv_time)
        TextView tvTime;

        @Bind(R.id.tv_display_name)
        TextView tvDisplayName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Intent intent = new Intent(itemView.getContext(), QuestionDetailActivity.class);
                    intent.putExtra("question", questionList.get(getAdapterPosition()));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_question, viewGroup, false));
    }

    @Override
    public int getItemCount() {

        return questionList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Question question = questionList.get(position);
        viewHolder.tvContent.setText(question.getContent());
        viewHolder.tvTime.setText(DateUtils.getFormatDateString(question.getEventTime(), new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)));
        viewHolder.tvDisplayName.setText(question.getDisplayName());
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}