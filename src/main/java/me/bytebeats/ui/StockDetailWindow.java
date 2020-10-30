package me.bytebeats.ui;

import com.intellij.ui.JBColor;
import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.UISettingProvider;
import me.bytebeats.tool.PinyinUtils;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2020/9/14 20:25
 * @Version 1.0
 * @Description StockDetailWindow displays details of stock
 */

public class StockDetailWindow implements UISettingProvider {
    private static final long REFRESH_INTERVAL = 10L * 1000L;
    //ui
    private JPanel stock_detail_panel;
    private JPanel stock_detail_ask_bid_panel;
    private JLabel stock_detail_handicap_label;
    private JPanel stock_detail_ask_bid_label_panel;
    private JLabel stock_detail_ask_big;
    private JLabel stock_detail_ask_small;
    private JLabel stock_detail_bid_big;
    private JLabel stock_detail_bid_small;
    private JLabel stock_detail_ask_big_val;
    private JLabel stock_detail_ask_small_val;
    private JLabel stock_detail_bid_big_val;
    private JLabel stock_detail_bid_small_val;
    private JPanel stock_detail_ask_bid_val_panel;
    private JPanel stock_detail_money_flow_panel;
    private JLabel stock_detail_money_flow_label;
    private JLabel stock_detail_flow_place_holder;
    private JLabel stock_detail_flow_in_label;
    private JLabel stock_detail_flow_out_label;
    private JLabel stock_detail_flow_in_sum_label;
    private JLabel stock_detail_in_rate_label;
    private JLabel stock_detail_flow_main_force_label;
    private JLabel stock_detail_flow_main_force_in;
    private JLabel stock_detail_flow_main_force_out;
    private JLabel stock_detail_flow_main_force_in_sum;
    private JLabel stock_detail_flow_main_force_in_rate;
    private JLabel stock_detail_flow_individual_label;
    private JLabel stock_detail_flow_individual_in;
    private JLabel stock_detail_flow_individual_out;
    private JLabel stock_detail_flow_individual_in_sum;
    private JLabel stock_detail_flow_individual_in_rate;
    private JLabel stock_detail_money_flow_sum_label;
    private JLabel stock_detail_label;
    private JLabel stock_detail_name;
    private JLabel stock_detail_close_label;
    private JLabel stock_detail_open_label;
    private JLabel stock_detail_highest_price_label;
    private JLabel stock_detail_lowest_price_label;
    private JLabel stock_detail_amplitude_label;
    private JPanel stock_detail_label_panel_1;
    private JPanel stock_detail_val_panel_1;
    private JLabel stock_detail_close;
    private JLabel stock_detail_open;
    private JLabel stock_detail_highest;
    private JLabel stock_detail_lowest;
    private JLabel stock_detail_amplitude;
    private JPanel stock_detail_label_panel_2;
    private JLabel stock_detail_total_mkt_val_label;
    private JLabel stock_detail_circulation_mkt_val_label;
    private JLabel stock_detail_exchange_rate_label;
    private JLabel stock_detail_per_label;
    private JLabel stock_detail_pbr_label;
    private JPanel stock_detail_val_panel_2;
    private JLabel stock_detail_total_mkt_val;
    private JLabel stock_detail_circulation_mkt_val;
    private JLabel stock_detail_exchange_rate;
    private JLabel stock_detail_per;
    private JLabel stock_detail_pbr;
    private JPanel stock_detail_flow_label_panel;
    private JPanel stock_detail_flow_main_force_panel;
    private JPanel stock_detail_flow_individual_panel;
    private JPanel stock_detail_timestamp_panel;
    private JButton stock_detail_sync;
    private JLabel stock_detail_timestamp;
    private JLabel stock_detail_symbol;
    private JLabel stock_detail_latest_price;
    private JLabel stock_detail_rise_fall;
    private JLabel stock_detail_rise_fall_rate;
    private JPanel stock_detail_base_panel;
    private JLabel stock_detail_flow_sum;
    private JLabel stock_detail_volume_label;
    private JLabel stock_detail_volume_val;
    private JLabel stock_detail_turnover_label;
    private JLabel stock_detail_turnover_val;
    private JPanel stock_ask_bid_5_panel;
    private JLabel ask_bid_unit;
    private JLabel ask_bid_label_1;
    private JLabel ask_bid_label_2;
    private JLabel ask_bid_label_3;
    private JLabel ask_bid_label_5;
    private JLabel ask_bid_label_4;
    private JPanel bid_5_panel;
    private JPanel ask_5_panel;
    private JLabel bid_5_label;
    private JLabel bid_1;
    private JLabel bid_2;
    private JLabel bid_3;
    private JLabel bid_4;
    private JLabel bid_5;
    private JLabel ask_5_label;
    private JLabel ask_1;
    private JLabel ask_2;
    private JLabel ask_3;
    private JLabel ask_4;
    private JLabel ask_5;
    private JLabel ask_bid_5_label;

