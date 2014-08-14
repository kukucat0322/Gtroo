
package cn.burgeon.core.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Simon on 2014/4/22.
 * 页面传递数据
 */
public class IntentData implements Parcelable {
    private String store;
    private String user;
    private String command;
    private String vipCardno;
    private Employee employee;

	private ArrayList<AllotInDetail> allotInDetails;
    private ArrayList<Product> products;
    
    public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getVipCardno() {
		return vipCardno;
	}

	public void setVipCardno(String vipCardno) {
		this.vipCardno = vipCardno;
	}

	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ArrayList<AllotInDetail> getAllotInDetails() {
        return allotInDetails;
    }

    public void setAllotInDetails(ArrayList<AllotInDetail> allotInDetails) {
        this.allotInDetails = allotInDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.store);
        dest.writeString(this.user);
        dest.writeString(this.command);
        dest.writeString(this.vipCardno);
        dest.writeSerializable(this.employee);
        dest.writeTypedList(allotInDetails);
        dest.writeTypedList(products);
    }

    public IntentData() {
    }

    private IntentData(Parcel in) {
        this.store = in.readString();
        this.user = in.readString();
        this.command = in.readString();
        this.vipCardno = in.readString();
        this.employee = (Employee) in.readSerializable();

        this.allotInDetails = new ArrayList<AllotInDetail>();
        in.readTypedList(allotInDetails, AllotInDetail.CREATOR);
        
        this.products = new ArrayList<Product>();
        in.readTypedList(products, Product.CREATOR);
    }

    public static Parcelable.Creator<IntentData> CREATOR = new Parcelable.Creator<IntentData>() {
        public IntentData createFromParcel(Parcel source) {
            return new IntentData(source);
        }

        public IntentData[] newArray(int size) {
            return new IntentData[size];
        }
    };
}

