package com.example.moa_cardview.data;

public class Items {

    private String Title;
    private String Date;
    private String Place;
    private String Price;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public Items(String title, String date, String place, String price) {
        this.Title = title;
        this.Date = date;
        this.Place = place;
        this.Price = price;
        this.expandable = false;
    }

    //Getter


    public String getTitle() {
        return Title;
    }

    public String getDate() {
        return Date;
    }

    public String getPlace() {
        return Place;
    }

    public String getPrice() {
        return Price;
    }

    //Setter


    public void setTitle(String title) {
        this.Title = title;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public void setPlace(String place) {
        this.Place = place;
    }

    public void setPrice(String price) {
        this.Price = price;
    }

    @Override
    public String toString() {
        return "Items{" +
                "Title='" + Title + '\'' +
                ", Date='" + Date + '\'' +
                ", Place='" + Place + '\'' +
                ", Price='" + Price + '\'' +
                '}';
    }
}
