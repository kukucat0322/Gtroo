package cn.burgeon.core.bean;

/**
 * Created by Simon on 2014/4/22.
 */
public class AllotReplenishmentApply {
	private String barcode; // 条码
	private String color; // 颜色
	private String size; // 尺寸
	private String num; // 数量
	private String style; // 款号

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
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

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
