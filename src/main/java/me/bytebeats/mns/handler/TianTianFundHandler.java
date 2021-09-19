package me.bytebeats.mns.handler;

import me.bytebeats.mns.listener.MousePressedListener;
import me.bytebeats.mns.network.HttpClientPool;
import me.bytebeats.mns.tool.LogUtil;
import me.bytebeats.mns.meta.Fund;
import me.bytebeats.mns.tool.GsonUtils;
import me.bytebeats.mns.tool.PinyinUtils;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.AppSettingState;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TianTianFundHandler extends AbstractHandler {

    protected List<Fund> funds = new ArrayList<>();
    private final int[] fundTabWidths = {0, 0, 0, 0, 0};
    private final String[] fundColumnNames = {StringResUtils.FUND_NAME, StringResUtils.FUND_CODE,
            StringResUtils.FUND_NET_VALUE_DATE, StringResUtils.FUND_NET_VALUE_ESTIMATED, StringResUtils.RISE_AND_FALL_RATIO};

    public TianTianFundHandler(JTable table, JLabel label) {
        super(table, label);
        table.addMouseListener(new MousePressedListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRowIdx = jTable.getSelectedRow();
                if (selectedRowIdx < 0) {
                    return;
                }
                String fundcode = funds.get(selectedRowIdx).getFundcode();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 2 && onItemDoubleClickListener != null) {
                        onItemDoubleClickListener.onItemDoubleClick(fundcode, e.getXOnScreen(), e.getYOnScreen());
                    } else if (e.getClickCount() == 1 && onItemClickListener != null) {
                        onItemClickListener.onItemClick(fundcode, e.getXOnScreen(), e.getYOnScreen());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (onItemRightClickListener != null) {
                        onItemRightClickListener.onItemRightClick(fundcode, e.getXOnScreen(), e.getYOnScreen());
                    }
                }
            }
        });
    }

    @Override
    public void updateFrequency() {
        this.frequency = AppSettingState.getInstance().fundFrequency * 1000L;
    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(fundColumnNames);
    }

    @Override
    public void load(List<String> symbols) {
        funds.clear();
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
        LogUtil.info("starts updating " + getTipText() + " funds");
    }

    @Override
    protected String getTipText() {
        return jTable.getToolTipText();
    }

    private void fetch(List<String> symbols) {
        if (symbols.isEmpty()) {
            return;
        }
        for (String symbol : symbols) {
            try {
                String entity = HttpClientPool.getInstance().get(getFundUrl(symbol));
                parse(entity);
                updateView();
            } catch (Exception e) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                    LogUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
                }
            }
        }
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
        String src = entity;
        int leftBracketCount = 0;
        int rightBracketCount = 0;
        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) == '(') {
                leftBracketCount++;
            } else if (src.charAt(i) == ')') {
                rightBracketCount++;
            }
        }
        if (leftBracketCount > 1 || rightBracketCount > 1) {
            StringBuilder sb = new StringBuilder();
            boolean is1stLeftBracket = true;
            for (int i = 0; i < src.length(); i++) {
                char ch = src.charAt(i);
                if (ch != '(' && ch != ')') {
                    sb.append(ch);
                } else if (ch == '(') {
                    if (is1stLeftBracket) {
                        sb.append(ch);
                        is1stLeftBracket = false;
                    }
                } else {
                    if (rightBracketCount == 1) {
                        sb.append(ch);
                    }
                    rightBracketCount--;
                }
            }
            src = sb.toString();
        }
        final String regExp = "(?<=jsonpgz\\()[^)]+";//正则表达式中的零宽断言, 该语句表示匹配 jsonpgz(xxx) 中的 xxx
        final Pattern pattern = Pattern.compile(regExp);
        final Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            updateFund(GsonUtils.fromJson(matcher.group(), Fund.class));
        }
    }

    @Override
    public void restoreTabSizes() {
        if (jTable.getColumnModel().getColumnCount() == 0) {
            return;
        }
        for (int i = 0; i < fundColumnNames.length; i++) {
            fundTabWidths[i] = jTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    @Override
    public void resetTabSize() {
        for (int i = 0; i < fundColumnNames.length; i++) {
            if (fundTabWidths[i] > 0) {
                jTable.getColumnModel().getColumn(i).setWidth(fundTabWidths[i]);
                jTable.getColumnModel().getColumn(i).setPreferredWidth(fundTabWidths[i]);
            }
        }
    }

    @Override
    public Object[][] convert2Data() {
        columnTextColors.clear();
        Object[][] data = new Object[funds.size()][fundColumnNames.length];
        for (int i = 0; i < funds.size(); i++) {
            Fund fund = funds.get(i);
            String name = fund.getName();
            if (isInHiddenMode()) {
                name = PinyinUtils.toPinyin(name);
            }
            if (i < funds.size()) {
                data[i] = new Object[]{name, fund.getFundcode(), fund.getDwjz(), fund.getGsz(),
                        fund.getEstimateNetValueRatio()};
                columnTextColors.put(i, Double.parseDouble(fund.getGszzl()));
            } else {
                break;
            }
        }
        return data;
    }

    private void updateFund(Fund fund) {
        int idx = funds.indexOf(fund);
        if (idx > -1 && idx < funds.size()) {
            funds.set(idx, fund);
        } else {
            funds.add(fund);
        }
    }

    private String getFundUrl(String code) {
        return String.format(StringResUtils.TIANTIAN_FUND_URL, code, System.currentTimeMillis());
    }
}
