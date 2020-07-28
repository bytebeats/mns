package me.bytebeats.handler;

import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.meta.Stock;

import javax.swing.*;
import java.util.ArrayList;
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
        for (String symbol : symbols) {
            stocks.add(new Stock(symbol));
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(symbols);
            }
        }, 0, REFRESH_INTERVAL);
        LogUtil.info("mns updated stock data.");
    }

    private void fetch(List<String> symbols) {
        LogUtil.info("fetching");
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
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
        }
    }

    private void parse(String entity) {
        String[] raws = entity.split("\n");
        List<Stock> data = new ArrayList<>();
        for (String raw : raws) {
            LogUtil.info(raw);
            LogUtil.info("==================");
            String symbol = raw.substring(6, raw.indexOf('='));
            String[] metas = raw.substring(raw.indexOf('=') + 2, raw.length() - 2).split("~");
            Stock stock = new Stock();
            stock.setSymbol(symbol);
            stock.setName(metas[1]);
            stock.setSymbol(metas[2]);
            stock.setLatestPrice(Double.parseDouble(metas[3]));
            stock.setChange(Double.parseDouble(metas[4]));
            stock.setChangeRatio(Double.parseDouble(metas[5]));
            stock.setVolume(Double.parseDouble(metas[6]));
            stock.setTurnover(Double.parseDouble(metas[7]));
            stock.setMarketValue(Double.parseDouble(metas[9]));
            LogUtil.info(stock.toString());
        }
        stocks.clear();
        stocks.addAll(data);
        LogUtil.info(stocks.size()+"");
        updateView();
    }
}
