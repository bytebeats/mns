package me.bytebeats.mns.ui;

import me.bytebeats.mns.SymbolParser;
import me.bytebeats.mns.handler.SinaDigitalCurrencyHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/bytebeats">bytebeats</a>
 * @email <happychinapc@gmail.com>
 * @since 2021/9/17 11:32
 */
public class DigitalCurrencyWindow implements SymbolParser {
    private JPanel digital_currency_window;
    private JScrollPane digital_currency_scroll;
    private JTable digital_currency_table;
    private JLabel digital_currency_timestamp;
    private JButton digital_currency_sync;
    private JButton digital_currency_search;

    private final SinaDigitalCurrencyHandler handler;

    private FundSearchDialog fundSearchDialog;

    private String fundSymbols;

    public DigitalCurrencyWindow() {
        handler = new SinaDigitalCurrencyHandler(digital_currency_table, digital_currency_timestamp);
    }

    public JPanel getJPanel() {
        return digital_currency_window;
    }

    public void onInit() {
        fundSymbols = AppSettingState.getInstance().dailyFunds;
        digital_currency_sync.addActionListener(e -> {
            handler.stop();
            syncRefresh();
        });
        digital_currency_search.addActionListener(e -> popSearchDialog());
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(parse());
    }

    @Override
    public String prefix() {
        return "";
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().cryptoCurrencies;
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

    private void popSearchDialog() {
        if (fundSearchDialog == null) {
            fundSearchDialog = new FundSearchDialog();
            fundSearchDialog.setCallback(() -> {
                // do nothing here
            });
            fundSearchDialog.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                    handler.stop();
                }

                @Override
                public void windowClosing(WindowEvent e) {

                }

                @Override
                public void windowClosed(WindowEvent e) {
                    fundSymbols = AppSettingState.getInstance().dailyFunds;
                    syncRefresh();
                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
        }
        if (fundSearchDialog.isVisible()) {
            return;
        }
        fundSearchDialog.pack();
        Dimension screenerSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screenerSize.getWidth() / 2 - fundSearchDialog.getWidth() / 2);
        int y = (int) (screenerSize.getHeight() / 2 - fundSearchDialog.getHeight() / 2);
        fundSearchDialog.setLocation(x, y);
        fundSearchDialog.setVisible(true);
    }
}