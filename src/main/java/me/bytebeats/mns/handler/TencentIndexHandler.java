package me.bytebeats.mns.handler;

import me.bytebeats.mns.HttpClientPool;
import me.bytebeats.mns.LogUtil;
import me.bytebeats.mns.OnSymbolSelectedListener;
import me.bytebeats.mns.meta.Index;
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

public class TencentIndexHandler extends AbstractHandler {

    protected List<Index> indices = new ArrayList<>();
    private final int[] IndexTabWidths = {0, 0, 0, 0, 0};
    private final String[] indexColumnNames = {StringResUtils.INDEX_NAME, StringResUtils.SYMBOL,
            StringResUtils.INDEX_LATEST, StringResUtils.RISE_AND_FALL, StringResUtils.RISE_AND_FALL_RATIO};

    private OnSymbolSelectedListener listener;

    public TencentIndexHandler(JTable table, JLabel label) {
        super(table, label);
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        this.listener = listener;
        jTable.setCellSelectionEnabled(true);
        ListSelectionModel model = jTable.getSelectionModel();
        model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.addListSelectionListener(e -> {
            int selectedRowIdx = jTable.getSelectedRow();
            if (selectedRowIdx > -1 && listener != null) {
                listener.onSelected(indices.get(selectedRowIdx).getSymbol());
            }
        });
    }

    @Override
    public void updateFrequency() {
        this.frequency = AppSettingState.getInstance().indicesFrequency * 1000L;
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
            updateFrequency();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(symbols);
            }
        }, 0, frequency);
        LogUtil.info("starts updating " + getTipText() + " indices");
    }

    @Override
    protected String getTipText() {
        return jTable.getToolTipText();
    }

    private void fetch(List<String> symbols) {
        LogUtil.info(getTipText() + ": " + frequency);
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
            timer.cancel();
            timer = null;
            LogUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
        }
    }

    /**
     * 股票详情
     * v_usAAPL="200~苹果~AAPL.OQ~134.18~129.04~132.76~152470142~29222201081093~29258751598320~134.99~36~0~0~0~0~0~0~0~0~135.00~91~0~0~0~0~0~0~0~0~~2020-09-01 16:00:01~5.14~3.98~134.80~130.53~USD~152470142~20266788153~0.89~40.69~~45.14~~3.31~~22948.18280~Apple Inc~3.30~134.80~50.26~-55~31.75~0.59~22948.18280~84.29~~SHARE~69.25~18.27~";
     * 股票简要信息
     * v_s_usAAPL="200~苹果~AAPL.OQ~134.18~5.14~3.98~152470142~20266788153~22948.18280~";
     *
     * @param symbols
     * @param entity
     */
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
                Index index = new Index();
//                index.setSymbol(symbol);
//                index.setName(metas[1]);
//                index.setLatest(Double.parseDouble(metas[3]));
//                index.setClose(Double.parseDouble(metas[4]));
//                index.setOpen(Double.parseDouble(metas[5]));
//                index.setTurnover(Double.parseDouble(metas[6]));
//                index.setChange(Double.parseDouble(metas[31]));
//                index.setChangeRatio(Double.parseDouble(metas[32]));
//                index.setHighest(Double.parseDouble(metas[33]));
//                index.setLowest(Double.parseDouble(metas[34]));
//                index.setTurnover(Double.parseDouble(metas[36]));
//                index.setDailyRatio(Double.parseDouble(metas[43]));
                //简要信息
                index.setName(metas[1]);
                index.setSymbol(symbol);
                index.setLatest(Double.parseDouble(metas[3]));
                index.setChange(Double.parseDouble(metas[4]));
                index.setChangeRatio(Double.parseDouble(metas[5]));
                updateIndex(index);
            }
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
        columnTextColors.clear();
        Object[][] data = new Object[indices.size()][indexColumnNames.length];
        for (int i = 0; i < indices.size(); i++) {
            Index index = indices.get(i);
            String name = index.getName();
//            String highest = index.getHighestString();
//            String lowest = index.getLowestString();
//            String open = index.getOpenString();
//            String close = index.getCloseString();
//            String dailyRatio = index.getDailyRatioString();
//            String turnover = index.getTurnoverString();
            if (isInHiddenMode()) {
                name = PinyinUtils.toPinyin(name);
            }
            if (i < indices.size()) {
                data[i] = new Object[]{name, index.getSymbol(), index.getLatest(), index.getChange(),
                        index.getChangeRatioString()};//, highest, lowest, open, close, dailyRatio, turnover
                columnTextColors.put(i, index.getChange());
            }
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
