package me.bytebeats.mns.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import me.bytebeats.mns.OnSymbolSelectedListener;
import me.bytebeats.mns.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;

public class MainWindow implements ToolWindowFactory, OnSymbolSelectedListener {
    private final CoreIndicesWindow indicesWindow = new CoreIndicesWindow();
    private final StockWindow stockWindow = new StockWindow();
    private final FundWindow fundWindow = new FundWindow();
    private final DigitalCurrencyWindow digitalCurrencyWindow = new DigitalCurrencyWindow();
    private final StockDetailWindow stockDetailWindow = new StockDetailWindow();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content indicesContent = contentFactory.createContent(indicesWindow.getJPanel(), StringResUtils.INDICES, true);
        Content stockContent = contentFactory.createContent(stockWindow.getJPanel(), StringResUtils.STOCK, true);
        Content fundContent = contentFactory.createContent(fundWindow.getJPanel(), StringResUtils.FUNDS, true);
        Content cryptoCurrencyContent = contentFactory.createContent(digitalCurrencyWindow.getJPanel(), StringResUtils.CRYPTO_CURRENCIES, true);
        Content stockDetailContent = contentFactory.createContent(stockDetailWindow.getJPanel(), StringResUtils.STOCK_DETAIL, true);

        //add stocks
        toolWindow.getContentManager().addContent(indicesContent);
        toolWindow.getContentManager().addContent(stockContent);
        toolWindow.getContentManager().addContent(fundContent);
        toolWindow.getContentManager().addContent(cryptoCurrencyContent);
        toolWindow.getContentManager().addContent(stockDetailContent);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        indicesWindow.setOnSymbolSelectedListener(this);
        stockWindow.setOnSymbolSelectedListener(this);
        stockWindow.onInit();
        indicesWindow.onInit();
        fundWindow.onInit();
        digitalCurrencyWindow.onInit();
        stockDetailWindow.onInit();
    }

    @Override
    public void onSelected(String symbol) {
        if (symbol != null && symbol.equals(stockDetailWindow.getSymbol())) {
            return;
        }
        stockDetailWindow.setSymbol(symbol);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }
}
