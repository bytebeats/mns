package me.bytebeats.ui;

import me.bytebeats.OnSymbolSelectedListener;
import me.bytebeats.SymbolParser;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShStockWindow implements SymbolParser {
    private JPanel sh_stock_window;
    private JScrollPane sh_stock_scroll;
    private JTable sh_stock_table;
    private JLabel sh_stock_timestamp;
    private JButton sh_sync;

    private AbsStockHandler handler;

    public ShStockWindow() {
        handler = new TencentStockHandler(sh_stock_table, sh_stock_timestamp);
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        handler.setOnSymbolSelectedListener(listener);
    }

    public JPanel getJPanel() {
        return sh_stock_window;
    }

    public void onInit() {
        sh_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
//        return "sh";//实时数据
        return "s_sh";//简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().getShStocks();
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
