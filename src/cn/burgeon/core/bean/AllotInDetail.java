package cn.burgeon.core.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Simon on 2014/4/25.
 */
public class AllotInDetail implements Parcelable {
    private String M_PRODUCTALIAS_ID;  // 条码
    private String M_ATTRIBUTESETINSTANCE_ID_VALUE1; // 颜色
    private String M_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE; // 尺寸
    private String QTYOUT; // 出库数量
    private String QTYIN; // 入库数量
    private String PRICELIST; // 标准价
    private String M_PRODUCT_ID_VALUE; // 品名

    public String getM_PRODUCTALIAS_ID() {
        return M_PRODUCTALIAS_ID;
    }

    public void setM_PRODUCTALIAS_ID(String m_PRODUCTALIAS_ID) {
        M_PRODUCTALIAS_ID = m_PRODUCTALIAS_ID;
    }

    public String getM_ATTRIBUTESETINSTANCE_ID_VALUE1() {
        return M_ATTRIBUTESETINSTANCE_ID_VALUE1;
    }

    public void setM_ATTRIBUTESETINSTANCE_ID_VALUE1(String m_ATTRIBUTESETINSTANCE_ID_VALUE1) {
        M_ATTRIBUTESETINSTANCE_ID_VALUE1 = m_ATTRIBUTESETINSTANCE_ID_VALUE1;
    }

    public String getM_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE() {
        return M_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE;
    }

    public void setM_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE(String m_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE) {
        M_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE = m_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE;
    }

    public String getQTYOUT() {
        return QTYOUT;
    }

    public void setQTYOUT(String QTYOUT) {
        this.QTYOUT = QTYOUT;
    }

    public String getQTYIN() {
        return QTYIN;
    }

    public void setQTYIN(String QTYIN) {
        this.QTYIN = QTYIN;
    }

    public String getPRICELIST() {
        return PRICELIST;
    }

    public void setPRICELIST(String PRICELIST) {
        this.PRICELIST = PRICELIST;
    }

    public String getM_PRODUCT_ID_VALUE() {
        return M_PRODUCT_ID_VALUE;
    }

    public void setM_PRODUCT_ID_VALUE(String m_PRODUCT_ID_VALUE) {
        M_PRODUCT_ID_VALUE = m_PRODUCT_ID_VALUE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.M_PRODUCTALIAS_ID);
        dest.writeString(this.M_ATTRIBUTESETINSTANCE_ID_VALUE1);
        dest.writeString(this.M_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE);
        dest.writeString(this.QTYOUT);
        dest.writeString(this.QTYIN);
        dest.writeString(this.PRICELIST);
        dest.writeString(this.M_PRODUCT_ID_VALUE);
    }

    public AllotInDetail() {
    }

    private AllotInDetail(Parcel in) {
        this.M_PRODUCTALIAS_ID = in.readString();
        this.M_ATTRIBUTESETINSTANCE_ID_VALUE1 = in.readString();
        this.M_ATTRIBUTESETINSTANCE_ID_VALUE2_CODE = in.readString();
        this.QTYOUT = in.readString();
        this.QTYIN = in.readString();
        this.PRICELIST = in.readString();
        this.M_PRODUCT_ID_VALUE = in.readString();
    }

    public static Parcelable.Creator<AllotInDetail> CREATOR = new Parcelable.Creator<AllotInDetail>() {
        public AllotInDetail createFromParcel(Parcel source) {
            return new AllotInDetail(source);
        }

        public AllotInDetail[] newArray(int size) {
            return new AllotInDetail[size];
        }
    };
}
