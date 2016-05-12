package pl.parser.nbp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;


public class Utility {

	private final static Utility instance = new Utility();
	
	final static int CODE_LENGTH = 11;
	final static String TABLE_TYPE = "c";
	final static int FOLLOWING_DAYS = 1;
	final static int PREVIOUS_DAYS = -1;
	
	private Utility() {
    }
	
	public static Utility getInstance() {
        return instance;
    }
  
	public List<String> getAdresses(String startDate, String endDate) throws ParseException, IOException {
		
		
		int yearRange = getYearRange(startDate, endDate);
		int startYear = getYear(startDate);
		String dirsContent = getNbpDirs(startYear, yearRange);
		
		String newFormatStartDateFollowingDays[] = getDaysInRange(startDate, 10, FOLLOWING_DAYS);
		String newFormatStartDatePreviousDays[] = getDaysInRange(endDate, 10, PREVIOUS_DAYS);
		
		List<String> listWithCodes = new ArrayList<>();
		
		String regex = generateRegex(newFormatStartDateFollowingDays, newFormatStartDatePreviousDays);
	    
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
	
	private String getNbpDirs(int year, int yearRange) throws IOException {
		
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
		    	if(s.startsWith(TABLE_TYPE)) {
		    		sb.append(s);
		    	}
		    }
		    scanner.close();
		    if(year == 2015 || year == 2016)
		    	allDirs += sb.replace(0, 1, "").toString();
		    else
		    	allDirs += sb.toString(); // delete unwanted character "?", only in 2015 and 2016
		}
	    return allDirs;
	}
	
	public void downloadXmlFilesFromParticularDays(List<String> codes) throws IOException {
		
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
		    scanner.close();
		    
		    PrintWriter writer;
			writer = new PrintWriter(codes.get(i), "UTF-8");
			writer.print(sb.toString());
			writer.close();
		    
		}
	}
	
	public void deleteXmlFiles(List<String> codes) {
		for(String code : codes) {
			File xmlFile = new File(code);
			xmlFile.delete();
		}
		
	}
	
	private String moveDate(String strDate, int move) throws ParseException {
		
		DateTime currentDate = new DateTime(getYear(strDate),
											getMonth(strDate),
											getDay(strDate),
											0, 0);
		DateTime oneDayBefore = currentDate.plusDays(move);
		
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
	
	private String[] getDaysInRange(String date, int numberOfDays, int FollowingOrPrevoius) throws ParseException {
		
		Parser parser = Parser.getInstance();
		String dates[] = new String[numberOfDays+1];
		dates[0] = date;
		for(int i = 1; i < numberOfDays+1; i++) {
			date = dates[i-1];
			dates[i-1] = parser.parseDate(dates[i-1]);
			dates[i] = moveDate(date, FollowingOrPrevoius);
		}
		dates[numberOfDays] = parser.parseDate(dates[numberOfDays]);
		
		return dates;
	}
		
	private String generateRegex(String[] followingDays, String[] previousDays) {
		
		String next = "(";
		String previous = "(";
		for(int i = 0; i < followingDays.length; i++) {
			next += followingDays[i] + "|";
			previous += previousDays[i] + "|";
		}
		next = next.substring(0, next.length()-1);
		next += ")";
		previous = previous.substring(0, previous.length()-1);
		previous += ")";
		String regex = "c.{3}z" + next + ".*c.{3}z" + previous;
		
		return regex;
	}
	
	private int getYearRange(String startDate, String endDate) { 
		int dateFromYear = getYear(startDate);
		int dateToYear = getYear(endDate);
		return dateToYear-dateFromYear;
	}
	
	private int getYear(String date) {
		
		return Integer.parseInt(date.substring(0, 4));
	}
	
	private int getMonth(String date) {
		
		return Integer.parseInt(date.substring(5, 7));
	}
	
	private int getDay(String date) {
		
		return Integer.parseInt(date.substring(8, 10));
	}
	
}
