package me.bytebeats.handler;

import com.intellij.ui.JBColor;
import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.UISettingProvider;
import me.bytebeats.meta.Index;
import me.bytebeats.tool.PinyinUtils;
import me.bytebeats.tool.StringResUtils;
import me.bytebeats.ui.AppSettingState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TencentIndexHandler implements UISettingProvider {

    public static final long REFRESH_INTERVAL = 3L * 1000L;

    protected List<Index> indices = new ArrayList<>();
    protected JTable jTable;
    private JLabel jLabel;
    private int[] tab_sizes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] column_names = {StringResUtils.INDEX_NAME, StringResUtils.SYMBOL, StringResUtils.INDEX_LATEST,
            StringResUtils.RISE_AND_FALL, StringResUtils.RISE_AND_FALL_RATIO, StringResUtils.INDEX_HIGHEST,
            StringResUtils.INDEX_LOWEST, StringResUtils.INDEX_OPEN, StringResUtils.INDEX_CLOSE,
            StringResUtils.INDEX_DAILY_RATIO, StringResUtils.TURNOVER};

    private int[] numColumnIdx = {3, 4};

    private Timer timer = new Timer();

    public TencentIndexHandler(JTable table, JLabel label) {
        this.jTable = table;
        this.jLabel = label;
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        FontMetrics metrics = jTable.getFontMetrics(jTable.getFont());
        jTable.setRowHeight(Math.max(jTable.getRowHeight(), metrics.getHeight()));
    }

    public void load(List<String> symbols) {
        indices.clear();
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

    protected void updateView() {
        SwingUtilities.invokeLater(() -> {
            restoreTabSizes();
            DefaultTableModel model = null;
            if (isInHiddenMode()) {
                String[] columnNames = new String[column_names.length];
                for (int i = 0; i < columnNames.length; i++) {
                    columnNames[i] = PinyinUtils.toPinyin(column_names[i]);
                }
                model = new DefaultTableModel(convert2Data(), columnNames);
            } else {
                model = new DefaultTableModel(convert2Data(), column_names);
            }
            jTable.setModel(model);
            resetTabSize();
            updateRowTextColors();
            updateTimestamp();
        });
    }

    private void updateTimestamp() {
        jLabel.setText(String.format(StringResUtils.REFRESH_TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringResUtils.TIMESTAMP_FORMATTER))));
        if (isInHiddenMode()) {
            jLabel.setForeground(JBColor.DARK_GRAY);
        } else {
            jLabel.setForeground(JBColor.RED);
        }
    }

    private void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < column_names.length; i++) {
            tab_sizes[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    private void resetTabSize() {
        for (int i = 0; i < column_names.length; i++) {
            if (tab_sizes[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(tab_sizes[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(tab_sizes[i]);
            }
        }
    }

    private Object[][] convert2Data() {
        Object[][] data = new Object[indices.size()][column_names.length];
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

    private void updateRowTextColors() {
        for (int idx : numColumnIdx) {
            jTable.getColumn(jTable.getColumnName(idx)).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    double chg = 0.0;
                    try {
                        String chgRaw = value.toString();
                        if (column == 4) {
                            chgRaw = chgRaw.substring(0, chgRaw.length() - 1);
                        }
                        chg = Double.parseDouble(chgRaw);
                    } catch (NumberFormatException e) {
                        chg = 0.0;
                    }
                    if (!isInHiddenMode()) {
                        if (chg == 0) {
                            setForeground(JBColor.DARK_GRAY);
                        } else if (isRedRise()) {
                            if (chg > 0) {
                                setForeground(JBColor.RED);
                            } else {
                                setForeground(JBColor.GREEN);
                            }
                        } else {
                            if (chg > 0) {
                                setForeground(JBColor.GREEN);
                            } else {
                                setForeground(JBColor.RED);
                            }
                        }
                    } else {
                        setForeground(JBColor.DARK_GRAY);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }
    }

    public String appendParams(String params) {
        return StringResUtils.QT_STOCK_URL + params;
    }

    @Override
    public boolean isInHiddenMode() {
        return AppSettingState.getInstance().isHiddenMode();
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise();
    }

    @Override
    public boolean isConciseMode() {
        if (AppSettingState.getInstance() != null) {
            return AppSettingState.getInstance().isConciseMode();
        } else {
            return false;
        }
    }
}
