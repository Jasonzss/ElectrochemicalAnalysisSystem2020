package com.bluedot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    private static Pattern humpPattern = Pattern.compile("[A-Z]");


    public static String tableNameToClassName(String str) {
        if ("t_".equals(str.substring(0, 2))) {
            return firstCharToUpperCase(lineToHump(str.substring(2)));
        } else {
            return firstCharToUpperCase(lineToHump(str));
        }

    }



    public static String columnNameToMethodName(String str) {
        return firstCharToUpperCase(lineToHump(str));
    }


    public static String firstCharToUpperCase(String str) {
        return str.toUpperCase().charAt(0) + str.substring(1);
    }

    public static String lineToHump(String str) {
        String newStr = str.toLowerCase();
        Matcher matcher = linePattern.matcher(newStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    public static String humpToLine(String str) {
        String newStr = str.substring(0, 1).toLowerCase() + str.substring(1);
        Matcher matcher = humpPattern.matcher(newStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }


    public static String stringTrim(String src) {
        return (null != src) ? src.trim() : null;
    }

}