    private String symbol = "";
    private Timer detailTimer;
    private Timer handicapTimer;
    private Timer moneyFlowTimer;

    private final List<JLabel> labels = new ArrayList<>();

    public StockDetailWindow() {

    }

    public void setSymbol(String symbol) {//like usTSLA, sh603501, sz300661, hk00981
        this.symbol = symbol;
        syncRefresh();
        AppSettingState.getInstance().setStockSymbol(symbol);
    }

    public String getSymbol() {
        return symbol;
    }

    public JPanel getJPanel() {
        return stock_detail_panel;
    }

    public void onInit() {
        symbol = AppSettingState.getInstance().getStockSymbol();
        stock_detail_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
        if (detailTimer == null) {
            detailTimer = new Timer();
        }
        detailTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetchStockDetails();
            }
        }, 0, REFRESH_INTERVAL);
        if (handicapTimer == null) {
            handicapTimer = new Timer();
        }
        handicapTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetchHandicap();
            }
        }, 0, REFRESH_INTERVAL);
        if (moneyFlowTimer == null) {
            moneyFlowTimer = new Timer();
        }
        moneyFlowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetchMoneyFlow();
            }
        }, 0, REFRESH_INTERVAL);
        updateLabels();
        LogUtil.info("starts updating " + symbol + " detail data");
    }

    private void fetchStockDetails() {
        if (symbol == null || symbol.equals("")) {
            detailTimer.cancel();
            detailTimer = null;
            LogUtil.info("symbol can't be null or empty");
            return;
        }
        try {
            String entity = HttpClientPool.getInstance().get(getStockDetailUrl());
            parseStockDetail(entity);
            updateTimestamp();
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            detailTimer.cancel();
            detailTimer = null;
            LogUtil.info("stops updating " + symbol + " detail data because of " + e.getMessage());
        }
    }

    private void parseStockDetail(String entity) {
        String assertion = String.format("(?<=v_%s=\").*?(?=\";)", symbol);
        Pattern pattern = Pattern.compile(assertion);
        Matcher matcher = pattern.matcher(entity);
        if (matcher.find()) {
            String[] metas = matcher.group().split("~");
            if (isInHiddenMode()) {
                stock_detail_name.setText(PinyinUtils.toPinyin(metas[1]));
            } else {
                stock_detail_name.setText(metas[1]);
            }
            stock_detail_symbol.setText(symbol);
            stock_detail_latest_price.setText(metas[3]);
            stock_detail_rise_fall.setText(metas[31]);
            stock_detail_rise_fall_rate.setText(metas[32] + "%");
            bid_1.setText(String.format("%s/%s", metas[9], metas[10]));
            bid_2.setText(String.format("%s/%s", metas[11], metas[12]));
            bid_3.setText(String.format("%s/%s", metas[13], metas[14]));
            bid_4.setText(String.format("%s/%s", metas[15], metas[16]));
            bid_5.setText(String.format("%s/%s", metas[17], metas[18]));

            ask_1.setText(String.format("%s/%s", metas[19], metas[20]));
            ask_2.setText(String.format("%s/%s", metas[21], metas[22]));
            ask_3.setText(String.format("%s/%s", metas[23], metas[24]));
            ask_4.setText(String.format("%s/%s", metas[25], metas[26]));
            ask_5.setText(String.format("%s/%s", metas[27], metas[28]));
            stock_detail_open.setText(metas[5]);
            stock_detail_close.setText(metas[4]);
            stock_detail_highest.setText(metas[33]);
            stock_detail_lowest.setText(metas[34]);
            stock_detail_amplitude.setText(metas[43] + "%");
            stock_detail_total_mkt_val.setText(metas[45]);
            if ("".equals(metas[44])) {
                stock_detail_circulation_mkt_val.setText("--");
            } else {
                stock_detail_circulation_mkt_val.setText(metas[44]);
            }
            stock_detail_exchange_rate.setText(metas[38]);
            stock_detail_per.setText(metas[39]);
            stock_detail_pbr.setText(metas[46]);
            stock_detail_volume_val.setText(metas[36]);
            stock_detail_turnover_val.setText(metas[37]);
            try {
                double latestPrice = Double.parseDouble(metas[3]);
                stock_detail_rise_fall.setForeground(getTextColor(Double.parseDouble(metas[31])));
                stock_detail_rise_fall_rate.setForeground(getTextColor(Double.parseDouble(metas[32])));
                bid_1.setForeground(getTextColor(Double.parseDouble(metas[9]) - latestPrice));
                bid_2.setForeground(getTextColor(Double.parseDouble(metas[11]) - latestPrice));
                bid_3.setForeground(getTextColor(Double.parseDouble(metas[13]) - latestPrice));
                bid_4.setForeground(getTextColor(Double.parseDouble(metas[15]) - latestPrice));
                bid_5.setForeground(getTextColor(Double.parseDouble(metas[17]) - latestPrice));
                ask_1.setForeground(getTextColor(Double.parseDouble(metas[19]) - latestPrice));
                ask_2.setForeground(getTextColor(Double.parseDouble(metas[21]) - latestPrice));
                ask_3.setForeground(getTextColor(Double.parseDouble(metas[23]) - latestPrice));
                ask_4.setForeground(getTextColor(Double.parseDouble(metas[25]) - latestPrice));
                ask_5.setForeground(getTextColor(Double.parseDouble(metas[27]) - latestPrice));
                stock_detail_open.setForeground(getTextColor(Double.parseDouble(metas[5]) - latestPrice));
                stock_detail_close.setForeground(getTextColor(Double.parseDouble(metas[4]) - latestPrice));
                stock_detail_highest.setForeground(getTextColor(Double.parseDouble(metas[33]) - latestPrice));
                stock_detail_lowest.setForeground(getTextColor(Double.parseDouble(metas[34]) - latestPrice));
            } catch (NumberFormatException e) {
                LogUtil.info(e.getMessage());
            }
        } else {
            stock_detail_name.setText("--");
            stock_detail_symbol.setText(symbol);
            stock_detail_latest_price.setText("--");
            stock_detail_rise_fall.setText("--");
            stock_detail_rise_fall_rate.setText("-- %");
            bid_1.setText(String.format("%s/%s", "--", "--"));
            bid_2.setText(String.format("%s/%s", "--", "--"));
            bid_3.setText(String.format("%s/%s", "--", "--"));
            bid_4.setText(String.format("%s/%s", "--", "--"));
            bid_5.setText(String.format("%s/%s", "--", "--"));
            ask_1.setText(String.format("%s/%s", "--", "--"));
            ask_2.setText(String.format("%s/%s", "--", "--"));
            ask_3.setText(String.format("%s/%s", "--", "--"));
            ask_4.setText(String.format("%s/%s", "--", "--"));
            ask_5.setText(String.format("%s/%s", "--", "--"));
            stock_detail_open.setText("--");
            stock_detail_close.setText("--");
            stock_detail_highest.setText("--");
            stock_detail_lowest.setText("--");
            stock_detail_amplitude.setText("-- %");
            stock_detail_total_mkt_val.setText("--");
            stock_detail_circulation_mkt_val.setText("--");
            stock_detail_exchange_rate.setText("--");
            stock_detail_per.setText("--");
            stock_detail_pbr.setText("--");
            stock_detail_volume_val.setText("--");
            stock_detail_turnover_val.setText("--");
        }
    }

    private void fetchHandicap() {
        if (symbol == null || symbol.equals("")) {
            handicapTimer.cancel();
            handicapTimer = null;
            LogUtil.info("symbol can't be null or empty");
            return;
        }
        try {
            String entity = HttpClientPool.getInstance().get(getHandicapUrl());
            parseHandicap(entity);
            updateTimestamp();
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            handicapTimer.cancel();
            handicapTimer = null;
            LogUtil.info("stops updating " + symbol + " handicap data because of " + e.getMessage());
        }
    }

    private void parseHandicap(String entity) {
        String assertion = String.format("(?<=v_s_pk%s=\").*?(?=\";)", symbol);
        Pattern pattern = Pattern.compile(assertion);
        Matcher matcher = pattern.matcher(entity);
        if (matcher.find()) {
            String[] metas = matcher.group().split("~");
            stock_detail_ask_big_val.setText(metas[0]);
            stock_detail_ask_small_val.setText(metas[1]);
            stock_detail_bid_big_val.setText(metas[2]);
            stock_detail_bid_small_val.setText(metas[3]);
        } else {
            stock_detail_ask_big_val.setText("--");
            stock_detail_ask_small_val.setText("--");
            stock_detail_bid_big_val.setText("--");
            stock_detail_bid_small_val.setText("--");
        }
    }

    private void fetchMoneyFlow() {
        if (symbol == null || symbol.equals("")) {
            moneyFlowTimer.cancel();
            moneyFlowTimer = null;
            LogUtil.info("symbol can't be null or empty");
            return;
        }
        try {
            String entity = HttpClientPool.getInstance().get(getMoneyFlowUrl());
            parseMoneyFlow(entity);
            updateTimestamp();
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            moneyFlowTimer.cancel();
            moneyFlowTimer = null;
            LogUtil.info("stops updating " + symbol + " money flow data because of " + e.getMessage());
        }
    }

    private void parseMoneyFlow(String entity) {
        String assertion = String.format("(?<=v_ff_%s=\").*?(?=\";)", symbol);
        Pattern pattern = Pattern.compile(assertion);
        Matcher matcher = pattern.matcher(entity);
        if (matcher.find()) {
            String[] metas = matcher.group().split("~");
            stock_detail_flow_main_force_in.setText(metas[1]);
            stock_detail_flow_main_force_out.setText(metas[2]);
            stock_detail_flow_main_force_in_sum.setText(metas[3]);
            if (metas[3].startsWith("-")) {
                stock_detail_flow_main_force_in_sum.setForeground(getTextColor(-1.0));
                stock_detail_flow_main_force_in_rate.setForeground(getTextColor(-1.0));
            } else {
                stock_detail_flow_main_force_in_sum.setForeground(getTextColor(1.0));
                stock_detail_flow_main_force_in_rate.setForeground(getTextColor(1.0));
            }
            stock_detail_flow_main_force_in_rate.setText(metas[4] + "%");
            stock_detail_flow_individual_in.setText(metas[5]);
            stock_detail_flow_individual_out.setText(metas[6]);
            stock_detail_flow_individual_in_sum.setText(metas[7]);
            if (metas[7].startsWith("-")) {
                stock_detail_flow_individual_in_sum.setForeground(getTextColor(-1.0));
                stock_detail_flow_individual_in_rate.setForeground(getTextColor(-1.0));
            } else {
                stock_detail_flow_individual_in_sum.setForeground(getTextColor(1.0));
                stock_detail_flow_individual_in_rate.setForeground(getTextColor(1.0));
            }
            stock_detail_flow_individual_in_rate.setText(metas[8] + "%");
            stock_detail_flow_sum.setText(metas[9]);
        } else {
            stock_detail_flow_main_force_in.setText("--");
            stock_detail_flow_main_force_out.setText("--");
            stock_detail_flow_main_force_in_sum.setText("--");
            stock_detail_flow_main_force_in_rate.setText("-- %");
            stock_detail_flow_individual_in.setText("--");
            stock_detail_flow_individual_out.setText("--");
            stock_detail_flow_individual_in_sum.setText("--");
            stock_detail_flow_individual_in_rate.setText("-- %");
            stock_detail_flow_sum.setText("--");
        }
    }

    private String getStockDetailUrl() {//股票详情
        return appendUrl("", symbol);
    }

    private String getMoneyFlowUrl() {//实时资金流向
        return appendUrl("ff_", symbol);
    }

    private String getHandicapUrl() {//盘口分析
        return appendUrl("s_pk", symbol);
    }

    private String appendUrl(String prefix, String symbol) {
        return StringResUtils.QT_STOCK_URL + prefix + symbol;
    }

    protected void updateTimestamp() {
        stock_detail_timestamp.setText(String.format(StringResUtils.REFRESH_TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringResUtils.TIMESTAMP_FORMATTER))));
        if (isInHiddenMode()) {
            stock_detail_timestamp.setForeground(JBColor.DARK_GRAY);
        } else {
            stock_detail_timestamp.setForeground(JBColor.RED);
        }
    }

    private void updateLabels() {
        if (labels.isEmpty()) {
            labels.add(stock_detail_handicap_label);
            labels.add(stock_detail_volume_label);
            labels.add(stock_detail_turnover_label);
            labels.add(stock_detail_ask_big);
            labels.add(stock_detail_ask_small);
            labels.add(stock_detail_bid_big);
            labels.add(stock_detail_bid_small);
            labels.add(stock_detail_money_flow_label);
            labels.add(stock_detail_money_flow_sum_label);
            labels.add(stock_detail_flow_in_label);
            labels.add(stock_detail_flow_out_label);
            labels.add(stock_detail_flow_in_sum_label);
            labels.add(stock_detail_in_rate_label);
            labels.add(stock_detail_flow_main_force_label);
            labels.add(stock_detail_flow_individual_label);
            labels.add(stock_detail_label);
            labels.add(stock_detail_close_label);
            labels.add(stock_detail_open_label);
            labels.add(stock_detail_highest_price_label);
            labels.add(stock_detail_lowest_price_label);
            labels.add(stock_detail_amplitude_label);
            labels.add(stock_detail_total_mkt_val_label);
            labels.add(stock_detail_circulation_mkt_val_label);
            labels.add(stock_detail_exchange_rate_label);
            labels.add(stock_detail_per_label);
            labels.add(stock_detail_pbr_label);
            labels.add(ask_bid_5_label);
            labels.add(ask_bid_unit);
            labels.add(ask_bid_label_1);
            labels.add(ask_bid_label_2);
            labels.add(ask_bid_label_3);
            labels.add(ask_bid_label_4);
            labels.add(ask_bid_label_5);
            labels.add(bid_5_label);
            labels.add(ask_5_label);
        }
        //toolTipText始终保持为汉字不变
        boolean different = !stock_detail_handicap_label.getText().equals(stock_detail_handicap_label.getToolTipText());
        if (isInHiddenMode() && !different) {
            for (JLabel label : labels) {
                label.setText(PinyinUtils.toPinyin(label.getToolTipText()));
            }
        } else if (!isInHiddenMode() && different) {
            for (JLabel label : labels) {
                label.setText(label.getToolTipText());
            }
        }
    }

    @Override
    public boolean isInHiddenMode() {
        return AppSettingState.getInstance().isHiddenMode();
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise();
    }
}
