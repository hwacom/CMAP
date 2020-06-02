package com.cmap.plugin.module.firewall;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.BaseDAO;
import com.cmap.dao.vo.CommonDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.DataPollerService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.CommonServiceVO;

@Service("firewallService")
@Transactional
public class FirewallServiceImpl extends CommonServiceImpl implements FirewallService {
    @Log
    private static Logger log;

    @Autowired
    private DataPollerService dataPollerService;

    @Autowired
    private FirewallDAO firewallDAO;


    @Override
    public List<String> getFieldNameList(String queryType, String fieldType) {
        List<String> tableTitleField = new ArrayList<>();
        try {
            String queryTypeSettingId;
            String queryTypeSettingName = "SETTING_ID_OF_" + queryType;

            List<FirewallVO> settings = findFirewallLogSetting(queryTypeSettingName);

            if (settings == null) {
                throw new ServiceLayerException("未設定此queryType對應的settingId (Setting_Name=" + queryTypeSettingName);
            }

            queryTypeSettingId = settings.get(0).getSettingValue();

            if (StringUtils.isBlank(queryTypeSettingId)) {
                throw new ServiceLayerException("此queryType設定的settingId為空 (Setting_Name=" + queryTypeSettingName);
            }

            //tableTitleField.add("");
            tableTitleField.addAll(dataPollerService.getFieldName(queryTypeSettingId, fieldType));

        } catch (ServiceLayerException e) {
            log.error(e.toString(), e);
        }

        return tableTitleField;
    }

