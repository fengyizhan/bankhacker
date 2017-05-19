package com.ccservice.webbrowser;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户账号
 * 
 * @author fyz
 *
 */
public class LoginForm {
	/**
	 * 所属的标签Tab顺序
	 */
	int tabIndex;
	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * 登录账号
	 */
	String username;
	/**
	 * 密码
	 */
	String password;
	/**
	 * 验证码
	 */
	String verifyCode;
	/**
	 * 登陆打码randomId
	 */
	String randomId;
	/**
	 * 客户端缓存
	 */
	String cookie;
	/**
	 * 会话标示
	 */
	String dse_sessionId = "";
	/**
	 * 登陆状态
	 */
	AtomicBoolean loginState = new AtomicBoolean(false);
	/**
	 * 最后尝试登陆时间
	 */
	Date lastLogin = new Date();
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * 初始化状态
	 */
	AtomicBoolean initState = new AtomicBoolean(false);
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
	public Date getLastBalance() {
		return lastBalance;
	}

	/**
	 * 最后心跳时间
	 */
	Date lastKeepalived = new Date();
	/**
	 * 是否成功保持心跳
	 */
	AtomicBoolean keepalived = new AtomicBoolean(true);
	/**
	 * 最后获取账单详情时间
	 */
	Date lastBillDetail = new Date();
	/**
	 * 账单文件名称
	 */
	String billDetailFileName = "";

	public String getRandomId() {
		return randomId;
	}

	public void setRandomId(String randomId) {
		this.randomId = randomId;
	}
	public Date getLastBillDetail() {
		return lastBillDetail;
	}

	public void setLastBillDetail(Date lastBillDetail) {
		this.lastBillDetail = lastBillDetail;
	}

	public String getBillDetailFileName() {
		return billDetailFileName;
	}

	public void setBillDetailFileName(String billDetailFileName) {
		this.billDetailFileName = billDetailFileName;
	}

	public double getBalance() {
		return balance.doubleValue();
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Date getLastSessionTimeout() {
		return lastSessionTimeout;
	}

	public void setLastSessionTimeout(Date lastSessionTimeout) {
		this.lastSessionTimeout = lastSessionTimeout;
	}

	public boolean getSessionTimeout() {
		return sessionTimeout.get();
	}

	public void setSessionTimeout(boolean sessionTimeout) {
		this.sessionTimeout.set(sessionTimeout);
	}

	public boolean getKeepalived() {
		return keepalived.get();
	}

	public void setKeepalived(boolean keepalived) {
		this.keepalived.set(keepalived);
	}

	public Date getLastKeepalived() {
		return lastKeepalived;
	}

	public void setLastKeepalived(Date lastKeepalived) {
		this.lastKeepalived = lastKeepalived;
	}

	public boolean getInitState() {
		return initState.get();
	}

	public void setInitState(boolean initState) {
		this.initState.set(initState);
	}

	public boolean getLoginState() {
		return loginState.get();
	}

	public void setLoginState(boolean loginState) {
		this.loginState.set(loginState);
	}

	public String getDse_sessionId() {
		return dse_sessionId;
	}

	public void setDse_sessionId(String dse_sessionId) {
		this.dse_sessionId = dse_sessionId;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
	
}
