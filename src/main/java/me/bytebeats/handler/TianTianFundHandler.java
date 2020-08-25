package me.bytebeats.handler;

import com.intellij.ui.JBColor;
import me.bytebeats.HttpClientPool;
import me.bytebeats.LogUtil;
import me.bytebeats.UISettingProvider;
import me.bytebeats.meta.Fund;
import me.bytebeats.tool.GsonUtils;
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

public class TianTianFundHandler implements UISettingProvider {

    public static final long REFRESH_INTERVAL = 10L * 1000L;

    protected List<Fund> funds = new ArrayList<>();
    protected final JTable jTable;
    private final JLabel jLabel;
    private int[] tab_sizes = {0, 0, 0, 0, 0, 0};
    private String[] column_names = {StringResUtils.FUND_NAME, StringResUtils.FUND_CODE, StringResUtils.FUND_NET_VALUE_DATE,
            StringResUtils.FUND_NET_VALUE_ESTIMATED, StringResUtils.RISE_AND_FALL_RATIO, StringResUtils.FUND_NET_VALUE_ESTIMATED_DATE};

    private int[] numColumnIdx = {3, 4};

    private Timer timer = new Timer();

    public TianTianFundHandler(JTable table, JLabel label) {
        this.jTable = table;
        this.jLabel = label;
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        FontMetrics metrics = jTable.getFontMetrics(jTable.getFont());
        jTable.setRowHeight(Math.max(jTable.getRowHeight(), metrics.getHeight()));
    }

    public void load(List<String> symbols) {
        funds.clear();
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
            } catch (Exception e) {
                LogUtil.info(e.getMessage());
                timer.cancel();
                LogUtil.info("mns stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
            }
        }
        updateView();
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
        Object[][] data = new Object[funds.size()][column_names.length];
        for (int i = 0; i < funds.size(); i++) {
            Fund index = funds.get(i);
            data[i] = new Object[]{index.getName(), index.getFundcode(), index.getNetValueAndDate(),
                    index.getGsz(), index.getEstimateNetValueRatio(), index.getGztime()};
        }
        return data;
    }

    protected void updateFund(Fund fund) {
        int idx = funds.indexOf(fund);
        if (idx > -1 && idx < funds.size()) {
            funds.set(idx, fund);
        } else {
            funds.add(fund);
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

    public String getFundUrl(String code) {
        return String.format(StringResUtils.TIANTIAN_FUND_URL, code, System.currentTimeMillis());
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
