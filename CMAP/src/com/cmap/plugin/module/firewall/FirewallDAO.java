package com.cmap.plugin.module.firewall;

import java.util.List;

public interface FirewallDAO {

    /**
     * 查找 Module_Firewall_Log_Setting 設定資料
     * @param settingName
     * @return
     */
    public List<ModuleFirewallLogSetting> getFirewallLogSetting(String settingName);

    public long countFirewallLogFromDB(FirewallVO fVO, List<String> searchLikeField, String tableName);

    public List<Object[]> findFirewallLogFromDB(
            FirewallVO fVO, Integer startRow, Integer pageLength, List<String> searchLikeField,
            String tableName, String selectSql);

}
