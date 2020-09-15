package me.bytebeats.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import me.bytebeats.LogUtil;
import me.bytebeats.OnSymbolSelectedListener;
import me.bytebeats.SymbolParser;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;
import me.bytebeats.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsStockWindow implements ToolWindowFactory, SymbolParser, OnSymbolSelectedListener {
    private JPanel us_stock_window;
    private JScrollPane us_stock_scroll;
    private JTable us_stock_table;
    private JLabel us_stock_timestamp;
    private JButton us_refresh;

    private AbsStockHandler handler;

    private final HkStockWindow hkStockWindow = new HkStockWindow();
    private final ShStockWindow shStockWindow = new ShStockWindow();
    private final SzStockWindow szStockWindow = new SzStockWindow();
    private final CoreIndicesWindow indciesWindow = new CoreIndicesWindow();
    private final FundWindow fundWindow = new FundWindow();
    private final StockDetailWindow stockDetailWindow = new StockDetailWindow();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        LogUtil.init(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content usStock = contentFactory.createContent(us_stock_window, StringResUtils.US_STOCK, true);
        Content hkStock = contentFactory.createContent(hkStockWindow.getJPanel(), StringResUtils.HK_STOCK, true);
        Content shStock = contentFactory.createContent(shStockWindow.getJPanel(), StringResUtils.SH_STOCK, true);
        Content szStock = contentFactory.createContent(szStockWindow.getJPanel(), StringResUtils.SZ_STOCK, true);
        Content indicesContent = contentFactory.createContent(indciesWindow.getJPanel(), StringResUtils.INDICES, true);
        Content fundContent = contentFactory.createContent(fundWindow.getJPanel(), StringResUtils.FUND, true);
        Content stockDetailContent = contentFactory.createContent(stockDetailWindow.getJPanel(), StringResUtils.STOCK_DETAIL, true);

        //add stocks
        toolWindow.getContentManager().addContent(usStock);
        toolWindow.getContentManager().addContent(hkStock);
        toolWindow.getContentManager().addContent(shStock);
        toolWindow.getContentManager().addContent(szStock);
        toolWindow.getContentManager().addContent(indicesContent);
        toolWindow.getContentManager().addContent(fundContent);
        toolWindow.getContentManager().addContent(stockDetailContent);
        us_refresh.addActionListener(e -> refreshHandler());
    }

    @Override
    public String prefix() {
//        return "us";//实时数据
        return "s_us";//简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().getUsStocks();
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

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        handler = new TencentStockHandler(us_stock_table, us_stock_timestamp);
        handler.setOnSymbolSelectedListener(this);
        hkStockWindow.setOnSymbolSelectedListener(this);
        shStockWindow.setOnSymbolSelectedListener(this);
        szStockWindow.setOnSymbolSelectedListener(this);
        indciesWindow.setOnSymbolSelectedListener(this);
        refreshHandler();
        hkStockWindow.onInit();
        shStockWindow.onInit();
        szStockWindow.onInit();
        indciesWindow.onInit();
        fundWindow.onInit();
        stockDetailWindow.onInit();
    }

    @Override
    public void onSelected(String symbol) {
        if (symbol != null && symbol.equals(stockDetailWindow.getSymbol())) {
            return;
        }
        stockDetailWindow.setSymbol(symbol);
    }

    private void refreshHandler() {
        handler.load(parse());
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return true;
    }
}
