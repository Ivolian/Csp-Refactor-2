package com.unicorn.csp.activity;

import android.os.Bundle;

import com.f2prateek.dart.InjectExtra;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;

import java.io.File;

import butterknife.Bind;

public class PdfActivity extends ToolbarActivity implements OnPageChangeListener {

    @InjectExtra("pdfPath")
    String pdfPath;

    @Bind(R.id.pdfview)
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        initToolbar("",false);
        initViews();
    }

    private void initViews(){

        pdfView.fromFile(new File(pdfPath))
                .enableSwipe(true)
                .enableDoubletap(true)
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

        // todo
        String title = "书名" + page + "/" + pageCount;
        atvToolbarTitle.setText(title);
    }

}
