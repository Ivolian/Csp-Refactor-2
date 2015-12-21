package com.unicorn.csp.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.flyco.banner.widget.Banner.BaseIndicaorBanner;
import com.unicorn.csp.R;
import com.unicorn.csp.model.News;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.volley.MyVolley;


public class SimpleImageBanner extends BaseIndicaorBanner<News, SimpleImageBanner> {

    public SimpleImageBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onTitleSlect(TextView tv, int position) {
        final News news = list.get(position);
        tv.setText(news.getTitle());
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = View.inflate(context, R.layout.adapter_simple_image, null);
        NetworkImageView networkImageView = (NetworkImageView) inflate.findViewById(R.id.iv);
        final News news = list.get(position);
        int itemWidth = dm.widthPixels;
        int itemHeight = (int) (itemWidth * 360 * 1.0f / 640);
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        networkImageView.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));
        networkImageView.setDefaultImageResId(R.drawable.default_news);
        networkImageView.setImageUrl(ConfigUtils.getBaseUrl() + news.getPicture(), MyVolley.getImageLoader());
        return inflate;
    }

}
