package com.ccservice.webbrowser.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户账户信息
 * @author fyz
 *
 */
public class AccountData {
	/**
	 * 登录账号
	 */
	String username;
	/**
	 * 最后会话超时时间
	 */
	Date lastSessionTimeout = new Date();
	/**
	 * 是否会话超时
	 */
	AtomicBoolean sessionTimeout = new AtomicBoolean(true);
	/**
	 * 账户余额
	 */
	Double balance = new Double(0d);

	/**
	 * 最后获取余额时间
	 */
	Date lastBalance = new Date();
	/**
	 * 最后更新时间（当表数据发生更新时，需要修改此内容）
	 */
	private Date lastUpdateDate;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getLastSessionTimeout() {
		return lastSessionTimeout;
	}
	public void setLastSessionTimeout(Date lastSessionTimeout) {
		this.lastSessionTimeout = lastSessionTimeout;
	}
	public AtomicBoolean getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(AtomicBoolean sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Date getLastBalance() {
		return lastBalance;
	}
	public void setLastBalance(Date lastBalance) {
		this.lastBalance = lastBalance;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
