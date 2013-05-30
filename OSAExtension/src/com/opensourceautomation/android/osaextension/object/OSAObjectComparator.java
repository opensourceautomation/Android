package com.opensourceautomation.android.osaextension.object;

import java.util.Comparator;

public class OSAObjectComparator implements Comparator<OSAObject>
{
    public int compare(OSAObject left, OSAObject right) {
        return left.getName().compareTo(right.getName());
    }
}