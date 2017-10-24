package com.redant.core.util;

import com.redant.core.constants.CommonConstants;
import com.redant.core.constants.HttpHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gris.wang
 * @create 2017-10-20
 */
public class HttpRenderUtil {

	private final static Logger logger = LoggerFactory.getLogger(HttpRenderUtil.class);

	private static final String EMPTY_CONTENT = "";

	/**
	 * 输出纯Json字符串
	 */
	public static FullHttpResponse renderJSON(byte[] json){
		return render(json, "application/json;charset=UTF-8");
	}
	
	/**
	 * 输出纯字符串
	 */
	public static FullHttpResponse renderText(byte[] text) {
		return render(text, "text/plain;charset=UTF-8");
	}
	
	/**
	 * 输出纯XML
	 */
	public static FullHttpResponse renderXML(byte[] xml) {
		return render(xml, "text/xml;charset=UTF-8");
	}
	
	/**
	 * 输出纯HTML
	 */
	public static FullHttpResponse renderHTML(byte[] html) {
		return render(html, "text/html;charset=UTF-8");
	}

	public static FullHttpResponse getNotFoundResponse(){
		String content = getPageContent(CommonConstants.BASE_VIEW_PATH+"404.vm",null);
		return render(getBytes(content), "text/html;charset=UTF-8");
	}

	public static FullHttpResponse getServerErrorResponse(){
		String content = getPageContent(CommonConstants.BASE_VIEW_PATH+"500.vm",null);
		return render(getBytes(content), "text/html;charset=UTF-8");
	}

	public static FullHttpResponse getErrorResponse(String errorMessage){
		Map<String,Object> contentMap = new HashMap<String,Object>(1);
		contentMap.put("errorMessage",errorMessage);
		String content = getPageContent(CommonConstants.BASE_VIEW_PATH+"error.vm",contentMap);
		return render(getBytes(content), "text/html;charset=UTF-8");
	}

	public static String getPageContent(String templateName,Map<String, Object> contentMap){
		try {
			return VelocityUtil.parse(templateName,contentMap);
		} catch (Exception e) {
			logger.error("getPageContent Error,cause:",e);
		}
		return CommonConstants.SERVER_INTERNAL_ERROR_DESC;
	}

	public static byte[] getBytes(Object content){
		if(content==null){
			return EMPTY_CONTENT.getBytes(CharsetUtil.UTF_8);
		}
		String data = content.toString();
		data = StringUtils.isBlank(data)?EMPTY_CONTENT:data;
		return data.getBytes(CharsetUtil.UTF_8);
	}

	/**
	 * response输出
	 * @param bytes
	 * @param contentType
	 */
	public static FullHttpResponse render(byte[] bytes, String contentType){
		if(bytes == null){
			bytes = HttpRenderUtil.getBytes(EMPTY_CONTENT);
		}
		ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
		response.headers().add(HttpHeaders.CONTENT_TYPE, contentType);
		response.headers().add(HttpHeaders.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

}