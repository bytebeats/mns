package me.bytebeats.mns.handler;

import me.bytebeats.mns.HttpClientPool;
import me.bytebeats.mns.LogUtil;
import me.bytebeats.mns.meta.Fund;
import me.bytebeats.mns.tool.GsonUtils;
import me.bytebeats.mns.tool.PinyinUtils;
import me.bytebeats.mns.tool.StringResUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CryptoCurrencyHandler extends AbstractHandler {

    private static final long REFRESH_INTERVAL = 10L * 1000L;

    protected List<Fund> cryptoCurrencies = new ArrayList<>();
    private final int[] cryptoCurrencyTabWidths = {0, 0, 0, 0, 0};
    private final String[] cryptoCurrencyColumnNames = {StringResUtils.CRYPTO_CURRENCY_NAME, StringResUtils.CRYPTO_CURRENCY_CODE,
            StringResUtils.CRYPTO_CURRENCY_PRICE, StringResUtils.CRYPTO_CURRENCY_VOLUME, StringResUtils.CRYPTO_CURRENCY_PNLR};

    public CryptoCurrencyHandler(JTable table, JLabel label) {
        super(table, label);
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
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(symbols);
            }
        }, 0, REFRESH_INTERVAL);
        LogUtil.info("starts updating " + getTipText() + " funds");
    }

    @Override
    protected String getTipText() {
        return jTable.getToolTipText();
    }

    private void fetch(List<String> symbols) {
        if (symbols.isEmpty()) {
            return;
        }
        for (String symbol : symbols) {
            try {
                String entity = HttpClientPool.getInstance().get(getFundUrl(symbol));
                parse(entity);
                updateView();
            } catch (Exception e) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                    LogUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
                }
            }
        }
    }

    /**
     * @param entity jsonpgz (
     *               {
     *               "fundcode": "001186",
     *               "name": "瀵屽浗鏂囦綋鍋ュ悍鑲＄エ",
     *               "jzrq": "2020-08-24",
     *               "dwjz": "2.0380",
     *               "gsz": "2.0393",
     *               "gszzl": "0.07",
     *               "gztime": "2020-08-25 10:54"
     *               }
     *               )
     */

    private void parse(String entity) {
        String src = entity;
        int leftBracketCount = 0;
        int rightBracketCount = 0;
        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) == '(') {
                leftBracketCount++;
            } else if (src.charAt(i) == ')') {
                rightBracketCount++;
            }
        }
        if (leftBracketCount > 1 || rightBracketCount > 1) {
            StringBuilder sb = new StringBuilder();
            boolean is1stLeftBracket = true;
            for (int i = 0; i < src.length(); i++) {
                char ch = src.charAt(i);
                if (ch != '(' && ch != ')') {
                    sb.append(ch);
                } else if (ch == '(') {
                    if (is1stLeftBracket) {
                        sb.append(ch);
                        is1stLeftBracket = false;
                    }
                } else {
                    if (rightBracketCount == 1) {
                        sb.append(ch);
                    }
                    rightBracketCount--;
                }
            }
            src = sb.toString();
        }
        final String regExp = "(?<=jsonpgz\\()[^)]+";//正则表达式中的零宽断言, 该语句表示匹配 jsonpgz(xxx) 中的 xxx
        final Pattern pattern = Pattern.compile(regExp);
        final Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            updateFund(GsonUtils.fromJson(matcher.group(), Fund.class));
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
        Object[][] data = new Object[cryptoCurrencies.size()][cryptoCurrencyColumnNames.length];
        for (int i = 0; i < cryptoCurrencies.size(); i++) {
            Fund fund = cryptoCurrencies.get(i);
            String name = fund.getName();
            if (isInHiddenMode()) {
                name = PinyinUtils.toPinyin(name);
            }
            data[i] = new Object[]{name, fund.getFundcode(), fund.getDwjz(), fund.getGsz(),
                    fund.getEstimateNetValueRatio()};
        }
        return data;
    }

    private void updateFund(Fund fund) {
        int idx = cryptoCurrencies.indexOf(fund);
        if (idx > -1 && idx < cryptoCurrencies.size()) {
            cryptoCurrencies.set(idx, fund);
        } else {
            cryptoCurrencies.add(fund);
        }
    }

    private String getFundUrl(String code) {
        return String.format(StringResUtils.TIANTIAN_FUND_URL, code, System.currentTimeMillis());
    }
}
