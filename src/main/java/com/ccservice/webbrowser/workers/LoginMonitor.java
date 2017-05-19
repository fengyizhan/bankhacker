package com.ccservice.webbrowser.workers;

import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.webbrowser.Bootstrap;
import com.ccservice.webbrowser.Constants;
import com.ccservice.webbrowser.LoginForm;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * 【自动登录】线程
 * @author fyz
 *
 */
public class LoginMonitor extends SwingWorker<Void,Void>{
	/**
	 * 当前焦点所在的账户
	 * 由于多个线程共享，所以，要用同步方法设置和读取
	 */
	private static String focusedAccount=null;
	
	
	public synchronized static String getFocusedAccount() {
		return focusedAccount;
	}
	public synchronized static void setFocusedAccount(String focusedAccount) {
		LoginMonitor.focusedAccount = focusedAccount;
	}
	private static Logger logger=LogManager.getLogger(LoginMonitor.class);
	public void business()
	{
		logger.info("====== LoginMonitor start work focusedAccount:"+focusedAccount);
		int tabIndex=0;
		try
		{
			List<LoginForm> accounts=Bootstrap.accounts;
				/**
				 * 检查会话是否超时
				 */
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						/**
						 * 按照顺序队列（从左至右）来执行【掉线用户】的重新登陆
						 */
						int loginCounter=0;
						for(int a=0;a<accounts.size();a++)
						{
							LoginForm lf=accounts.get(a);
							String accountName=lf.getUsername();
							boolean initState=lf.getInitState();
							logger.info("（1）LoginMonitor["+a+"] accountName:"+accountName+" initState"+initState);
							if(!initState)
							{//如果账号未初始化完成，不处理
								logger.info("（1）LoginMonitor accountName:"+accountName+"界面尚未初始化完成，等待下次执行机会");
								continue;
							}
							isSessionTimeout(accountName);
							logger.info("（2）LoginMonitor ["+a+"] accountName:"+accountName+", isSessionTimeout："+lf.getSessionTimeout());
							/**
							 * 取到一个就作为目标，直到这个登陆成功后，继续下一个
							 */
							if(lf.getSessionTimeout())
							{
								setFocusedAccount(lf.getUsername());
								break;
							}else
							{
								loginCounter++;
							}
						}
						logger.info("（2）LoginMonitor loginCounter："+loginCounter);
						if(loginCounter==accounts.size())
						{//如果所有账号全都已经是登陆状态
							logger.info("（2）LoginMonitor 检测到所有账号都是【已登录】状态");
							setFocusedAccount(null);
						}
						/**
						 * 如果当前存在未登录【用户】，才可以开始工作
						 */
						if(getFocusedAccount()==null)
						{
							logger.info("（3）LoginMonitor no getFocusedAccount，等待下次执行机会");
							return;
						}
						logger.info("（4）LoginMonitor 开启 getFocusedAccount："+getFocusedAccount()+"【登陆系统】线程======");
	            		LoginAction loginAction=new LoginAction();
	            		loginAction.setAccountName(getFocusedAccount());
	            		loginAction.business();
	            		
					}});
			
		}catch(Exception e)
		{
			logger.error("LoginMonitor Exception:",e);
			e.printStackTrace();
		}
		logger.info("====== LoginMonitor finished work focusedAccount:"+focusedAccount+"，tabIndex:"+tabIndex);
	}
	/**
	 * 会话是否超时
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void isSessionTimeout(String accountName)
	{
		JWebBrowser wb=Bootstrap.accountWebBrower.get(accountName);
		LoginForm account=Bootstrap.accountMap.get(accountName);
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.isSessionTimeout();");
		account.setSessionTimeout(Boolean.valueOf(String.valueOf(result)).booleanValue());
		account.setLastSessionTimeout(new Date());
		logger.info("accountName:"+accountName+"，isSessionTimeout:"+result);
	}
	
	@Override
	protected void process(List chunks) {
		super.process(chunks);
	}
	@Override
	protected void done() {
		super.done();
		logger.info("LoginMonitor getFocusedAccount:"+focusedAccount+"，done:");
	}
	@Override
	protected Void doInBackground() throws Exception {
		while(Constants.run)
		{
			business();
			try {
				Thread.sleep((Constants.logincheckThreadInterval)*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
