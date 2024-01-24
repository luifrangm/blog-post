package com.luifrangm.labels.utils;


public class LabelUtils {

    private LabelUtils() {}


    public static String renameFileName(final String imgSource) {
        return
            imgSource.substring(0, imgSource.length() - 4)
                .concat("_")
                .concat(imgSource.substring(imgSource.length() - 4));
    }

}
