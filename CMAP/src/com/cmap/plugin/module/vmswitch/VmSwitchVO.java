package com.cmap.plugin.module.vmswitch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VmSwitchVO {

	private enum SwitchType {
		POWER_OFF,	// 切成備援
		POWER_ON	// 復原
	};

	private String logKey;
	private String apiVmName;
	private SwitchType switchType;
	private boolean isEPDG;

	private String restoreVersionId;
	private List<String> configContent;

	private Map<Integer, String> esxiIdNameMapping = new HashMap<>();
	private String deviceListId;

	public String getApiVmName() {
		return apiVmName;
	}
	public void setApiVmName(String apiVmName) {
		this.apiVmName = apiVmName;
	}
	public SwitchType getSwitchType() {
		return switchType;
	}
	public void setSwitchType(SwitchType switchType) {
		this.switchType = switchType;
	}
    public Map<Integer, String> getEsxiIdNameMapping() {
        return esxiIdNameMapping;
    }
    public void setEsxiIdNameMapping(Map<Integer, String> esxiIdNameMapping) {
        this.esxiIdNameMapping = esxiIdNameMapping;
    }
    public String getDeviceListId() {
        return deviceListId;
    }
    public void setDeviceListId(String deviceListId) {
        this.deviceListId = deviceListId;
    }
    public String getLogKey() {
        return logKey;
    }
    public void setLogKey(String logKey) {
        this.logKey = logKey;
    }
    public boolean isEPDG() {
        return isEPDG;
    }
    public void setEPDG(boolean isEPDG) {
        this.isEPDG = isEPDG;
    }
    public List<String> getConfigContent() {
        return configContent;
    }
    public void setConfigContent(List<String> configContent) {
        this.configContent = configContent;
    }
    public String getRestoreVersionId() {
        return restoreVersionId;
    }
    public void setRestoreVersionId(String restoreVersionId) {
        this.restoreVersionId = restoreVersionId;
    }
}
