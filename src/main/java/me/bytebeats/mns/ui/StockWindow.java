package me.bytebeats.mns.ui;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.ui.awt.RelativePoint;
import me.bytebeats.mns.OnSymbolSelectedListener;
import me.bytebeats.mns.enumation.StockChartType;
import me.bytebeats.mns.handler.AbsStockHandler;
import me.bytebeats.mns.handler.TencentStockHandler;
import me.bytebeats.mns.listener.OnItemRightClickListener;
import me.bytebeats.mns.tool.PopupsUtil;
import me.bytebeats.mns.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StockWindow {
    private JPanel stock_window;
    private JScrollPane stock_scroll;
    private JTable stock_table;
    private JLabel stock_timestamp;
    private JButton stock_refresh;
    private JComboBox<String> stock_market_list;

    private final AbsStockHandler handler;

    private static final String[] markets = {StringResUtils.STOCK_ALL, StringResUtils.STOCK_US, StringResUtils.STOCK_HK, StringResUtils.STOCK_CN};

    public StockWindow() {
        handler = new TencentStockHandler(stock_table, stock_timestamp) {
            @Override
            protected String getTipText() {
                return Objects.requireNonNull(stock_market_list.getSelectedItem()).toString();
            }
        };
        handler.setOnItemDoubleClickListener((s, xOnScreen, yOnScreen) -> PopupsUtil.INSTANCE.popupStockChart(s, StockChartType.Minute, new Point(xOnScreen, yOnScreen)));
        handler.setOnItemRightClickListener(new OnItemRightClickListener<String>() {
            @Override
            public void onItemRightClick(String s, int xOnScreen, int yOnScreen) {
                JBPopupFactory.getInstance()
                        .createListPopup(new BaseListPopupStep<StockChartType>("K线图", StockChartType.values()) {
                            @Override
                            public @NotNull
                            String getTextFor(StockChartType value) {
                                return value.getDescription();
                            }

                            @Override
                            public @Nullable
                            PopupStep<?> onChosen(StockChartType selectedValue, boolean finalChoice) {
                                PopupsUtil.INSTANCE.popupStockChart(s, selectedValue, new Point(xOnScreen, yOnScreen));
                                return super.onChosen(selectedValue, finalChoice);
                            }
                        })
                        .show(RelativePoint.fromScreen(new Point(xOnScreen, yOnScreen)));
            }
        });
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        handler.setOnSymbolSelectedListener(listener);
    }

    public JPanel getJPanel() {
        return stock_window;
    }

    public void onInit() {
        stock_market_list.removeAllItems();
        for (String market : markets) {
            stock_market_list.addItem(market);
        }
        stock_market_list.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                handler.stop();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                syncRefresh();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        stock_market_list.setSelectedIndex(0);
        stock_refresh.addActionListener(e -> {
            handler.stop();
            syncRefresh();
        });
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    public List<String> parse() {
        List<String> symbols = new ArrayList<>();
        switch (stock_market_list.getSelectedIndex()) {
            case 1:
                Arrays.stream(AppSettingState.getInstance().usStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_us" + s));
                break;
            case 2:
                Arrays.stream(AppSettingState.getInstance().hkStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_hk" + s));
                break;
            case 3:
                Arrays.stream(AppSettingState.getInstance().shStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sh" + s));
                Arrays.stream(AppSettingState.getInstance().szStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sz" + s));
                break;
            default:
                Arrays.stream(AppSettingState.getInstance().usStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_us" + s));
                Arrays.stream(AppSettingState.getInstance().hkStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_hk" + s));
                Arrays.stream(AppSettingState.getInstance().shStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sh" + s));
                Arrays.stream(AppSettingState.getInstance().szStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sz" + s));
                break;
        }
        return symbols;
    }
}
