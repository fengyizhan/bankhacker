package com.ccservice.webbrowser.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccservice.Util.db.DBHelper;
import com.ccservice.webbrowser.LoginForm;
import com.csvreader.CsvReader;

/**
 * 读取【用户支付账单csv】文件
 * 
 * @author fyz
 *
 */
public class CsvUtil {
	public static int recordStart = 2;
	private static Logger logger = LogManager.getLogger(CsvUtil.class);

	public static void main(String[] args) {
		String path = "C:/gsyh/per_b2cdetail136.csv";
		List<BillDetailData> datas = readCsvFile(path);
		for (int i = 0; i < datas.size(); i++) {
			logger.info("record:" + i + "，data:" + datas.get(i));
		}
		processDatas(datas, null);
	}

	/**
	 * 读csv格式. 开源jar包有opencsv， javacsv, csvObjects(可以装换成对象)，csv, csvjdbc等.
	 * 此处用javacsv
	 * 
	 * @param dir
	 * @return
	 */
	public static List<BillDetailData> readCsvFile(String path) {
		List<BillDetailData> list = new ArrayList<BillDetailData>();
		File csvFile = new File(path);
		if (!csvFile.exists()) {
			return list;
		}
		CsvReader reader = null;
		ArrayList<String[]> csvList = new ArrayList<String[]>(); // 用来保存数据
		try {
			reader = new CsvReader(path, ',', Charset.forName("GBK"));
			// 一般用这编码读就可以了
			reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
			while (reader.readRecord()) { // 逐行读入除表头的数据
				csvList.add(reader.getValues());
			}
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int row = recordStart; row < csvList.size(); row++) {
				String rowDatas[] = csvList.get(row);
				try {
					logger.info("csv reader row start======:" + row);
					BillDetailData data = new BillDetailData();
					for (int column = 0; column < rowDatas.length; column++) {
						String columnData = rowDatas[column];
						if (columnData != null && !"".equals(columnData)) {
							columnData = columnData.replaceAll(",", "").trim();
						}
						logger.info("csv reader column:" + column + ",row:" + row + "，data：" + columnData);// 取得第row行第colum列的数据
						if (column == 0) {
							data.setNumber(columnData);
						}
						if (column == 1) {
							data.setMoney(Double.parseDouble(columnData));
						}
						if (column == 2) {
							data.setUseScore(Long.parseLong(columnData));
						}
						if (column == 3) {
							data.setScoreMoney(Double.parseDouble(columnData));
						}
						if (column == 4) {
							data.setBackMoney(Double.parseDouble(columnData));
						}
						if (column == 5) {
							data.setShopname(columnData);
						}
						if (column == 6) {
							data.setPayDate(columnData);
						}
						if (column == 7) {
							data.setPayType(columnData);
						}
						if (column == 8) {
							data.setPayState(columnData);
						}
					}
					data.setLastUpdateDate(new Date());
					list.add(data);
					logger.info("csv reader row end======:" + row);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("csv reader row exception:" + row, e);
				}finally
				{
					/**
					 * 清理临时文件
					 */
					try {
						FileUtils.deleteQuietly(csvFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return list;
	}

	/**
	 * CSV数据导入数据库
	 * 
	 * @param datas
	 *            数据内容
	 */
	public static void processDatas(List<BillDetailData> datas, LoginForm account) {
		String accountName=account.getUsername();
		if (datas != null && datas.size() > 0) {
			for (int i = 0; i < datas.size(); i++) {
				BillDetailData data = datas.get(i);
				try {
					String order_number = data.getNumber();
					Double order_money = data.getMoney();
					String order_payDate = data.getPayDate();
					Double order_backMoney = data.getBackMoney();
					String order_payState = data.getPayState();
					String order_payType = data.getPayType();
					String order_shopname = data.getShopname();
					Long order_useScore = data.getUseScore();
					Double order_scoreMoney = data.getScoreMoney();
					StringBuffer selectSql = new StringBuffer();
					selectSql.append(" gsyh_billdetail where order_number='" + order_number + "' ").append("\n");
					logger.info(data + ",\n selectSql:" + selectSql.toString());
					int result = DBHelper.getCount(selectSql.toString());
					if (result > 0) {
						/**
						 * 如果记录存在，需要更新操作
						 */
						StringBuffer updateSql = new StringBuffer();
						updateSql.append("  UPDATE gsyh_billdetail SET order_money=" + order_money + ",");
						updateSql.append("  order_useScore=" + order_useScore + ",");
						updateSql.append("  order_scoreMoney=" + order_scoreMoney + ",");
						updateSql.append("  order_backMoney=" + order_backMoney + ",");
						updateSql.append("  order_shopname='" + order_shopname + "',");
						updateSql.append("  order_payDate='" + order_payDate + "',");
						updateSql.append("  order_payType='" + order_payType + "',");
						updateSql.append("  order_payState='" + order_payState + "',");
						updateSql.append("  order_username='" + accountName + "',");
						updateSql.append("  order_lastUpdateDate=getdate() ").append("\n");
						updateSql.append("  where order_number='" + order_number + "' ").append("\n");
						logger.info(data + ",\n updateSql:" + updateSql.toString());
						DBHelper.UpdateData(updateSql.toString());
					} else {
						/**
						 * 如果记录不存在，需要插入操作
						 */
						StringBuffer saveSql = new StringBuffer();
						saveSql.append(
								"  INSERT into gsyh_billdetail (order_number,order_money,order_useScore,order_scoreMoney,order_backMoney,order_shopname,order_payDate,order_payType,order_payState,order_lastUpdateDate,order_username) values('"
										+ order_number + "'," + order_money + "," + order_useScore + ","
										+ order_scoreMoney + "," + order_backMoney + ",'" + order_shopname + "','"
										+ order_payDate + "','" + order_payType + "','" + order_payState
										+ "',getdate(),'"+accountName+"'); ");
						logger.info(data + ",\n saveSql:" + saveSql.toString());
						DBHelper.insertSql(saveSql.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("data:" + data + " 插入异常，请检查！");
				}
			}
		}
		if (account == null) {
			return;
		} else {
			try {
				String username = account.getUsername();
				Double balance = account.getBalance();
				Date lastBalance = account.getLastBalance();
				String lastBalance_str = "";
				try {
					lastBalance_str = DateUtils.dateToStr(lastBalance, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
				boolean timeout = account.getSessionTimeout();
				Date lastTimeout = account.getLastSessionTimeout();
				String lastTimeout_str = "";
				try {
					lastTimeout_str = DateUtils.dateToStr(lastTimeout, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Date lastKeepalived = account.getLastKeepalived();
				String lastKeepalived_str = "";
				try {
					lastKeepalived_str = DateUtils.dateToStr(lastKeepalived, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
				StringBuffer selectSql = new StringBuffer();
				selectSql.append(" gsyh_account where username='" + username + "' ").append("\n");
				logger.info(account + ",\n selectSql:" + selectSql.toString());
				int result = DBHelper.getCount(selectSql.toString());
				if (result > 0) {
					/**
					 * 如果记录存在，需要更新操作
					 */
					StringBuffer updateSql = new StringBuffer();
					updateSql.append("  UPDATE gsyh_account SET balance=" + balance + ",");
					updateSql.append("  lastBalance='" + lastBalance_str + "',");
					updateSql.append("  timeout='" + timeout + "',");
					updateSql.append("  lastTimeout='" + lastTimeout_str + "',");
					updateSql.append("  lastKeepalived='" + lastKeepalived_str + "',");
					updateSql.append("  lastUpdateDate=getdate() ").append("\n");
					updateSql.append("  where username='" + username + "' ").append("\n");
					logger.info(account + ",\n updateSql:" + updateSql.toString());
					DBHelper.UpdateData(updateSql.toString());
				} else {
					/**
					 * 如果记录不存在，需要插入操作
					 */
					StringBuffer saveSql = new StringBuffer();
					saveSql.append(
							"  INSERT into gsyh_account (username,balance,lastBalance,timeout,lastTimeout,lastKeepalived,lastUpdateDate) values('"
									+ username + "'," + balance + ",'"+lastBalance_str + "','" + timeout + "','" + lastTimeout_str + "','"
									+ lastKeepalived_str + "',getdate()); ");
					logger.info(account + ",\n saveSql:" + saveSql.toString());
					DBHelper.insertSql(saveSql.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("account data:" + account + " 插入异常，请检查！");
			}

		}

	}
}