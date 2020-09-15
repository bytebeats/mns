package me.bytebeats.meta;

import me.bytebeats.tool.NumberFormatUtils;

import java.util.Objects;

public class Stock {
    private String symbol = "";//股票代码
    private String name = "";//股票名称
    private double latestPrice = 0.0;//最新价格
    private double change = 0.0;//涨跌
    private double changeRatio = 0.0;//涨跌幅
    private double volume = 0.0;//成交量
    private double turnover = 0.0;//成交额
    private double marketValue = 0.0;//成交额

    public Stock() {

    }

    public String getSymbol() {
        if (symbol.contains("_")) {
            return symbol.substring(symbol.indexOf("_") + 1);
        } else {
            return symbol;
        }
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangeRatio() {
        return changeRatio;
    }

    public String getChangeRatioString() {
        return changeRatio + "%";
    }

    public void setChangeRatio(double changeRatio) {
        this.changeRatio = changeRatio;
    }

    public double getVolume() {
        return volume;
    }

    public String getVolumeString() {
        return NumberFormatUtils.formatDouble(volume);
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTurnover() {
        return turnover;
    }

    public String getTurnoverString() {
        return NumberFormatUtils.formatDouble(turnover);
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public String getMarketValueString() {
        return NumberFormatUtils.formatDouble(marketValue);
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return symbol.equals(stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", latestPrice=" + latestPrice +
                ", change=" + change +
                ", changeRatio=" + changeRatio +
                ", volume=" + volume +
                ", turnover=" + turnover +
                ", marketValue=" + marketValue +
                '}';
    }
}
