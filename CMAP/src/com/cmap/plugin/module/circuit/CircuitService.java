package com.cmap.plugin.module.circuit;

import java.util.List;

import com.cmap.exception.ServiceLayerException;

public interface CircuitService {

    /**
     * 查詢符合條件資料(頁面使用)
     * @param brVO
     * @return
     * @throws ServiceLayerException
     */
    public List<CircuitVO> findModuleCircuitDiagramInfo(CircuitVO cVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param brVO
     * @return
     * @throws ServiceLayerException
     */
    public List<ModuleCircuitDiagramInfo> findModuleCircuitDiagramInfo(CircuitVO cVO) throws ServiceLayerException;

	boolean saveOrUpdateSetting(List<ModuleCircuitDiagramSetting> entities);

	public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramInfoSetting(String e1Ip);

	boolean deleteSetting(String e1Ip);
    
}
