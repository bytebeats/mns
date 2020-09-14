package me.bytebeats.ui;

import me.bytebeats.SymbolParser;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SzStockWindow implements SymbolParser {
    private JPanel sz_stock_window;
    private JScrollPane sz_stock_scroll;
    private JTable sz_stock_table;
    private JLabel sz_stock_timestamp;
    private JButton sz_sync;

    private AbsStockHandler handler;

    public SzStockWindow() {
        handler = new TencentStockHandler(sz_stock_table, sz_stock_timestamp);
    }

    public JPanel getJPanel() {
        return sz_stock_window;
    }

    public void onInit() {
        sz_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
//        return "sz";//实时数据
        return "s_sz";//简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().getSzStocks();
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
