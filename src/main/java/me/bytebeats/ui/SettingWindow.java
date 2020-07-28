package me.bytebeats.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import me.bytebeats.tool.Keys;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class SettingWindow implements Configurable {
    private JPanel mns_setting;
    private JTextField us_stock_input;
    private JTextField hk_stock_input;
    private JTextField a_stock_input;
    private JLabel us_stock;
    private JLabel hk_stock;
    private JLabel a_stock;
    private JCheckBox mkt_setting;
    private JRadioButton red_rise_green_fall;
    private JRadioButton red_fall_green_rise;
    private JPanel mkt_setting_radio;
    private JLabel hide_mode_desc;
    private JCheckBox hide_mode_setting;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return mns_setting.getToolTipText();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        us_stock_input.setText(PropertiesComponent.getInstance().getValue(Keys.KEY_US_STOCK));
        hk_stock_input.setText(PropertiesComponent.getInstance().getValue(Keys.KEY_HK_STOCK));
        a_stock_input.setText(PropertiesComponent.getInstance().getValue(Keys.KEY_A_STOCK));
        boolean isHidden = PropertiesComponent.getInstance().getBoolean(Keys.KEY_HIDE_MODE, false);
        red_rise_green_fall.setEnabled(!isHidden);
        red_fall_green_rise.setEnabled(!isHidden);
        hide_mode_setting.setSelected(isHidden);
        red_rise_green_fall.setSelected(PropertiesComponent.getInstance().getBoolean(Keys.KEY_RED_RISE, true));
        red_rise_green_fall.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                red_fall_green_rise.setSelected(false);
            }
        });
        red_fall_green_rise.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                red_rise_green_fall.setSelected(false);
            }
        });
        hide_mode_setting.addItemListener(e -> {
            boolean hidden = e.getStateChange() == ItemEvent.SELECTED;
            mkt_setting.setEnabled(!hidden);
            red_rise_green_fall.setEnabled(!hidden);
            red_fall_green_rise.setEnabled(!hidden);
        });
        mkt_setting.addItemListener(e -> {
            //do nothing here, it should be a JLabel other than a JCheckBox.
        });
        return mns_setting;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(Keys.KEY_US_STOCK, us_stock_input.getText());
        PropertiesComponent.getInstance().setValue(Keys.KEY_HK_STOCK, hk_stock_input.getText());
        PropertiesComponent.getInstance().setValue(Keys.KEY_A_STOCK, a_stock_input.getText());
        PropertiesComponent.getInstance().setValue(Keys.KEY_RED_RISE, red_rise_green_fall.isSelected());
        PropertiesComponent.getInstance().setValue(Keys.KEY_HIDE_MODE, hide_mode_setting.isSelected());
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mns_setting;
    }

    @Override
    public void reset() {
        hide_mode_setting.setSelected(false);
        red_rise_green_fall.setEnabled(true);
        red_rise_green_fall.setSelected(true);
        red_fall_green_rise.setEnabled(false);
        red_fall_green_rise.setSelected(false);
    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public void cancel() {

    }
}
