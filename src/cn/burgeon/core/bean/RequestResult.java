package cn.burgeon.core.bean;

public class RequestResult {
	private String code;
	private String message;
	public RequestResult() {
		super();
	}
	public RequestResult(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
