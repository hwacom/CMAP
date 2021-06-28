package com.cmap.plugin.module.circuit;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;
import com.fasterxml.jackson.databind.JsonNode;

public interface CircuitService {

    /**
     * 查詢符合條件資料 For API查詢
     * @param brVO
     * @return
     * @throws ServiceLayerException
     */
    public List<Map<String, Object>> findModuleCircuitDiagramInfo(CircuitVO cVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param cVO
     * @return
     */    
    public List<ModuleCircuitDiagramInfo> findModuleCircuitDiagramInfoByVO(CircuitVO cVO);
    
    public List<CircuitOpenListVO> findModuleE1OpenList(CircuitOpenListVO cVO);
    
    public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramSetting(CircuitOpenListVO cVO);
    
    public boolean saveOrUpdateCircuitData(List<Object> entities);

    public boolean deleteCircuitData(List<Object> entities);

	public Map<String, Object> doE1OpenProvision(JsonNode jsonData);

	Map<String, Object> doL3BGPCircuitOpenProvision(JsonNode jsonData);

}
