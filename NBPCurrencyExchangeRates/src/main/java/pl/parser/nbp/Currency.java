package pl.parser.nbp;

public class Currency {
	
	private String currencyName;
	private String currencySymbol;
	private double currencySellRate;
	private double currencyBuyRate;
	private String publishDate;
	
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public double getCurrencyBuyRate() {
		return currencyBuyRate;
	}
	public void setCurrencyBuyRate(double currencyBuyRate) {
		this.currencyBuyRate = currencyBuyRate;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	public double getCurrencySellRate() {
		return currencySellRate;
	}
	public void setCurrencySellRate(double currencySellRate) {
		this.currencySellRate = currencySellRate;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	
}
