package me.bytebeats.ui;

import me.bytebeats.SymbolParser;
import me.bytebeats.UISettingProvider;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HkStockWindow implements SymbolParser, UISettingProvider {
    private JPanel hk_stock_window;
    private JScrollPane hk_stock_scroll;
    private JTable hk_stock_table;
    private JLabel hk_stock_timestamp;
    private JButton hk_sync;

    private AbsStockHandler handler;

    public HkStockWindow() {
        handler = new TencentStockHandler(hk_stock_table, hk_stock_timestamp);
    }

    public JPanel getHk_stock_window() {
        return hk_stock_window;
    }

    public void onInit() {
        hk_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.setHidden(isInHiddenMode());
        handler.setRedRise(isRedRise());
        handler.load(parse());
    }

    @Override
    public String prefix() {
        return "hk";//实时数据
//        return "s_hk";//简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().getHkStocks();
    }

    @Override
    public List<String> parse() {
        List<String> symbols = new ArrayList<>();
        String raw = raw();
        if (!raw.isEmpty()) {
            Arrays.stream(raw.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add(prefix() + s));
        }
        return symbols;
    }

    @Override
    public boolean isInHiddenMode() {
        return AppSettingState.getInstance().isHiddenMode();
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise();
    }

}
