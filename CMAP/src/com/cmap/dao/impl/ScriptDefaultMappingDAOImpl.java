package com.cmap.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.ScriptDefaultMappingDAO;
import com.cmap.dao.vo.ScriptStepDAOVO;
import com.cmap.model.ScriptListDefault;

@Repository("scriptListDefaultDAOImpl")
@Transactional
public class ScriptDefaultMappingDAOImpl extends BaseDaoHibernate implements ScriptDefaultMappingDAO {
	@Log
    private static Logger log;
	
	private List<ScriptStepDAOVO> transModel2DAOVO(List<ScriptListDefault> modelList) {
		List<ScriptStepDAOVO> voList = new ArrayList<>();

		ScriptStepDAOVO daovo;
		for (ScriptListDefault model : modelList) {
			daovo = new ScriptStepDAOVO();
			BeanUtils.copyProperties(model, daovo);
			daovo.setScriptTypeId(model.getScriptType().getScriptTypeId());
			daovo.setScriptStepOrder(String.valueOf(model.getScriptStepOrder()));
			daovo.setHeadCuttingLines(model.getHeadCuttingLines() != null ? String.valueOf(model.getHeadCuttingLines()) : null);
			daovo.setTailCuttingLines(model.getTailCuttingLines() != null ? String.valueOf(model.getTailCuttingLines()) : null);
			voList.add(daovo);
		}

		return voList;
	}

}
