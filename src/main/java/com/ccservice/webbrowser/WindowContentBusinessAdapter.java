package com.ccservice.webbrowser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.webbrowser.workers.InitThread;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;

/**
 * 针对窗口内容的处理
 * @author fyz
 */
public class WindowContentBusinessAdapter extends WebBrowserAdapter{
	private static Logger logger=LogManager.getLogger(WindowContentBusinessAdapter.class);
	protected static final String LS = System.getProperty("line.separator");
	/**
	 * 对应的系统账号
	 */
	public String accountName="";
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * 添加的js代码
	 */
	public static String jsContent="";
	/**
	 * 加载JS
	 * @param url 地址
	 * @return js内容
	 */
	public static String addJS(String url)
	{
		InputStream is=WindowContentBusinessAdapter.class.getClassLoader().getResourceAsStream(url);
		String js_content="";
		try {
			js_content=IOUtils.toString(is,Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuffer content=new StringBuffer();
		content.append(js_content).append(LS);
		return content.toString();
	}
	/**
	 * 初始化业务
	 */
	public void init(JWebBrowser wb)
	{
		/**
		 * 以非阻塞的形式进行初始化操作，不然会影响页面控件正常加载
		 */
		InitThread initThread=new InitThread();
		initThread.setAccountName(accountName);
		SwingUtilities.invokeLater(initThread);
//		Bootstrap.initThreadPool.execute(initThread);
	}
    @Override
    public void windowWillOpen(WebBrowserWindowWillOpenEvent e)
    {
//    	System.out.println("windowWillOpen:"+e.getSource());
    }
    @Override
    public void windowOpening(WebBrowserWindowOpeningEvent e)
    {
//    	System.out.println("windowOpening:"+e.getSource());
    }
	@Override
	public void commandReceived(WebBrowserCommandEvent e) {
	}
	@Override
	public void locationChangeCanceled(WebBrowserNavigationEvent e) {
	}

	@Override
	public void locationChanged(WebBrowserNavigationEvent e) {
//		System.out.println("locationChanged:"+e.getNewResourceLocation());
	}

	@Override
	public void locationChanging(WebBrowserNavigationEvent e) {
//		System.out.println("locationChanging:"+e.getNewResourceLocation());
	}

	@Override
	public void titleChanged(WebBrowserEvent e) {
		super.titleChanged(e);
	}

	@Override
	public void loadingProgressChanged(WebBrowserEvent e) {
		JWebBrowser wb=e.getWebBrowser();
		int progress=wb.getLoadingProgress();
		String loadUrl=wb.getResourceLocation();
		LoginForm account=Bootstrap.accountMap.get(accountName);
		if(progress==100)
		{
			if(account.getInitState()==false)
			{//防止重复执行初始化方法
				account.setInitState(true);
				logger.info("loadingProgressChanged init url:"+loadUrl);
				init(wb);
			}
		}
	}
	@Override
	public void statusChanged(WebBrowserEvent e) {
		super.statusChanged(e);
	}
	@Override
	public void windowClosing(WebBrowserEvent e) {
		super.windowClosing(e);
	}
    

}
