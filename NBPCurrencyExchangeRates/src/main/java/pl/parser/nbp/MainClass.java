package pl.parser.nbp;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class MainClass {
	
	public static void main(String[] args) throws ParseException {
		
		/*String currencySymbol = args[0];
		String startDate = args[1];
		String endDate = args[2];*/
				
		List<String> listWithAdresses = null;
		List<Currency> currencies = null;
		try {
			listWithAdresses = Utility.getAdresses("2012-01-06", "2013-01-31");
			Utility.downloadXmlFilesFromParticularDays(listWithAdresses);
			currencies = Parser.parseDataFromXmlFiles(listWithAdresses, "EUR");
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println(Statistics.averageBuyRate(currencies));
		System.out.println(Statistics.standardDeviationSellRate(currencies));

		Utility.deleteXmlFiles(listWithAdresses);

	}

}
