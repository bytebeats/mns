package me.bytebeats.mns.handler;

import me.bytebeats.mns.listener.MousePressedListener;
import me.bytebeats.mns.network.HttpClientPool;
import me.bytebeats.mns.tool.NotificationUtil;
import me.bytebeats.mns.meta.Stock;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TencentStockHandler extends AbsStockHandler {

    public TencentStockHandler(JTable table, JLabel label) {
        super(table, label);
        table.addMouseListener(new MousePressedListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRowIdx = jTable.getSelectedRow();
                if (selectedRowIdx < 0) {
                    return;
                }
                String symbol = stocks.get(selectedRowIdx).getSymbol();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 2 && onItemDoubleClickListener != null) {
                        onItemDoubleClickListener.onItemDoubleClick(symbol, e.getXOnScreen(), e.getYOnScreen());
                    } else if (e.getClickCount() == 1 && onItemClickListener != null) {
                        onItemClickListener.onItemClick(symbol, e.getXOnScreen(), e.getYOnScreen());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (onItemRightClickListener != null) {
                        onItemRightClickListener.onItemRightClick(symbol, e.getXOnScreen(), e.getYOnScreen());
                    }
                }
            }
        });

    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(stockColumnNames);
    }

    @Override
    public void load(List<String> symbols) {
        stocks.clear();
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
        NotificationUtil.info("starts updating " + getTipText() + " stocks");
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
            params.append(symbol);
        }
        try {
            String entity = HttpClientPool.getInstance().get(appendParams(params.toString()));
            parse(symbols, entity);
        } catch (Exception e) {
            NotificationUtil.info(e.getMessage());
            timer.cancel();
            timer = null;
            NotificationUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
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
            String assertion = String.format("(?<=v_%s=\").*?(?=\";)", symbol);
            Pattern pattern = Pattern.compile(assertion);
            Matcher matcher = pattern.matcher(raw);
            while (matcher.find()) {
                String[] metas = matcher.group().split("~");
                Stock stock = new Stock();
//                stock.setSymbol(symbol);
//                stock.setName(metas[1]);
//                stock.setLatestPrice(Double.parseDouble(metas[3]));
//                stock.setChange(Double.parseDouble(metas[31]));
//                stock.setChangeRatio(Double.parseDouble(metas[32]));
//                stock.setVolume(Double.parseDouble(metas[36]));
//                stock.setTurnover(Double.parseDouble(metas[37]));
//                stock.setMarketValue(Double.parseDouble(metas[45]));
                //简要信息
                stock.setSymbol(symbol);
                stock.setName(metas[1]);
                stock.setLatestPrice(Double.parseDouble(metas[3]));
                stock.setChange(Double.parseDouble(metas[4]));
                stock.setChangeRatio(Double.parseDouble(metas[5]));
                updateStock(stock);
                updateView();
            }
        }
    }
}
