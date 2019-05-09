package com.cmap.plugin.module.firewall;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public long countFirewallLogRecordFromDB(FirewallVO fVO, List<String> searchLikeField)
            throws ServiceLayerException {
        long retCount = 0;
        try {
            retCount = firewallDAO.countFirewallLogFromDB(fVO, searchLikeField, getQueryTableName(fVO));

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗，請重新操作");
        }
        return retCount;
    }

    @Override
    public List<FirewallVO> findFirewallLogRecordFromDB(FirewallVO fVO, Integer startRow,
            Integer pageLength, List<String> searchLikeField) throws ServiceLayerException {
        List<FirewallVO> retList = new ArrayList<>();
        try {
            List<String> tableTitleField = getFieldNameList(fVO.getQueryType(), DataPollerService.FIELD_TYPE_TARGET);

            StringBuffer queryFieldsSQL = new StringBuffer();
            for (int i=0; i<tableTitleField.size(); i++) {
                String fieldName = tableTitleField.get(i);
                queryFieldsSQL.append("`").append(fieldName).append("`");

                if (i < tableTitleField.size() - 1) {
                    queryFieldsSQL.append(", ");
                }
            }

            Map<Integer, CommonServiceVO> protocolMap = getProtoclSpecMap();

            final String queryTable = getQueryTableName(fVO);
            List<Object[]> dataList = firewallDAO.findFirewallLogFromDB(fVO, startRow, pageLength, searchLikeField, queryTable, queryFieldsSQL.toString());

            if (dataList != null && !dataList.isEmpty()) {
                List<String> fieldList = getFieldNameList(fVO.getQueryType(), DataPollerService.FIELD_TYPE_SOURCE);

                if (fieldList == null || (fieldList != null && fieldList.isEmpty())) {
                    throw new ServiceLayerException("查無欄位標題設定");

                } else {
                    FirewallVO vo;
                    for (Object[] data : dataList) {
                        vo = new FirewallVO();
                        vo.setType(fVO.getQueryType());

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
                                BigDecimal sizeByte = new BigDecimal(Objects.toString(data[dataIdx], "0"));

                                fValue = convertByteSizeUnit(sizeByte, Env.NET_FLOW_SHOW_UNIT_OF_RESULT_DATA_SIZE);

                            } else if (oriName.equals("proto")) {
                                String tmpStr = Objects.toString(data[dataIdx]);
                                Integer protocolNo = tmpStr != null ? Integer.valueOf(tmpStr) : null;
                                String protocolName = protocolMap.get(protocolNo).getProtocolName();

                                fValue = protocolName;

                            } else {
                                fValue = Objects.toString(data[dataIdx]);
                            }

                            BeanUtils.setProperty(vo, fName, fValue);
                        }

                        retList.add(vo);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗，請重新操作");
        }
        return retList;
    }
}
