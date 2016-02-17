package com.ospring.o2lounge.others;

public class MenuItemObj {
    private String mName;
    private String mDes;
    private int mThumbnail;
    private String cartCount;
    private int mPrice;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDes() {
        return mDes;
    }

    public void setDes(String des) {
        this.mDes = des;
    }

    public int getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.mThumbnail = thumbnail;
    }

    public String getCartCount(){
        return cartCount;
    }

    public void setCartCount(String cCount){
        this.cartCount = cCount;
    }

    public int getPrice(){
        return mPrice;
    }

    public void setPrice(int price){
        this.mPrice = price;
    }
}