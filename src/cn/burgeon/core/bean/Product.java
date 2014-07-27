package cn.burgeon.core.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int id;
    private String uuid;
    private String name;
    private String barCode;
    private String price;
    private String discount;
    private String count;
    private String money;
    private String color;
    private String size;
    private String shelf;
    private String style;
    private int salesType;
    public int getSalesType() {
		return salesType;
	}

	public void setSalesType(int salesType) {
		this.salesType = salesType;
	}

	public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String moeny) {
        this.money = moeny;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.barCode);
        dest.writeString(this.price);
        dest.writeString(this.discount);
        dest.writeString(this.count);
        dest.writeString(this.money);
        dest.writeString(this.uuid);
        dest.writeString(this.style);
        dest.writeString(this.color);
        dest.writeString(this.size);
        dest.writeInt(this.salesType);
    }

    public Product() {
    }

    public Product(Parcel in) {
        this.name = in.readString();
        this.barCode = in.readString();
        this.price = in.readString();
        this.discount = in.readString();
        this.count = in.readString();
        this.money = in.readString();
        this.uuid = in.readString();
        this.style = in.readString();
        this.color = in.readString();
        this.size = in.readString();
        this.salesType = in.readInt();
    }

    public static Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}

