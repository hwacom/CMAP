package com.cmap.plugin.module.netflowpoller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.cmap.dao.BaseDAO;
import com.cmap.model.DataPollerSetting;
import com.cmap.plugin.module.netflow.NetFlowIpStat;
import com.cmap.service.vo.DataPollerServiceVO;

public interface NetFlowTraceDAO extends BaseDAO {

	public long countNetFlowDataFromDB(NetFlowTraceVO nfVO, List<String> searchLikeField, String tableName);

	public List<Object[]> findNetFlowDataFromDB(NetFlowTraceVO nfVO, Integer startRow, Integer pageLength, List<String> searchLikeField, String tableName, String selectSql);

	public BigDecimal getTotalFlowOfQueryConditionsFromDB(NetFlowTraceVO nfVO, List<String> searchLikeField, String tableName);

	public NetFlowTraceVO findNetFlowDataFromFile(
			DataPollerSetting setting,
			Map<Integer, DataPollerServiceVO> fieldIdxMap,
			Map<String, DataPollerServiceVO> fieldVOMap,
			Map<String, NetFlowTraceVO> queryMap,
			Integer startRow,
			Integer pageLength);

	/**
	 * 取得已設定好Data_Poller_Setting的資料 (判斷方式: File_Name_Regex != Env.DEFAULT_NET_FLOW_FILE_NAME_REGEX)
	 * @return
	 */
	public List<DataPollerSetting> getHasAlreadySetUpNetFlowDataPollerInfo();

}
