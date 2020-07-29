package me.bytebeats.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "me.bytebeats.ui.AppSettingState", storages = {@Storage("mns_plugin_setting.xml")})
public class AppSettingState implements PersistentStateComponent<AppSettingState> {

    private boolean isRedRise = true;
    private boolean isHiddenMode = false;
    private String usStocks = "AAPL";
    private String hkStocks = "00981;09988;09618";
    private String shStocks = "600036";
    private String szStocks = "002352";

    public static AppSettingState getInstance() {
        return ServiceManager.getService(AppSettingState.class);
    }

    public boolean deepCopy(@Nullable AppSettingState instance) {
        if (instance == null) {
            return false;
        } else {
            isRedRise = instance.isRedRise;
            isHiddenMode = instance.isHiddenMode;
            usStocks = instance.usStocks;
            hkStocks = instance.hkStocks;
            shStocks = instance.shStocks;
            szStocks = instance.szStocks;
            return true;
        }
    }

    @Nullable
    @Override
    public AppSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingState appSettingState) {
        XmlSerializerUtil.copyBean(appSettingState, this);
    }

    public boolean isRedRise() {
        return isRedRise;
    }

    public void setRedRise(boolean redRise) {
        isRedRise = redRise;
    }

    public boolean isHiddenMode() {
        return isHiddenMode;
    }

    public void setHiddenMode(boolean hiddenMode) {
        isHiddenMode = hiddenMode;
    }

    public String getUsStocks() {
        return usStocks;
    }

    public void setUsStocks(String usStocks) {
        this.usStocks = usStocks;
    }

    public String getHkStocks() {
        return hkStocks;
    }

    public void setHkStocks(String hkStocks) {
        this.hkStocks = hkStocks;
    }

    public String getShStocks() {
        return shStocks;
    }

    public void setShStocks(String shStocks) {
        this.shStocks = shStocks;
    }

    public String getSzStocks() {
        return szStocks;
    }

    public void setSzStocks(String szStocks) {
        this.szStocks = szStocks;
    }
}
