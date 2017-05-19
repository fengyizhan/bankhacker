package com.ccservice.webbrowser.workers;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.webbrowser.Bootstrap;
import com.ccservice.webbrowser.Constants;
import com.ccservice.webbrowser.LoginForm;
import com.ccservice.webbrowser.WindowContentBusinessAdapter;
import com.ccservice.webbrowser.util.DDUtil;
import com.ccservice.webbrowser.util.DDUtil.DD;
import com.ccservice.webbrowser.util.DaMaResult;
import com.ccservice.webbrowser.util.HttpResourceUtil;
import com.ccservice.webbrowser.util.Page;
import com.ccservice.webbrowser.util.RuoKuaiUtils;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * 【自动登录】线程
 * @author fyz
 *
 */
public class LoginAction extends SwingWorker<Void,Void>{
	private static Logger logger=LogManager.getLogger(LoginAction.class);
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
		int tabIndex=0;
		logger.info("========= LoginAction start work accountName:"+accountName);
		try
		{
			/**
			 * 获取账号对应的浏览器控件
			 */
			JWebBrowser wb=Bootstrap.accountWebBrower.get(accountName);
			LoginForm account=Bootstrap.accountMap.get(accountName);
			tabIndex=account.getTabIndex();
			Bootstrap.tabbedPane.setSelectedIndex(tabIndex);
			logger.info("LoginAction start work accountName:"+accountName+"，setSelectedIndex:"+tabIndex);
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
					isSessionTimeout(wb,account);
					/**
					 * 只有在会话超时的情况下，才需要进行【重新登陆】
					 */
					boolean needToDoWork=(account.getSessionTimeout());
					if(!needToDoWork){return;}
					/**
					 * 读取【随机码】并开始自动登录
					 */
					getRandomId(wb,account);
					String randomId=account.getRandomId();
					if(randomId==null||"".equals(randomId))
					{//如果没有获取【随机数】令牌，无法继续刷新【验证码】，退出
						logger.info("LoginAction dama work accountName:"+accountName+"，tabIndex:"+account.getTabIndex()+"，randomId获取不到，等待下次执行机会");
						return;
					}
					Page imagePage=refreshVerifyImage(randomId);
					String imagePath=imagePage.getDownloadPath();
					/**
					 * 验证码
					 */
					String verifyCode="";
					/**
					 * 打码过程
					 */
					for(int i=0;i<Constants.damaRetryTimes;i++)
					{
						DaMaResult dmr=RuoKuaiUtils.dama(imagePath);
						boolean isSuccess=dmr.isSuccess();
						if(isSuccess)
						{
							verifyCode=dmr.getResult();
							account.setVerifyCode(verifyCode);
							logger.info("LoginAction dama work accountName:"+accountName+"，tabIndex:"+account.getTabIndex()+"，verifyCode:"+verifyCode);
							try
							{
								File damaImageFile=new File(imagePath);
								if(damaImageFile.exists())
								{
									logger.info("LoginAction after dama work deleteQuietly imagePath："+imagePath);
									FileUtils.deleteQuietly(damaImageFile);
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							break;
						}
					}
					/**
					 * 由于填写密码和验证码的位置，需要当前【用户焦点】，所以，同时，只能处理一个“自动登录”，当处理完后，在处理其他的
					 */
					formLogin(wb,account);
					logger.info("========= LoginAction end work accountName:"+accountName);
				}
			});
		}catch(Exception e)
		{
			logger.error("LoginAction Exception:",e);
			e.printStackTrace();
		}
		logger.info("LoginAction finished work accountName:"+accountName+"，tabIndex:"+tabIndex);
	}
	/**
	 * 用户模拟登陆
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void formLogin(JWebBrowser wb,LoginForm account)
	{		
		Bootstrap.frame.setState(JFrame.NORMAL);
		Bootstrap.frame.toFront();
		String password=account.getPassword();
		String verifyCode=account.getVerifyCode();
		/**
		 * 设置当前需要处理登陆的‘焦点账户’
		 */
		logger.info("（1）LoginAction accountName:"+accountName+", work password:"+password+"，verifyCode:"+verifyCode);
		
		/**
		 * 1.登陆框焦点设置，用户账号的输入
		 */
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.initLoginForm('"+accountName+"');");
		logger.info("LoginAction formLogin work accountName:"+accountName+"，result:"+result);
		DD keyUtils=DDUtil.DD.INSTANCE;
		/**	
		 * TAB键
		 */
		keyUtils.DD_key(300,1);
		keyUtils.DD_key(300,2);
		/**
		 * 2.设置密码域
		 * 消除密码域上的默认提示文字
		 */