    @Override
    public List<FirewallVO> findFirewallLogSetting(String settingName) {
        List<FirewallVO> retList = null;
        try {
            List<ModuleFirewallLogSetting> settings = firewallDAO.getFirewallLogSetting(settingName);

            if (settings != null && !settings.isEmpty()) {
                retList = new ArrayList<>();
                FirewallVO vo = null;
                for (ModuleFirewallLogSetting s : settings) {
                    vo = new FirewallVO();
                    vo.setSettingName(s.getSettingName());
                    vo.setSettingValue(s.getSettingValue());
                    vo.setOrderNo(s.getOrderNo());
                    vo.setRemark(s.getRemark());

                    retList.add(vo);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retList;
    }

    private String getQueryTableName(FirewallVO fVO) throws ServiceLayerException {
        final String queryType = fVO.getQueryType();
        String queryTableName = "MODULE_FIREWALL_LOG_" + queryType;
        return queryTableName;
    }

    @Override
    public long countFirewallLogRecordFromDB(FirewallVO fVO, Map<String, List<String>> fieldsMap)
            throws ServiceLayerException {
        long retCount = 0;
        try {
            List<String> searchLikeField = fieldsMap.get(fVO.getQueryType());
            retCount = firewallDAO.countFirewallLogFromDB(fVO, searchLikeField, getQueryTableName(fVO));

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗，請重新操作");
        }
        return retCount;
    }

    @Override
    public List<FirewallVO> findFirewallLogRecordFromDB(FirewallVO fVO, Integer startRow,
            Integer pageLength, Map<String, List<String>> fieldsMap) throws ServiceLayerException {
        List<FirewallVO> retList = null;
        try {
            Map<String, String> typeNameMap = fVO.getTypeNameMap();
            String queryType = fVO.getQueryType();
            List<String> tableTitleField = getFieldNameList(queryType, DataPollerService.FIELD_TYPE_TARGET);
            String queryFieldsSQL = composeQueryFieldsStr(queryType, typeNameMap, tableTitleField);

            final String queryTable = getQueryTableName(fVO);
            List<String> searchLikeField = fieldsMap.get(fVO.getQueryType());
            List<Object[]> dataList = firewallDAO.findFirewallLogFromDB(fVO, startRow, pageLength, searchLikeField, queryTable, queryFieldsSQL);

            if (dataList != null && !dataList.isEmpty()) {
                List<String> fieldList = getFieldNameList(fVO.getQueryType(), DataPollerService.FIELD_TYPE_SOURCE);
                retList = composeOutputVO(fieldList, dataList);

            } else {
                retList = new ArrayList<>();
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗，請重新操作");
        }
        return retList;
    }

    private List<FirewallVO> composeOutputVO(List<String> fieldList, List<Object[]> dataList) throws ServiceLayerException {
        List<FirewallVO> retList = new ArrayList<>();

        if (fieldList == null || (fieldList != null && fieldList.isEmpty())) {
            throw new ServiceLayerException("查無欄位標題設定");

        } else {
            Map<Integer, CommonServiceVO> protocolMap = getProtoclSpecMap();

            FirewallVO vo;
            for (Object[] data : dataList) {
                vo = new FirewallVO();

                for (int i=0; i<fieldList.size(); i++) {
                    int fieldIdx = i;
                    int dataIdx = i;

                    final String oriName = fieldList.get(fieldIdx);
                    String fName = oriName.substring(0, 1).toLowerCase() + oriName.substring(1, oriName.length());

                    String fValue = "";
                    if (oriName.equals("date")) {
                        if (data[dataIdx] != null) {
                            fValue = Constants.FORMAT_YYYY_MM_DD.format(data[dataIdx]);
                        }

                    } else if (oriName.equals("time")) {
                        if (data[dataIdx] != null) {
                            fValue = Constants.FORMAT_HH24_MI_SS.format(data[dataIdx]);
                        }

                    } else if (oriName.equals("sentByte") || oriName.equals("rcvdByte")) {
                        fValue = null;
                        if (StringUtils.isNotBlank(Objects.toString(data[dataIdx], null))) {
                            BigDecimal sizeByte = new BigDecimal(Objects.toString(data[dataIdx], "0"));

                            fValue = convertByteSizeUnit(sizeByte, Env.NET_FLOW_SHOW_UNIT_OF_RESULT_DATA_SIZE);
                        }

                    } else if (oriName.equals("proto")) {
                        String tmpStr = Objects.toString(data[dataIdx]);
                        Integer protocolNo = StringUtils.isNotBlank(tmpStr) ? Integer.valueOf(tmpStr) : null;
                        String protocolName = protocolNo == null ? null : protocolMap.get(protocolNo).getProtocolName();

                        fValue = protocolName;

                    } else {
                        fValue = Objects.toString(data[dataIdx]);
                    }

                    try {
                        BeanUtils.setProperty(vo, fName, fValue);

                    } catch (Exception e) {
                        log.error(e.toString(), e);
                        throw new ServiceLayerException("查詢結果轉換過程異常");
                    }
                }

                String typeName = Objects.toString(data[data.length - 1], "");
                vo.setType(typeName);

                retList.add(vo);
            }
        }

        return retList;
    }

    private String composeQueryFieldsStr(String queryType, Map<String, String> typeNameMap, List<String> fieldsList) {
        StringBuffer queryFieldsSQL = new StringBuffer();

        for (int i=0; i<fieldsList.size(); i++) {
            String fieldName = fieldsList.get(i);
            queryFieldsSQL.append("`").append(fieldName).append("`");

            queryFieldsSQL.append(", ");
        }

        queryFieldsSQL.append(" '").append(typeNameMap.get(queryType)).append("' TYPE ");
        return queryFieldsSQL.toString();
    }

    private String composeQueryFieldsStr(String queryType, Map<String, String> typeNameMap, List<String> targetFieldsList, List<String> referFieldsList) {
        StringBuffer queryFieldsSQL = new StringBuffer();

        for (int i=0; i<referFieldsList.size(); i++) {
            String rField = referFieldsList.get(i);

            boolean exist = false;
            for (String tField : targetFieldsList) {
                if (tField.equals(rField)) {
                    exist = true;
                    break;
                }
            }

            if (exist) {
                queryFieldsSQL.append("`").append(rField).append("`");
            } else {
                queryFieldsSQL.append("'' ").append(rField);
            }

            /*
            if (i < referFieldsList.size() - 1) {
                queryFieldsSQL.append(", ");
            }
            */
            queryFieldsSQL.append(", ");
        }

        queryFieldsSQL.append(" '").append(typeNameMap.get(queryType)).append("' TYPE ");
        return queryFieldsSQL.toString();
    }

    @Override
    public long countFirewallLogRecordFromDBbyAll(FirewallVO fVO, Map<String, List<String>> fieldsMap) throws ServiceLayerException {
        long retCount = 0;
        try {
            retCount = firewallDAO.countFirewallLogFromDBbyAll(fVO, fieldsMap);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗，請重新操作");
        }
        return retCount;
    }

    @Override
    public List<FirewallVO> findFirewallLogRecordFromDBbyAll(FirewallVO fVO, Integer startRow,
            Integer pageLength, Map<String, List<String>> fieldsMap) throws ServiceLayerException {
        List<FirewallVO> retList = null;
        try {
            Map<String, String> typeNameMap = fVO.getTypeNameMap();
            List<String> allTitleField = fieldsMap.get(Constants.FIREWALL_LOG_TYPE_ALL);
            List<String> appTitleField = fieldsMap.get(Constants.FIREWALL_LOG_TYPE_APP);
            List<String> forwardingTitleField = fieldsMap.get(Constants.FIREWALL_LOG_TYPE_FORWARDING);
            List<String> intrusionTitleField = fieldsMap.get(Constants.FIREWALL_LOG_TYPE_INTRUSION);
            List<String> systemTitleField = fieldsMap.get(Constants.FIREWALL_LOG_TYPE_SYSTEM);
            List<String> webfilterTitleField = fieldsMap.get(Constants.FIREWALL_LOG_TYPE_WEBFILTER);

            String appSelectSql = composeQueryFieldsStr(Constants.FIREWALL_LOG_TYPE_APP, typeNameMap, appTitleField, allTitleField);
            String forwardingSelectSql = composeQueryFieldsStr(Constants.FIREWALL_LOG_TYPE_FORWARDING, typeNameMap, forwardingTitleField, allTitleField);
            String intrusionSelectSql = composeQueryFieldsStr(Constants.FIREWALL_LOG_TYPE_INTRUSION, typeNameMap, intrusionTitleField, allTitleField);
            String systemSelectSql = composeQueryFieldsStr(Constants.FIREWALL_LOG_TYPE_SYSTEM, typeNameMap, systemTitleField, allTitleField);
            String webfilterSelectSql = composeQueryFieldsStr(Constants.FIREWALL_LOG_TYPE_WEBFILTER, typeNameMap, webfilterTitleField, allTitleField);

            Map<String, String> selectSqlMap = new HashMap<>();
            selectSqlMap.put(Constants.FIREWALL_LOG_TYPE_APP, appSelectSql);
            selectSqlMap.put(Constants.FIREWALL_LOG_TYPE_FORWARDING, forwardingSelectSql);
            selectSqlMap.put(Constants.FIREWALL_LOG_TYPE_INTRUSION, intrusionSelectSql);
            selectSqlMap.put(Constants.FIREWALL_LOG_TYPE_SYSTEM, systemSelectSql);
            selectSqlMap.put(Constants.FIREWALL_LOG_TYPE_WEBFILTER, webfilterSelectSql);

            List<Object[]> dataList = firewallDAO.findFirewallLogFromDBbyAll(fVO, startRow, pageLength, selectSqlMap, fieldsMap);

            if (dataList != null && !dataList.isEmpty()) {
                List<String> fieldList = getFieldNameList(Constants.FIREWALL_LOG_TYPE_ALL, DataPollerService.FIELD_TYPE_SOURCE);
                retList = composeOutputVO(fieldList, dataList);

            } else {
                retList = new ArrayList<>();
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗，請重新操作");
        }
        return retList;
    }

    @Override
    public Map<String, String> getActionMenu(String queryType) throws ServiceLayerException {
        Map<String, String> retMap = new HashMap<>();
        try {
            String queryMenuCode = null;

            switch (queryType) {
                case Constants.FIREWALL_LOG_TYPE_APP:
                    queryMenuCode = Constants.FIREWALL_LOG_ACTION_APP;
                    break;
                case Constants.FIREWALL_LOG_TYPE_FORWARDING:
                    queryMenuCode = Constants.FIREWALL_LOG_ACTION_FORWARDING;
                    break;
                case Constants.FIREWALL_LOG_TYPE_INTRUSION:
                    queryMenuCode = Constants.FIREWALL_LOG_ACTION_INTRUSION;
                    break;
                case Constants.FIREWALL_LOG_TYPE_SYSTEM:
                    queryMenuCode = Constants.FIREWALL_LOG_ACTION_SYSTEM;
                    break;
                case Constants.FIREWALL_LOG_TYPE_WEBFILTER:
                    queryMenuCode = Constants.FIREWALL_LOG_ACTION_WEBFILTER;
                    break;
            }

            if (StringUtils.isNotBlank(queryMenuCode)) {
                retMap = getMenuItem(queryMenuCode, true);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retMap;
    }

    @Override
    public long getTableRoughlyTotalCount(FirewallVO fVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            String queryType = fVO.getQueryType();

            String[] tableNames = null;
            if (StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_ALL)) {
                tableNames = new String[5];
                tableNames[0] = Constants.FIREWALL_LOG_TABLE_NAME_OF_APP;
                tableNames[1] = Constants.FIREWALL_LOG_TABLE_NAME_OF_FORWARDING;
                tableNames[2] = Constants.FIREWALL_LOG_TABLE_NAME_OF_INTRUSION;
                tableNames[3] = Constants.FIREWALL_LOG_TABLE_NAME_OF_SYSTEM;
                tableNames[4] = Constants.FIREWALL_LOG_TABLE_NAME_OF_WEBFILTER;

            } else {
                tableNames = new String[1];
                tableNames[0] = getQueryTableName(fVO);
            }

            CommonDAOVO cVO = null;
            for (String tableName : tableNames) {
            	cVO = firewallDAO.getTableInformation(BaseDAO.TARGET_PRIMARY_DB, tableName);
            	if (cVO != null) {
            		long tableRows = cVO.getTableInfoOfRows();
                    retVal += tableRows;
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVal;
    }
}
