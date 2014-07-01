package cn.burgeon.core.bean;

public class PayWay {
	private int id;
	private String payWay;
	private String payMoney;
	public PayWay() {
		super();
	}
	public PayWay(int id,String payWay, String payMoney) {
		super();
		this.id = id;
		this.payWay = payWay;
		this.payMoney = payMoney;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
}
