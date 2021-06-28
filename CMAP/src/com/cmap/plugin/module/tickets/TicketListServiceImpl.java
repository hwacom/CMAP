package com.cmap.plugin.module.tickets;

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
import com.cmap.service.impl.CommonServiceImpl;

@Service("ticketListService")
@Transactional
public class TicketListServiceImpl extends CommonServiceImpl implements TicketListService {
    @Log
    private static Logger log;

    @Autowired
    private TicketListDAO ticketListDAO;
    
    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public long countModuleTicketList(TicketListVO tlVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = ticketListDAO.countModuleTicketList(tlVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<TicketListVO> findModuleTicketList(TicketListVO tlVO, Integer startRow, Integer pageLength)
            throws ServiceLayerException {
        List<TicketListVO> retList = new ArrayList<>();
        try {
            List<Object[]> entities = ticketListDAO.findModuleTicketList(tlVO, startRow, pageLength);

            Map<String, String> statusMap = getMenuItem("TICKET_STATUS", false);
            Map<String, String> ownerUserMap = findUserList(null);
            Map<String, String> ownerGroupMap = findUserGroupList(null);
            if (entities == null || (entities != null && entities.isEmpty())) {
                return retList;
            }

            TicketListVO vo;
            for (Object[] entity : entities) {
            	Timestamp updateTime = entity[0] != null ? (Timestamp)entity[0] : null;
            	String priority = Objects.toString(entity[1], "");
            	String listId = Objects.toString(entity[2]);
            	String subject = Objects.toString(entity[3]);
            	String owner = Objects.toString(entity[4]);
            	String ownerType = Objects.toString(entity[10], "");
            	String ownerStr = Objects.toString(ownerType.equals("G")?ownerGroupMap.get(owner):ownerUserMap.get(owner), owner);
                String status = Objects.toString(entity[5], "");
                status = Objects.toString(statusMap.get(status), status);
                String remark = Objects.toString(entity[6], "");
                String createTime = Objects.toString(entity[7], "");
                String createBy = Objects.toString(entity[8], "");
                String updateBy = Objects.toString(entity[9], "");
                String mailFlag = Objects.toString(entity[11], "");
                String execFlag = Objects.toString(entity[12], "");
                
                vo = new TicketListVO();
                vo.setListId(listId);
                vo.setStatus(status);
                vo.setOwner(owner);
				vo.setOwnerStr((ownerType.equals("G") ? messageSource.getMessage("group.name", Locale.TAIWAN, null)
						: messageSource.getMessage("user", Locale.TAIWAN, null)).concat("-").concat(ownerStr));
                vo.setSubject(subject);
                vo.setPriority(priority);
                vo.setRemark(remark);
                vo.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(updateTime));
                vo.setUpdateBy(updateBy);
                vo.setOwnerType(ownerType);
                vo.setMailFlag(mailFlag);
                vo.setExecFlag(execFlag);
                
                retList.add(vo);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retList;
    }

	@Override
	public List<ModuleTicketList> findModuleTicketList(TicketListVO tlVO) throws ServiceLayerException {

		return ticketListDAO.findModuleTicketList(tlVO);
	}

	@Override
	public ModuleTicketList findTicketList(Long listId) throws ServiceLayerException {
		ModuleTicketList result = ticketListDAO.findModuleTicketListByPK(listId);

		if (result == null) {
			throw new ServiceLayerException("工單內容讀取錯誤!!");
		}
		return result;
	}

    @Override
	public ModuleTicketList findModuleTicketListByVO(TicketListVO tlVO) throws ServiceLayerException {

		return ticketListDAO.findModuleTicketListByVO(tlVO);
	}
    
	@Override
	public List<ModuleTicketDetail> findModuleTicketDetail(Long listId) throws ServiceLayerException {

		return ticketListDAO.findModuleTicketDetailByListId(listId);
	}
	
    private ModuleTicketList transVO2Model(TicketListVO tlVO) {
        ModuleTicketList entity = new ModuleTicketList();
        BeanUtils.copyProperties(tlVO, entity);
        entity.setCreateTime(currentTimestamp());
        entity.setCreateBy(currentUserName());
        entity.setUpdateTime(currentTimestamp());
        entity.setUpdateBy(currentUserName());
        return entity;
    }

    private TicketListVO transModel2VO(ModuleTicketList entity) {
    	TicketListVO tlVO = new TicketListVO();
        BeanUtils.copyProperties(entity, tlVO);
        return tlVO;
    }
    
    
    @Override
    public void forwardTicket(ModuleTicketDetail mtd, String ownerType, String owner) throws ServiceLayerException {
        try {
        	ModuleTicketList entity = findTicketList(mtd.getListId());
            
        	entity.setOwnerType(messageSource.getMessage("group.name", Locale.TAIWAN, null).equals(ownerType)?"G":"U");
        	entity.setOwner(owner);
        	entity.setUpdateTime(currentTimestamp());
    		entity.setUpdateBy(currentUserName());
    		
        	ticketListDAO.saveOrUpdateTicketList(Arrays.asList(entity));
        	ticketListDAO.saveOrUpdateTicketList(Arrays.asList(mtd));
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新工單紀錄時異常! (ModuleTicketList)");
        }
    }
    
    @Override
    public void saveOrUpdateTicketList(TicketListVO tlVO) throws ServiceLayerException {
        try {
        	ModuleTicketList entity;
        	boolean updateFlag = false;
        	
        	if(StringUtils.isNotBlank(tlVO.getListId())) {
        		entity = ticketListDAO.findModuleTicketListByVO(tlVO);

        	}else {
        		entity = new ModuleTicketList();
        		entity.setCreateTime(currentTimestamp());
        		entity.setCreateBy(currentUserName());
        		entity.setAlarmId(tlVO.getAlarmId());
        		updateFlag = true;
        	}
            
        	if(StringUtils.isNotBlank(tlVO.getStatus()) && !StringUtils.equals(tlVO.getStatus(), entity.getStatus())) {
        		entity.setStatus(tlVO.getStatus());
        		updateFlag = true;
        	}
        	if(StringUtils.isNotBlank(tlVO.getOwnerType()) && !StringUtils.equals(tlVO.getOwnerType(), entity.getOwnerType())) {
        		entity.setOwnerType(tlVO.getOwnerType());
        		updateFlag = true;
        	}
        	if(StringUtils.isNotBlank(tlVO.getOwner()) && !StringUtils.equals(tlVO.getOwner(), entity.getOwner())) {
        		entity.setOwner(tlVO.getOwner());
        		updateFlag = true;
        	}
        	if(StringUtils.isNotBlank(tlVO.getSubject()) && !StringUtils.equals(tlVO.getSubject(), entity.getSubject())) {
        		entity.setSubject(tlVO.getSubject());
        		updateFlag = true;
        	}
        	if(StringUtils.isNotBlank(tlVO.getMessage()) && !StringUtils.equals(tlVO.getMessage(), entity.getMessage())) {
        		entity.setMessage(tlVO.getMessage());
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
        	if(StringUtils.isNotBlank(tlVO.getMailFlag()) && !StringUtils.equals(tlVO.getMailFlag(), entity.getMailFlag())) {
        		entity.setMailFlag(tlVO.getMailFlag());
        		updateFlag = true;
        	}
        	if(StringUtils.isNotBlank(tlVO.getExecFlag()) && !StringUtils.equals(tlVO.getExecFlag(), entity.getExecFlag())) {
        		entity.setExecFlag(tlVO.getExecFlag());
        		updateFlag = true;
        	}
        	
        	if(updateFlag) {
        		entity.setUpdateTime(currentTimestamp());
        		entity.setUpdateBy(currentUserName());
        		ticketListDAO.saveOrUpdateTicketList(entity);
        	}

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新工單紀錄時異常! (ModuleTicketList)");
        }
    }
    
    @Override
    public void saveOrUpdateTicketDetail(ModuleTicketDetail mtd) throws ServiceLayerException {
        try {
        	ticketListDAO.saveOrUpdateTicketList(mtd);
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新工單紀錄時異常! (ModuleTicketList)");
        }
    }
}
