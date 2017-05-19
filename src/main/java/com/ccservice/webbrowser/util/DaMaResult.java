package com.ccservice.webbrowser.util;

public class DaMaResult {
    //打码唯一标识，用于打码错误时，提交错误申请
    //-1：打码错误，此时result表示错误信息
    private String id;
    private boolean success=false;
    public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	//打码结果
    private String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}