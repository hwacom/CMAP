package com.cmap.dao;

import java.nio.file.Path;
import java.util.List;

public interface BaseDAO {

    public static final String TARGET_PRIMARY_DB = "PRIMARY";
    public static final String TARGET_SECONDARY_DB = "SECONDARY";

    public Object insertEntityAndGetReturnIdValue(Object entity);

	public boolean insertEntity(Object entity);

	public Integer loadDataInFile(
	        String targetDB, String tableName, String filePath, String charset,
	        String fieldsTerminatedBy, String linesTerminatedBy, String extraSetStr);

	public boolean insertEntities(List<? extends Object> entities);

	public boolean updateEntity(Object entity);

	public boolean updateEntities(List<? extends Object> entities);

	public boolean deleteEntity(Object entity);

	public boolean deleteEntities(List<? extends Object> entities);

	public boolean insertEntitiesByNativeSQL(List<String> nativeSQLs);

	public boolean deleteEntitiesByNativeSQL(List<String> nativeSQLs);

	public boolean insertEntities2File(Path filePath, List<String> recordList, boolean appendFile);
}
