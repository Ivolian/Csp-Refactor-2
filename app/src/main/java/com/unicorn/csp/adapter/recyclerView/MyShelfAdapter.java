package com.unicorn.csp.adapter.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.model.BookHelper;
import com.unicorn.csp.other.LoginHelper;
import com.unicorn.csp.other.PdfHelper;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.DateUtils;
import com.unicorn.csp.utils.ToastUtils;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.geometerplus.android.fbreader.OrientationUtil;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.library.BookInfoActivity;
import org.geometerplus.fbreader.book.Book;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MyShelfAdapter extends RecyclerView.Adapter<MyShelfAdapter.ViewHolder> {


    Activity activity;

    public MyShelfAdapter(Activity activity) {
        this.activity = activity;
    }

    private List<com.unicorn.csp.model.Book> bookList = new ArrayList<>();


    // ======================== 绑定视图，添加事件 ========================

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.cardview)
        CardView cardView;

        @Bind(R.id.tv_book_name)
        TextView tvBookName;

        @Bind(R.id.progress)
        NumberProgressBar readProgress;

        @Bind(R.id.tv_event_time)
        TextView tvEventTime;

        @Bind(R.id.tv_comment_count)
        TextView tvCommentCount;

        @Bind(R.id.tv_thumb_count)
        TextView tvThumbCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    //

    private MaterialDialog showConfirmDownloadDialog(final com.unicorn.csp.model.Book book) {

        return new MaterialDialog.Builder(activity)
                .title("该书籍未缓存，是否需要下载？")
                .positiveText("确认")
                .negativeText("取消")
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        downloadBook(book);
                    }
                })
                .show();
    }

    private boolean isBookExist(com.unicorn.csp.model.Book book) {

        return new File(getBookPath(book)).exists();
    }

    private String getBookPath(com.unicorn.csp.model.Book book) {

        return ConfigUtils.getDownloadDirPath() + "/" + book.getEbookFilename();
    }

    private void openBook(com.unicorn.csp.model.Book book) {

        LoginHelper.checkLoginTime();
        if (book.getEbookFilename().endsWith(".pdf")) {
            String pdfPath = getBookPath(book);
            Uri uri = Uri.parse(pdfPath);
            Intent intent = new Intent(activity, MuPDFActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.putExtra("pdfId", book.getId());
            activity.startActivity(intent);
            return;
        }
        Book bookzz = new Book(book.getOrderNo(), getBookPath(book), book.getName(), null, null);
        Intent intent = new Intent(activity, BookInfoActivity.class);
        FBReaderIntents.putBookExtra(intent, bookzz);
        OrientationUtil.startActivity(activity, intent);
    }


    private void downloadBook(final com.unicorn.csp.model.Book book) {

        final MaterialDialog downloadDialog = showDownloadDialog(book);
        String url = ConfigUtils.getBaseUrl() + book.getEbook();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(MyApplication.getInstance()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {

                File file = new File(getBookPath(book));
                if (file.exists()) {
                    file.delete();
                }
                try {
                    FileUtils.copyFile(response, file);
                } catch (Exception e) {
                    //
                }
                downloadDialog.dismiss();
                openBook(book);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                downloadDialog.dismiss();
                ToastUtils.show("下载失败");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

                downloadDialog.setMaxProgress((int) totalSize / 1024);
                downloadDialog.setProgress((int) bytesWritten / 1024);
            }
        });
    }

    private MaterialDialog showDownloadDialog(final com.unicorn.csp.model.Book book) {

        return new MaterialDialog.Builder(activity)
                .title("下载书籍中")
                .progress(false, 100)
                .cancelable(false)
                .show();
    }

    //

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final com.unicorn.csp.model.Book book = bookList.get(position);
        viewHolder.tvBookName.setText(book.getName());
        viewHolder.tvEventTime.setText(DateUtils.getFormatDateString(book.getEventTime(), new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)));
        viewHolder.tvCommentCount.setText("评论 " + book.getCommentCount());
        viewHolder.tvThumbCount.setText("点赞 " + book.getThumbCount());

        BookHelper.getBookReadingProgress(book);
        int percent = book.getDenominator() != 0 ? book.getNumerator() * 100 / book.getDenominator() : 0;
        viewHolder.readProgress.setProgress(percent);
        if (book.getEbookFilename().endsWith("pdf")) {
            int page = PdfHelper.getPDFPage(book, activity);
            int pageCount = PdfHelper.getPDFPageCount(book, activity);
            percent = pageCount != 0 ? page * 100 / pageCount : 0;
            viewHolder.readProgress.setProgress(percent);
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBookExist(book)) {
                    openBook(book);
                } else {
                    showConfirmDownloadDialog(book);
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_book, viewGroup, false));
    }

    @Override
    public int getItemCount() {

        return bookList.size();
    }

    public List<com.unicorn.csp.model.Book> getBookList() {

        return bookList;
    }

    public void setBookList(List<com.unicorn.csp.model.Book> bookList) {

        this.bookList = bookList;
    }

}
