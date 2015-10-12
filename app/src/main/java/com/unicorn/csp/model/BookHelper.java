package com.unicorn.csp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.unicorn.csp.MyApplication;


public class BookHelper {

    public static void getBookReadingProgress(Book book) {

        SQLiteDatabase db = MyApplication.getInstance().openOrCreateDatabase("books.db", Context.MODE_PRIVATE, null);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM BookReadingProgress WHERE book_id = ?", new String[]{book.getOrderNo() + ""});
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            book.setNumerator(0);
            book.setDenominator(0);
            return;
        }

        if (cursor.getCount() == 0) {
            book.setNumerator(0);
            book.setDenominator(0);
        } else {
            if (cursor.moveToFirst()) {
                int numerator = cursor.getInt(cursor.getColumnIndex("numerator"));
                int denominator = cursor.getInt(cursor.getColumnIndex("denominator"));
                book.setNumerator(numerator);
                book.setDenominator(denominator);
            }
        }
        cursor.close();
        db.close();
    }

}
