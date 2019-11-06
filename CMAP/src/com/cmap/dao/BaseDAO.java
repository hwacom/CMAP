package com.cmap.dao;

import java.nio.file.Path;
import java.util.List;
import com.cmap.dao.vo.CommonDAOVO;

public interface BaseDAO {

    public static final String TARGET_ALL_DB = "ALL";
    public static final String TARGET_PRIMARY_DB = "PRIMARY";
    public static final String TARGET_SECONDARY_DB = "SECONDARY";

    public Object insertEntityAndGetReturnIdValue(Object entity);

	public Integer loadDataInFile(
	        String targetDB, String tableName, String filePath, String charset,
	        String fieldsTerminatedBy, String linesTerminatedBy, String extraSetStr);

	public boolean insertEntity(String targetDB, Object entity);

	public boolean insertEntity2Secondary(Object entity);

	public boolean insertEntities(String targetDB, List<? extends Object> entities);

	public boolean insertEntities2Secondary(List<? extends Object> entities);

	public boolean updateEntity(String targetDB, Object entity);

	public boolean updateEntity2Secondary(Object entity);

	public boolean updateEntities(String targetDB, List<? extends Object> entities);

	public boolean updateEntities2Secondary(List<? extends Object> entities);

	public boolean deleteEntity(String targetDB, Object entity);

	public boolean deleteEntity2Secondary(Object entity);

	public boolean deleteEntities(String targetDB, List<? extends Object> entities);

	public boolean deleteEntities2Secondary(List<? extends Object> entities);

	public boolean insertEntitiesByNativeSQL(List<String> nativeSQLs);

	public boolean deleteEntitiesByNativeSQL(List<String> nativeSQLs);

	public boolean insertEntities2File(Path filePath, List<String> recordList, boolean appendFile);

	public CommonDAOVO getTableInformation(String targetDB, String tableName);
}
