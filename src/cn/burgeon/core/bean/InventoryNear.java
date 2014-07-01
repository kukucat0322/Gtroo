package cn.burgeon.core.bean;

public class InventoryNear {
	
	private String storeName;
	private String styleNumber;
	private String styleCount;
	private String barCode;
	
	public InventoryNear() {
		super();
	}
	
	public InventoryNear(String storeName,String styleNumber, String styleCount, String barCode) {
		super();
		this.storeName = storeName;
		this.styleNumber = styleNumber;
		this.styleCount = styleCount;
		this.barCode = barCode;
	}
	
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStyleNumber() {
		return styleNumber;
	}
	
	public void setStyleNumber(String styleNumber) {
		this.styleNumber = styleNumber;
	}
	
	public String getStyleCount() {
		return styleCount;
	}
	
	public void setStyleCount(String styleCount) {
		this.styleCount = styleCount;
	}
	
	public String getBarCode() {
		return barCode;
	}
	
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
}
