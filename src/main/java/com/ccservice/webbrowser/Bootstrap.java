package com.ccservice.webbrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.Util.PropertyUtil;
import com.ccservice.webbrowser.workers.LoginAction;
import com.ccservice.webbrowser.workers.LoginMonitor;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * @author fyz
 * 项目启动引导程序
 */
public class Bootstrap
{
	private static Logger logger=LogManager.getLogger(Bootstrap.class);
	/**
	 * 【浏览器加载主界面后的初始化】总控线程池
	 */
	public static ExecutorService initThreadPool = Executors.newCachedThreadPool();
	/**
	 * 【登陆系统】总控线程池
	 */
	public static ExecutorService loginThreadPool = Executors.newCachedThreadPool();
	/**
	 * 【保持心跳】总控线程池
	 */
	public static ExecutorService heartbeatThreadPool = Executors.newCachedThreadPool();
	/**
	 * 【获取账单详情】总控线程池
	 */
	public static ExecutorService billDetailThreadPool = Executors.newCachedThreadPool();
	/**
	 * 【获取余额】总控线程池
	 */
	public static ExecutorService balanceThreadPool = Executors.newCachedThreadPool();
    public static final String LS = System.getProperty("line.separator");
    
    /**
     * 系统账号
     */
    public static List<LoginForm> accounts=new ArrayList<LoginForm>();
    /**
     * key:username  value: LoginForm
     */
    public static Map<String,LoginForm> accountMap=new HashMap<String,LoginForm>();
    /**
     * 账号与浏览器实例的绑定，便于控制操作
     */
    public static Map<String,JWebBrowser> accountWebBrower=new HashMap<String,JWebBrowser>();
    /**
     * 业务类别
     */
    public static String type=Constants.TYPE_GSYH;
    /**
     * 主窗体
     */
    public static JFrame frame;
    /**
     * 账号余额区域
     */
    public static JLabel userBalanceValue;
    /**
     * 内容区
     */
    public static JPanel contentPane;
    /**
     * 多标签集
     */
    public static JTabbedPane tabbedPane;
    /**
     * 根据标签的顺序，获得账号信息
     * @param index 顺序
     * @return 账号信息
     */
    public static LoginForm getAccountByTabIndex(int index)
    {
        for(int a=0;a<accounts.size();a++)
        {
      	  LoginForm account=accounts.get(a);
      	  if(account.getTabIndex()==index)
      	  {
      		  return account;
      	  }
        }
        return null;
    }
    /**
     * 界面初始化
     */
    public static void initCompnents()
    {
    	try {
			String billDetailBackwardTimePeriod=PropertyUtil.getValue("billDetailBackwardTimePeriod", "database-config.properties");
			Constants.billDetailBackwardTimePeriod=Integer.valueOf(billDetailBackwardTimePeriod);
			logger.info("支付账单拉取天数设置："+billDetailBackwardTimePeriod);
    	} catch (Exception e) {
			e.printStackTrace();
		}
        contentPane = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel();
        southPanel.setBorder(BorderFactory.createTitledBorder("当前账号相关操作"));
        JButton loginButton = new JButton("手动登陆");
        loginButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	int currentIndex = tabbedPane.getSelectedIndex();
                LoginForm currentAccount=getAccountByTabIndex(currentIndex);
                String accountName=currentAccount.getUsername();
                if(currentAccount!=null)
                {
            		logger.info(accountName+"，开启 accountName："+accountName+"【登陆系统】线程======");
            		LoginAction loginAction=new LoginAction();
            		loginAction.setAccountName(accountName);
            		loginAction.business();
                }
            }
        });
        southPanel.add(loginButton);
        
        JPanel textPanel = new JPanel(new FlowLayout());
        JLabel userBalanceLabel=new JLabel();
        userBalanceLabel.setText("账户余额：");
        JLabel userBalanceValue=new JLabel();
        userBalanceValue.setText("");
        textPanel.add(userBalanceLabel);
        textPanel.add(userBalanceValue);
        
        Bootstrap.userBalanceValue=userBalanceValue;
        
        southPanel.add(textPanel);
        
        contentPane.add(southPanel, BorderLayout.SOUTH);
        
        String loadJs=WindowContentBusinessAdapter.addJS(type+"/bootstrap.js");
        logger.info("init loadJs:"+loadJs);
    	WindowContentBusinessAdapter.jsContent=loadJs;
    	
    	/**
    	 * 添加标签切换监听器
    	 */
    	tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int currentIndex = sourceTabbedPane.getSelectedIndex();
                LoginForm currentAccount=getAccountByTabIndex(currentIndex);
                if(currentAccount!=null)
                {
                	userBalanceValue.setText(String.valueOf(currentAccount.getBalance()));
                }
              }
            });
    }
    
    /**
     * 添加一个账号信息
     * @param account 账号实体
     */
    public static void addAccount(LoginForm account)
    {
    	accounts.add(account);
    	accountMap.put(account.getUsername(),account);
    }
    /**
     * 添加新内容
     * @param tabName 标签页名字
     * @param url 要加载的链接地址
     */
    public static void newContent(String tabName,String url)
    {
    	JWebBrowser webBrowser = new JWebBrowser();
    	webBrowser.setJavascriptEnabled(true);
        webBrowser.setBarsVisible(false);			//页面url地址输入栏是否显示
        webBrowser.setMenuBarVisible(false);
        webBrowser.setStatusBarVisible(true);		//status状态栏
        webBrowser.navigate(url);
        addWebBrowserListener(tabbedPane, tabName,webBrowser);
        tabbedPane.addTab(tabName, webBrowser);
    }
    /**
     * 添加一个新窗口
     * @param tabbedPane 标签页集
     * @param tabName 标签名字
     * @param webBrowser 浏览器实例
     */
    private static void addWebBrowserListener(final JTabbedPane tabbedPane, final String tabName, final JWebBrowser webBrowser)
    {
    	WindowContentBusinessAdapter contentBusinessAdapter=new WindowContentBusinessAdapter();
    	contentBusinessAdapter.setAccountName(tabName);
    	webBrowser.addWebBrowserListener(contentBusinessAdapter);
        accountWebBrower.put(tabName, webBrowser);
    }
    /**
     * 加载内容逻辑
     * @param type 银行类型
     * @param url 加载界面
     */
    public static void loadContents(String type,String url)
    {
    	for(int i=0;i<accounts.size();i++)
    	{
    		LoginForm account=accounts.get(i);
    		try {
				String tabName=account.getUsername();
				newContent(tabName,url);
				//设置account对应的tab页的顺序
				account.setTabIndex(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    /**
     * 登陆调度器
     */
    public static void initLoginMonitor()
    {
    	logger.info("initLoginMonitor，开启 【账户登陆状态监控】调度器======");
		LoginMonitor loginMonitor=new LoginMonitor();
		loginMonitor.execute();
    }
    public static void main(String[] args)
    {
    	/**
    	 * 初始化账号列表
    	 */
    	String accountList=PropertyUtil.getValue("account.list","account-config.properties");
    	String[] users=accountList.split("\\$");
    	for(String userStr:users)
    	{
    		String[] userPair=userStr.split("\\^");
    		LoginForm account=new LoginForm();
    		account.setUsername(userPair[0]);
    		account.setPassword(userPair[1]);
    		addAccount(account);
    	}
    	
    	final String loginUrl="https://mybank.icbc.com.cn/icbc/newperbank/perbank3/frame/frame_index.jsp";
        NativeInterface.open();
        UIUtils.setPreferredLookAndFeel();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
            	initCompnents();
            	frame = new JFrame("工商银行");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(contentPane, BorderLayout.CENTER);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                loadContents(type,loginUrl);
                initLoginMonitor();
            }
        });
        NativeInterface.runEventPump();
    }

}
