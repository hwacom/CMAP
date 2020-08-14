package com.cmap.service;

import java.util.List;
import java.util.Map;

import com.cmap.comm.enums.ScriptType;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ScriptInfo;
import com.cmap.service.vo.ScriptServiceVO;

public interface ScriptService {

    /**
     * 取得預設腳本資料並替換掉指令中參數部分，回傳替換好的指令List(備份、還原)
     * @param deviceListId
     * @param type
     * @return
     * @throws ServiceLayerException
     */
	public List<ScriptServiceVO> loadDefaultScript(String deviceListId, ScriptType type) throws ServiceLayerException;

	/**
	 * 取得預設腳本的 Script_Info 資料
	 * @param deviceId
	 * @param type
	 * @param undoFlag
	 * @return
	 * @throws ServiceLayerException
	 */
	public ScriptInfo loadDefaultScriptInfo(String deviceModel, String scriptType, String undoFlag) throws ServiceLayerException;

	/**
	 * 取得指定腳本 Action / Check 指令內容
	 * @param scriptInfoId
	 * @param scriptCode
	 * @param varMapList
	 * @param scripts
	 * @param scriptMode (Constants.SCRIPT_MODE_ACTION / Constants.SCRIPT_MODE_CHECK)
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<ScriptServiceVO> loadSpecifiedScript(String scriptInfoId, String scriptCode, List<Map<String, String>> varMapList, List<ScriptServiceVO> scripts, String scriptMode) throws ServiceLayerException;

	/**
	 * 查找[腳本類別 + 設備系統版本]對應的預設腳本資訊
	 * @param scriptType
	 * @param deviceModel
	 * @return
	 * @throws ServiceLayerException
	 */
	public ScriptServiceVO findDefaultScriptInfoByScriptTypeAndSystemVersion(String scriptType, String deviceModel);

	/**
	 * 依查詢條件查詢符合的資料筆數
	 * @param ssVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public long countScriptInfo(ScriptServiceVO ssVO) throws ServiceLayerException;

	/**
	 * 依查詢條件查詢符合的資料
	 * @param ssVO
	 * @param startRow
	 * @param pageLength
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<ScriptServiceVO> findScriptInfo(ScriptServiceVO ssVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

	/**
	 * 以 Script_Info_Id 查找資料
	 * @param scriptInfoId
	 * @return
	 * @throws ServiceLayerException
	 */
	public ScriptServiceVO getScriptInfoByScriptInfoId(String scriptInfoId) throws ServiceLayerException;

	/**
	 * 以 Script_Code 查找資料 (查詢失敗跳出錯誤，for前端頁面使用)
	 * @param scriptCode
	 * @return
	 * @throws ServiceLayerException
	 */
	public ScriptServiceVO getScriptInfoByScriptCode(String scriptCode) throws ServiceLayerException;

	public String deleteScriptInfoByIdOrCode(String scriptInfoId, String scriptCode) throws ServiceLayerException;

	public com.cmap.model.ScriptType getScriptTypeByCode(String scriptTypeCode);

	String addOrModifyScriptInfo(ScriptInfo info) throws ServiceLayerException;

	ScriptInfo getScriptInfoEntityByScriptCode(String scriptCode) throws ServiceLayerException;

	String deleteScriptTypeByCode(String scriptTypeCode) throws ServiceLayerException;

	String addOrModifyScriptType(com.cmap.model.ScriptType type) throws ServiceLayerException;

	List<ScriptInfo> getScriptInfoByScriptTypeCode(String scriptTypeCode, String deviceModel)
			throws ServiceLayerException;

}
