package com.cmap.dao;

import java.util.List;

import com.cmap.dao.vo.ScriptStepDAOVO;
import com.cmap.model.ScriptStepAction;

public interface ScriptStepDAO {

	public List<ScriptStepDAOVO> findScriptStepByScriptInfoIdOrScriptCode(String scriptInfoId, String scriptCode);

	void delete(ScriptStepAction action);

}
