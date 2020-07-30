package me.bytebeats.meta;

import me.bytebeats.tool.NumberFormatUtils;

import java.util.Objects;

public class Index {
    private String symbol = "";//股票代码
    private String name = "";//股票名称
    private double latest = 0.0;//最新
    private double change = 0.0;//涨跌
    private double changeRatio = 0.0;//涨跌幅
    private double open = 0.0;//今开
    private double close = 0.0;//昨收
    private double lowest = 0.0;//最低
    private double highest = 0.0;//最高
    private double turnover = 0.0;//成交额
    private double dailyRatio = 0.0;//日振幅

    public String getSymbol() {
        return symbol;
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

    public double getLatest() {
        return latest;
    }

    public void setLatest(double latest) {
        this.latest = latest;
    }

    public double getChange() {
        return change;
    }

    public String getChangeString() {
        return NumberFormatUtils.formatDouble(change);
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

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLowest() {
        return lowest;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

    public double getHighest() {
        return highest;
    }

    public void setHighest(double highest) {
        this.highest = highest;
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

    public double getDailyRatio() {
        return dailyRatio;
    }

    public String getDailyRatioString() {
        return dailyRatio + "%";
    }

    public void setDailyRatio(double dailyRatio) {
        this.dailyRatio = dailyRatio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index that = (Index) o;
        return symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
