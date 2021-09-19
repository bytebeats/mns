package me.bytebeats.mns.ui;

import me.bytebeats.mns.OnSymbolSelectedListener;
import me.bytebeats.mns.SymbolParser;
import me.bytebeats.mns.handler.TencentIndexHandler;

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

    private final TencentIndexHandler handler;

    public CoreIndicesWindow() {
        handler = new TencentIndexHandler(indices_table, indices_timestamp);
//        handler.setOnItemDoubleClickListener((s, xOnScreen, yOnScreen) -> PopupsUtil.INSTANCE.popupStockChart(s, StockChartType.Minute, new Point(xOnScreen, yOnScreen)));
//        handler.setOnItemRightClickListener(new OnItemRightClickListener<String>() {
//            @Override
//            public void onItemRightClick(String s, int xOnScreen, int yOnScreen) {
//                JBPopupFactory.getInstance()
//                        .createListPopup(new BaseListPopupStep<StockChartType>("K线图", StockChartType.values()) {
//                            @Override
//                            public @NotNull
//                            String getTextFor(StockChartType value) {
//                                return value.getDescription();
//                            }
//
//                            @Override
//                            public @Nullable
//                            PopupStep<?> onChosen(StockChartType selectedValue, boolean finalChoice) {
//                                PopupsUtil.INSTANCE.popupStockChart(s, selectedValue, new Point(xOnScreen, yOnScreen));
//                                return super.onChosen(selectedValue, finalChoice);
//                            }
//                        })
//                        .show(RelativePoint.fromScreen(new Point(xOnScreen, yOnScreen)));
//            }
//        });
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        handler.setOnSymbolSelectedListener(listener);
    }

    public JPanel getJPanel() {
        return indices_window;
    }

    public void onInit() {
        indices_sync.addActionListener(e -> {
            syncRefresh();
        });
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
//        return "";//实时数据
        return "s_";//简要信息
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
