package com.cmap.dao;

import java.util.List;

import com.cmap.dao.vo.ScriptDAOVO;
import com.cmap.model.ScriptStepAction;

public interface ScriptStepDAO {

	public List<ScriptDAOVO> findScriptStepByScriptInfoIdOrScriptCode(String scriptInfoId, String scriptCode);

	void delete(ScriptStepAction action);

}
