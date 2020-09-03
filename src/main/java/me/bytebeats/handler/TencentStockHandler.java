package me.bytebeats.handler;

import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.meta.Stock;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TencentStockHandler extends AbsStockHandler {
    public static final long REFRESH_INTERVAL = 3L * 1000L;

    public TencentStockHandler(JTable table, JLabel label) {
        super(table, label);
    }

    @Override
    public String[] getColumnNames() {
        String[] columns = handleColumnNames(stockColumnNames);
        if (isConciseMode()) {
            for (int i = columns.length - 3; i < columns.length; i++) {
                columns[i] = StringResUtils.STR_PLACE_HOLDER;
            }
        }
        return columns;
    }

    @Override
    public void load(List<String> symbols) {
        stocks.clear();
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
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            params.append(symbols.get(i));
            if (i != symbols.size() - 1) {
                params.append(',');
            }
        }
        try {
            String entity = HttpClientPool.getInstance().get(appendParams(params.toString()));
            parse(symbols, entity);
            updateView();
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            timer.cancel();
            timer = null;
            LogUtil.info("mns stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
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
//            简要信息接口解析的断言
//            String assertion = String.format("(?<=v_s_%s=\").*?(?=\";)", symbol);
            String assertion = String.format("(?<=v_%s=\").*?(?=\";)", symbol);
            Pattern pattern = Pattern.compile(assertion);
            Matcher matcher = pattern.matcher(raw);
            while (matcher.find()) {
                String[] metas = matcher.group().split("~");
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                stock.setName(metas[1]);
                stock.setLatestPrice(Double.parseDouble(metas[3]));
                stock.setChange(Double.parseDouble(metas[31]));
                stock.setChangeRatio(Double.parseDouble(metas[32]));
                stock.setVolume(Double.parseDouble(metas[36]));
                stock.setTurnover(Double.parseDouble(metas[37]));
                stock.setMarketValue(Double.parseDouble(metas[45]));
                //简要信息
//            stock.setName(metas[1]);
//            stock.setSymbol(metas[2]);
//            stock.setLatestPrice(Double.parseDouble(metas[3]));
//            stock.setChange(Double.parseDouble(metas[4]));
//            stock.setChangeRatio(Double.parseDouble(metas[5]));
//            stock.setVolume(Double.parseDouble(metas[6]));
//            stock.setTurnover(Double.parseDouble(metas[7]));
//            stock.setMarketValue(Double.parseDouble(metas[8]));
                updateStock(stock);
            }
        }
    }
}
