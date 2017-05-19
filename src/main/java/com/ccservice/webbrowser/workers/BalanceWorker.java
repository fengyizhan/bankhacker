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
 * 【账户余额】线程
 * @author fyz
 *
 */
public class BalanceWorker extends SwingWorker<Void,Void>{
	private static Logger logger=LogManager.getLogger(BalanceWorker.class);
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
		logger.info("BalanceWorker start work accountName:"+accountName);
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
			/**
			 * 检查会话是否超时
			 */
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getBalance(wb,account);
				}
			});
			
		}catch(Exception e)
		{
			logger.error("BalanceWorker Exception:",e);
			e.printStackTrace();
		}
		logger.info("BalanceWorker finished work accountName:"+accountName);
	}
	/**
	 * 保持心跳
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void getBalance(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 只有在会话没有超时的情况下，才需要进行定期【会话保持】
		 * 如果已经弹出登陆页，即使进行【会话保持】也没有任何意义了
		 */
		boolean needToDoWork=(account.getKeepalived() && (!account.getSessionTimeout()));
		if(!needToDoWork){return;}
		/**
		 * 【获取余额】业务处理
		 */
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.getBalance();");
		try
		{
			if(result!=null&&!"".equals(String.valueOf(result)))
			{
				String balance_result=String.valueOf(result);
				balance_result=balance_result.replaceAll(",","");
				double balance_double=Double.parseDouble(balance_result);
				//如果界面正在刷新的时候，获取余额区域内容js函数会返回-1，这是无效的
				if(balance_double>=0)
				{
					account.setBalance(balance_double);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		account.setLastKeepalived(new Date());
		logger.info("accountName:"+accountName+"，balance:"+result);
	}
	@Override
	protected void process(List chunks) {
		super.process(chunks);
	}
	@Override
	protected void done() {
		super.done();
		logger.info("BalanceWorker accountName:"+accountName+"，done:");
	}
	@Override
	protected Void doInBackground() throws Exception {
		while(Constants.run)
		{
			business();
			try {
				Random randomSleepSeed = new Random();
				Thread.sleep((randomSleepSeed.nextInt(Constants.randomSeed)+Constants.balanceThreadInterval)*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
