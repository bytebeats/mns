package me.bytebeats.ui;

import me.bytebeats.SymbolParser;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HkStockWindow implements SymbolParser {
    private JPanel hk_stock_window;
    private JScrollPane hk_stock_scroll;
    private JTable hk_stock_table;
    private JLabel hk_stock_timestamp;
    private JButton hk_sync;

    private AbsStockHandler handler;

    public HkStockWindow() {
        handler = new TencentStockHandler(hk_stock_table, hk_stock_timestamp);
    }

    public JPanel getJPanel() {
        return hk_stock_window;
    }

    public void onInit() {
        hk_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
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
        assert raw != null;
        if (!raw.isEmpty()) {
            Arrays.stream(raw.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add(prefix() + s));
        }
        return symbols;
    }
}
