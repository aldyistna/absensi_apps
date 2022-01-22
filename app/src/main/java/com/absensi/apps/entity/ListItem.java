package com.absensi.apps.entity;

public abstract class ListItem {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_ITEM = 1;

    abstract public int getType();
}
