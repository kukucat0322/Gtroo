package cn.burgeon.core.bean;

/**
 * Created by Simon on 2014/4/22.
 */
public class AllotReplenishment {
    private int ID;
    private String DOCNO;         // 单据编号
    private String UPLOAD_STATUS; // 上传状态
    private String DOCDATE;       // 单据日期
    private String OUT_STORE;     // 发货店仓
    private String APPLY_PEOPLE;  // 申请人
    private String REMARK;        // 备注

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDOCNO() {
        return DOCNO;
    }

    public void setDOCNO(String DOCNO) {
        this.DOCNO = DOCNO;
    }

    public String getUPLOAD_STATUS() {
        return UPLOAD_STATUS;
    }

    public void setUPLOAD_STATUS(String UPLOAD_STATUS) {
        this.UPLOAD_STATUS = UPLOAD_STATUS;
    }

    public String getDOCDATE() {
        return DOCDATE;
    }

    public void setDOCDATE(String DOCDATE) {
        this.DOCDATE = DOCDATE;
    }

    public String getOUT_STORE() {
        return OUT_STORE;
    }

    public void setOUT_STORE(String OUT_STORE) {
        this.OUT_STORE = OUT_STORE;
    }

    public String getAPPLY_PEOPLE() {
        return APPLY_PEOPLE;
    }

    public void setAPPLY_PEOPLE(String APPLY_PEOPLE) {
        this.APPLY_PEOPLE = APPLY_PEOPLE;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }
}
