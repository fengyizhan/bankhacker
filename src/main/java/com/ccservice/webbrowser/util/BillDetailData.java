package com.ccservice.webbrowser.util;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

/**
 * 支付账单明细
 * @author fyz
 *
 */
public class BillDetailData {
	/**
	 * 交易流水号
	 */
	private String number;
	/**
	 * 订单总金额
	 */
	private Double money;
	/**
	 * 使用积分
	 */
	private Long useScore;
	/**
	 * 积分抵现
	 */
	private Double scoreMoney;
	/**
	 * 退货总金额
	 */
	private Double backMoney;
	/**
	 * 商户名称
	 */
	private String shopname;
	/**
	 * 交易时间
	 */
	private String payDate;
	/**
	 * 支付渠道
	 */
	private String payType;
	/**
	 * 交易状态
	 */
	private String payState;
	/**
	 * 最后更新时间（当表数据发生更新时，需要修改此内容）
	 */
	private Date lastUpdateDate;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Long getUseScore() {
		return useScore;
	}
	public void setUseScore(Long useScore) {
		this.useScore = useScore;
	}
	public Double getScoreMoney() {
		return scoreMoney;
	}
	public void setScoreMoney(Double scoreMoney) {
		this.scoreMoney = scoreMoney;
	}
	public Double getBackMoney() {
		return backMoney;
	}
	public void setBackMoney(Double backMoney) {
		this.backMoney = backMoney;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayState() {
		return payState;
	}
	public void setPayState(String payState) {
		this.payState = payState;
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
