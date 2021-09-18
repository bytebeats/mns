package me.bytebeats.mns.handler;

import me.bytebeats.mns.HttpClientPool;
import me.bytebeats.mns.LogUtil;
import me.bytebeats.mns.meta.DigitalCurrency;
import me.bytebeats.mns.tool.PinyinUtils;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.AppSettingState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaDigitalCurrencyHandler extends AbstractHandler {

    protected List<DigitalCurrency> cryptoCurrencies = new ArrayList<>();
    private final int[] cryptoCurrencyTabWidths = {0, 0, 0, 0, 0};
    private final String[] cryptoCurrencyColumnNames = {StringResUtils.CRYPTO_CURRENCY_NAME, StringResUtils.CRYPTO_CURRENCY_CODE,
            StringResUtils.CRYPTO_CURRENCY_PRICE, StringResUtils.RISE_AND_FALL, StringResUtils.RISE_AND_FALL_RATIO};

    public SinaDigitalCurrencyHandler(JTable table, JLabel label) {
        super(table, label);
    }

    @Override
    public void updateFrequency() {
        this.frequency = AppSettingState.getInstance().cryptoFrequency * 1000L;
    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(cryptoCurrencyColumnNames);
    }

    @Override
    public void load(List<String> symbols) {
        cryptoCurrencies.clear();
        if (timer == null) {
            timer = new Timer();
            updateFrequency();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(symbols);
            }
        }, 0, frequency);
        LogUtil.info("starts updating " + getTipText());
    }

    @Override
    protected String getTipText() {
        return jTable.getToolTipText();
    }

    private void fetch(List<String> symbols) {
        if (symbols.isEmpty()) {
            return;
        }

        StringBuilder params = new StringBuilder();
        for (String symbol : symbols) {
            if (params.length() != 0) {
                params.append(',');
            }
            params.append("btc_btc");
            params.append(symbol.toLowerCase());
            params.append("usd");
        }
        try {
            String entity = HttpClientPool.getInstance().get(getCryptoCurrencyUrl(params.toString()));
            parse(symbols, entity);
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            timer.cancel();
            timer = null;
            LogUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
        }
    }

    private void parse(List<String> symbols, String entity) {
        String[] raws = entity.split("\n");
        if (symbols.size() != raws.length) {
            return;
        }
        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            String raw = raws[i];
            String assertion = String.format("(?<=var hq_str_btc_btc%susd=\").*?(?=\";)", symbol.toLowerCase());
            Pattern pattern = Pattern.compile(assertion);
            Matcher matcher = pattern.matcher(raw);
            while (matcher.find()) {
                String[] metas = matcher.group().split(",");
                if (metas.length < 2) {
                    break;
                }
                String name = metas[9].replace(String.format("(%s/USD)", symbol.toUpperCase()), "");
                String price = metas[8];
                String preClose = metas[5];
                String volume = metas[10];
                updateCryptoCurrency(new DigitalCurrency(symbol, name, preClose, price, volume));
                updateView();
            }
        }
    }

    @Override
    public void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < cryptoCurrencyColumnNames.length; i++) {
            cryptoCurrencyTabWidths[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    @Override
    public void resetTabSize() {
        for (int i = 0; i < cryptoCurrencyColumnNames.length; i++) {
            if (cryptoCurrencyTabWidths[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(cryptoCurrencyTabWidths[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(cryptoCurrencyTabWidths[i]);
            }
        }
    }

    @Override
    public Object[][] convert2Data() {
        columnTextColors.clear();
        Object[][] data = new Object[cryptoCurrencies.size()][cryptoCurrencyColumnNames.length];
        for (int i = 0; i < cryptoCurrencies.size(); i++) {
            DigitalCurrency currency = cryptoCurrencies.get(i);
            String name = currency.getName();
            if (isInHiddenMode()) {
                name = PinyinUtils.toPinyin(name);
            }
            if (i < cryptoCurrencies.size()) {
                data[i] = new Object[]{name, currency.getSymbol(), currency.getPrice(), currency.formattedPnl(), currency.getPnlR()};
                columnTextColors.put(i, currency.pnl());
            } else {
                break;
            }
        }
        return data;
    }

    private void updateCryptoCurrency(DigitalCurrency currency) {
        int idx = cryptoCurrencies.indexOf(currency);
        if (idx > -1 && idx < cryptoCurrencies.size()) {
            cryptoCurrencies.set(idx, currency);
        } else {
            cryptoCurrencies.add(currency);
        }
    }

    private String getCryptoCurrencyUrl(String code) {
        return String.format(StringResUtils.SINA_CRYPTO_CURRENCY_URL, code);
    }
}
