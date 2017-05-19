package com.ccservice.webbrowser.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 页面信息
 * @author fyz
 */
public class Page {
	/**
	 * POST、GET
	 */
	private String method;
	/**
	 * URL地址
	 */
	private String url;
	/**
	 * 文件编码
	 */
	private String charset;
	/**
	 * cookie
	 */
	private String cookie;
	/**
	 * 参数列表
	 */
	private Map<String,Object> params=new HashMap<String,Object>();
	/**
	 * 内容类型如：text/html
	 */
	private String contentType;
	/**
	 * 是否下载
	 */
	private boolean isDownload=false;
	/**
	 * 下载保存路径
	 */
	private String downloadPath;
	public String getDownloadPath() {
		return downloadPath;
	}
	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
	/**
	 * 内容流对象
	 */
	private InputStream inputStream;
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public boolean isDownload() {
		return isDownload;
	}
	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
	private String html;
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
}
