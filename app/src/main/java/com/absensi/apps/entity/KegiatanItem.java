package com.absensi.apps.entity;

public class KegiatanItem extends ListItem{

    private Kegiatan kegiatan;

    public Kegiatan getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(Kegiatan kegiatan) {
        this.kegiatan = kegiatan;
    }

    @Override
    public int getType() {
        return TYPE_ITEM;
    }
}
