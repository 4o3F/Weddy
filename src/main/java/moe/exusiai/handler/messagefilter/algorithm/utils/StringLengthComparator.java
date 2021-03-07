package moe.exusiai.handler.messagefilter.algorithm.utils;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String> {
    @Override
    public int compare(String str1, String str2) {
        int num = str2.length() - str1.length();
        if(num == 0)
            return str1.compareTo(str2);
        return num;
    }
}
