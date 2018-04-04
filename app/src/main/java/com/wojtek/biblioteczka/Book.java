package com.wojtek.biblioteczka;

import android.os.Parcel;
import android.os.Parcelable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

        author = in.readString();
        title = in.readString();
        city = in.readString();
        publisher = in.readString();
        year = in.readString();
        cover = in.readString();

        // TODO missing borrow fields
    }

    public Book(File file) throws IOException, XmlPullParserException {
        super();

        FileInputStream is = new FileInputStream(file);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(is, null);

        String text = null;

        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    switch (tagName) {
                        default:
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    switch (tagName) {
                        case "id":
                            id = Integer.parseInt(text);
                            break;
                        case "author":
                            author = text;
                            break;
                        case "title":
                            title = text;
                            break;
                        case "city":
                            city = text;
                            break;
                        case "publisher":
                            publisher = text;
                            break;
                        case "year":
                            year = text;
                            break;
                        case "cover":
                            cover = text;
                            break;
                    }
                    break;
            }
        }
    }

    public void toXml(File file) throws IOException {
        FileOutputStream os = new FileOutputStream(file);

        StringBuilder sb = new StringBuilder();

        sb.append("<book>\n");
        sb.append("   <id>" + Integer.toString(id) + "</id>\n");
        sb.append("   <author>" + author + "</author>\n");
        sb.append("   <title>" + title + "</title>\n");
        sb.append("   <city>" + city + "</city>\n");
        sb.append("   <publisher>" + publisher + "</publisher>\n");
        sb.append("   <year>" + year + "</year>\n");
        sb.append("   <cover>" + cover + "</cover>\n");
        sb.append("</book>\n");

        os.write(sb.toString().getBytes());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(city);
        dest.writeString(publisher);
        dest.writeString(year);
        dest.writeString(cover);
    }
}
