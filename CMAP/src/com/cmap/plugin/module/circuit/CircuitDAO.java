package com.cmap.plugin.module.circuit;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface CircuitDAO extends BaseDAO {

    /**
     * 取得符合條件資料(頁面使用)
     * @param cVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleBlockedList(CircuitVO cVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件資料
     * @param cVO
     * @return
     */
    public List<ModuleCircuitDiagramInfo> findModuleInfo(CircuitVO cVO);

	void saveOrUpdateSetting(List<ModuleCircuitDiagramSetting> entities);

	public List<ModuleCircuitDiagramSetting> findModuleSettingByIp(String e1Ip);

	void deleteSetting(String e1Ip);
    
}
