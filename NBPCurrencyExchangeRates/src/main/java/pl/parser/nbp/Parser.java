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

	private final static Parser instance = new Parser();
	
	private Parser() {
    }
	
	public static Parser getInstance() {
        return instance;
    }
	
	public String parseDate(String date) {
		
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
	
	public List<Currency> parseDataFromXmlFiles(List<String> codes, String currencySymbol) throws ParserConfigurationException, SAXException, IOException {
		
		List<Currency> currencyRates = new ArrayList<>();
		boolean foundCurrencySymbol = false;
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
                if(getValueFromAttribute(eElementCurrency, "kod_waluty").equals(currencySymbol.toUpperCase())) {
                	currencyRates.add(getCurrencyInfo(eElementPublishDate, eElementCurrency));
                	foundCurrencySymbol = true;
                }
	         }	 
		}
		if(!foundCurrencySymbol)
			throw new ParserConfigurationException("Error: not found currency symbol");
		return currencyRates;
	}
	
	private String getValueFromAttribute(Element eElement, String attribute) {
		
		return eElement.getElementsByTagName(attribute).item(0).getTextContent();
	}
	
	private Element getElement(Document xmlFile, String attributeName, int position) {
		
		NodeList nodeList = xmlFile.getElementsByTagName(attributeName);
		Node node = nodeList.item(position);
		Element element = (Element) node;
		
		return element;
	}
	
	private Currency getCurrencyInfo(Element eElementPublishDate, Element eElementCurrency) {
		
		String publishDate = getValueFromAttribute(eElementPublishDate, "data_publikacji");
    	String currencyName = getValueFromAttribute(eElementCurrency, "nazwa_waluty");
    	String currencySymbol = getValueFromAttribute(eElementCurrency, "kod_waluty");
    	double buyRate = Double.parseDouble(getValueFromAttribute(eElementCurrency, "kurs_kupna").replace(',', '.'));
    	double sellRate = Double.parseDouble(getValueFromAttribute(eElementCurrency, "kurs_sprzedazy").replace(',', '.'));
    	Currency currency = new Currency(currencyName, currencySymbol, sellRate, buyRate, publishDate);
    	
    	return currency;
	}
}
