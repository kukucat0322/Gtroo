package cn.burgeon.core.bean;

/**
 * Created by Simon on 2014/4/22.
 */
public class AllotOut {
    private int ID;               // 调拨表单ID
    private String DOCNO;         // 单据编号
    private String UPLOAD_STATUS; // 上传状态
    private String DOC_STATUS;    // 单据状态
    private String BILLDATE;      // 单据日期
    private String C_DEST_ID;     // 收货店仓
    private String TOT_QTYOUT;    // 出库数量

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

    public String getDOC_STATUS() {
        return DOC_STATUS;
    }

    public void setDOC_STATUS(String DOC_STATUS) {
        this.DOC_STATUS = DOC_STATUS;
    }

    public String getBILLDATE() {
        return BILLDATE;
    }

    public void setBILLDATE(String BILLDATE) {
        this.BILLDATE = BILLDATE;
    }

    public String getC_DEST_ID() {
        return C_DEST_ID;
    }

    public void setC_DEST_ID(String c_DEST_ID) {
        C_DEST_ID = c_DEST_ID;
    }

    public String getTOT_QTYOUT() {
        return TOT_QTYOUT;
    }

    public void setTOT_QTYOUT(String TOT_QTYOUT) {
        this.TOT_QTYOUT = TOT_QTYOUT;
    }
}
