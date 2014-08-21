package cn.burgeon.core.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable{
	private int id;
	private String name;
	private String sex;
	private String cardNum;
	private String iDentityCardNum;
	private String phoneNum;
	private String type;
	private String birthday;
	private String email;
	private String employee;
	private String discount;
	private String yue;
	private String status;
	private String vipState;
	private String typeid;
	private int customerID;
	private int storeID;

	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getVipState() {
		return vipState;
	}
	public void setVipState(String vipState) {
		this.vipState = vipState;
	}

	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
	public int getStoreID() {
		return storeID;
	}
	public void setStoreID(int storeID) {
		this.storeID = storeID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getYue() {
		return yue;
	}
	public void setYue(String yue) {
		this.yue = yue;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}



	private String createCardDate;
	public Member() {
		super();
	}
	public Member(String name, String sex, String cardNum,
			String iDentityCardNum, String phoneNum, String type, String birthday,
			String email, String createCardDate) {
		super();
		this.name = name;
		this.sex = sex;
		this.cardNum = cardNum;
		this.iDentityCardNum = iDentityCardNum;
		this.phoneNum = phoneNum;
		this.type = type;
		this.birthday = birthday;
		this.email = email;
		this.createCardDate = createCardDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getiDentityCardNum() {
		return iDentityCardNum;
	}
	public void setiDentityCardNum(String iDentityCardNum) {
		this.iDentityCardNum = iDentityCardNum;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCreateCardDate() {
		return createCardDate;
	}
	public void setCreateCardDate(String createCardDate) {
		this.createCardDate = createCardDate;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.name);
		dest.writeString(this.sex);
		dest.writeString(this.cardNum);
		dest.writeString(this.iDentityCardNum);
		dest.writeString(this.phoneNum);
		dest.writeString(this.type);
		dest.writeString(this.birthday);
		dest.writeString(this.email);
		dest.writeString(this.createCardDate);
		dest.writeString(this.employee);
		dest.writeString(this.discount);
		dest.writeString(this.typeid);
	}
	
	
	
    public Member(Parcel in) {
    	this.id=in.readInt();
    	this.name=in.readString();
    	this.sex=in.readString();
    	this.cardNum=in.readString();
    	this.iDentityCardNum=in.readString();
    	this.phoneNum=in.readString();
    	this.type=in.readString();
    	this.birthday=in.readString();
    	this.email=in.readString();
    	this.createCardDate=in.readString();
    	this.employee=in.readString();
    	this.discount=in.readString();
    	this.typeid=in.readString();
	}



	public static Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}
