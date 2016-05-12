package pl.parser.nbp;

import java.text.DecimalFormat;
import java.util.List;

public class Statistics {
	
	private final static Statistics instance = new Statistics();
	
	private Statistics() {
    }
	
	public static Statistics getInstance() {
        return instance;
    }
	
	public double averageBuyRate(List<Currency> rates) {
		
		double sum = 0;
		for(int i = 0; i < rates.size(); i++) {
			sum += rates.get(i).getCurrencyBuyRate();
		}
		double averageRate = sum / rates.size();
		
		return round(averageRate);
	}
	
	public double averageSellRate(List<Currency> rates) {
		
		double sum = 0;
		for(int i = 0; i < rates.size(); i++) {
			sum += rates.get(i).getCurrencySellRate();
		}
		double averageRate = sum/rates.size();
	
		return round(averageRate);
	}
	
	public double standardDeviationSellRate(List<Currency> rates) {
		
		double averageRate = averageSellRate(rates);
		double variance = variance(rates, averageRate);
		double standardDeviation = Math.sqrt(variance);
		return round(standardDeviation);
	}
	
	public double variance(List<Currency> rates, double averageRate) {
		
		double variance = 0;
		for(Currency rate : rates) {
			double difference = rate.getCurrencySellRate()-averageRate;
			variance += Math.pow(difference, 2.0);
		}
		variance /= rates.size(); 
		
		return variance;
	}
	
	private double round(double value) {
		
		DecimalFormat df = new DecimalFormat("#.####");
		return Double.parseDouble(df.format(value).replace(',', '.')); 
	}

}
