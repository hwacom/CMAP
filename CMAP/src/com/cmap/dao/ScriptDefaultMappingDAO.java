package com.cmap.dao;

import com.cmap.comm.enums.ScriptType;
import com.cmap.dao.vo.ScriptStepDAOVO;

public interface ScriptDefaultMappingDAO {

	public long countScriptList(ScriptStepDAOVO slDAOVO);

	public String findDefaultScriptCodeBySystemVersion(ScriptType scriptType, String systemVersion);
}
