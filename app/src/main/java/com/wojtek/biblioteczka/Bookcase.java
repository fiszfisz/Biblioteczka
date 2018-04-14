package com.wojtek.biblioteczka;

import java.io.File;
import java.util.ArrayList;

public class Bookcase {

    private ArrayList<Book> books;
    private File dataFile;

    public Bookcase(File file) {
        books = new ArrayList<>();
        dataFile = file;

        Book book;
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
}
