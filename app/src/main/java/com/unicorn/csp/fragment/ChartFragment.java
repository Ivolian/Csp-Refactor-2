package com.unicorn.csp.fragment;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.lzyzsd.randomcolor.RandomColor;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.unicorn.csp.R;
import com.unicorn.csp.fragment.base.ButterKnifeFragment;
import com.unicorn.csp.model.Book;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.volley.MyVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;


// todo 重构
public class ChartFragment extends ButterKnifeFragment {
    protected List<String> mParties = new ArrayList<>();


    @Bind(R.id.chart)
    PieChart mChart;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_chart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getDataFromServer();
        return rootView;
    }


    private void getDataFromServer(){

        MyVolley.addRequest(new JsonObjectRequest(getUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        List<Book> bookList = parseBookList(response);

                        Map<String,Integer> map = new HashMap<String, Integer>();
                        for (Book book:bookList){
                            if (!map.containsKey(book.getName())){
                                map.put(book.getName(),1);
                            }else {
                                map.put(book.getName(),map.get(book.getName())+1);
                            }
                        }

                        initView(map);
                    }
                },
               MyVolley.getDefaultErrorListener()));

    }

    private String getUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/favoritebook?").buildUpon();
        builder.appendQueryParameter("pageNo", "1");
        builder.appendQueryParameter("pageSize", "1000");
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        return builder.toString();
    }

    private List<Book> parseBookList(JSONObject response) {

        JSONArray bookJSONArray = JSONUtils.getJSONArray(response, "content", null);
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i != bookJSONArray.length(); i++) {
            JSONObject bookJSONObject = JSONUtils.getJSONObject(bookJSONArray, i);
            String name = JSONUtils.getString(bookJSONObject, "menuName", "");
            Book book = new Book();
            book.setName(name);
            bookList.add(book);
        }
        return bookList;
    }


    private void initView(Map<String,Integer> map) {

        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setCenterText("阅读分布");

        setData(map, 100);

        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        mChart.getLegend().setEnabled(false);
    }

    private void setData(Map<String,Integer> map, float range) {

//        mParties.add("sdf");
//        mParties.add("sdff");
        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        int i=0;
        for(Map.Entry<String, Integer> entry:map.entrySet()){
            yVals1.add(new Entry((float) entry.getValue(), i++));

        }
//        for (int i = 0; i < map.size(); i++) {
//            yVals1.add(new Entry((float) i, i));
//        }

        ArrayList<String> xVals = new ArrayList<String>();
        for(Map.Entry<String, Integer> entry:map.entrySet()){
            xVals.add(entry.getKey());
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        RandomColor randomColor =new RandomColor();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for(Map.Entry<String, Integer> entry:map.entrySet()){
            colors.add(randomColor.randomColor());
        }
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

}
