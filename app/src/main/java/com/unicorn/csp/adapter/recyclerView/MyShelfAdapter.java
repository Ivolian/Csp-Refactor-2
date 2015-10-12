package com.unicorn.csp.adapter.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.model.BookHelper;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.geometerplus.android.fbreader.OrientationUtil;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.library.BookInfoActivity;
import org.geometerplus.fbreader.book.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        @Bind(R.id.niv_picture)
        NetworkImageView nivPicture;

        @Bind(R.id.tv_book_name)
        TextView tvBookName;

        @Bind(R.id.tv_summary)
        TextView tvSummary;

        @Bind(R.id.progress)
        NumberProgressBar readProgress;

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

        // todo 目前暂时用 BookDetailActivity 解决
        // todo 貌似 bookId 和 bookPath 都不能重复
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
        viewHolder.tvSummary.setText(book.getSummary());
        viewHolder.nivPicture.setDefaultImageResId(R.drawable.default_book);
        viewHolder.nivPicture.setImageUrl(ConfigUtils.getBaseUrl() + book.getPicture(), MyVolley.getImageLoader());

        BookHelper.getBookReadingProgress(book);
        int percent = book.getDenominator() != 0 ? book.getNumerator() * 100 / book.getDenominator() : 0;
        viewHolder.readProgress.setProgress(percent);

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
