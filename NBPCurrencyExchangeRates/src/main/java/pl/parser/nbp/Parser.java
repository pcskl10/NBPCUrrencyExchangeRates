package pl.parser.nbp;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class Parser {

	
	public static String parseDate(String date) {
		
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat nbpFormat = new SimpleDateFormat("yyMMdd");

		String reformattedStr = null;
		try {
			reformattedStr = nbpFormat.format(inputFormat.parse(date));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		return reformattedStr;
	} 
	
	public static List<Currency> parseDataFromXmlFiles(List<String> codes, String currencySymbol) throws ParserConfigurationException, SAXException, IOException {
		
		List<Currency> currencyRates = new ArrayList<>();
		for(String code : codes) { 
			File inputFile = new File(code);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();
	         NodeList pozycja = doc.getElementsByTagName("pozycja");
	         for(int i = 0; i < pozycja.getLength(); i++) {
	            Element eElementCurrency = getElement(doc, "pozycja", i);
	            Element eElementPublishDate = getElement(doc, "tabela_kursow", 0);
                if(getValueFromAttribute(eElementCurrency, "kod_waluty").equals(currencySymbol)) {
                	currencyRates.add(getCurrencyInfo(eElementPublishDate, eElementCurrency));
                }
	         }
		}
		return currencyRates;
	}
	
	private static String getValueFromAttribute(Element eElement, String attribute) {
		
		return eElement.getElementsByTagName(attribute).item(0).getTextContent();
	}
	
	private static Element getElement(Document xmlFile, String attributeName, int position) {
		
		NodeList nodeList = xmlFile.getElementsByTagName(attributeName);
		Node node = nodeList.item(position);
		Element element = (Element) node;
		
		return element;
	}
	
	public static Currency getCurrencyInfo(Element eElementPublishDate, Element eElementCurrency) {
		
		Currency currency = new Currency();
		currency.setPublishDate(getValueFromAttribute(eElementPublishDate, "data_publikacji"));
    	currency.setCurrencyName(getValueFromAttribute(eElementCurrency, "nazwa_waluty"));
    	currency.setCurrencySymbol(getValueFromAttribute(eElementCurrency, "kod_waluty"));
    	currency.setCurrencyBuyRate(Double.parseDouble(getValueFromAttribute(eElementCurrency, "kurs_kupna").replace(',', '.')));
    	currency.setCurrencySellRate(Double.parseDouble(getValueFromAttribute(eElementCurrency, "kurs_sprzedazy").replace(',', '.')));
    	
    	return currency;
	}
}
