package com.ccservice.webbrowser;
/**
 * 系统公共配置类
 * @author fyz
 *
 */
public class Constants {
	/**
	 * 由于有多标签页，后台【工作线程】每次业务逻辑执行后的休眠时间，为了能够降低‘并发可能性’，引入随机因子
	 */
	public static int randomSeed=20;
	/**
	 * 所有线程的总控制开关
	 */
	public static boolean run=true;
	/**
	 * 登陆状态检查时间间隔（秒）
	 */
	public static long logincheckThreadInterval=20;
	
	/**
	 * 保持会话不退出的心跳时间间隔（秒）
	 */
	public static long heatbeat=60;
	/**
	 * 抓取【余额信息】工作间隔休眠时间（秒）
	 */
	public static long balanceThreadInterval=30;
	/**
	 * 抓取【账单详情】工作间隔休眠时间（秒）
	 */
	public static long billDetailThreadInterval=100;
	/**
	 * 扫描【账单详情】时时间向前跨度，截止时间为今天，向前推30
	 */
	public static int billDetailBackwardTimePeriod=32;
	/**
	 * 工商银行业务类型 加载
	 */
	public static String TYPE_GSYH="gsyh";
	/**
	 * url请求默认超时时间
	 */
	public static int request_timeout=5000;
	public static String damaImageType=".jpg";
	public static int damaRetryTimes=2;
	/**
	 * 打码图片下载主目录
	 * 下面的是按照账号名称归类
	 */
	public static String damaRootDir="C:/"+TYPE_GSYH+"_dama/";
	/**
	 * 账单详情下载主目录
	 * 下面的是按照账号名称归类
	 */
	public static String billDetailRootDir="C:/"+TYPE_GSYH+"_billdetail/";
	/**
	 * 账单文件类型
	 */
	public static String billDetailFileType=".csv";
	/**
	 * 工商银行header中的 USER_AGENT 特别设定
	 */
	public static String USER_AGENT="Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E;  QIHU 360SE)";
}
