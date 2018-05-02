package com.wojtek.biblioteczka;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BookcaseTest {
    @Test
    public void synchronizeArraysTest() {
        Book book;

        Bookcase books = new Bookcase();

        ArrayList<Book> books1 = new ArrayList<>();
        ArrayList<Book> books2 = new ArrayList<>();

        book = new Book();
        book.id = 0;
        book.version = 0;
        books1.add(book);

        books.synchronizeArrays(books1, books2);

        assertEquals(books1.size(), 1);
        book = books1.get(0);
        assertEquals(book.id, 0);
        assertEquals(book.version, 0);

        assertEquals(books2.size(), 1);
        book = books2.get(0);
        assertEquals(book.id, 0);
        assertEquals(book.version, 0);

        book.version = 1;

        books.synchronizeArrays(books1, books2);

        assertEquals(books1.size(), 1);
        book = books1.get(0);
        assertEquals(book.id, 0);
        assertEquals(book.version, 1);

        assertEquals(books2.size(), 1);
        book = books2.get(0);
        assertEquals(book.id, 0);
        assertEquals(book.version, 1);

        book = new Book();
        book.id = 1;
        book.version = 0;
        books2.add(book);

        books.synchronizeArrays(books1, books2);

        assertEquals(books1.size(), 2);
        assertEquals(books2.size(), 2);
    }
}
