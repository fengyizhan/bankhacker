package com.ccservice.webbrowser.workers;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.webbrowser.Bootstrap;
import com.ccservice.webbrowser.Constants;
import com.ccservice.webbrowser.LoginForm;
import com.ccservice.webbrowser.WindowContentBusinessAdapter;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * 【保持心跳】线程
 * @author fyz
 *
 */
public class HeartBeatWorker extends SwingWorker<Void,Void>{
	private static Logger logger=LogManager.getLogger(HeartBeatWorker.class);
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
	public void business()
	{
		logger.info("HeartBeatWorker start work accountName:"+accountName);
		try
		{
			/**
			 * 获取账号对应的浏览器控件
			 */
			JWebBrowser wb=Bootstrap.accountWebBrower.get(accountName);
			LoginForm account=Bootstrap.accountMap.get(accountName);
			
			try {
				/**
				 * 注入控制js
				 */
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						wb.executeJavascript(WindowContentBusinessAdapter.jsContent);
					}
				});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Object cookie=wb.executeJavascriptWithResult("return document.cookie;");
						account.setCookie(String.valueOf(cookie));
						logger.info("accountName:"+accountName+",cookie:"+cookie);
					}
				});
			} catch (Exception e) {
				logger.error("HeartBeatWoker get cookie accountName:"+accountName,e);
				e.printStackTrace();
			}
			/**
			 * 读取【会话标示】
			 */
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getSessionId(wb,account);
				}
			});
			/**
			 * 保持心跳
			 */
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					keepalived(wb,account);
				}
			});
		}catch(Exception e)
		{
			logger.error("HeartBeatWoker Exception:",e);
			e.printStackTrace();
		}
		logger.info("HeartBeatWorker finished work accountName:"+accountName);
	}
	/**
	 * 保持心跳
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void keepalived(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 检测会话超时
		 */
		isSessionTimeout(wb,account);
		/**
		 * 只有在会话没有超时的情况下，才需要进行定期【会话保持】
		 * 如果已经弹出登陆页，即使进行【会话保持】也没有任何意义了
		 */
		if(!account.getSessionTimeout())
		{
			Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.keepalived();");
			account.setKeepalived(Boolean.valueOf(String.valueOf(result)).booleanValue());
			account.setLastKeepalived(new Date());
			logger.info("accountName:"+accountName+"，keepalived:"+result);
		}
	}
	/**
	 * 会话是否超时
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void isSessionTimeout(JWebBrowser wb,LoginForm account)
	{
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.isSessionTimeout();");
		account.setSessionTimeout(Boolean.valueOf(String.valueOf(result)).booleanValue());
		account.setLastSessionTimeout(new Date());
		logger.info(accountName+"，isSessionTimeout:"+result);
	}
	/**
	 * 获取dse_sessionId重要会话标示
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void getSessionId(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 只有在会话没有超时的情况下，才需要进行定期【会话保持】
		 * 如果已经弹出登陆页，即使进行【会话保持】也没有任何意义了
		 */
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.getSessionId();");
		account.setDse_sessionId(String.valueOf(result));
		logger.info("accountName:"+accountName+"，dse_sessionId:"+result);
	}
	@Override
	protected void process(List chunks) {
		super.process(chunks);
	}
	@Override
	protected void done() {
		super.done();
		logger.info("HeartBeatWoker accountName:"+accountName+"，done:");
	}
	@Override
	protected Void doInBackground() throws Exception {
		while(Constants.run)
		{
			business();
			try {
				Random randomSleepSeed = new Random();
				Thread.sleep((randomSleepSeed.nextInt(Constants.randomSeed)+Constants.heatbeat)*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
