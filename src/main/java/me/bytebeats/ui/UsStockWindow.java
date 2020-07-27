package me.bytebeats.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import me.bytebeats.LogUtil;
import me.bytebeats.SymbolParser;
import me.bytebeats.handler.AbsStockHandler;
import me.bytebeats.handler.TencentStockHandler;
import me.bytebeats.tool.Keys;
import me.bytebeats.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsStockWindow implements ToolWindowFactory, SymbolParser {
    private JPanel us_stock_window;
    private JScrollPane us_stock_scroll;
    private JTable us_stock_table;
    private JLabel us_stock_timestamp;
    private JButton us_refresh;

    private AbsStockHandler handler;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        LogUtil.init(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content usStock = contentFactory.createContent(us_stock_window, StringResUtils.US_STOCK, false);

        //add us stock
        toolWindow.getContentManager().addContent(usStock);
        us_refresh.addActionListener(e -> resetRefreshHandler());
    }

    @Override
    public String prefix() {
        return "s_us";
    }

    @Override
    public String raw() {
        return PropertiesComponent.getInstance().getValue(Keys.KEY_US_STOCK);
    }

    @Override
    public List<String> parse() {
        List<String> symbols = new ArrayList<>();
        String raw = raw();
        if (!raw.isEmpty()) {
            Arrays.stream(raw.split("[,; ]]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add(prefix() + s));
        }
        return symbols;
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return true;
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        resetRefreshHandler();
    }

    private void resetRefreshHandler() {
        boolean isHidden = PropertiesComponent.getInstance().getBoolean(Keys.KEY_HIDE_MODE, false);
        boolean isRedRise = PropertiesComponent.getInstance().getBoolean(Keys.KEY_RED_RISE, true);
        handler = new TencentStockHandler(us_stock_table, us_stock_timestamp);
        handler.setHidden(isHidden);
        handler.setRedRise(isRedRise);
        handler.load(parse());
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }
}
