package com.cmap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.comm.enums.ScriptType;
import com.cmap.dao.BaseDAO;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.ProvisionLogDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.model.ProvisionAccessLog;
import com.cmap.model.ProvisionLogConfigBackupError;
import com.cmap.model.ProvisionLogDetail;
import com.cmap.model.ProvisionLogDevice;
import com.cmap.model.ProvisionLogMaster;
import com.cmap.model.ProvisionLogRetry;
import com.cmap.model.ProvisionLogStep;
import com.cmap.service.ProvisionService;
import com.cmap.service.StepService.Result;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.ProvisionAccessLogVO;
import com.cmap.service.vo.ProvisionServiceVO;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.impl.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("provisionService")
@Transactional
public class ProvisionServiceImpl extends CommonServiceImpl implements ProvisionService {
	@Log
	private static Logger log;

	@Autowired
	private ProvisionLogDAO provisionLogDAO;

	@Autowired
	private DeviceDAO deviceDAO;

	private ProvisionServiceVO transMasterEntity2VO(ProvisionLogDetail entity) {
		ProvisionServiceVO psVO = new ProvisionServiceVO();

		return psVO;
	}

	@Override
	public List<ProvisionServiceVO> findProvisionLog(ProvisionServiceVO psVO) throws ServiceLayerException {
		List<ProvisionServiceVO> retList = new ArrayList<>();

		try {
			/*
			List<ProvisionLogDetail> entities = provisionLogDAO.findProvisionLogByDAOVO(null);

			if (entities != null && !entities.isEmpty()) {
				ProvisionServiceVO retPsVO;
				for (ProvisionLogDetail entity : entities) {
					retPsVO = transMasterEntity2VO(entity);
					retList.add(retPsVO);
				}
			}
			*/

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e);
		}

