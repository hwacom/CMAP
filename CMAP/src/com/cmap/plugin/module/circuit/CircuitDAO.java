package com.cmap.plugin.module.circuit;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface CircuitDAO extends BaseDAO {

    /**
     * 取得符合條件資料(頁面使用)
     * @param cVO
     * @param startRow
     * @param pageLength
     * @return 
     */
    public List<ModuleCircuitE1OpenList> findModuleE1OpenList(CircuitVO cVO);

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
