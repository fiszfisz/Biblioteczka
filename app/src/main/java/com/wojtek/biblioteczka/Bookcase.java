package com.wojtek.biblioteczka;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public class Bookcase {

    private final String tag = "Bookcase";

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

    private int bookIndex(ArrayList<Book> list, int id) {
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index).id == id) {
                return index;
            }
        }

        return -1;
    }

    public Book get(int id) {
        int index = bookIndex(books, id);
        return books.get(index);
    }

    public void set(Book book) {
        int index = bookIndex(books, book.id);
        books.set(index, book);
    }

    public void remove(Book book) {
        int index = bookIndex(books, book.id);
        book = books.get(index);
        book.version = -Math.abs(++book.version);
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    protected String fileToString (File file) throws IOException {
        StringBuilder sb = new StringBuilder();

        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        return sb.toString();
    }

    protected void stringToFile (String data, File file) throws IOException {
        FileOutputStream os = new FileOutputStream(file);
        os.write(data.getBytes());
        os.close();
    }

    protected String smbFileToString(SmbFile file) throws IOException {
        StringBuilder sb = new StringBuilder();

        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(file)));

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();
        }

        return sb.toString();
    }

    protected void stringToSmbFile(String data, SmbFile file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(file)));
        writer.write(data);
        writer.close();
    }

    protected String bookListToXml(ArrayList<Book> list) {
        StringBuilder sb = new StringBuilder();

        Collections.sort(list, Book.IdComparator);

        final String format =
                "  <book>\n" +
                        "    <id>%d</id>\n" +
                        "    <version>%d</version>\n" +
                        "    <author>%s</author>\n" +
                        "    <title>%s</title>\n" +
                        "    <publisher>%s</publisher>\n" +
                        "    <city>%s</city>\n" +
                        "    <year>%s</year>\n" +
                        "    <cover>%s</cover>\n" +
                        "  </book>\n";

        sb.append("<books>\n");

        for (Book book : list) {
            String str = String.format(
                    format,
                    book.id,
                    book.version,
                    book.author,
                    book.title,
                    book.publisher,
                    book.city,
                    book.year,
                    book.cover);
            sb.append(str);
        }

        sb.append("</books>\n");

        return sb.toString();
    }

    protected void bookListFromXml(ArrayList<Book> list, String data) throws IOException, XmlPullParserException {
        InputStream is = new ByteArrayInputStream(data.getBytes());

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
                            break;
                        default:
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText().trim();
                    break;

                case XmlPullParser.END_TAG:
                    switch (tagName) {
                        case "id":
                            book.id = Integer.parseInt(text);
                            break;
                        case "version":
                            book.version = Integer.parseInt(text);
                        case "author":
                            book.author = text;
                            break;
                        case "title":
                            book.title = text;
                            break;
                        case "publisher":
                            book.publisher = text;
                            break;
                        case "city":
                            book.city = text;
                            break;
                        case "year":
                            book.year = text;
                            break;
                        case "cover":
                            book.cover = text;
                            break;
                        case "book":
                            list.add(book);
                            book = null;
                            break;
                    }
                    break;
            }

            eventType = parser.next();
        }

        is.close();
    }

    protected boolean synchronizeArrays(ArrayList<Book> books1, ArrayList<Book> books2) {
        boolean changed = false;

        int index;
        Book other;

        for (Book book : books1) {
            index = bookIndex(books2, book.id);
            if (index > -1) {
                other = books2.get(index);
            } else {
                other = null;
            }

            if (other == null) {
                books2.add(book.clone());
                changed = true;
                continue;
            }

            if (Math.abs(book.version) > Math.abs(other.version)) {
                other.copy(book);
                changed = true;
            }
        }

        for (Book book : books2) {
            index = bookIndex(books1, book.id);
            if (index > -1) {
                other = books1.get(index);
            } else {
                other = null;
            }

            if (other == null) {
                books1.add(book.clone());
                changed = true;
                continue;
            }

            if (Math.abs(book.version) > Math.abs(other.version)) {
                other.copy(book);
                changed = true;
            }
        }

        // TODO maybe return number of changed items?
        return changed;
    }

    public void loadFromXml(File file) throws IOException, XmlPullParserException {
        String data;

        books.clear();
        data = fileToString(file);
        bookListFromXml(books, data);
    }

    public void saveAsXml(File file) throws IOException {
        String data;

        data = bookListToXml(books);
        stringToFile(data, file);
    }

    public synchronized void synchronizeWithSmb(final SmbFile remoteFile) {
        boolean changed;
        String data;
        ArrayList<Book> remoteBooks;

        try {
            data = smbFileToString(remoteFile);
            remoteBooks = new ArrayList<>();
            bookListFromXml(remoteBooks, data);

            changed = synchronizeArrays(books, remoteBooks);

            if (changed) {
                data = bookListToXml(remoteBooks);
                stringToSmbFile(data, remoteFile);
            }
        } catch (Exception e) {
            Log.e(tag, "Synchronization exception: " + e.getMessage());
        }
    }
}
