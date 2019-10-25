package com.cmap.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.MibDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.MibOidMapping;
import com.cmap.model.MibValueMapping;
import com.cmap.service.MibService;
import com.cmap.service.vo.MibVO;

@Service("mibService")
@Transactional
public class MibServiceImpl extends CommonServiceImpl implements MibService {
	@Log
	private static Logger log;
	
	@Autowired
	private MibDAO mibDAO;

	@Override
	public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames) throws ServiceLayerException {
		List<MibOidMapping> retList = null;
		try {
			retList = mibDAO.findMibOidMappingByNames(oidNames);
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException();
		}
		return retList;
	}

	@Override
	public List<MibOidMapping> findMibOidMappingOfTableEntryByNameLike(String tableOidName) throws ServiceLayerException {
		List<MibOidMapping> retList = null;
		try {
			retList = mibDAO.findMibOidMappingOfTableEntryByNameLike(tableOidName);
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException();
		}
		return retList;
	}

	@Override
	public Map<String, Map<String, MibVO>> findMibValueMappingByOidTable(String oidTable) throws ServiceLayerException {
		/*
		 * retMap結構:
		 * <L1>
		 *   Key: [Oid_Name]
		 *   Value: Map<String, String>
		 *          <L2>
		 *            Key: [Entry_Value]
		 *            Value: [Entry_Value_Desc]
		 */
		Map<String, Map<String, MibVO>> retMap = null;
		try {
			List<MibValueMapping> entities = mibDAO.findMibValueMappingByOidTable(oidTable);
			
			if (entities != null && !entities.isEmpty()) {
				retMap = new HashMap<>();
				
				String oidName;
				MibVO mVO;
				String entryValue;
				Map<String, MibVO> entryMap = null;
				for (MibValueMapping entity : entities) {
					oidName = entity.getOidName();
					entryValue = entity.getEntryValue();
					
					mVO = new MibVO();
					mVO.setEntryValue(entryValue);
					mVO.setEntryValueDesc(entity.getEntryValueDesc());
					mVO.setUiPresentType(entity.getUiPresentType());
					
					if (retMap.containsKey(oidName)) {
						entryMap = retMap.get(oidName);
						
					} else {
						entryMap = new HashMap<>();
					}
					
					if (entryMap != null && retMap != null) {
						entryMap.put(entryValue, mVO);
						retMap.put(oidName, entryMap);
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException();
		}
		return retMap;
	}
}
