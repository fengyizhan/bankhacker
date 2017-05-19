package com.ccservice.webbrowser.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.webbrowser.Constants;

/**
 * 页面解析工具
 * @author fyz
 *
 */
public class HttpResourceUtil {
	private static Logger logger=LogManager.getLogger(HttpResourceUtil.class);
	
	/**
	 * 加载指定页面
	 * @param page 页面内容
	 * @return 页面结果
	 */
	public static void getPage(Page page,int timeout)
	{
		/**
		 * 是否需要下载
		 */
		boolean isDownload=page.isDownload();
		String url=page.getUrl();
		/**
		 * 下载保存路径
		 */
		String downloadPath=page.getDownloadPath();
		String charset=page.getCharset();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", Constants.USER_AGENT);
		httpGet.addHeader("Connection", "Keep-Alive");
		httpGet.addHeader("Host", "mybank.icbc.com.cn");
//		RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(timeout)
//                .setConnectTimeout(timeout)
//                .setConnectionRequestTimeout(timeout)
//                .build();
//		httpGet.setConfig(requestConfig);
		String cookie=page.getCookie();
		if(cookie!=null)
		{
			httpGet.addHeader("Cookie", cookie);
		}
		CloseableHttpResponse httpResponse=null;
		try {
			httpResponse = httpClient.execute(httpGet);
			logger.info(url+" GET Response Status:: " + httpResponse.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity httpEntity=httpResponse.getEntity();
		InputStream responseInputStream=null;
		try {
			responseInputStream=httpEntity.getContent();
			if(isDownload)
			{
				File downloadSaveFile=new File(downloadPath);
				FileUtils.copyInputStreamToFile(responseInputStream,downloadSaveFile);
				logger.info("from url:"+ url+"， download File:"+downloadPath);
				try {
					IOUtils.closeQuietly(responseInputStream);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		} catch (UnsupportedOperationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader=null;
		try
		{
			/**
			 * 如果不指定编码，会智能选择
			 */
			String getHtml="";
			if(charset!=null)
			{
				getHtml=EntityUtils.toString(httpEntity,charset);
			}
			else
			{
//				getHtml=EntityUtils.toString(httpEntity);
				reader = new BufferedReader(new InputStreamReader(responseInputStream));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = reader.readLine()) != null) {
					response.append(inputLine).append("\n");
				}
				getHtml=response.toString();
			} 
				
			page.setHtml(getHtml);
			logger.info("from url:"+ url+"， load page html");
		} catch (UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}
		try {
				IOUtils.closeQuietly(reader);
			} catch (Exception e) {
				e.printStackTrace();
		}
		try {
			IOUtils.closeQuietly(responseInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 * @param fileName
	 * @return
	 */
	public static boolean downloadFromUrl(String url, String fileName) {
		boolean result = false;
		try {
			URL httpurl = new URL(url);
			File f = new File(fileName);
			FileUtils.copyURLToFile(httpurl, f);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}

	/**
	 * 
	 * @param urlPath
	 *            下载路径
	 * @param downloadDir
	 *            下载存放目录
	 * @return 返回下载文件
	 */
	public static boolean downloadFile(String urlPath, String fileName) {
		boolean result = false;
		File file = null;
		try {
			// 统一资源
			URL url = new URL(urlPath);
			// 连接类的父类，抽象类
			URLConnection urlConnection = url.openConnection();
			// http的连接类
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
			// 设定请求的方法，默认是GET
			httpURLConnection.setRequestMethod("POST");
			// 设置字符编码
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			// 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
			httpURLConnection.connect();

			// 文件大小
			int fileLength = httpURLConnection.getContentLength();

			// 文件名
			String filePathUrl = httpURLConnection.getURL().getFile();
			String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);

			System.out.println("file length---->" + fileLength);

			URLConnection con = url.openConnection();

			BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());

			file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			OutputStream out = new FileOutputStream(file);
			int size = 0;
			int len = 0;
			byte[] buf = new byte[1024];
			while ((size = bin.read(buf)) != -1) {
				len += size;
				out.write(buf, 0, size);
				// 打印下载百分比
				// System.out.println("下载了-------> " + len * 100 / fileLength +
				// "%\n");
			}
			bin.close();
			out.close();
			result = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	public static void main(String[] args) {
		// 下载文件测试
//		downloadFile("http://localhost:8080/images/1467523487190.png", "/Users/H__D/Desktop");
		Page page=new Page();
		page.setCharset("UTF-8");
		page.setUrl("https://mybank.icbc.com.cn/icbc/newperbank/Epay/detail/Epay_detail.jsp?pageMark=2&dse_sessionId=JIBMAVJUFVFWCDAOHAJCBWAUDQAQHUFTECFOJFBE");
		getPage(page,5000);
		System.out.println(page.getHtml());
	}

}
