package com.unicorn.csp.utils;


public class BookUtils {

    public static String getBookPath(com.unicorn.csp.model.Book book) {

        return ConfigUtils.getDownloadDirPath() + "/" + book.getEbookFilename();
    }

}
