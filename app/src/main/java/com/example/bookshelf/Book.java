package com.example.bookshelf;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Book {
    private int id;
    private String image;
    private String title;
    private String author;
    private String date;
    private int yet;
    private Float rating;
    private String thought;
    private ImageView imageView;
    private Bitmap spineBitmap;

    public Book(int id, String image, String title, String author, String date, int yet, Float rating, String thought) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.author = author;
        this.date = date;
        this.yet = yet;
        this.rating = rating;
        this.thought = thought;
    }

    public Book(int id, String image, String title, String author) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.author = author;
    }

    public enum ItemType {
        BOOK, ITEM
    }

    public ItemType itemType = ItemType.BOOK;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }


    public int getYet() {
        return yet;
    }


    public Float getRating() {
        return rating;
    }


    public String getThought() {
        return thought;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
}

    public Bitmap getSpineBitmap() {
        return spineBitmap;
    }

    public void setSpineBitmap(Bitmap spineBitmap) {
        this.spineBitmap = spineBitmap;
    }
}
