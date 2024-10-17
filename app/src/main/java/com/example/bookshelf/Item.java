package com.example.bookshelf;

public class Item {
    private int id;
    private int image;
    private String name;
    private int get;

    public int getId() { return id;}
    public void setId(int id) { this.id = id; }
    public int getImage() {
        return image;
    }
    public void setImage(int image) { this.image = image;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getGet() { return get;}
    public void setGet(int get) { this.get = get; }
}
