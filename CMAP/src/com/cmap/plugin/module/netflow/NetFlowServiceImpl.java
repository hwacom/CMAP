package com.cmap.plugin.module.netflow;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.DataPollerDAO;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.PrtgDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DataPollerMapping;
import com.cmap.model.DataPollerSetting;
import com.cmap.model.DeviceList;
import com.cmap.model.PrtgUserRightSetting;
import com.cmap.service.DataPollerService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.CommonServiceVO;
import com.cmap.service.vo.DataPollerServiceVO;

@Service("netFlowService")
public class NetFlowServiceImpl extends CommonServiceImpl implements NetFlowService {
	@Log
	private static Logger log;

	@Autowired
	private NetFlowDAO netFlowDAO;

	@Autowired
	private DataPollerService dataPollerService;

	@Autowired
	private DataPollerDAO dataPollerDAO;

	@Autowired
	private DeviceDAO deviceDAO;

	@Autowired
	private PrtgDAO prtgDAO;
	
	//是否查詢條件為sensorId
	private boolean isSensorSearchMode = StringUtils.isNotBlank(Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR) && Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR.equalsIgnoreCase(Constants.DATA_Y);
			
//	private String getTodayTableName() {
//		String tableName = Env.DATA_POLLER_NET_FLOW_TABLE_BASE_NAME;
//		/*
//		 * Y181207, Ken Lin
//		 * 因資料量過於龐大，拆分不同星期寫入不同TABLE，一張TABLE儲存一天資料
//		 */
//		Calendar cal = Calendar.getInstance();
//		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);	//取得當前系統時間是星期幾 (Sunday=1、Monday=2、...)
//		tableName += "_" + StringUtils.leftPad(String.valueOf(dayOfWeek), 3, "0"); //TABLE流水編碼部分補0成3碼(ex:1→001)
//
//		return tableName;
//	}

//	private String getSpecifyDayTableName(String date) throws ServiceLayerException {
//		String tableName = Env.DATA_POLLER_NET_FLOW_TABLE_BASE_NAME;
//		try {
//			Date queryDate = Constants.FORMAT_YYYY_MM_DD.parse(date);
//			/*
//			 * Y181207, Ken Lin
//			 * 因資料量過於龐大，拆分不同星期寫入不同TABLE，一張TABLE儲存一天資料
//			 */
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(queryDate);
//
//			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);	//取得當前系統時間是星期幾 (Sunday=1、Monday=2、...)
//			tableName += "_" + StringUtils.leftPad(String.valueOf(dayOfWeek), 3, "0"); //TABLE流水編碼部分補0成3碼(ex:1→001)
//
//		} catch (ParseException e) {
//			log.error(e.toString(), e);
//			throw new ServiceLayerException("轉換查詢日期成Date物件時異常，queryDate >> " + date);
//		}
//
//		return tableName;
//	}

//	private String getGroupIdMapping2TableName(String groupId) throws ServiceLayerException {
//		String tableName = null;
//		try {
//			tableName = netFlowDAO.findTargetTableNameByGroupId(groupId);
//
//			if (StringUtils.isBlank(tableName)) {
//				throw new ServiceLayerException("查詢groupId對應TableName為空，groupId >> " + groupId);
//			}
//
//		} catch (ServiceLayerException sle) {
//			log.error(sle.toString(), sle);
//
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			throw new ServiceLayerException("取得groupId對應TableName失敗，groupId >> " + groupId);
//		}
//
//		return tableName;
//	}

//	private String getQueryTableName(NetFlowVO nfVO) throws ServiceLayerException {
//		final String groupId = nfVO.getQueryGroupId();
//		String recordBy = dataPollerService.getRecordBySetting(Constants.DATA_TYPE_OF_NET_FLOW);
//
//		String queryTableName = null;
//
//		switch (recordBy) {
//			case Constants.RECORD_BY_DAY:
//				queryTableName = getSpecifyDayTableName(nfVO.getQueryDateBegin());
//				break;
//
//			case Constants.RECORD_BY_MAPPING:
//				queryTableName = getGroupIdMapping2TableName(groupId);
//				break;
//		}
//
//		return queryTableName;
//	}

//	@Override
//	public long countNetFlowRecordFromDB(NetFlowVO nfVO, List<String> searchLikeField) throws ServiceLayerException {
//		long retCount = 0;
//		try {
//			retCount = netFlowDAO.countNetFlowDataFromDB(nfVO, searchLikeField, getQueryTableName(nfVO));
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			throw new ServiceLayerException("查詢失敗，請重新操作");
//		}
//		return retCount;
//	}

//	@Override
//	public List<NetFlowVO> findNetFlowRecordFromDB(NetFlowVO nfVO, Integer startRow, Integer pageLength, List<String> searchLikeField) throws ServiceLayerException {
//		List<NetFlowVO> retList = new ArrayList<>();
//		try {
//			List<String> tableTitleField = dataPollerService.getFieldName(Env.SETTING_ID_OF_NET_FLOW, DataPollerService.FIELD_TYPE_TARGET);
//			StringBuffer queryFieldsSQL = new StringBuffer();
//
//			for (int i=0; i<tableTitleField.size(); i++) {
//				String fieldName = tableTitleField.get(i);
//				queryFieldsSQL.append("`").append(fieldName).append("`, ");
//
//				/*
//				if (i < tableTitleField.size() - 1) {
//					queryFieldsSQL.append(", ");
//				}
//				*/
//			}
//			queryFieldsSQL.append("data_id");
//
//			log.debug("queryFieldsSQL=" + queryFieldsSQL.toString());
//			Map<Integer, CommonServiceVO> protocolMap = getProtoclSpecMap();
//
//			final String queryTable = getQueryTableName(nfVO);
//			log.debug("queryTable == " + queryTable);
//			List<Object[]> dataList = netFlowDAO.findNetFlowDataFromDB(nfVO, startRow, pageLength, searchLikeField, queryTable, queryFieldsSQL.toString());
//			
//			if (dataList != null && !dataList.isEmpty()) {
//				List<String> fieldList = dataPollerService.getFieldName(Env.SETTING_ID_OF_NET_FLOW, DataPollerService.FIELD_TYPE_SOURCE);
//				log.debug("fieldList == " + fieldList.toString());
//				if (fieldList == null || (fieldList != null && fieldList.isEmpty())) {
//					throw new ServiceLayerException("查無欄位標題設定 >> Setting_Id: " + Env.SETTING_ID_OF_NET_FLOW);
//
//				} else {
//					
//					String groupId = nfVO.getQueryGroupId();
//					String groupSubnet = "";
//					if(!isSensorSearchMode) {
//					    groupSubnet = getGroupSubnetSetting(groupId, Constants.IPV4);
//					}
//				    log.debug("isSensorSearchMode="+isSensorSearchMode);
//				    boolean hasGetDevice = false;
//				    boolean hasGetSensor = false;
//                    DeviceList device = null;
//                    List<PrtgUserRightSetting> list = null;
//                    
//					NetFlowVO vo;
//					for (Object[] data : dataList) {
//						vo = new NetFlowVO();
//						
//						for (int i=0; i<fieldList.size(); i++) {
//							int fieldIdx = i;
//							int dataIdx = i;
//
//							final String oriName = fieldList.get(fieldIdx);
//							String fName = oriName.substring(0, 1).toLowerCase() + oriName.substring(1, oriName.length());
//
//							String fValue = "";							
//							if (oriName.equals("Now") || oriName.equals("FromDateTime") || oriName.equals("ToDateTime")) {
//								if (data[dataIdx] != null) {
//									fValue = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[dataIdx]);
//								}
//
//							} else if (oriName.equals("Size")) {
//								BigDecimal sizeByte = new BigDecimal(Objects.toString(data[dataIdx], "0"));
//
//								fValue = convertByteSizeUnit(sizeByte, Env.NET_FLOW_SHOW_UNIT_OF_RESULT_DATA_SIZE);
//
//							} else if (oriName.equals("GroupId")) {
//								fValue = Objects.toString(data[dataIdx]);
//								
//								if(!isSensorSearchMode) {
//									if (hasGetDevice == false && device == null) {
//									    /*
//									     * 查詢條件已限制只能查一所學校，因此不需要每一筆查詢結果都再做一次學校查詢
//									     */
//									    device = deviceDAO.findDeviceListByGroupAndDeviceId(fValue, null);
//
//									    if (!hasGetDevice) {
//									        hasGetDevice = true;
//									    }
//									}
//									
//									if (device != null) {
//										BeanUtils.setProperty(vo, "groupName", device.getGroupName());
//									}
//								}
//							} else if (oriName.equals("SourceIP") || oriName.equals("DestinationIP")) {
//							    fValue = Objects.toString(data[dataIdx]);
//							    boolean ipInGroup = isSensorSearchMode ? false : chkIpInGroupSubnet(groupSubnet, fValue, Constants.IPV4);
//
//							    String fNameFlag = fName.concat("InGroup");
//							    BeanUtils.setProperty(vo, fNameFlag, ipInGroup ? Constants.DATA_Y : Constants.DATA_N); // 塞入SourceIPInGroup or DestinationIPInGroup
//
//							} else if (oriName.equals("Protocol")) {
//								String tmpStr = Objects.toString(data[dataIdx]);
//								Integer protocolNo = tmpStr != null ? Integer.valueOf(tmpStr) : null;
//								String protocolName = protocolMap.get(protocolNo).getProtocolName();
//
//								fValue = protocolName;
//
//							} else if(oriName.equals("SensorId")){
//								fValue = Objects.toString(data[dataIdx]);
//								
//								if(isSensorSearchMode) {
//									if (hasGetSensor == false && list == null) {
//										list = prtgDAO.findPrtgUserRightSettingBySettingValueAndType(fValue, Constants.PRTG_RIGHT_SETTING_TYPE_OF_SENSOR);
//
//									    if (!hasGetSensor) {
//									    	hasGetSensor = true;
//									    }
//									}
//									
//									if (list != null && list.size()>0) {
//										BeanUtils.setProperty(vo, "groupName", list.get(0).getRemark());
//									}
//								}
//							}	else {							
//								fValue = Objects.toString(data[dataIdx]);
//							}
//							BeanUtils.setProperty(vo, fName, fValue);
//						}
//						
//						vo.setDataId(Objects.toString(data[data.length - 1]));
//						retList.add(vo);
//					}
//
//					/*
//					 * Y190729, 總流量透過另一個AJAX查詢，提升查詢效率
//					BigDecimal flowSum = netFlowDAO.getTotalFlowOfQueryConditionsFromDB(nfVO, searchLikeField, queryTable);
//					String totalFlow = (flowSum == null) ? "N/A" : convertByteSizeUnit(flowSum, Env.NET_FLOW_SHOW_UNIT_OF_TOTOAL_FLOW);
//					retList.get(0).setTotalFlow(totalFlow);	// 塞入總流量至第一筆VO內
//					*/
//				}
//			}
//
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			throw new ServiceLayerException("查詢失敗，請重新操作");
//		}
//		return retList;
//	}

//	private Map<String, NetFlowVO> composeQueryMap(NetFlowVO nfVO) {
//		Map<String, NetFlowVO> retMap = new HashMap<>();
//
//		NetFlowVO vo = new NetFlowVO();
//		vo.setQueryValue(nfVO.getQueryDate());
//		vo.setQueryCondition(Constants.SYMBOL_EQUAL);
//		retMap.put("FromDateTime", vo);
//
//		vo = new NetFlowVO();
//		vo.setQueryValue(nfVO.getQueryDate());
//		vo.setQueryCondition(Constants.SYMBOL_EQUAL);
//		retMap.put("ToDateTime", vo);
//
//		if (StringUtils.isNotBlank(nfVO.getQuerySourceIp())) {
//			vo = new NetFlowVO();
//			vo.setQueryValue(nfVO.getQuerySourceIp());
//			vo.setQueryCondition(Constants.SYMBOL_END_LIKE);
//			retMap.put("SourceIP", vo);
//		}
//		if (StringUtils.isNotBlank(nfVO.getQuerySourceIp())) {
//			vo = new NetFlowVO();
//			vo.setQueryValue(nfVO.getQuerySourcePort());
//			vo.setQueryCondition(Constants.SYMBOL_EQUAL);
//			retMap.put("SourcePort", vo);
//		}
//		if (StringUtils.isNotBlank(nfVO.getQuerySourceIp())) {
//			vo = new NetFlowVO();
//			vo.setQueryValue(nfVO.getQueryDestinationIp());
//			vo.setQueryCondition(Constants.SYMBOL_END_LIKE);
//			retMap.put("DestinationIP", vo);
//		}
//		if (StringUtils.isNotBlank(nfVO.getQuerySourceIp())) {
//			vo = new NetFlowVO();
//			vo.setQueryValue(nfVO.getQueryDestinationIp());
//			vo.setQueryCondition(Constants.SYMBOL_EQUAL);
//			retMap.put("DestinationPort", vo);
//		}
//
//		return retMap;
//	}

//	private NetFlowVO doQuery(
//			DataPollerSetting setting,
//			Map<Integer, DataPollerServiceVO> fieldIdxMap,
//			Map<String, DataPollerServiceVO> fieldVOMap,
//			Map<String, NetFlowVO> queryMap,
//			Integer startRow,
//			Integer pageLength) throws Exception {
//
//		NetFlowVO retVO = new NetFlowVO();
//
//		String mappingCode = setting.getMappingCode();
//		List<DataPollerMapping> mappings = dataPollerDAO.findDataPollerMappingByMappingCode(mappingCode);
//
//		if (mappings != null && !mappings.isEmpty()) {
//			DataPollerServiceVO dpsVO = null;
//			for (DataPollerMapping dpm : mappings) {
//				dpsVO = new DataPollerServiceVO();
//				BeanUtils.copyProperties(dpsVO, dpm);
//
//				fieldIdxMap.put(dpm.getTargetFieldIdx(), dpsVO);
//				fieldVOMap.put(dpm.getTargetFieldName(), dpsVO);
//
//			}
//		}
//
//		retVO = netFlowDAO.findNetFlowDataFromFile(setting, fieldIdxMap, fieldVOMap, queryMap, startRow, pageLength);
//
//		return retVO;
//	}

//	@Override
//	public NetFlowVO findNetFlowRecordFromFile(NetFlowVO nfVO, Integer startRow, Integer pageLength) throws ServiceLayerException {
//		NetFlowVO retVO = new NetFlowVO();
//		try {
//			String querySchoolId = nfVO.getQuerySchoolId();
//			DataPollerSetting setting = dataPollerDAO.findDataPollerSettingByDataTypeAndQueryId(Constants.DATA_TYPE_OF_NET_FLOW, querySchoolId);
//
//			Map<Integer, DataPollerServiceVO> fieldIdxMap = new HashMap<>();
//			Map<String, DataPollerServiceVO> fieldVOMap = new HashMap<>();
//
//			Map<String, NetFlowVO> queryMap = composeQueryMap(nfVO);
//
//			if (setting == null) {
//				List<DataPollerSetting> settings = dataPollerDAO.findDataPollerSettingByDataType(Constants.DATA_TYPE_OF_NET_FLOW);
//
//				NetFlowVO tmpVO = null;
//				for (DataPollerSetting dps : settings) {
//					tmpVO = doQuery(dps, fieldIdxMap, fieldVOMap, queryMap, startRow, pageLength);
//				}
//
//			} else {
//				retVO = doQuery(setting, fieldIdxMap, fieldVOMap, queryMap, startRow, pageLength);
//			}
//
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			throw new ServiceLayerException("查詢失敗，請重新操作");
//		}
//		return retVO;
//	}

//    @Override
//    public String getTotalTraffic(NetFlowVO nfVO, List<String> searchLikeField)
//            throws ServiceLayerException {
//
//        String totalFlow = "N/A";
//        try {
//            final String queryTable = getQueryTableName(nfVO);
//            BigDecimal flowSum = netFlowDAO.getTotalFlowOfQueryConditionsFromDB(nfVO, searchLikeField, queryTable);
//            if (flowSum != null) {
//                totalFlow = convertByteSizeUnit(flowSum, Env.NET_FLOW_SHOW_UNIT_OF_TOTOAL_FLOW);
//
//            } else {
//                totalFlow = "EMPTY";
//            }
//
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//        }
//        return totalFlow;
//    }

//	@Override
//	public NetFlowVO findNetFlowRecordByGroupIdAndDataId(String groupId, String dataId, String fromDateTime) throws ServiceLayerException {
//		NetFlowVO retVO = null;
//		try {
//			String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_NET_FLOW);
//
//			if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
//				/*
//				 * Option 1. 走 FILE 模式查詢
//				 */
//				//TODO
//
//			} else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
//			    SimpleDateFormat FORMAT_YYYYMMDD_HH24MISS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//			    SimpleDateFormat FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
//			    String dateBegin = FORMAT_YYYY_MM_DD.format(FORMAT_YYYYMMDD_HH24MISS.parse(fromDateTime));
//
//				NetFlowVO nfVO = new NetFlowVO();
//				nfVO.setQueryDataId(dataId);
//				nfVO.setQueryGroupId(groupId);
//				nfVO.setQueryDateBegin(dateBegin);
//				List<NetFlowVO> dataList = findNetFlowRecordFromDB(nfVO, null, null, null);
//
//				if (dataList != null && !dataList.isEmpty()) {
//					retVO = dataList.get(0);
//				}
//			}
//
//		} catch (Exception e) {
//            log.error(e.toString(), e);
//        }
//		return retVO;
//	}
}
