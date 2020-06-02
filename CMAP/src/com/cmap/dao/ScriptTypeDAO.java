package com.cmap.dao;

import java.util.List;

import com.cmap.model.ScriptType;

public interface ScriptTypeDAO {

	public List<ScriptType> findScriptTypeByDefaultFlag(String defaultFlag);

	void saveOrUpdateScriptTypeByCode(ScriptType type);

	public ScriptType findScriptTypeNotDefaultByCode(String scriptTypeCode);
}
