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
		
		Parser parser = Parser.getInstance();
		Utility utility = Utility.getInstance();
		Statistics statistics = Statistics.getInstance();
		List<String> listWithAdresses = null;
		List<Currency> currencies = null;
		try {
			listWithAdresses = utility.getAdresses("2013-01-28", "2013-01-31");
			utility.downloadXmlFilesFromParticularDays(listWithAdresses);
			currencies = parser.parseDataFromXmlFiles(listWithAdresses, "eur");
			System.out.println(statistics.averageBuyRate(currencies));
			System.out.println(statistics.standardDeviationSellRate(currencies));
		} 
		catch (ParserConfigurationException | SAXException | IOException e) {
			//e.printStackTrace();
		}
		finally {
			utility.deleteXmlFiles(listWithAdresses);
		}
	

	}

}
