package com.ccservice.webbrowser.util;


import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
	  /**
	   * Date类型转换
	   */
	  public static Date parseDate(String time) {
	    /** */
	    /**
	     * 字符串转换为java.util.Date 支持格式�? yyyy.MM.dd G 'at' hh:mm:ss z �?'2002-1-1 AD
	     * at 22:10:59 PSD' yy/MM/dd HH:mm:ss �?'2002/1/1 17:55:00' yy/MM/dd
	     * HH:mm:ss pm �?'2002/1/1 17:55:00 pm' yy-MM-dd HH:mm:ss �?'2002-1-1
	     * 17:55:00' yy-MM-dd HH:mm:ss am �?'2002-1-1 17:55:00 am'
	     * 
	     * @param time String 字符�?
	     * @return Date 日期
	     */
	    try {
	      SimpleDateFormat formatter;

	      int tempPos = time.indexOf("AD");
	      time = time.trim();
	      formatter = new SimpleDateFormat(" yyyy.MM.dd G 'at' hh:mm:ss z ");
	      if (tempPos > -1) {
	        time = time.substring(0, tempPos) + " 公元 " + time.substring(tempPos + " AD ".length()); // china
	        formatter = new SimpleDateFormat(" yyyy.MM.dd G 'at' hh:mm:ss z ");
	      }

	      tempPos = time.indexOf("-");

	      if (time.indexOf(".") != -1) {
	        time = time.substring(0, time.indexOf("."));
	      }

	      if (tempPos > -1) {// 包含�?�?
	        if (time.indexOf(":") == -1) {
	          formatter = new SimpleDateFormat("yyyy-MM-dd");
	        } else if (time.indexOf(":") == time.lastIndexOf(":")) {
	          formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        } else {
	          formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        }
	      } else if (time.indexOf("/") > -1) {// 包含�?�?
	        if (time.indexOf(":") == -1) {
	          formatter = new SimpleDateFormat("yyyy/MM/dd");
	        } else {
	          formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	        }
	      } else if (time.substring(0, 8).indexOf("-") == -1) {
	        time = time.substring(0, 8);
	        formatter = new SimpleDateFormat("yyyyMMdd");
	      } else {
	        formatter = new SimpleDateFormat("HH:mm");
	      }

	      ParsePosition pos = new ParsePosition(0);
	      java.util.Date ctime = formatter.parse(time, pos);
	      return ctime;

	    } catch (Exception ex) {
	      return null;
	    }
	  }
	  
	  /**
	   * Data格式�?
	   */
	  public static String dateFormat(Date date, String pattern) {
	    SimpleDateFormat df = new SimpleDateFormat(pattern);
	    try {
	      return df.format(date);
	    } catch (Exception e) {
	      return "";
	    }
	  }
	  public static Date dateFormatRetDate(Date date, String pattern) {
		    SimpleDateFormat df = new SimpleDateFormat(pattern);
		    try {
		      return parseDate(df.format(date));
		    } catch (Exception e) {
		      e.printStackTrace();
		      return null;
		    }
	  }
	  

	  /**
	   * 得到月初
	   */
	  public static Date getFirstDayOfMonth(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	    return getCurrentDate(cal.getTime());
	  }

	  /**
	   * 得到月末
	   */
	  public static Date getLastDayOfMonth(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	    return getCurrentDate(cal.getTime());
	  }

	  /**
	   * 获取当前日期前几�?
	   */
	  public static Date addDayOfMonth(Date date, int days) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(Calendar.DATE, days);
	    return getCurrentDate(calendar.getTime());
	  } 

	  /**
	   * 处理当前日期去掉小时分钟�?
	   */
	  public static Date getCurrentDate(Date date) {
	    return parseDate(dateFormat(date, "yyyy-MM-dd"));
	  }
	  
	  /**
	   * 1、比较两个日期差多少�?
	   * @param args
	   */

	  public static void main(String[] args){
	    System.out.println(dateFormat(addDayOfMonth(new Date(), 1), "yyyy-MM-dd"));
	  }

	  /**
	   * 将Date时间转成字符�?
	   */
	  public static String DateToStr(String format, Date date) {
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
	    return simpleDateFormat.format(date);
	  }

	  /**
	   * 获取2个字符日期的天数�?
	   * @return 天数�?
	   */
	  public static long getDaysOfTowDiffDate(String p_startDate, String p_endDate) {

	    Date l_startDate = DateUtils.strToDate(DateUtils.YYYY_MM_DD, p_startDate);
	    Date l_endDate = DateUtils.strToDate(DateUtils.YYYY_MM_DD, p_endDate);
	    long l_startTime = l_startDate.getTime();
	    long l_endTime = l_endDate.getTime();
	    long betweenDays = (long) ((l_endTime - l_startTime) / (1000 * 60 * 60 * 24));
	    return betweenDays;
	  }

	  /**
	   * 获取2个Date型日期的分钟数差�?
	   * @param p_startDate
	   * @param p_endDate
	   * @return 分钟数差�?
	   */
	  public static long getMinutesOfTowDiffDate(Date p_startDate, Date p_endDate) {
	    long l_startTime = p_startDate.getTime();
	    long l_endTime = p_endDate.getTime();
	    long betweenMinutes = (long) ((l_endTime - l_startTime) / (1000 * 60));
	    return betweenMinutes;
	  }

	  /**
	   * 获取2个字符日期的天数�?
	   * @param p_startDate
	   * @param p_endDate
	   * @return 天数�?
	   */
	  public static long getDaysOfTowDiffDate(Date l_startDate, Date l_endDate) {

	    long l_startTime = l_startDate.getTime();
	    long l_endTime = l_endDate.getTime();
	    long betweenDays = (long) ((l_endTime - l_startTime) / (1000 * 60 * 60 * 24));
	    return betweenDays;
	  }

	  /**
	   * 给出日期添加�?��时间后的日期
	   * 
	   * @param dateStr
	   * @param plus
	   * @return
	   */
	  public static String getPlusDays(String format, String dateStr, long plus) {

	    Date date = DateUtils.strToDate(format, dateStr);
	    long time = date.getTime() + plus * 24 * 60 * 60 * 1000;

	    return DateUtils.DateToStr(format, new Date(time));
	  }

	  /**
	   * 给出日期添加�?��时间后的日期
	   */
	  public static String getPlusDays(String format, Date date, long plus) {

	    long time = date.getTime() + plus * 24 * 60 * 60 * 1000;

	    return DateUtils.DateToStr(format, new Date(time));
	  }
	/*
	 * 格式化日期函数 字符串转化成日期
	 */
	public static Date strToDate(String str, int type) {
		Date date = null;
		SimpleDateFormat formatk = null;
		if (type == 0) {
			formatk = new SimpleDateFormat("yyMMdd");
		} else if (type == 1) {
			formatk = new SimpleDateFormat("yyyy-MM-dd");
		} else if (type == 2) {
			formatk = new SimpleDateFormat("yyyyMMddHHmmss");
		} else if (type == 4) {
			formatk = new SimpleDateFormat("yyMMddHHmmss");
		} else if (type == 5) {
			formatk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		else if (type == 6) {
			formatk = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		try {
			date = formatk.parse(str);
		} catch (ParseException ex) {
			return new Date(0);
		}

		return date;
	}
	
	public static Date strToDate(String str, String format) {
		Date date = null;
		SimpleDateFormat formatk = new SimpleDateFormat(format);
		try {
			date = formatk.parse(str);
		} catch (ParseException ex) {
			return new Date();
		}
		return date;
	}

	/*
	 * 格式日期函数 日期转换成字符串
	 */
	public static String dateToStr(Date date, int type) {
		if (null == date) return null;
		SimpleDateFormat format;
		if (type == 0) {
			format = new SimpleDateFormat("yyyyMMdd");
		} else if (type == 1) {
			format = new SimpleDateFormat("yyyy-MM-dd");
		} else if (type == 2) {
			format = new SimpleDateFormat("yyyyMMddHHmmss");
		} else if (type == 4) {
			format = new SimpleDateFormat("yyMMddHHmmss");
		} else if (type == 5) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		else if (type == 6) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		else {
			format = new SimpleDateFormat("HH:mm:ss");
		}

		return format.format(date);
	}
	
	public static String dateToStr(Date date, String format) {
		if (null == date) return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static String getStr(Date date, int field, int value, String format) {
		Calendar c = Calendar.getInstance();
		c.setTime(date == null ? new Date() : date);
		c.add(field, value);
		
		return getStr(c.getTime(), format);
	}
	
	public static String getStr(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	  public static String YYYYMMDD = "yyyyMMdd";
	  public static String YYYYMM = "yyyyMM";
	  public static String YYYY_MM = "yyyy-MM";
	  public static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	  public static String YYYYMMDDHHMM = "yyyyMMddHHmm";
	  public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	  public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	  public final static String YYYY_MM_DD = "yyyy-MM-dd";
}
