package com.unicorn.csp.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.unicorn.csp.R;
import com.unicorn.csp.model.Notice;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import me.corer.labelview.LabelView;

public class CardsDataAdapter extends ArrayAdapter<Notice> {

    public CardsDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {

        LabelView labelView = (LabelView) contentView.findViewById(R.id.label);
        labelView.setNum(position + 1 + "");

        final Notice notice = getItem(position);
        TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        tvTitle.setText(notice.getTitle());
        TextView tvContent = (TextView) contentView.findViewById(R.id.tv_content);
        tvContent.setText(notice.getContent());
        TextView tvEventTime = (TextView) contentView.findViewById(R.id.tv_event_time);
        PrettyTime prettyTime = new PrettyTime();
        tvEventTime.setText(prettyTime.format(new Date(notice.getEventTime())));

        return contentView;
    }

}