		return retList;
	}

	@Override
	public boolean insertProvisionLog(ProvisionServiceVO masterVO) throws ServiceLayerException {
		try {
			ProvisionLogMaster masterEntity = null;
			List<ProvisionLogDetail> detailEntities = null;
			List<ProvisionLogStep> stepEntities = null;
			List<ProvisionLogDevice> deviceEntities = null;
			List<ProvisionLogRetry> retryEntities = null;
			List<ProvisionLogConfigBackupError> backupErrorEntities = null;

			boolean isBackup = false;
			if(StringUtils.equals(ScriptType.BACKUP.toString(), masterVO.getScriptType())) {
				isBackup = true;				
			}
			
			if (masterVO != null) {
				final String logMasterId = UUID.randomUUID().toString();
				final String triggerName = masterVO.getTriggerName();					
				final String userName = masterVO.getUserName();
				final String userIp = masterVO.getUserIp();

				/*
				 * Convert ProvisionLogMaster entity
				 */
				masterEntity = new ProvisionLogMaster();
				BeanUtils.copyProperties(masterVO, masterEntity);
				masterEntity.setBeginTime(new Timestamp(masterVO.getBeginTime().getTime()));
				masterEntity.setEndTime(new Timestamp(masterVO.getEndTime().getTime()));
				masterEntity.setLogMasterId(logMasterId);
				masterEntity.setSpendTimeInSeconds(
						CommonUtils.calculateSpendTime(masterVO.getBeginTime(), masterVO.getEndTime()));
				masterEntity.setCreateBy(StringUtils.isBlank(triggerName)?userName : triggerName ); //RESTAPI呼叫會帶入triggerName, 手動呼叫只能參考userName
				masterEntity.setCreateTime(new Timestamp((new Date()).getTime()));

				detailEntities = new ArrayList<>();
				stepEntities = new ArrayList<>();
				deviceEntities = new ArrayList<>();
				retryEntities = new ArrayList<>();
				backupErrorEntities = new ArrayList<>();

				ProvisionLogDetail detailEntity = null;
				List<ProvisionServiceVO> detailVOs = masterVO.getDetailVO();
				for (ProvisionServiceVO detailVO : detailVOs) {
					final String logDetailId = UUID.randomUUID().toString();
					final String logStepId = UUID.randomUUID().toString();

					/*
					 * Convert ProvisionLogDetail entity
					 */
					detailEntity = new ProvisionLogDetail();
					BeanUtils.copyProperties(detailVO, detailEntity);
					detailEntity.setProvisionLogMaster(masterEntity);
					detailEntity.setLogDetailId(logDetailId);
					detailEntity.setUserName(userName);
					if(StringUtils.isNotBlank(userIp)) //只有RESTAPI參考的VersionService.backupConfig方法會填入userIp至傳入的masterVO(為了取得deviceIp)
						detailEntity.setUserIp(userIp); //非RESTAPI會在stepService.doBackupStep階段填入
					detailEntity.setSpendTimeInSeconds(
							CommonUtils.calculateSpendTime(detailVO.getBeginTime(), detailVO.getEndTime()));
					detailEntity.setBeginTime(new Timestamp(detailVO.getBeginTime().getTime()));
					detailEntity.setEndTime(new Timestamp(detailVO.getEndTime().getTime()));
					detailEntity.setCreateBy(StringUtils.isBlank(triggerName)?userName : triggerName);
					detailEntity.setCreateTime(new Timestamp((new Date()).getTime()));
					detailEntities.add(detailEntity);

					/*
					 * Convert ProvisionLogStep entities
					 */
					List<ProvisionServiceVO> stepVOList = detailVO.getStepVO();
					if (stepVOList != null && !stepVOList.isEmpty()) {

						ProvisionLogStep stepEntity = null;
						for (ProvisionServiceVO stepVO : stepVOList) {
							//log.info(stepVO.getProcessLog());

							stepEntity = new ProvisionLogStep();
							BeanUtils.copyProperties(stepVO, stepEntity);
							stepEntity.setLogStepId(logStepId);
							stepEntity.setProvisionLogDetail(detailEntity);
							stepEntity.setSpendTimeInSeconds(
									CommonUtils.calculateSpendTime(stepVO.getBeginTime(), stepVO.getEndTime()));
							stepEntity.setBeginTime(new Timestamp(stepVO.getBeginTime().getTime()));
							stepEntity.setEndTime(new Timestamp(stepVO.getEndTime().getTime()));
							stepEntity.setCreateBy(StringUtils.isBlank(triggerName)?userName : triggerName);
							stepEntity.setCreateTime(new Timestamp((new Date()).getTime()));

							if (StringUtils.isBlank(stepEntity.getScriptCode())) {
								stepEntity.setScriptCode("(EMPTY)");
							}

							stepEntities.add(stepEntity);

							/*
							 * Convert ProvisionLogDevice entities
							 */
							List<ProvisionServiceVO> deviceVOList = stepVO.getDeviceVO();
							if (deviceVOList != null && !deviceVOList.isEmpty()) {
								DeviceList deviceListEntity = null;
								ProvisionLogDevice deviceEntity = null;
								ProvisionLogConfigBackupError backupErrorEntity = null;
								
								for (ProvisionServiceVO deviceVO : deviceVOList) {
									deviceEntity = new ProvisionLogDevice();
									BeanUtils.copyProperties(deviceVO, deviceEntity);
									deviceEntity.setLogDeviceId(UUID.randomUUID().toString());
									deviceEntity.setProvisionLogStep(stepEntity);

									if (StringUtils.isNotBlank(deviceVO.getDeviceListId())) {
										deviceListEntity = deviceDAO.findDeviceListByDeviceListId(deviceVO.getDeviceListId());

									} else {
										deviceListEntity = new DeviceList();
										deviceListEntity.setDeviceListId(deviceVO.getDeviceInfoStr());
									}
									deviceEntity.setDeviceList(deviceListEntity);

									deviceEntity.setCreateBy(StringUtils.isBlank(triggerName)?userName : triggerName);
									deviceEntity.setCreateTime(new Timestamp((new Date()).getTime()));

									deviceEntities.add(deviceEntity);
									
									if(isBackup && StringUtils.equals(Result.ERROR.toString(), stepEntity.getResult())) {
										backupErrorEntity = new ProvisionLogConfigBackupError();
										BeanUtils.copyProperties(deviceListEntity, backupErrorEntity);
										BeanUtils.copyProperties(stepEntity, backupErrorEntity);
										backupErrorEntity.setLogMasterId(logMasterId);
										
										backupErrorEntities.add(backupErrorEntity);
									}
								}
							}

							/*
							 * Convert ProvisionLogRetry entities
							 */
							List<ProvisionServiceVO> retryVOList = stepVO.getRetryVO();
							if (retryVOList != null && !retryVOList.isEmpty()) {
								ProvisionLogRetry retryEntity = null;
								for (ProvisionServiceVO retryVO : retryVOList) {
									retryEntity = new ProvisionLogRetry();
									BeanUtils.copyProperties(retryVO, retryEntity);
									retryEntity.setLogRetryId(UUID.randomUUID().toString());
									retryEntity.setProvisionLogStep(stepEntity);
									retryEntity.setCreateBy(StringUtils.isBlank(triggerName)?userName : triggerName);
									retryEntity.setCreateTime(new Timestamp((new Date()).getTime()));
									retryEntities.add(retryEntity);
								}
							}
						}
					}
				}

				provisionLogDAO.insertProvisionLog(masterEntity, detailEntities, stepEntities, deviceEntities, retryEntities, backupErrorEntities);
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;
		}

		return true;
	}

	@Override
	public ProvisionAccessLogVO checkOrInsertProvisionAccessLog(ProvisionAccessLogVO palVO, boolean isNew, boolean doChk) throws ServiceLayerException {
		ProvisionAccessLogVO retVO = new ProvisionAccessLogVO();
		try {
			String logId = null;

			if (isNew) {
				logId = UUID.randomUUID().toString();
			} else {
				logId = palVO.getLogId();
			}

			ProvisionAccessLog entity = provisionLogDAO.findProvisionAccessLogById(logId);

			if (isNew && (entity != null)) {
				throw new ServiceLayerException("[ProvisionServiceImpl] isNew == true，但 tokenId 已存在");
			}

			if (isNew) {
				entity = new ProvisionAccessLog();
				BeanUtils.copyProperties(palVO, entity);
				entity.setLogId(logId);
				entity.setCreateBy(currentUserName());
				entity.setCreateTime(currentTimestamp());
				entity.setUpdateBy(currentUserName());
				entity.setUpdateTime(currentTimestamp());

			} else {
				BeanUtils.copyProperties(palVO, entity, new String[] {"logId", "createBy", "createTime"});
				entity.setUpdateBy(currentUserName());
				entity.setUpdateTime(currentTimestamp());
			}

			if (!doChk) {
				provisionLogDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, entity);

			} else {
				final String psJSON = palVO.getParameterJson();

				ObjectMapper oMapper = new ObjectMapper();
				DeliveryParameterVO psVO = oMapper.readValue(psJSON, DeliveryParameterVO.class);


				boolean success = true;
				String chkResultMsg = "OK";
				String commonErrorMsg = "供裝前系統檢核異常";

				if (!entity.getScriptCode().equals(psVO.getScriptCode())) {
					chkResultMsg = "供裝前參數檢核異常 >> Entity.ScriptInfoId : " + entity.getScriptInfoId()
													+ " ; Entity.ScriptCode : " + entity.getScriptCode()
													+ " <> VO.ScriptCode : " + psVO.getScriptCode();

					entity.setChkResult(chkResultMsg);

					retVO.setAccessLogChkResult(false);
					retVO.setAccessLogChkMsg(commonErrorMsg);

					success = false;
				}

				provisionLogDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, entity);

				if (success) {
					retVO.setAccessLogChkResult(true);
					retVO.setAccessLogChkMsg(chkResultMsg);
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			retVO.setAccessLogChkResult(false);
		}

		return retVO;
	}
	
	@Override
	public List<ProvisionLogConfigBackupError> findProvisionLogConfigBackupError(VersionServiceVO vsVO) throws ServiceLayerException {
		try {
			return provisionLogDAO.findProvisionLogConfigBackupErrorByDAOVO(vsVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e);
		}
	}
}
