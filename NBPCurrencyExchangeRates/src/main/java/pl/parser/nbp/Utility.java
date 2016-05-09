package pl.parser.nbp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;


public class Utility {

	
	final static int CODE_LENGTH = 11;
	
	public static List<String> getAdresses(String startDate, String endDate) throws ParseException, IOException {
		
		
		int yearRange = getYearRange(startDate, endDate);
		int startYear = getYear(startDate);
		String dirsContent = getNbpDirs(startYear, yearRange);
		
		String newFormatOneDayBeforeStartDay = Parser.parseDate(getPreviousDay(startDate, 1));
		String newFormatTwoDaysBeforeStartDay = Parser.parseDate(getPreviousDay(startDate, 2));
		String newFormatStartDate = Parser.parseDate(startDate);
		
		String newFormatOneDayBeforeEndDay = Parser.parseDate(getPreviousDay(endDate, 1));
		String newFormatTwoDaysBeforeEndDay = Parser.parseDate(getPreviousDay(endDate, 2));
		String newFormatEndDate = Parser.parseDate(endDate);
		
		List<String> listWithCodes = new ArrayList<>();
		
		String s1 = "(" + newFormatStartDate + "|" + newFormatOneDayBeforeStartDay + "|" + newFormatTwoDaysBeforeStartDay + ")";
		String s2 = "(" + newFormatEndDate + "|" + newFormatOneDayBeforeEndDay + "|" + newFormatTwoDaysBeforeEndDay + ")";
		String regex = "c.{3}z" + s1 + ".*c.{3}z" + s2;
	    
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher =  pattern.matcher(dirsContent);
		 
		String codes = ""; 
		while(matcher.find())
		{
			codes = matcher.group();
		}
		for(int i = 0; i < codes.length() / CODE_LENGTH; i++)
			listWithCodes.add(codes.substring(i * CODE_LENGTH, (i+1) * CODE_LENGTH));
		 
		return listWithCodes;
	}
	
	public static String getNbpDirs(int year, int yearRange) throws IOException {
		
		URL url;
		Scanner scanner = null;
		String nbpUrl = null;
		String allDirs = null;
		for(int i = 0; i <= yearRange; i++, year++) {
			if(year == 2016)
				nbpUrl = "http://www.nbp.pl/kursy/xml/dir.txt";
			else
				nbpUrl = "http://www.nbp.pl/kursy/xml/dir" + year + ".txt";
			
			url = new URL(nbpUrl);
			scanner = new Scanner(url.openStream(), "UTF-8");
			StringBuilder sb = new StringBuilder("");
		    while(scanner.hasNextLine()) {
		    	String s = scanner.nextLine();
		    	if(s.startsWith("c")) {
		    		sb.append(s);
		    	}
		    }
		    if(year == 2015 || year == 2016)
		    	allDirs += sb.replace(0, 1, "").toString();
		    else
		    	allDirs += sb.toString(); // delete unwanted character "?", only in 2015 and 2016
		}
	    return allDirs;
	}
	
	public static void downloadXmlFilesFromParticularDays(List<String> codes) throws IOException {
		
		URL url;
		Scanner scanner = null;
		String urlToParticularDay = null;
		for(int i = 0; i < codes.size(); i++) {
			urlToParticularDay = "http://www.nbp.pl/kursy/xml/" + codes.get(i) + ".xml";
			url = new URL(urlToParticularDay);
			scanner = new Scanner(url.openStream(), "UTF-8");
			StringBuilder sb = new StringBuilder("");
		    while(scanner.hasNextLine()) {
		    	String s = scanner.nextLine();
		    	sb.append(s);
		    	sb.append("\r\n");
		    }
		    
		    PrintWriter writer;
			writer = new PrintWriter(codes.get(i), "UTF-8");
			writer.print(sb.toString());
			writer.close();
		    
		}
	}
	
	public static void deleteXmlFiles(List<String> codes) {
		for(String code : codes) {
			File xmlFile = new File(code);
			xmlFile.delete();
		}
		
	}
	
	public String getFridayIfWeekend(String date) {
		
		String tab[] = new String[2];
		if(date.startsWith("0"))
			date = "1" + date;
		int dateAsInteger= Integer.parseInt(date);
		
		for(int i = 0; i < tab.length; i++) {
			dateAsInteger--;
			tab[i] = "" + dateAsInteger;
			if(tab[i].length() > 6)
				tab[i] = tab[i].substring(1, tab[i].length());
		}
		
		return tab[0]+"|"+tab[1];
	}
	
	public static String getPreviousDay(String strDate, int move) throws ParseException {
		
		DateTime currentDate = new DateTime(getYear(strDate),
											getMonth(strDate),
											getDay(strDate),
											0, 0);
		DateTime oneDayBefore = currentDate.minusDays(move);
		
		String oneDayBeforeAsString = oneDayBefore.getYear() + "-";
		
		if(oneDayBefore.getMonthOfYear() < 10)
			oneDayBeforeAsString += "0" + oneDayBefore.getMonthOfYear() + "-";
		else
			oneDayBeforeAsString += oneDayBefore.getMonthOfYear() + "-";
		
		if(oneDayBefore.getDayOfMonth() < 10)
			oneDayBeforeAsString += "0" + oneDayBefore.getDayOfMonth();
		else
			oneDayBeforeAsString += oneDayBefore.getDayOfMonth();
		
		return oneDayBeforeAsString;
	}
	
	private static int getYearRange(String startDate, String endDate) {
		int dateFromYear = getYear(startDate);
		int dateToYear = getYear(endDate);
		return dateToYear-dateFromYear;
	}
	
	private static int getYear(String date) {
		
		return Integer.parseInt(date.substring(0, 4));
	}
	
	private static int getMonth(String date) {
		
		return Integer.parseInt(date.substring(5, 7));
	}
	
	private static int getDay(String date) {
		
		return Integer.parseInt(date.substring(8, 10));
	}
	
}
