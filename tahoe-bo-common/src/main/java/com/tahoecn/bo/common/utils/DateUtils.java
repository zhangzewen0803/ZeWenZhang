package com.tahoecn.bo.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;

/**
 * 类名称：DateUtils 
 * 创建人：lee
 * @version 1.0.0
 */
public class DateUtils {

	private static final Log logger = LogFactory.get();

	/** 定义常量 **/
	public static final String DATE_JFP_STR = "yyyyMM";
	public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_SMALL_STR = "yyyy-MM-dd";
	public static final String DATE_KEY_STR = "yyMMddHHmmss";

	public static final String DATE_FORMAT_TEXT = "yyyy年MM月dd日";
	public static final String DATE_FORMAT_TIME_TEXT = DATE_FORMAT_TEXT + " HH:mm";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static String timePattern = DATE_FORMAT + " HH:mm:ss";
	public static String TIME_FORMAT = "HH:mm:ss";

	/**
	 * 两个时间比较
	 */
	public static int compareDateWithNow(Date date1) {
		Date date2 = new Date();
		int rnum = date1.compareTo(date2);
		return rnum;
	}

	/**
	 * 两个时间比较(时间戳比较)
	 */
	public static int compareDateWithNow(long date1) {
		long date2 = dateToUnixTimestamp();
		if (date1 > date2) {
			return 1;
		} else if (date1 < date2) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 将日期转成年月日 时分秒格式的字符串
	 */
	public static final String convertDateTimeToString(Date aDate) {
		return getDateTime(timePattern, aDate);
	}

	/**
	 * 将日期转为系统格式的（年月日）日期字符串
	 */
	public static final String convertDateToString(Date aDate) {
		return getDateTime(DATE_FORMAT, aDate);
	}
	
	/**
	 * 把日期转换为字符串
	 */
	public static String dateToString(Date date, String format) {
		String result = "";
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			result = formater.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 在当前日期之上增减,例如 addDate(3,Calendar.MONTH)表示当前时间加3个月,第一个参数为负数则表示减法操作
	 */
	public static Date currentDateAddDate(int pCount, int pField) {
		// 年，月，日、时，分，秒
		Date startDate = getCurrentDateByFormat(DATE_FORMAT); // 先获取当前日期
		int year = getYearOfDate(startDate);
		int month = getMonthOfDate(startDate) - 1;
		int day = getDayOfDate(startDate);
		int hour = getHourOfDate(startDate);
		int minute = getMinuteOfDate(startDate);
		int second = getSecondOfDate(startDate);
		Calendar calendar = new GregorianCalendar(year, month, day, hour, minute, second);
		calendar.add(pField, pCount);
		return calendar.getTime();
	}

	public static Date currentDateTimeAddDate(int pcount, int pfield) throws ParseException {
		// 年，月，日、时，分，秒
		Date startDate = getNowTimeDate(timePattern); // 先获取当前日期
		int year = getYearOfDate(startDate);
		int month = getMonthOfDate(startDate) - 1;
		int day = getDayOfDate(startDate);
		int hour = getHourOfDate(startDate);
		int minute = getMinuteOfDate(startDate);
		int second = getSecondOfDate(startDate);
		Calendar calendar = new GregorianCalendar(year, month, day, hour, minute, second);
		calendar.add(pfield, pcount);
		return calendar.getTime();
	}

	/**
	 * 将当前日期转换成Unix时间戳
	 * @return long 时间戳
	 */
	public static long dateToUnixTimestamp() {
		long timestamp = System.currentTimeMillis();
		return timestamp;
	}

	/**
	 * 将指定的日期转换成Unix时间戳
	 * @param String date 需要转换的日期 yyyy-MM-dd HH:mm:ss
	 * @return long 时间戳
	 */
	public static long dateToUnixTimestamp(String date) {
		long timestamp = 0;
		try {
			timestamp = new SimpleDateFormat(DATE_FULL_STR).parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * 将指定的日期转换成Unix时间戳
	 * @param String  date 需要转换的日期 yyyy-MM-dd
	 * @return long 时间戳
	 */
	public static long dateToUnixTimestamp(String date, String dateFormat) {
		long timestamp = 0;
		try {
			timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * 获取当前系统时间
	 * @param  format
	 * @return util时间格式
	 */
	public static Date getCurrentDateByFormat(String  format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat( format);
		String dateStr = sdf.format(d).substring(0, 10);
		Date dd = null;
		try {
			dd = toUtilDateFromStrDateByFormat(dateStr,  format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dd;
	}

	/**
	 * 将日期转为指定格式的字符串
	 * 
	 * @Methods Name getDateTime
	 * @param aMask 日期格式
	 * @param aDate 日期
	 * @return String
	 */
	public static final String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";
		if (aDate != null) {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}
		return (returnValue);
	}

	/**
	 * 获取指定日期的日份
	 * @param  date 日期
	 * @return int 日份
	 */
	public static int getDayOfDate(Date  date) {
		Calendar c = Calendar.getInstance();
		c.setTime( date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取指定日期的小时
	 * 
	 * @param  date util.Date日期
	 * @return int 小时
	 */
	public static int getHourOfDate(Date  date) {
		Calendar c = Calendar.getInstance();
		c.setTime( date);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取系统当前计费期
	 * 
	 * @return
	 */
	public static String getJFPTime() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_JFP_STR);
		return df.format(new Date());
	}

	/**
	 * 获取指定日期的分钟
	 * 
	 * @param  date
	 *            util.Date日期
	 * @return int 分钟
	 */
	public static int getMinuteOfDate(Date  date) {
		Calendar c = Calendar.getInstance();
		c.setTime( date);
		return c.get(Calendar.MINUTE);
	}

	/**
	 * 获取某一日期前后几个月的第一天
	 * 
	 * @param baseDateStr
	 *            计算基础日期字符串(yyyy-MM-dd)
	 * 
	 * @param modifyCount
	 *            0当前月,1下一个月,-1上一个月
	 * @return 某个月的第一天
	 * @author lijing 2015年10月13日
	 */
	public static Date getMonthFirstDay(String baseDateStr, int modifyCount) {
		Date baseDate = null;

		try {
			baseDate = toUtilDateFromStrDateByFormat(baseDateStr, DATE_FORMAT);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseDate);
		calendar.add(Calendar.MONTH, modifyCount);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

		return calendar.getTime();
	}

	/**
	 * 获取某个日期前后几个月的最后一天
	 * 
	 * @param baseDateStr
	 *            计算基础日期字符串(yyyy-MM-dd)
	 * 
	 * @param modifyCount
	 *            0当前月,1下一个月,-1上一个月
	 * @return 某个月的最后一天
	 * @author lijing 2015年10月13日
	 *
	 */
	public static Date getMonthLastDay(String baseDateStr, int modifyCount) {
		Date baseDate = null;

		try {
			baseDate = toUtilDateFromStrDateByFormat(baseDateStr, DATE_FORMAT);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseDate);
		calendar.add(Calendar.MONTH, modifyCount);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		return calendar.getTime();
	}

	/**
	 * 获取指定日期的月份
	 * 
	 * @param  date
	 *            util.Date日期
	 * @return int 月份
	 */
	public static int getMonthOfDate(Date  date) {
		Calendar c = Calendar.getInstance();
		c.setTime( date);
		return c.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getNowTime() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FULL_STR);
		return df.format(new Date());
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getNowTime(String type) {
		SimpleDateFormat df = new SimpleDateFormat(type);
		return df.format(new Date());
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @param  format
	 *            时间格式
	 * @return Date
	 * @throws ParseException
	 */
	public static Date getNowTimeDate(String  format) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat( format);// 设置日期格式
		String time = df.format(new Date());// new Date()为获取当前系统时间
		return toUtilDateFromStrDateByFormat(time,  format);

	}

	/**
	 * 获取系统当前时间
	 * 
	 * @param  format
	 *            时间格式
	 * @return
	 */
	public static String getNowTimeString(String  format) {
		SimpleDateFormat df = new SimpleDateFormat( format);// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}


	/**
	 * 获取指定日期的秒钟
	 * 
	 * @param  date
	 *            util.Date日期
	 * @return int 秒钟
	 */
	public static int getSecondOfDate(Date  date) {
		Calendar c = Calendar.getInstance();
		c.setTime( date);
		return c.get(Calendar.SECOND);
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @param  format
	 * @return 字符串类型
	 */
	public static String getSystemOfDateByFormat(String  format) {
		long time = System.currentTimeMillis();
		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat( format);
		String dateStr = sdf.format(d);
		return dateStr;
	}

	/**
	 * 
	 * 方法说明：获取指定月份的工作日天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 * @author wangchaojie 2015年10月13日
	 *
	 */
	public static int getWorkDayByMonth(int year, int month) {
		List<Date> dates = new ArrayList<Date>();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, 1);

		while (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) < month) {
			int day = cal.get(Calendar.DAY_OF_WEEK);

			if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
				dates.add((Date) cal.getTime().clone());
			}
			cal.add(Calendar.DATE, 1);
		}
		return dates.size();
	}

	/**
	 * 获取指定日期的年份
	 * 
	 * @param  date
	 *            util.Date日期
	 * @return int 年份
	 */
	public static int getYearOfDate(Date  date) {
		Calendar c = Calendar.getInstance();
		c.setTime( date);
		return c.get(Calendar.YEAR);
	}

	/**
	 * 使用预设格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @return
	 */
	public static Date parse(String strDate) {
		return parse(strDate, DATE_FULL_STR);
	}

	/**
	 * 使用用户格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static Date parse(String strDate, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static java.sql.Date toSqlDateFromStrDate(String  strDate) throws ParseException {
		java.sql.Date returnDate = null;
		java.text.DateFormat sdf = new java.text.SimpleDateFormat();
		if ( strDate != null && (!"".equals( strDate))) {
			returnDate = new java.sql.Date(sdf.parse( strDate).getTime());
		}
		return returnDate;
	}

	/**
	 * util.Date型日期转化指定格式的字符串型日期
	 */
	public static String toStrDateFromUtilDateByFormat(Date  utilDate, String  format) {
		String  result = "";
		if ( utilDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat( format);
			 result = sdf.format( utilDate);
		}
		return  result;
	}

	/**
	 * 字符型日期转化util.Date型日期
	 * 
	 * @Param: strDate 字符型日期
	 * @param  format
	 *            格式:"yyyy-MM-dd" / "yyyy-MM-dd hh:mm:ss"
	 * @Return:Date util.Date型日期
	 * @Throws: ParseException
	 */
	public static Date toUtilDateFromStrDateByFormat(String  strDate, String  format) throws ParseException {
		Date  date = null;
		java.text.DateFormat df = new java.text.SimpleDateFormat( format);
		if ( strDate != null && (!"".equals( strDate)) &&  format != null && (!"".equals( format))) {
			 date = df.parse( strDate);
		}
		return  date;
	}

	public static String toStrDateFromUtilDateByFormat(String  strDate,String  format) {
		String d_result = "";
		if ( strDate != null && (!"".equals( strDate)) &&  format != null && (!"".equals( format))) {
			try {
				Date date = toUtilDateFromStrDateByFormat( strDate, format);
				d_result = toStrDateFromUtilDateByFormat(date, format);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return d_result;
	}
	
	/**
	 * 将Unix时间戳转换成日期
	 * 
	 * @param long
	 *            timestamp 时间戳
	 * @return String 日期字符串
	 */
	public static String unixTimestampToDate(long timestamp) {
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FULL_STR);
		sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return sd.format(new Date(timestamp));
	}

	/**
	 * 获取精确到秒的时间戳
	 * 
	 * @return
	 */
	public static int getSecondTimestamp(Date date) {
		if (null == date) {
			return 0;
		}
		String timestamp = String.valueOf(date.getTime());
		int length = timestamp.length();
		if (length > 3) {
			return Integer.valueOf(timestamp.substring(0, length - 3));
		} else {
			return 0;
		}
	}
	
	/** 
     * 当前季度的开始时间，即2012-01-1 00:00:00 
     * @return 
     */  
    public static Date getCurrentQuarterStartTime() {  
        Calendar c = Calendar.getInstance();  
        int currentMonth = c.get(Calendar.MONTH) + 1;  
        Date now = null;  
        try {  
            if (currentMonth >= 1 && currentMonth <= 3) {
            	c.set(Calendar.MONTH, 0);  
            }  else if (currentMonth >= 4 && currentMonth <= 6)  {
            	c.set(Calendar.MONTH, 3);  
            }else if (currentMonth >= 7 && currentMonth <= 9) {
            	c.set(Calendar.MONTH, 6);
            }else if (currentMonth >= 10 && currentMonth <= 12) {
            	c.set(Calendar.MONTH, 9);  
            }  
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1); 
            now = c.getTime();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return now;  
    } 
	
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        //指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
         SimpleDateFormat format = new SimpleDateFormat(DATE_SMALL_STR);
         try {
        	 //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
         } catch (ParseException e) {
        	 // e.printStackTrace();
        	 // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
             convertSuccess=false;
         } 
         return convertSuccess;
  }
    
    /** 
     * 时间戳转日期 
     * @param ms 
     * @return 
     */  
    public static Date transForDate(Integer ms){  
        if(ms==null){  
            ms=0;  
        }  
        long msl=(long)ms*1000;  
        SimpleDateFormat sdf=new SimpleDateFormat(DATE_FULL_STR);
        Date date=null;
        if(ms!=null){  
            try {  
                 date = sdf.parse(sdf.format(msl));
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return date;  
    }
    
	/**
	 * 将日期字符串转换为LocalDateTime对象
	 * @param dateStr 如：2019-06-05
	 * @param dateTimeFormatter
	 * @return
	 */
    public static LocalDateTime parseDate(String dateStr, DateTimeFormatter dateTimeFormatter){
		try {
			return LocalDateTime.of(LocalDate.parse(dateStr,dateTimeFormatter), LocalTime.MIN);
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}
    
/*	public static void main(String[] args) throws ParseException {
//		String baseDate = "2018-08-02";
//		System.out.println(baseDate+"上个最后一天："+getDateTime("MM/dd",getMonthLastDay(baseDate,-1)));
		
		System.out.println(getDateTime("MM/dd",getCurrentQuarterStartTime()));
		System.out.println(toStrDateFromUtilDateByFormat(toUtilDateFromStrDateByFormat("2018-10-10 10:22:00","yyyy-MM-dd"),"yyyy-MM-dd"));
	}*/
}
