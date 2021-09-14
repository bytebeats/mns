package me.bytebeats.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "me.bytebeats.ui.AppSettingState", storages = {@Storage("mns_plugin_setting.xml")})
public class AppSettingState implements PersistentStateComponent<AppSettingState> {

    public final static boolean IS_RED_RISE = true;
    public final static boolean IS_HIDDEN_MODE = false;
    public final static String US_STOCKS = "AAPL;TSLA;NFLX;MSFT";
    public final static String HK_STOCKS = "00981;09988;09618";
    public final static String SH_STOCKS = "600036";
    public final static String SZ_STOCKS = "002352";
    public final static String DAILY_FUNDS = "320003;002621;519674";
    public final static String ALL_INDICES = "usDJI;usIXIC;usINX;usNDX;hkHSI;hkHSTECH;hkHSCEI;hkHSCCI;sh000001;sh588090;sz399001;sz399006;sh000300;sh000016;sz399903;sh000011;sz399103;sz399330";
    public final static String STOCK_SYMBOL = "sh600519";
    public final static String FUND_SYMBOL = "570008";

    public boolean isRedRise = true;
    public boolean isHiddenMode = false;
    public String usStocks = "AAPL;TWTR;TSLA;NFLX;MSFT";
    public String hkStocks = "00981;09988;09618";
    public String shStocks = "600036";
    public String szStocks = "002352";
    public String dailyFunds = "320003;002621;519674";
    public String stockSymbol = "usTSLA";
    public String fundSymbol = "570008";

    public static AppSettingState getInstance() {
        return ServiceManager.getService(AppSettingState.class);
    }

    public void reset() {
        isRedRise = IS_RED_RISE;
        isHiddenMode = IS_HIDDEN_MODE;
        usStocks = US_STOCKS;
        hkStocks = HK_STOCKS;
        shStocks = SH_STOCKS;
        szStocks = SZ_STOCKS;
        dailyFunds = DAILY_FUNDS;
        stockSymbol = STOCK_SYMBOL;
        fundSymbol = FUND_SYMBOL;
    }

    @Nullable
    @Override
    public AppSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingState appSettingState) {
        XmlSerializerUtil.copyBean(appSettingState, this);
    }

    public boolean addFundSymbol(String fundSymbol) {
        if (dailyFunds.contains(fundSymbol)) {
            return false;
        }
        String df = dailyFunds.trim();
        if (df.endsWith(";")) {
            dailyFunds = df + fundSymbol;
        } else {
            dailyFunds = df + ";" + fundSymbol;
        }
        return true;
    }

    public boolean deleteFundSymbol(String fundSymbol) {
        if (!dailyFunds.contains(fundSymbol)) {
            return false;
        }
        String df = dailyFunds.trim();
        dailyFunds = df.replace(fundSymbol, "");
        return true;
    }
}
