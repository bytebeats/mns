package me.bytebeats.handler;

import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.meta.Fund;
import me.bytebeats.tool.GsonUtils;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TianTianFundHandler extends AbstractHandler {

    public static final long REFRESH_INTERVAL = 10L * 1000L;

    protected List<Fund> funds = new ArrayList<>();
    private final int[] fundTabWidths = {0, 0, 0, 0, 0, 0};
    private final String[] fundColumnNames = {StringResUtils.FUND_NAME, StringResUtils.FUND_CODE, StringResUtils.FUND_NET_VALUE_DATE,
            StringResUtils.FUND_NET_VALUE_ESTIMATED, StringResUtils.RISE_AND_FALL_RATIO, StringResUtils.FUND_NET_VALUE_ESTIMATED_DATE};

    public TianTianFundHandler(JTable table, JLabel label) {
        super(table, label);
    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(fundColumnNames);
    }

    @Override
    public void load(List<String> symbols) {
        funds.clear();
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(symbols);
            }
        }, 0, REFRESH_INTERVAL);
        LogUtil.info("mns starts updating " + jTable.getToolTipText() + " data");
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
                timer.cancel();
                timer = null;
                LogUtil.info("mns stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
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
        String json = entity.substring(8, entity.length() - 2);
        updateFund(GsonUtils.fromJson(json, Fund.class));
    }

    @Override
    public void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < fundColumnNames.length; i++) {
            fundTabWidths[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    @Override
    public void resetTabSize() {
        for (int i = 0; i < fundColumnNames.length; i++) {
            if (fundTabWidths[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(fundTabWidths[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(fundTabWidths[i]);
            }
        }
    }

    @Override
    public Object[][] convert2Data() {
        Object[][] data = new Object[funds.size()][fundColumnNames.length];
        for (int i = 0; i < funds.size(); i++) {
            Fund index = funds.get(i);
            data[i] = new Object[]{index.getName(), index.getFundcode(), index.getDwjz(),
                    index.getGsz(), index.getEstimateNetValueRatio(), index.getGztime()};
        }
        return data;
    }

    private void updateFund(Fund fund) {
        int idx = funds.indexOf(fund);
        if (idx > -1 && idx < funds.size()) {
            funds.set(idx, fund);
        } else {
            funds.add(fund);
        }
    }

    private String getFundUrl(String code) {
        return String.format(StringResUtils.TIANTIAN_FUND_URL, code, System.currentTimeMillis());
    }
}
