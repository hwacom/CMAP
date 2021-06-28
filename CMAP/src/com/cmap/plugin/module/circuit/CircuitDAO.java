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
    public List<ModuleCircuitE1OpenList> findModuleE1OpenList(CircuitOpenListVO cVO);

    /**
     * 取得符合條件資料 For API查詢
     * @param cVO
     * @return
     */
    public List<Object[]> findModuleInfoAndList(CircuitVO cVO);

    /**
     * 取得符合條件資料
     * @param cVO
     * @return
     */
    public List<ModuleCircuitDiagramInfo> findModuleInfoAndListByVO(CircuitVO cVO);
	
    public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramSetting(CircuitOpenListVO cVO);
    
    public void saveOrUpdateCircuitData(List<Object> entityList);

    public void deleteCircuitData(List<Object> entities);

}
