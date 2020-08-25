package me.bytebeats.ui;

import me.bytebeats.SymbolParser;
import me.bytebeats.handler.TencentIndexHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoreIndicesWindow implements SymbolParser {
    private JPanel indices_window;
    private JScrollPane indices_scroll;
    private JTable indices_table;
    private JLabel indices_timestamp;
    private JButton indices_sync;

    private TencentIndexHandler handler;

    public CoreIndicesWindow() {
        handler = new TencentIndexHandler(indices_table, indices_timestamp);
    }

    public JPanel getJPanel() {
        return indices_window;
    }

    public void onInit() {
        indices_sync.addActionListener(e -> syncRefresh());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
        return "";//实时数据
//        return "s_";//简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.ALL_INDICES;
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
