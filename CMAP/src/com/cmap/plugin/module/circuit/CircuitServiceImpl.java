package com.cmap.plugin.module.circuit;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.impl.CommonServiceImpl;

@Service("circuitService")
@Transactional
public class CircuitServiceImpl extends CommonServiceImpl implements CircuitService {
    @Log
    private static Logger log;

    @Autowired
    private CircuitDAO circuitDAO;


	@Override
	public List<CircuitVO> findModuleCircuitDiagramInfo(CircuitVO cVO, Integer startRow, Integer pageLength)
			throws ServiceLayerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModuleCircuitDiagramInfo> findModuleCircuitDiagramInfo(CircuitVO cVO) throws ServiceLayerException {
		return circuitDAO.findModuleInfo(cVO);
	}

	@Override
	public boolean saveOrUpdateSetting(List<ModuleCircuitDiagramSetting> entities)  {
		circuitDAO.saveOrUpdateSetting(entities);
		return true;
	}
	
	@Override
	public boolean deleteSetting(String e1Ip)  {
		circuitDAO.deleteSetting(e1Ip);
		return true;
	}
	
	@Override
	public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramInfoSetting(String e1Ip) {
		return circuitDAO.findModuleSettingByIp(e1Ip);
	}
}
