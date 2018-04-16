package com.wojtek.biblioteczka;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Wojtek on 2018-03-28.
 */

public class Book implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public int id;
    public String author;
    public String title;
    public String city;
    public String publisher;
    public String year;
    public String cover;
    public Calendar add_date;
    public String borrowed_by;
    public Calendar borrowed_date;

    public Book() {
        id = 0;
        author = "";
        title = "";
        city = "";
        publisher = "";
        year = "";
        cover = "";
        add_date = null;
        borrowed_by = "";
        borrowed_date = null;
    }

    public Book(Parcel in) {
        super();

        id = in.readInt();
        author = in.readString();
        title = in.readString();
        city = in.readString();
        publisher = in.readString();
        year = in.readString();
        cover = in.readString();

        // TODO missing borrow fields
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(city);
        dest.writeString(publisher);
        dest.writeString(year);
        dest.writeString(cover);

        // TODO missing borrow fields
    }
}