//		keyUtils.DD_btn(1);
		/**
		 * 2.设置密码域
		 * 设置密码域内容
		 */
		keyUtils.DD_str(password);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/**
		 * TAB键
		 */
		keyUtils.DD_key(300,1);
		keyUtils.DD_key(300,2);
		logger.info("（2）LoginAction formLogin keyUtils work password:"+password);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/**
		 * 3.设置验证码域
		 */
		keyUtils.DD_str(verifyCode);
		logger.info("（3）LoginAction formLogin keyUtils work verifyCode:"+verifyCode);
		/**
		 * 4.提交表单
		 */
		formSubmit(wb,account);
		logger.info("（4）LoginAction formSubmit work");
		/**
		 * 5.用完需要恢复出厂设置
		 */
		account.setLastLogin(new Date());
	}
	/**
	 * 刷新一个验证码，因为打码失败时，需要换图重试
	 * @param randomId 随机码
	 * @return 打码图片
	 */
	public Page refreshVerifyImage(String randomId)
	{
		String verifyImageUrl="https://mybank.icbc.com.cn/servlet/com.icbc.inbs.person.servlet.Verifyimage2?disFlag=2&isCn=0&randomKey="+randomId+"&imgheight=36&imgwidth=95";
		logger.info("accountName:"+accountName+"，refreshVerifyImage:"+verifyImageUrl);
		Page verifyCodePage=new Page();
		verifyCodePage.setUrl(verifyImageUrl);
		verifyCodePage.setDownload(true);
		verifyCodePage.setDownloadPath(Constants.damaRootDir+accountName+"/"+randomId+"_"+new Date().getTime()+Constants.damaImageType);
		try {
			HttpResourceUtil.getPage(verifyCodePage, Constants.request_timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return verifyCodePage;
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
		logger.info("accountName:"+accountName+"，isSessionTimeout:"+result);
	}
	/**
	 * 获取randomId用来打码登陆
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void getRandomId(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 只有在会话没有超时的情况下，才需要进行定期【会话保持】
		 * 如果已经弹出登陆页，即使进行【会话保持】也没有任何意义了
		 */
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.getRandomId();");
		account.setRandomId(String.valueOf(result));
		logger.info("accountName:"+accountName+"，randomId:"+result);
	}
	
	/**
	 * 初始化表单用户名，并且设置登录框焦点输入
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void initLoginForm(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 1.设置焦点；
		 * 2.清空输入框的值；
		 */
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.initLoginForm();");
		logger.info("accountName:"+accountName+"，initLoginForm:"+result);
	}
	
	/**
	 * 登陆表单提交
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void formSubmit(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 1.表单校验
		 * 2.失败后自动重新刷新登陆窗口
		 */
		Object result=wb.executeJavascriptWithResult("return hthy_ccservice_domain.formSubmit();");
		try
		{
			Boolean loginState=Boolean.valueOf(String.valueOf(result));
			account.setLoginState(loginState);
			account.setLastLogin(new Date());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		logger.info("accountName:"+accountName+"，formSubmit:"+result);
	}
	
	@Override
	protected void process(List chunks) {
		super.process(chunks);
	}
	@Override
	protected void done() {
		super.done();
		logger.info("========= LoginAction end work accountName:"+accountName);
	}
	@Override
	protected Void doInBackground() throws Exception {
		business();
		return null;
	}

}
