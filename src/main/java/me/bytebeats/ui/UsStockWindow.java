package me.bytebeats.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import me.bytebeats.LogUtil;
import me.bytebeats.SymbolParser;
import me.bytebeats.UISettingProvider;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;
import me.bytebeats.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsStockWindow implements ToolWindowFactory, SymbolParser, UISettingProvider {
    private JPanel us_stock_window;
    private JScrollPane us_stock_scroll;
    private JTable us_stock_table;
    private JLabel us_stock_timestamp;
    private JButton us_refresh;

    private AbsStockHandler handler;

    private HkStockWindow hkStockWindow = new HkStockWindow();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        LogUtil.init(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content usStock = contentFactory.createContent(us_stock_window, StringResUtils.US_STOCK, false);
        Content hkStock = contentFactory.createContent(hkStockWindow.getHk_stock_window(), StringResUtils.HK_STOCK, false);

        //add us stock
        toolWindow.getContentManager().addContent(usStock);
        toolWindow.getContentManager().addContent(hkStock);
        us_refresh.addActionListener(e -> refreshHandler());
    }

    @Override
    public String prefix() {
        return "us";//实时数据
//        return "s_us";//简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().getUsStocks();
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
    public void init(@NotNull ToolWindow toolWindow) {
        handler = new TencentStockHandler(us_stock_table, us_stock_timestamp);
        refreshHandler();
        hkStockWindow.onInit();
    }

    private void refreshHandler() {
        handler.setHidden(isInHiddenMode());
        handler.setRedRise(isRedRise());
        handler.load(parse());
    }

    @Override
    public boolean isInHiddenMode() {
        return AppSettingState.getInstance().isHiddenMode();
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise();
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return true;
    }
}
