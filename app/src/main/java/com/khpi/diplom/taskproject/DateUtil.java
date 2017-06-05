package com.khpi.diplom.taskproject;

import android.util.LruCache;

import java.text.SimpleDateFormat;

/**
 * Created on 05/06/2017 21:08.
 */

public class DateUtil {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");

    private LruCache<Long, String> cache = new LruCache<>(20);

    public String formatDate(long date){
        String formattedDate = cache.get(date);

        if (formattedDate == null) {
            formattedDate = simpleDateFormat.format(date);
            cache.put(date, formattedDate);
        }

        return formattedDate;
    }

}
