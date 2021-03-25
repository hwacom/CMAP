package com.cmap.plugin.module.circuit;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;
import com.fasterxml.jackson.databind.JsonNode;

public interface CircuitService {

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

	List<ModuleCircuitE1OpenList> findModuleE1OpenList(CircuitVO cVO);

	public Map<String, Object> doE1OpenProvision(JsonNode jsonData, String ip);
    
}
