package me.bytebeats.mns.handler;

import com.intellij.ui.JBColor;
import me.bytebeats.mns.UISettingProvider;
import me.bytebeats.mns.listener.OnItemClick;
import me.bytebeats.mns.listener.OnItemDoubleClick;
import me.bytebeats.mns.listener.OnItemRightClickListener;
import me.bytebeats.mns.tool.PinyinUtils;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.AppSettingState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * @author bytebeats
 * @version 1.0
 * @email <happychinapc@gmail.com>
 * @github https://github.com/bytebeats
 * @created on 2020/8/29 16:21
 * @description AbstractHandler defines common fields and methods to ui and data operation.
 */

public abstract class AbstractHandler implements UISettingProvider {
    protected Timer timer = null;
    protected final JTable jTable;
    private final JLabel jLabel;

    private final int[] numColumnIdx = {2, 3, 4};//哪些列需要修改字体颜色, 当UI设置发生改变的时候.
    protected final Map<Integer, Double> columnTextColors = new HashMap<>();

    protected long frequency = 0L;

    protected OnItemClick<String> onItemClickListener;
    protected OnItemDoubleClick<String> onItemDoubleClickListener;
    protected OnItemRightClickListener<String> onItemRightClickListener;

    public AbstractHandler(JTable table, JLabel label) {
        this.jTable = table;
        this.jLabel = label;
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        FontMetrics metrics = jTable.getFontMetrics(jTable.getFont());
        jTable.setRowHeight(Math.max(jTable.getRowHeight(), metrics.getHeight()));
    }

    protected void updateView() {
        SwingUtilities.invokeLater(() -> {
            restoreTabSizes();
            DefaultTableModel model = new DefaultTableModel(convert2Data(), getColumnNames());
            jTable.setModel(model);
            resetTabSize();
            updateRowTextColors();
            updateTimestamp();
        });
    }

    public abstract void load(List<String> symbols);

    public abstract void updateFrequency();

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected abstract String getTipText();

    public abstract Object[][] convert2Data();

    public abstract String[] getColumnNames();

    public abstract void restoreTabSizes();

    public abstract void resetTabSize();

    protected void updateRowTextColors() {
        for (int idx : numColumnIdx) {
            jTable.getColumn(jTable.getColumnName(idx)).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    setForeground(getTextColor(columnTextColors.getOrDefault(row, 0.0)));
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }
    }

    protected void updateTimestamp() {
        jLabel.setText(String.format(StringResUtils.REFRESH_TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringResUtils.TIMESTAMP_FORMATTER))));
        if (isInHiddenMode()) {
            jLabel.setForeground(JBColor.DARK_GRAY);
        } else {
            jLabel.setForeground(JBColor.RED);
        }
    }

    protected String[] handleColumnNames(String[] columnNames) {
        if (isInHiddenMode()) {
            String[] columns = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                columns[i] = PinyinUtils.toPinyin(columnNames[i]);
            }
            return columns;
        } else {
            return columnNames.clone();
        }
    }

    @Override
    public boolean isInHiddenMode() {
        return AppSettingState.getInstance().isHiddenMode;
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise;
    }

    public OnItemClick<String> getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClick<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemDoubleClick<String> getOnItemDoubleClickListener() {
        return onItemDoubleClickListener;
    }

    public void setOnItemDoubleClickListener(OnItemDoubleClick<String> onItemDoubleClickListener) {
        this.onItemDoubleClickListener = onItemDoubleClickListener;
    }

    public OnItemRightClickListener<String> getOnItemRightClickListener() {
        return onItemRightClickListener;
    }

    public void setOnItemRightClickListener(OnItemRightClickListener<String> onItemRightClickListener) {
        this.onItemRightClickListener = onItemRightClickListener;
    }
}
