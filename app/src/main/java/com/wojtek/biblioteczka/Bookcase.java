package com.wojtek.biblioteczka;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Bookcase {

    private ArrayList<Book> books;

    public Bookcase() {
        books = new ArrayList<>();
    }

    private int getFreeId() {
        // Use next free number as local identifier.

        boolean ok;
        int id = 1;

        while (true)
        {
            ok = true;

            for (Book book : books) {
                if (book.id == id) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                break;
            } else {
                id++;
            }
        }

        return id;
    }

    public void add(Book book) {
        if (book.id == 0) {
            book.id = getFreeId();
        }

        books.add(book);
    }

    public void set(Book book) {
        for (int index = 0; index < books.size(); index++) {
            if (books.get(index).id == book.id) {
                books.set(index, book);
            }
        }
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void saveAsXml(File file) throws IOException {
        FileOutputStream os = new FileOutputStream(file);
        StringBuilder sb = new StringBuilder();

        final String format =
                "  <book>\n" +
                "    <id>%d</id>\n" +
                "    <author>%s</author>\n" +
                "    <title>%s</title>\n" +
                "    <city>%s</city>\n" +
                "    <publisher>%s</publisher>\n" +
                "    <year>%s</year>\n" +
                "    <cover>%s</cover>\n" +
                "  </book>\n";

        sb.append("<books>\n");

        for (Book book : books) {
            String str = String.format(
                    format,
                    book.id,
                    book.author,
                    book.title,
                    book.city,
                    book.publisher,
                    book.year,
                    book.cover);
            sb.append(str);
        }

        sb.append("</books>\n");

        os.write(sb.toString().getBytes());
        os.close();
    }

    public void loadFromXml(File file) throws IOException, XmlPullParserException {
        FileInputStream is = new FileInputStream(file);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(is, null);

        Book book = null;
        String text = "";

        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    switch (tagName) {
                        case "book":
                            book = new Book();
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
                            book.id = Integer.parseInt(text);
                            break;
                        case "author":
                            book.author = text;
                            break;
                        case "title":
                            book.title = text;
                            break;
                        case "city":
                            book.city = text;
                            break;
                        case "publisher":
                            book.publisher = text;
                            break;
                        case "year":
                            book.year = text;
                            break;
                        case "cover":
                            book.cover = text;
                            break;
                        case "book":
                            books.add(book);
                            book = null;
                            break;
                    }
                    break;
            }

            eventType = parser.next();
        }
    }
}
