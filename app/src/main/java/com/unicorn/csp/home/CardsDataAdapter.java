package com.unicorn.csp.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.unicorn.csp.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import me.corer.labelview.LabelView;

public class CardsDataAdapter extends ArrayAdapter<String> {

    public CardsDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        PrettyTime prettyTime = new PrettyTime();


        LabelView labelView = (LabelView) contentView.findViewById(R.id.label);
        labelView.setNum(position + 1 + "");


        TextView tvContent = (TextView)contentView.findViewById(R.id.tv_content);
        String content = "重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。重要通知，重要通知。";
        tvContent.setText("\t\t\t\t"+content);

        TextView tvTime = (TextView)contentView.findViewById(R.id.tv_time);
        tvTime.setText(prettyTime.format(new Date(System.currentTimeMillis() - 1000*60*10)));

        return contentView;
    }

}

