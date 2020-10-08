package me.bytebeats.ui;

import me.bytebeats.SymbolParser;
import me.bytebeats.handler.TianTianFundHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/bytebeats">bytebeats</a>
 * @email <happychinapc@gmail.com>
 * @since 2020/8/25 11:32
 */
public class FundWindow implements SymbolParser {
    private JPanel fund_window;
    private JScrollPane fund_scroll;
    private JTable fund_table;
    private JLabel fund_timestamp;
    private JButton fund_sync;
    private JButton fund_search;

    private final TianTianFundHandler handler;

    private FundSearchDialog fundSearchDialog;

    public FundWindow() {
        handler = new TianTianFundHandler(fund_table, fund_timestamp);
    }

    public JPanel getJPanel() {
        return fund_window;
    }

    public void onInit() {
        fund_sync.addActionListener(e -> syncRefresh());
        fund_search.addActionListener(e -> popSearchDialog());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
        return "";
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().getDailyFunds();
    }

    @Override
    public List<String> parse() {
        List<String> symbols = new ArrayList<>();
        String raw = raw();
        assert raw != null;
        if (!raw.isEmpty()) {
            Arrays.stream(raw.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add(prefix() + s));
        }
        return symbols;
    }

    private void popSearchDialog() {
        if (fundSearchDialog == null) {
            fundSearchDialog = new FundSearchDialog();
            fundSearchDialog.setCallback(() -> syncRefresh());
            fundSearchDialog.setLocationRelativeTo(null);
        }
        fundSearchDialog.pack();
        fundSearchDialog.setVisible(true);
    }
}