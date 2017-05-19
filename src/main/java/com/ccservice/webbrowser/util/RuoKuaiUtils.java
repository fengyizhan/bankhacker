package com.ccservice.webbrowser.util;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 自定义打码工具类
 * 
 * @author fyz
 */
public class RuoKuaiUtils {
	private static Logger logger = LogManager.getLogger(RuoKuaiUtils.class);
	static String username = "hyccservicecom";// 用户名
	static String password = "HANGtian126";// 密码
	static String typeid = "3040";// 验证码类型， 3040 (全是4位的)
	static String softid = "42778";// 打码软件ID
	static String softkey = "7b4b50bf31434907b6829398fcbd5136";// 打码软件密码

	/**
	 * 本地图片路径
	 * 
	 * @param imagePath
	 *            图片路径
	 */
	public static DaMaResult dama(String imagePath) {
		File imageFile = null;
		DaMaResult dmr=new DaMaResult();
		try {
			imageFile = new File(imagePath);
			if (imageFile.exists()) 
			{
				long fileSize = imageFile.length();
				if (fileSize <= 0)
				{
					logger.error("imagePath:"+imagePath+",dama fileSize:"+fileSize);
					return dmr;
				}
			}else
			{
				logger.error("imagePath:"+imagePath+"，文件不存在！");
				return dmr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("imagePath:"+imagePath+"，文件加载异常！");
			return dmr;
		}
		String dmrId="";
		String dmrResult="";
		try 
		{
			RuoKuaiDaMaApi ruokuaidamaapi = new RuoKuaiDaMaApi(username, password, typeid, softid, softkey);
			dmr = ruokuaidamaapi.localPrint(imagePath);
			logger.info("imagePath:"+imagePath+",dama result:"+dmrResult);
			if("-1".equals(dmrId))
			{
				ruokuaidamaapi.error(dmrId);
				dmr.setSuccess(false);
			}
			dmr.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("imagePath:"+imagePath+",dama result:"+dmrResult);
			dmr.setSuccess(false);
		}
		return dmr;
	}

	public static void main(String[] args) {
		DaMaResult result=dama("C:/a.jpg");
	}
}
