package me.bytebeats.handler;

import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.meta.Index;
import me.bytebeats.tool.StringResUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TencentIndexHandler extends AbstractHandler {

    public static final long REFRESH_INTERVAL = 5L * 1000L;

    protected List<Index> indices = new ArrayList<>();
    private final int[] IndexTabWidths = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final String[] indexColumnNames = {StringResUtils.INDEX_NAME, StringResUtils.SYMBOL, StringResUtils.INDEX_LATEST,
            StringResUtils.RISE_AND_FALL, StringResUtils.RISE_AND_FALL_RATIO, StringResUtils.INDEX_HIGHEST,
            StringResUtils.INDEX_LOWEST, StringResUtils.INDEX_OPEN, StringResUtils.INDEX_CLOSE,
            StringResUtils.INDEX_DAILY_RATIO, StringResUtils.TURNOVER};

    public TencentIndexHandler(JTable table, JLabel label) {
        super(table, label);
    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(indexColumnNames);
    }

    @Override
    public void load(List<String> symbols) {
        indices.clear();
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
            parse(entity);
            updateView();
        } catch (Exception e) {
            timer.cancel();
            timer = null;
            LogUtil.info("mns stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
        }
    }

    private void parse(String entity) {
        String[] raws = entity.split("\n");
        for (String raw : raws) {
            //实时数据
            String[] metas = raw.substring(raw.indexOf('=') + 2, raw.length() - 2).split("~");
            Index index = new Index();
            String symbol = raw.substring(2, raw.indexOf("="));
            index.setSymbol(symbol);
            index.setName(metas[1]);
            index.setLatest(Double.parseDouble(metas[3]));
            index.setClose(Double.parseDouble(metas[4]));
            index.setOpen(Double.parseDouble(metas[5]));
            index.setTurnover(Double.parseDouble(metas[6]));
            index.setChange(Double.parseDouble(metas[31]));
            index.setChangeRatio(Double.parseDouble(metas[32]));
            index.setHighest(Double.parseDouble(metas[33]));
            index.setLowest(Double.parseDouble(metas[34]));
            index.setTurnover(Double.parseDouble(metas[36]));
            index.setDailyRatio(Double.parseDouble(metas[43]));
            //简要信息
//            String symbol = raw.substring(2, raw.indexOf("="));
//            index.setSymbol(symbol);
//            index.setName(metas[1]);
//            index.setLatest(Double.parseDouble(metas[3]));
//            index.setChange(Double.parseDouble(metas[4]));
//            index.setChangeRatio(Double.parseDouble(metas[5]));
            updateIndex(index);
        }
    }

    @Override
    public void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < indexColumnNames.length; i++) {
            IndexTabWidths[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    @Override
    public void resetTabSize() {
        for (int i = 0; i < indexColumnNames.length; i++) {
            if (IndexTabWidths[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(IndexTabWidths[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(IndexTabWidths[i]);
            }
        }
    }

    @Override
    public Object[][] convert2Data() {
        Object[][] data = new Object[indices.size()][indexColumnNames.length];
        for (int i = 0; i < indices.size(); i++) {
            Index index = indices.get(i);
            data[i] = new Object[]{index.getName(), index.getSymbol(), index.getLatest(), index.getChange(),
                    index.getChangeRatioString(), index.getHighest(), index.getLowest(), index.getOpen(), index.getClose(),
                    index.getDailyRatioString(), index.getTurnoverString()};
        }
        return data;
    }

    protected void updateIndex(Index index) {
        int idx = indices.indexOf(index);
        if (idx > -1 && idx < indices.size()) {
            indices.set(idx, index);
        } else {
            indices.add(index);
        }
    }

    public String appendParams(String params) {
        return StringResUtils.QT_STOCK_URL + params;
    }
}
