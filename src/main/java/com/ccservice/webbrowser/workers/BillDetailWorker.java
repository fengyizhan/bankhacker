package com.ccservice.webbrowser.workers;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ccservice.webbrowser.Bootstrap;
import com.ccservice.webbrowser.Constants;
import com.ccservice.webbrowser.LoginForm;
import com.ccservice.webbrowser.WindowContentBusinessAdapter;
import com.ccservice.webbrowser.util.BillDetailData;
import com.ccservice.webbrowser.util.CsvUtil;
import com.ccservice.webbrowser.util.DateUtils;
import com.ccservice.webbrowser.util.HttpResourceUtil;
import com.ccservice.webbrowser.util.Page;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * 【获取账单】线程
 * @author fyz
 *
 */
public class BillDetailWorker extends SwingWorker<Void,Void>{
	private static Logger logger=LogManager.getLogger(BillDetailWorker.class);
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
		logger.info("BillDetailWorker start work accountName:"+accountName);
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
			 * 获取账单详情
			 */
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getBillDetail(wb,account);
				}
			});
			
		}catch(Exception e)
		{
			logger.error("BillDetailWorker Exception:",e);
			e.printStackTrace();
		}
		logger.info("BillDetailWorker finished work accountName:"+accountName);
	}
	/**
	 * 获取账单详情
	 * @param wb 浏览器控件
	 * @param account 账号
	 */
	public void getBillDetail(JWebBrowser wb,LoginForm account)
	{
		/**
		 * 只有在会话没有超时的情况下，才需要进行定期【会话保持】
		 * 如果已经弹出登陆页，即使进行【会话保持】也没有任何意义了
		 */
		boolean needToDoWork=(account.getKeepalived() && (!account.getSessionTimeout()));
		if(!needToDoWork){return;}
		Date fetchDate=new Date();
		String billDetailFileName=DateUtils.dateToStr(fetchDate, 5);
		/**
		 * 【获取账单】业务处理
		 */
		try
		{
			/**
			 * 关键【会话标示】
			 */
			String dse_sessionid=account.getDse_sessionId();
			if(dse_sessionid!=null&&!"".equals(dse_sessionid))
			{
				/**
				 * 因为目标【下载url】包含有动态令牌，所以，每次需要先获取最新的【动态令牌】
				 */
				String Epay_detail_url="";
				String pageHtml="";
				try {
					Epay_detail_url = "https://mybank.icbc.com.cn/icbc/newperbank/Epay/detail/Epay_detail.jsp?pageMark=2&dse_sessionId="+dse_sessionid;
					logger.info("（1）加载个人支付明细页面 Epay_detail url:"+Epay_detail_url);
					Page page=new Page();
					page.setCharset("GBK");
					page.setUrl(Epay_detail_url);
					HttpResourceUtil.getPage(page,Constants.request_timeout);
					pageHtml = page.getHtml();
					logger.info("pageHTML:"+pageHtml);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.info("（1）加载个人支付明细页面 Epay_detail url 发生异常:"+Epay_detail_url+" ，pageHtml:"+pageHtml);
					return;
				}
				
				//获取动态令牌
				String requestTokenid="";
				try	{
					logger.info("（2）从页面中获取动态令牌 Epay_detail Jsoup to fetch requestTokenid:"+Epay_detail_url);
					Document firstPageDoc=Jsoup.parse(pageHtml);
					Element requestTokenid_ele=firstPageDoc.select("input[name=requestTokenid]").first();
					requestTokenid=requestTokenid_ele.val();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				//文件下载路径
				String downloadPath="";
				try {
					Date endDate=new Date();
					/**
					 * 下载时间，按照当前时间戳作为文件名称
					 */
					String billDetail_downloadTime=DateUtils.dateToStr(endDate,5).replaceAll(" ","_").replaceAll(":","_");
					Date beginDate=DateUtils.addDayOfMonth(endDate,-Constants.billDetailBackwardTimePeriod);
					String beginDate_str=DateUtils.dateToStr(beginDate, 1);
					String endDate_str=DateUtils.dateToStr(endDate, 1);
					String billDetail_downloadPage="https://mybank.icbc.com.cn/servlet/ICBCINBSReqServlet?dse_sessionId="+dse_sessionid+"&requestTokenid="+requestTokenid+"&styFlag=1&Begin_pos=&Tran_flag=2&begDate="+beginDate_str+"&endDate="+endDate_str+"&dse_operationName=per_AccountQueryEpayDetailOp&queryType=0";
					logger.info("（3）生成账单下载页面 billDetail_downloadPage url:"+billDetail_downloadPage);
					Page downloadPage=new Page();
					downloadPage.setUrl(billDetail_downloadPage);
					downloadPage.setDownload(true);
					downloadPath=Constants.billDetailRootDir+accountName+"/"+billDetail_downloadTime+Constants.billDetailFileType;
					downloadPage.setDownloadPath(downloadPath);
					HttpResourceUtil.getPage(downloadPage,Constants.request_timeout);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				try
				{
					logger.info("（4）从生成的账单文件CSV downloadPath:"+downloadPath+"开始导入到数据库中");
					List<BillDetailData> billDetailData=CsvUtil.readCsvFile(downloadPath);
					logger.info("账单文件CSV downloadPath:"+downloadPath+"导入到数据库记录条数："+billDetailData==null?0:billDetailData.size());
					CsvUtil.processDatas(billDetailData,account);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		account.setLastBillDetail(fetchDate);
		account.setBillDetailFileName(billDetailFileName);
		logger.info("accountName:"+accountName+"，billDetailFileName:"+billDetailFileName);
	}
	@Override
	protected void process(List chunks) {
		super.process(chunks);
	}
	@Override
	protected void done() {
		super.done();
		logger.info("BillDetailWorker accountName:"+accountName+"，done:");
	}
	@Override
	protected Void doInBackground() throws Exception {
		while(Constants.run)
		{
			business();
			try {
				Random randomSleepSeed = new Random();
				Thread.sleep((randomSleepSeed.nextInt(Constants.randomSeed)+Constants.billDetailThreadInterval)*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
