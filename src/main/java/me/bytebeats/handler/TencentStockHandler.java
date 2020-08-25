package me.bytebeats.handler;

import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.meta.Stock;

import javax.swing.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TencentStockHandler extends AbsStockHandler {

    private Timer timer = new Timer();

    public TencentStockHandler(JTable table, JLabel label) {
        super(table, label);
    }

    @Override
    public void load(List<String> symbols) {
        stocks.clear();
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
            parse(entity);
            updateView();
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
            LogUtil.info("mns stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
        }
    }

    private void parse(String entity) {
        String[] raws = entity.split("\n");
        for (String raw : raws) {
            //实时数据
            String[] metas = raw.substring(raw.indexOf('=') + 2, raw.length() - 2).split("~");
            Stock stock = new Stock();
            String symbol = raw.substring(2, raw.indexOf("="));
            stock.setSymbol(symbol);
            stock.setName(metas[1]);
            stock.setLatestPrice(Double.parseDouble(metas[3]));
            stock.setChange(Double.parseDouble(metas[31]));
            stock.setChangeRatio(Double.parseDouble(metas[32]));
            stock.setVolume(Double.parseDouble(metas[36]));
            stock.setTurnover(Double.parseDouble(metas[37]));
            stock.setMarketValue(Double.parseDouble(metas[45]));
            //简要信息
//            String symbol = raw.substring(2, raw.indexOf("="));
//            stock.setSymbol(symbol);
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
