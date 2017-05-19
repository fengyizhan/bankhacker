package com.ccservice.webbrowser.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public abstract class PageResponseHandler implements ResponseHandler<Page> {

    private Page page;

    public PageResponseHandler(Page page) {
    	this.page = page;
    }

    public void setPage(Page page) {
    	this.page = page;
    }

    public Page getPage() {
    	return page;
    }

    @Override
    public Page handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
		    EntityUtils.consume(entity);
		    throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		if (entity == null)
		{
			return null;
		}
		// 利用HTTPClient自带的EntityUtils把当前HttpResponse中的HttpEntity转化成HTML代码
		String html = EntityUtils.toString(entity);
		page.setHtml(html);
		return page;
    }

}
