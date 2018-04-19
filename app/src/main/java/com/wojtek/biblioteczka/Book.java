package com.wojtek.biblioteczka;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Wojtek on 2018-03-28.
 */

public class Book implements Parcelable {

    public static Comparator<Book> IdComparator = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            int v1 = o1.id;
            int v2 = o2.id;

            return Integer.compare(v1, v2);
        }
    };

    public static Comparator<Book> AuthorComparator = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            String v1 = o1.author.toUpperCase();
            String v2 = o2.author.toUpperCase();

            return v1.compareTo(v2);
        }
    };

    public static Comparator<Book> TitleComparator = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            String v1 = o1.title.toUpperCase();
            String v2 = o2.title.toUpperCase();

            return v1.compareTo(v2);
        }
    };

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
    public String publisher;
    public String city;
    public String year;
    public String cover;
    public Calendar add_date;
    public String borrowed_by;
    public Calendar borrowed_date;

    public Book() {
        id = 0;
        author = "";
        title = "";
        publisher = "";
        city = "";
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
        publisher = in.readString();
        city = in.readString();
        year = in.readString();
        cover = in.readString();

        // TODO missing borrow fields
    }

    public boolean isEmpty() {
        return (id == 0 &&
                author.equals("") &&
                title.equals("") &&
                publisher.equals("") &&
                city.equals("") &&
                year.equals("") &&
                cover.equals(""));

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
        dest.writeString(publisher);
        dest.writeString(city);
        dest.writeString(year);
        dest.writeString(cover);

        // TODO missing borrow fields
    }
}
