package me.bytebeats.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import me.bytebeats.tool.Utils;

import javax.swing.*;

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
        us_stock_input.setText(PropertiesComponent.getInstance().getValue(Utils.KEY_US_STOCK));
        hk_stock_input.setText(PropertiesComponent.getInstance().getValue(Utils.KEY_HK_STOCK));
        a_stock_input.setText(PropertiesComponent.getInstance().getValue(Utils.KEY_A_STOCK));
        boolean isHidden = PropertiesComponent.getInstance().getBoolean(Utils.KEY_HIDE_MODE, false);
        red_rise_green_fall.setEnabled(!isHidden);
        red_fall_green_rise.setEnabled(!isHidden);
        hide_mode_setting.setSelected(isHidden);
        red_rise_green_fall.setSelected(PropertiesComponent.getInstance().getBoolean(Utils.KEY_RED_RISE, true));
        red_fall_green_rise.setSelected(PropertiesComponent.getInstance().getBoolean(Utils.KEY_RED_FALL, false));
        return mns_setting;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(Utils.KEY_US_STOCK, us_stock_input.getText());
        PropertiesComponent.getInstance().setValue(Utils.KEY_HK_STOCK, hk_stock_input.getText());
        PropertiesComponent.getInstance().setValue(Utils.KEY_A_STOCK, a_stock_input.getText());
        PropertiesComponent.getInstance().setValue(Utils.KEY_RED_RISE, red_rise_green_fall.isSelected());
        PropertiesComponent.getInstance().setValue(Utils.KEY_RED_FALL, red_fall_green_rise.isSelected());
        PropertiesComponent.getInstance().setValue(Utils.KEY_HIDE_MODE, hide_mode_setting.isSelected());
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

    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public void cancel() {

    }
}
