package com.cmap.plugin.module.alarm.summary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.plugin.module.tickets.TicketListService;
import com.cmap.plugin.module.tickets.TicketListVO;
import com.cmap.service.impl.CommonServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;

@Service("alarmSummaryService")
@Transactional
public class AlarmSummaryServiceImpl extends CommonServiceImpl implements AlarmSummaryService {
    @Log
    private static Logger log;

	@Autowired
	private TicketListService ticketListService;
	
    @Autowired
    private AlarmSummaryDAO alarmSummaryDAO;
    
    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public long countModuleAlarmSummary(AlarmSummaryVO tlVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = alarmSummaryDAO.countModuleAlarmSummary(tlVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<AlarmSummaryVO> findModuleAlarmSummary(AlarmSummaryVO tlVO, Integer startRow, Integer pageLength)
            throws ServiceLayerException {
        List<AlarmSummaryVO> retList = new ArrayList<>();
        try {
            List<Object[]> entities = alarmSummaryDAO.findModuleAlarmSummary(tlVO, startRow, pageLength);

            Map<String, String> statusMap = getMenuItem("ALARM_STATUS", false);

            if (entities == null || (entities != null && entities.isEmpty())) {
                return retList;
            }

            AlarmSummaryVO vo;
            for (Object[] entity : entities) {
            	String sensorName = Objects.toString(entity[0], "");
            	String sensorType = Objects.toString(entity[1], "");
            	String groupName = Objects.toString(entity[2], "");
            	String deviceName = Objects.toString(entity[3]);
            	String alarmStatus = Objects.toString(entity[4]);
            	alarmStatus = Objects.toString(statusMap.get(alarmStatus), alarmStatus);
            	Timestamp alarmTime = entity[5] != null ? (Timestamp)entity[5] : null;
            	Timestamp closeTime = entity[6] != null ? (Timestamp)entity[6] : null;
            	String lastValue = Objects.toString(entity[7], "");
            	String message = Objects.toString(entity[8], "");
                String priority = Objects.toString(entity[9], "");
                String remark = Objects.toString(entity[10], "");
                Long alarmId = entity[11] != null ? ((Number)entity[11]).longValue() : null;
                String groupId = Objects.toString(entity[12], "");
                String deviceId = Objects.toString(entity[13], "");
                String sensorId = Objects.toString(entity[14], "");
                String alarmDataStatus = Objects.toString(entity[15]);
                Timestamp updateTime = entity[16] != null ? (Timestamp)entity[16] : null;
                String updateBy = Objects.toString(entity[17], "");
                
                vo = new AlarmSummaryVO();
                vo.setSensorName(sensorName);
                vo.setSensorType(sensorType);
                vo.setGroupName(groupName);
                vo.setDeviceName(deviceName);
                vo.setAlarmStatus(alarmStatus);
                vo.setAlarmDataStatus(alarmDataStatus);
                vo.setAlarmTime(alarmTime);
                vo.setAlarmTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(alarmTime));
                vo.setCloseTime(closeTime);
                vo.setCloseTimeStr(closeTime != null ?Constants.FORMAT_YYYYMMDD_HH24MISS.format(closeTime) : "");
                vo.setLastValue(lastValue);
                vo.setMessage(message);
                vo.setPriority(priority);
                vo.setRemark(remark);
                vo.setAlarmId(alarmId);
                vo.setGroupId(groupId);
                vo.setDeviceId(deviceId);
                vo.setSensorId(sensorId);
                vo.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(updateTime));
                vo.setUpdateBy(updateBy);
                
                retList.add(vo);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retList;
    }

	@Override
	public List<ModuleAlarmSummary> findModuleAlarmSummary(AlarmSummaryVO tlVO) throws ServiceLayerException {

		return alarmSummaryDAO.findModuleAlarmSummary(tlVO);
	}

	@Override
	public ModuleAlarmSummary findAlarmSummary(Long alarmId) throws ServiceLayerException {
		ModuleAlarmSummary result = alarmSummaryDAO.findModuleAlarmSummaryByPK(alarmId);

		if (result == null) {
			throw new ServiceLayerException("警報內容讀取錯誤!!");
		}
		return result;
	}

    private ModuleAlarmSummary transVO2Model(AlarmSummaryVO tlVO) {
        ModuleAlarmSummary entity = new ModuleAlarmSummary();
        BeanUtils.copyProperties(tlVO, entity);
        entity.setUpdateTime(currentTimestamp());
        entity.setUpdateBy(currentUserName());
        return entity;
    }

    private AlarmSummaryVO transModel2VO(ModuleAlarmSummary entity) {
    	AlarmSummaryVO tlVO = new AlarmSummaryVO();
        BeanUtils.copyProperties(entity, tlVO);
        return tlVO;
    }
    
    @Override
    public void createTicket(JsonNode jsonData) throws ServiceLayerException {
        try {
        	Long ids = jsonData.has("ids") ? jsonData.findValue("ids").get(0).asLong(): null;
			String inputOwner = jsonData.has("inputOwner") ? jsonData.findValue("inputOwner").asText() : null;
			
        	ModuleAlarmSummary entity = findAlarmSummary(ids);

        	if(StringUtils.equalsAnyIgnoreCase(Constants.ALARM_SUMMARY_DATA_STATUS_DOING, entity.getAlarmDataStatus())) {
        		throw new ServiceLayerException("該警報已開立工單，無法新增!");
        	}
        	if(StringUtils.equals(Constants.ALARM_SUMMARY_ALARM_STATUS_UP, entity.getAlarmStatus())) {
        		throw new ServiceLayerException("該警報已解除，無法新增!");
        	}
        	
			TicketListVO vo = new TicketListVO();
			vo.setAlarmId(entity.getAlarmId());
			vo.setStatus("OPEN");
			if(StringUtils.isNoneBlank(inputOwner.substring(inputOwner.indexOf("-")+1))) {
				String isGroup = messageSource.getMessage("group.name", Locale.TAIWAN, null).equals(inputOwner.substring(0, inputOwner.indexOf("-")))?"G":"U";
				vo.setOwnerType(isGroup);
				vo.setOwner(inputOwner.substring(inputOwner.indexOf("-")+1));
			}
    		vo.setSubject(entity.getSensorName().concat(" ").concat(entity.getAlarmDataStatus()));
    		vo.setMessage(entity.getMessage());
    		vo.setPriority(entity.getPriority());
    		vo.setRemark(entity.getRemark());
    		vo.setMailFlag(Constants.DATA_Y);
    		vo.setExecFlag(Constants.DATA_Y);
			ticketListService.saveOrUpdateTicketList(vo);
			
			entity.setAlarmDataStatus(Constants.ALARM_SUMMARY_DATA_STATUS_DOING);
			saveOrUpdateAlarmSummary(Arrays.asList(transModel2VO(entity)), null);
			
        } catch (ServiceLayerException se) {
        	throw new ServiceLayerException(se.getMessage());
        	
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新工單紀錄時異常!".concat(e.getMessage()));
        }
    }
    
    @Override
	public List<ModuleAlarmSummaryLog> findModuleAlarmSummaryLog(Long alarmId) {
		return alarmSummaryDAO.findModuleAlarmSummaryLogByAlarmId(alarmId);
	}
    
    @Override
    public void saveOrUpdateAlarmSummary(List<AlarmSummaryVO> tlVOs, JsonNode jsonData) throws ServiceLayerException {
        try {
        	ModuleAlarmSummary entity;
        	List<Object> updateList = new ArrayList<>();
//        	Map<String, String> statusMap = getMenuItem("ALARM_DATA_STATUS", false);
        	
            for (AlarmSummaryVO tlVO : tlVOs) {
            	boolean updateFlag = false;
            	
            	if(tlVO.getQueryAlarmId() != null) {
            		entity = alarmSummaryDAO.findModuleAlarmSummaryByPK(tlVO.getQueryAlarmId());

            	}else {
            		entity = new ModuleAlarmSummary();
            		entity.setUpdateTime(currentTimestamp());
            		entity.setUpdateBy(currentUserName());
            		updateFlag = true;
            	}
                
            	if(StringUtils.isNotBlank(tlVO.getAlarmDataStatus()) && !StringUtils.equals(tlVO.getAlarmDataStatus(), entity.getAlarmDataStatus())) {
            		ModuleAlarmSummaryLog newLog = new ModuleAlarmSummaryLog();
            		newLog.setAlarmId(entity.getAlarmId());
            		newLog.setContent(currentUserName().concat(" ").concat(messageSource.getMessage("func.plugin.alarm.summary.update.status", Locale.TAIWAN, null)
            				.concat("：").concat(messageSource.getMessage("func.plugin.alarm.summary.alarm.data.status."+entity.getAlarmDataStatus(), Locale.TAIWAN, null))
            				.concat("->").concat(messageSource.getMessage("func.plugin.alarm.summary.alarm.data.status."+tlVO.getAlarmDataStatus(), Locale.TAIWAN, null))));
            		newLog.setCreateTime(currentTimestamp());
            		newLog.setCreateBy(currentUserName());
            		updateList.add(newLog);
            		
            		entity.setAlarmDataStatus(tlVO.getAlarmDataStatus());
            		
//            		if(StringUtils.equalsIgnoreCase(Constants.ALARM_SUMMARY_DATA_STATUS_DOING, tlVO.getAlarmDataStatus())) {
//            			createTicket(jsonData);
//            		}
            		updateFlag = true;
            	}
            	if(StringUtils.isNotBlank(tlVO.getPriority()) && !StringUtils.equals(tlVO.getPriority(), entity.getPriority())) {
            		entity.setPriority(tlVO.getPriority());
            		updateFlag = true;
            	}
            	if(StringUtils.isNotBlank(tlVO.getRemark()) && !StringUtils.equals(tlVO.getRemark(), entity.getRemark())) {
            		entity.setRemark(tlVO.getRemark());
            		updateFlag = true;
            	}
            	
            	if(updateFlag) {
            		entity.setUpdateTime(currentTimestamp());
            		entity.setUpdateBy(currentUserName());
            		updateList.add(entity);
            	} else {
            		 throw new ServiceLayerException("紀錄無異動!");
            	}
            }

            if (updateList.size() > 0) {
            	alarmSummaryDAO.saveOrUpdateAlarmSummary(updateList);
            }
        } catch (ServiceLayerException e) {
        	throw new ServiceLayerException(e.getMessage());
        	
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("更新紀錄時異常!");
        }
    }

}
