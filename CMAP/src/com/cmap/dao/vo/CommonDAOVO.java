package com.cmap.dao.vo;

public class CommonDAOVO {

	private String searchValue;
	private String orderColumn;
	private String orderDirection;

	/*
	 * INFORMATION_SCHEMA.TABLES 內 TABLE 的概略數據
	 */
	private String tableInfoOfTableName;
	private long tableInfoOfRows;
	private String tableInfoOfRowFormat;
	private String tableInfoOfDataSizeInKBytes;
	private String tableInfoOfIndexSizeInKBytes;

    public String getSearchValue() {
        return searchValue;
    }
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    public String getOrderColumn() {
        return orderColumn;
    }
    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }
    public String getOrderDirection() {
        return orderDirection;
    }
    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }
    public String getTableInfoOfTableName() {
        return tableInfoOfTableName;
    }
    public void setTableInfoOfTableName(String tableInfoOfTableName) {
        this.tableInfoOfTableName = tableInfoOfTableName;
    }
    public long getTableInfoOfRows() {
        return tableInfoOfRows;
    }
    public void setTableInfoOfRows(long tableInfoOfRows) {
        this.tableInfoOfRows = tableInfoOfRows;
    }
    public String getTableInfoOfRowFormat() {
        return tableInfoOfRowFormat;
    }
    public void setTableInfoOfRowFormat(String tableInfoOfRowFormat) {
        this.tableInfoOfRowFormat = tableInfoOfRowFormat;
    }
    public String getTableInfoOfDataSizeInKBytes() {
        return tableInfoOfDataSizeInKBytes;
    }
    public void setTableInfoOfDataSizeInKBytes(String tableInfoOfDataSizeInKBytes) {
        this.tableInfoOfDataSizeInKBytes = tableInfoOfDataSizeInKBytes;
    }
    public String getTableInfoOfIndexSizeInKBytes() {
        return tableInfoOfIndexSizeInKBytes;
    }
    public void setTableInfoOfIndexSizeInKBytes(String tableInfoOfIndexSizeInKBytes) {
        this.tableInfoOfIndexSizeInKBytes = tableInfoOfIndexSizeInKBytes;
    }
}
