package com.cmap.service;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;
import com.cmap.model.MibOidMapping;
import com.cmap.service.vo.MibVO;

public interface MibService extends CommonService {

	/**
	 * 查找 MIB_OID_MAPPING by 多組 oidName
	 * @param oidNames
	 * @return
	 */
	public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames) throws ServiceLayerException;
    
	/**
	 * 查找 MIB_OID_MAPPING by oidName like (用來查找某個 oid table 底下設定的 entry)
	 * @param tableOidName
	 * @return
	 */
    public List<MibOidMapping> findMibOidMappingOfTableEntryByNameLike(String tableOidName) throws ServiceLayerException;
   
    /**
     * 查找 MIB_VALUE_MAPPING by oid table 名稱
     * @param oidTable
     * @return
     */
    public Map<String, Map<String, MibVO>> findMibValueMappingByOidTable(String oidTable) throws ServiceLayerException;
}
