package com.ccservice.webbrowser.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.webbrowser.Bootstrap;
import com.ccservice.webbrowser.WindowContentBusinessAdapter;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * 【浏览器加载主界面后的初始化】线程
 * @author fyz
 *
 */
public class InitThread extends Thread{
	private static Logger logger=LogManager.getLogger(InitThread.class);
	/**
	 * 账号名称
	 */
	private String accountName="";
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public void process()
	{
		/**
		 * 获取账号对应的浏览器控件
		 */
		JWebBrowser wb=Bootstrap.accountWebBrower.get(accountName);
//		/**
//		 * 初始化【登陆系统】线程
//		 */
//		logger.info(accountName+"，初始化【登陆系统】线程======");
//		LoginWorker loginWorker=new LoginWorker();
//		loginWorker.setAccountName(accountName);
//		loginWorker.execute();
		/**
		 * 初始化【保持心跳】线程
		 */
		logger.info(accountName+"，初始化【保持心跳】线程======");
		HeartBeatWorker heartBeatWorker=new HeartBeatWorker();
		heartBeatWorker.setAccountName(accountName);
		heartBeatWorker.execute();
		/**
		 * 初始化【获取余额】线程
		 */
		logger.info(accountName+"，初始化【获取余额】线程======");
		BalanceWorker balanceWorker=new BalanceWorker();
		balanceWorker.setAccountName(accountName);
		balanceWorker.execute();
		/**
		 * 初始化【获取账单详情】线程
		 */
		logger.info(accountName+"，初始化【获取账单详情】线程======");
		BillDetailWorker billDetailWorker=new BillDetailWorker();
		billDetailWorker.setAccountName(accountName);
		billDetailWorker.execute();
		try {
			/**
			 * 注入控制js
			 */
			wb.executeJavascript(WindowContentBusinessAdapter.jsContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		process();
	}

}
