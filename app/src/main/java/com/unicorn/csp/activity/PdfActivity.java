package com.unicorn.csp.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.f2prateek.dart.InjectExtra;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.greendao.PdfHistory;
import com.unicorn.csp.model.Book;
import com.unicorn.csp.utils.BookUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;

import butterknife.Bind;


public class PdfActivity extends ToolbarActivity implements OnPageChangeListener {


    // ================= views =================

    @InjectExtra("book")
    Book book;

    @Bind(R.id.seekbar)
    DiscreteSeekBar seekBar;

    @Bind(R.id.pdfview)
    PDFView pdfView;


    // ================= onCreate =================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        initToolbar("", false);
        initViews();
    }


    // ================= initViews =================

    private void initViews() {

        String pdfPath = BookUtils.getBookPath(book);

        // 好像 id 必须是 long 或 string
        PdfHistory pdfHistory = MyApplication.getPdfHistoryDao().load(book.getId());
        int defaultPage = pdfHistory == null ? 1 : pdfHistory.getPage();
        pdfView.fromFile(new File(pdfPath))
                .enableSwipe(true)
                .onPageChange(this)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int i) {
                        initSeekBar();
                    }
                })
                .defaultPage(defaultPage)
                .load();
    }

    private void initSeekBar() {

        seekBar.setVisibility(View.GONE);
        seekBar.setMin(1);
        seekBar.setMax(pdfView.getPageCount());
        seekBar.setProgress(pdfView.getCurrentPage());
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

                pdfView.jumpTo(discreteSeekBar.getProgress());
            }
        });
    }


    // ================= onPageChanged =================

    @Override
    public void onPageChanged(int page, int pageCount) {

        String title = book.getName() + " " + page + "/" + pageCount;
        atvToolbarTitle.setText(title);
        seekBar.setProgress(page);
    }


    // ========================== 菜单 ==========================

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.pdf, menu);
        menu.findItem(R.id.paging).setIcon(getActionDrawable());
        return super.onCreateOptionsMenu(menu);
    }

    private Drawable getActionDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_swap)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (seekBar.getVisibility() == View.GONE) {
            seekBar.setVisibility(View.VISIBLE);
        } else {
            seekBar.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }


    // ========================== onBackPressed ==========================

    @Override
    public void onBackPressed() {

        saveOrUpdatePdfHistory();
        super.onBackPressed();
    }

    private void saveOrUpdatePdfHistory() {

        PdfHistory pdfHistory = new PdfHistory();
        pdfHistory.setId(book.getId());
        pdfHistory.setPage(pdfView.getCurrentPage() + 1);
        MyApplication.getPdfHistoryDao().insertOrReplace(pdfHistory);
    }


}