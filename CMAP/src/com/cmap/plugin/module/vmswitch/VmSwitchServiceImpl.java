package com.cmap.plugin.module.vmswitch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.RestoreMethod;
import com.cmap.comm.enums.ScriptType;
import com.cmap.dao.ConfigDAO;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.vo.ConfigVersionInfoDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ConfigVersionInfo;
import com.cmap.model.DeviceList;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DeliveryService;
import com.cmap.service.ScriptService;
import com.cmap.service.StepService;
import com.cmap.service.VersionService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.VersionServiceVO;

@Service("vmSwitchService")
@Transactional
public class VmSwitchServiceImpl extends CommonServiceImpl implements VmSwitchService {
	@Log
	private static Logger log;

	@Autowired
	private VmSwitchDAO vmSwitchDAO;

	@Autowired
	private DeviceDAO deviceDAO;

	@Autowired
	private ConfigDAO configVersionInfoDAO;

	@Autowired
	private VersionService versionService;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
    private StepService stepService;

	private String logKey = "";
	private Integer logOrderNo = 1;

	private boolean chkListIsEmpty(List<? extends Object> chkList) {
		return (chkList == null || (chkList != null && chkList.isEmpty()));
	}

	private void chkBackupHostStatus() throws ServiceLayerException {
	    try {


	    } catch (Exception e) {

	    }
	}

	private VmSwitchVO getEsxiAndVmNameMapping(VmSwitchVO vsVO, String apiVmName) throws ServiceLayerException {
	    Map<Integer, String> esxiIdNameMapping = new HashMap<>();
	    try {
	        ModuleVmNameMapping mapping = vmSwitchDAO.findVmNameMappingInfoByApiVmName(apiVmName);

	        if (mapping == null) {
	            throw new ServiceLayerException("API傳入的VM name查詢不到 ModuleVmNameMapping 資料 >> apiVmName: " + apiVmName);
	        }

	        final String nameOfCmap = mapping.getNameOfCmap();
	        final String deviceListId = mapping.getDeviceListId();
	        vsVO.setDeviceListId(deviceListId);

	        for (ModuleVmNameMappingDetail detail : mapping.getModuleVmNameMappingDetail()) {
	            Integer esxiSettingId = detail.getModuleVmEsxiSetting().getSettingId();
	            String nameOfVmware = detail.getNameOfVmware();

	            esxiIdNameMapping.put(esxiSettingId, nameOfVmware);
	        }

	        if (StringUtils.isBlank(deviceListId)) {
	            throw new ServiceLayerException("API傳入的VM name對照不到CMAP名稱 >> apiVmName: " + apiVmName + ", deviceListId: " + deviceListId);
	        }

	    } catch (ServiceLayerException sle) {
	        throw sle;

	    } catch (Exception e) {

	        throw new ServiceLayerException("非預期錯誤");
	    }

	    if (esxiIdNameMapping == null || (esxiIdNameMapping != null && esxiIdNameMapping.isEmpty())) {
            throw new ServiceLayerException("API傳入的VM name查詢不到 ModuleVmNameMapping 資料 >> apiVmName: " + apiVmName);
        }

	    vsVO.setEsxiIdNameMapping(esxiIdNameMapping);

        return vsVO;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void writeLog(String logKey, Step step, Status status, String msg) {
	    String stepMsg = "N/A";
	    switch (step) {
            case CHECK_BACKUP_HOST_STATUS:
                stepMsg = "確認備援機目前狀態是否可使用";
                break;

            case GET_VM_MAPPING_TABLE:
                stepMsg = "查詢VM名稱對照表";
                break;

            case GET_SWITCH_HOST_INFO:
                stepMsg = "查詢要切換的設備相關資料";

            case GET_CONFIG_BACKUP_RECORD:
                stepMsg = "查詢指定的VM名稱系統組態備份紀錄";
                break;

            case STEP_RESULT:
                stepMsg = "<STEP_RESULT>";
                break;

            case PROCESS_END:
                stepMsg = "<PROCESS_END>";
                break;

            case NONE:
                stepMsg = "";
                break;
        }

	    String result = "";
	    String message = "N/A";
	    switch (status) {
	        case EXECUTING:
	            message = "executing..";
	            break;

	        case FINISH:
	            result = "<OK>";
	            message = "finish OK !!";
	            break;

	        case ERROR:
	            result = "<ERROR>";
	            message = msg;
	            break;

	        case END:
	            result = "<OK>";
	            message = "=== END ===";
	            break;
	    }

	    ModuleVmProcessLog pLog = new ModuleVmProcessLog();
        pLog.setLogKey(logKey);
        pLog.setStep(stepMsg);
        pLog.setResult(result);
        pLog.setMessage(message);
        pLog.setOrderNo(++logOrderNo);
        pLog.setCreateBy(SecurityUtil.getSecurityUser().getUsername());
        pLog.setUpdateBy(SecurityUtil.getSecurityUser().getUsername());
        vmSwitchDAO.saveOrUpdateProcessLog(pLog);
	}

	private boolean chkSwitchHostIsEpdgOrNot(String deviceIp) throws ServiceLayerException {

	    return true;
	}

	private void getSwitchHostConfigBackupData(VmSwitchVO vmSwitchVO, DeviceList deviceList) throws ServiceLayerException {
	    String errorMsg = "";
	    try {
	        final String apiVmName = vmSwitchVO.getApiVmName();
	        final String groupId = deviceList.getGroupId();
            final String deviceId = deviceList.getDeviceId();
            final String deviceEngName = deviceList.getDeviceEngName();

            writeLog(logKey, Step.GET_CONFIG_BACKUP_RECORD, Status.EXECUTING, null);

	        /*
             * Step 2-2. 查詢指定的 VM name 系統組態備份紀錄，確認有無備份紀錄，有的話才有辦法執行後續動作
             */
            ConfigVersionInfoDAOVO cviDAOVO = new ConfigVersionInfoDAOVO();
            cviDAOVO.setQueryGroup1(groupId);
            cviDAOVO.setQueryDevice1(deviceId);
            List<Object[]> versionList = configVersionInfoDAO.findConfigVersionInfoByDAOVO4New(cviDAOVO, null, null);

            if (chkListIsEmpty(versionList)) {
                errorMsg = "此設備查無組態備份紀錄 (設備名稱: " + deviceEngName + ")";

                writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
                throw new ServiceLayerException("API傳入的VM名稱查詢不到組態備份紀錄(ConfigVersionInfo) >> apiVmName: " + apiVmName + ", groupId: " + groupId + ", deviceId: " + deviceId);
            }

            final ConfigVersionInfo configVersionInfo = (ConfigVersionInfo)versionList.get(0)[0];
            final String restoreVersionId = configVersionInfo.getVersionId();

            /*
             * Step 2-3. 取得最新備份版本內容
             */
            List<String> configContent = null;

            List<String> versionIDs = new ArrayList<>();
            versionIDs.add(restoreVersionId);

            List<VersionServiceVO> vsVOList = versionService.findConfigFilesInfo(versionIDs);

            if (chkListIsEmpty(vsVOList)) {
                // 確認最新版本號資料是否存在
                errorMsg = "此設備查無組態備份紀錄 (設備名稱: " + deviceEngName + ", 最新版本號: " + restoreVersionId + ")";

                writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
                throw new ServiceLayerException("API傳入的VM名稱查詢不到組態備份紀錄(ConfigVersionInfo) >> apiVmName: " + apiVmName + ", groupId: " + groupId + ", deviceId: " + deviceId + ", restoreVersionId: " + restoreVersionId);

            } else {
                VersionServiceVO retVO = vsVOList.get(0);

                String filePath = "";
                String fileName = "";
                try {
                    retVO = versionService.getConfigFileContent(retVO);

                    filePath = retVO.getConfigFileDirPath();
                    fileName = retVO.getFileFullName();

                } catch (ServiceLayerException sle) {
                    // FTP取檔過程異常
                    errorMsg = "於FTP取得設備最新備份檔失敗 (設備名稱: " + deviceEngName + ", FTP路徑: " + filePath + ", 檔名: " + fileName + ")";

                    writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
                    throw new ServiceLayerException(
                            "API傳入的VM名稱於FTP上取得最新備份的組態檔時失敗 >> apiVmName: " + apiVmName + ", groupId: " + groupId + ", deviceId: " + deviceId + ", filePath: " + filePath + ", fileName: " + fileName);
                }

                if (retVO.getConfigContentList() == null
                        || (retVO.getConfigContentList() != null && retVO.getConfigContentList().isEmpty())) {
                    // FTP取檔內容為空

                    errorMsg = "於FTP取得的設備最新備份檔內容為空 (設備名稱: " + deviceEngName + ", FTP路徑: " + filePath + ", 檔名: " + fileName + ")";

                    writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
                    throw new ServiceLayerException(
                            "API傳入的VM名稱於FTP上取得的組態檔內容為空 >> apiVmName: " + apiVmName + ", groupId: " + groupId + ", deviceId: " + deviceId + ", filePath: " + filePath + ", fileName: " + fileName);

                } else {
                    configContent = retVO.getConfigContentList();
                    vmSwitchVO.setRestoreVersionId(restoreVersionId);
                    vmSwitchVO.setConfigContent(configContent);

                    writeLog(logKey, Step.STEP_RESULT, Status.FINISH, null);
                }
            }

	    } catch (ServiceLayerException sle) {
	        throw sle;

	    } catch (Exception e) {
	        log.error(e.toString(), e);
	        errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";

	        writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
	        throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
	    }
	}

	/**
	 * 確認當下要切換的設備 SSH 是否可通
	 * @param deviceList
	 * @return
	 */
	private boolean chkSwitchHostSSHStatus(String deviceIp, boolean writeLog) throws ServiceLayerException {
	    boolean sshStatusOK = false;

	    String errorMsg = "";
        try {
            ConfigInfoVO ciVO = new ConfigInfoVO();
            ciVO.setDeviceIp(deviceIp);

            sshStatusOK = stepService.chkSSHIsEnable(ciVO);

            return sshStatusOK;

        } catch (Exception e) {
            log.error(e.toString(), e);

            if (writeLog) {
                errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";
                writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
            }

            throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
        }
	}

	/**
	 * (SSH可通) 連進要切換的設備，關閉所有interface (no shutdown → shutdown)
	 * @param deviceList
	 * @throws ServiceLayerException
	 */
	private void disableSwitchHostInterface(DeviceList deviceList) throws ServiceLayerException {
	    String errorMsg = "";
        try {


        /*
        } catch (ServiceLayerException sle) {
            throw sle;
        */
        } catch (Exception e) {
            log.error(e.toString(), e);
            errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";

            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
            throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
        }
	}

	/**
	 * (SSH不通) 連進6台ESXi，將要切換的設備VM關機
	 * @param vmSwitchVO
	 * @throws ServiceLayerException
	 */
	private void powerOffSwitchHostVmFromEsxi(VmSwitchVO vmSwitchVO) throws ServiceLayerException {
	    final Map<Integer, String> esxiIdNameMapping = vmSwitchVO.getEsxiIdNameMapping();

	    String errorMsg = "";
	    try {
	        writeLog(logKey, Step.POWER_OFF_FROM_ESXI, Status.EXECUTING, null);

	        /*
	         * Step 4-2. 取得 VMWare ESXi 主機連線資訊
	         */
	        List<ModuleVmEsxiSetting> esxiSettings = vmSwitchDAO.findAllVmEsxiSetting();

	        if (chkListIsEmpty(esxiSettings)) {
	            errorMsg = "查詢不到 VM ESXi 主機表設定 (Module_Vm_Esxi_Setting)";

	            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
	            throw new ServiceLayerException("查詢不到 VM ESXi 主機表設定 >> ModuleVmEsxiSetting");
	        }

	        /*
	         * Step 4-2-1. 迴圈執行多台 ESXi 主機備援切換動作
	         */
	        ScriptServiceVO vmInfoScriptVO = scriptService.findDefaultScriptInfoByScriptTypeAndSystemVersion(
	                ScriptType.VM_INFO.toString(), Constants.DATA_STAR_SYMBOL);

	        if (vmInfoScriptVO == null) {
	            errorMsg = "查詢不到ESXi[查詢VM ID]腳本";

	            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
	            throw new ServiceLayerException("查詢不到預設腳本 for 查詢VM ID >> scriptType: " + ScriptType.VM_INFO);
	        }

	        ScriptServiceVO powerOffScriptVO = scriptService.findDefaultScriptInfoByScriptTypeAndSystemVersion(
	                ScriptType.VM_POWER_OFF.toString(), Constants.DATA_STAR_SYMBOL);

	        if (powerOffScriptVO == null) {
	            errorMsg = "查詢不到ESXi[關機VM]腳本";

	            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
	            throw new ServiceLayerException("查詢不到預設腳本 for 關機VM >> scriptType: " + ScriptType.VM_POWER_OFF);
	        }

	        Map<String, String> deviceInfo;
	        DeliveryServiceVO deliveryVO;
	        for (ModuleVmEsxiSetting esxi : esxiSettings) {
	            final Integer esxiID = esxi.getSettingId();

	            int round = 1;
	            int retryTimes = StringUtils.isNotBlank(Env.RETRY_TIMES) ? Integer.parseInt(Env.RETRY_TIMES) : 1;

	            while (round <= retryTimes) {
	                try {
	                    /*
	                     * Step 4-2-2. 執行 CLI 取得 VM name 對應的 VM ID
	                     */
	                    DeliveryParameterVO dpVO = new DeliveryParameterVO();
	                    dpVO.setScriptInfoId(vmInfoScriptVO.getScriptInfoId());
	                    dpVO.setScriptCode(vmInfoScriptVO.getScriptCode());

	                    deviceInfo = new HashMap<>();
	                    deviceInfo.put(Constants.DEVICE_IP, esxi.getHostIp());
	                    deviceInfo.put(Constants.DEVICE_NAME, esxi.getEsxiName());
	                    deviceInfo.put(Constants.DEVICE_LOGIN_ACCOUNT, base64Decoder(esxi.getLoginAccount()));      //Base64解碼
	                    deviceInfo.put(Constants.DEVICE_LOGIN_PASSWORD, base64Decoder(esxi.getLoginPassword()));    //Base64解碼
	                    dpVO.setDeviceInfo(deviceInfo);

	                    deliveryVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_VM_SWITCH, dpVO, true, "PRTG", "【VM備援切換】取得ESXi主機上VM清單", false);

	                    final String nameOfVMware = esxiIdNameMapping.get(esxiID);
	                    final String vmInfo = StringUtils.split(deliveryVO.getCmdOutputList().get(0), Env.COMM_SEPARATE_SYMBOL)[1];
	                    System.out.println(nameOfVMware + " >> [ VM INFO ] ##############################################################################");
	                    System.out.println(vmInfo);

	                    final String vmId = analyzeVmId(vmInfo, nameOfVMware);
	                    System.out.println("*********** VM_ID = [" + vmId + "]");

	                    if (vmId == null) {
	                        errorMsg = "查詢不到VMware ID (ESXi名稱: " + esxi.getEsxiName() + ", VMware名稱: " + nameOfVMware + ")";

	                        writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
	                        throw new ServiceLayerException(errorMsg);
	                    }

	                    /*
	                     * Step 4-2-3. 執行 CLI 將指定的 VM ID 關機(power off)
	                     */
	                    dpVO.setScriptInfoId(powerOffScriptVO.getScriptInfoId());
	                    dpVO.setScriptCode(powerOffScriptVO.getScriptCode());

	                    final String scriptKey = powerOffScriptVO.getActionScriptVariable();

	                    List<String> varKey = (List<String>)transJSON2Object(scriptKey, List.class);
	                    dpVO.setVarKey(varKey);

	                    List<List<String>> varValue = new ArrayList<>();
	                    List<String> vmIdList = new ArrayList<>();
	                    vmIdList.add(vmId);
	                    varValue.add(vmIdList);
	                    dpVO.setVarValue(varValue);

	                    //deliveryVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_VM_SWITCH, dpVO, true, "PRTG", "【VM備援切換】將異常的VM進行關機", false);

	                    break;      //執行過程正常下停止retry迴圈

	                } catch (Exception e) {
	                    log.error(e.toString(), e);
	                    round++;    //執行異常進行retry
	                }
	            }
	        }

	    } catch (ServiceLayerException sle) {
            throw sle;

        } catch (Exception e) {
            log.error(e.toString(), e);
            errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";

            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
            throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
        }
	}

	/**
	 * (ePDG備援) 修改boot設定 & reload
	 * @throws ServiceLayerException
	 */
	private void modifyBackupHostBootInfoAndReload() throws ServiceLayerException {
	    String errorMsg = "";
        try {


        /*
        } catch (ServiceLayerException sle) {
            throw sle;
        */
        } catch (Exception e) {
            log.error(e.toString(), e);
            errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";

            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
            throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
        }
	}

	/**
	 * (HeNBGW備援) 將要備援的設備Config內容 context & port 部分寫入到備援機的Config
	 * @param vmSwitchVO
	 * @param deviceList
	 * @throws ServiceLayerException
	 */
	private void insertConfig2BackupHost(VmSwitchVO vmSwitchVO, DeviceList deviceList) throws ServiceLayerException {
	    final String deviceListId = deviceList.getDeviceListId();
        final String restoreVersionId = vmSwitchVO.getRestoreVersionId();
        final List<String> configContent = vmSwitchVO.getConfigContent();

	    String errorMsg = "";
	    try {


	        VersionServiceVO vsVO = new VersionServiceVO();
            vsVO.setDeviceListId(deviceListId);
            vsVO.setRestoreVersionId(restoreVersionId);
            vsVO.setRestoreContentList(configContent);
            versionService.restoreConfig(
                    RestoreMethod.CLI, Constants.RESTORE_TYPE_VM_SWITCH, vsVO, "PRTG", "【VM備援切換】3/3.寫入備份組態設定至還原機");

	    } catch (ServiceLayerException sle) {
            throw sle;

	    } catch (Exception e) {
	        log.error(e.toString(), e);
            errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";

            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
            throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
	    }
	}

	@Override
	public String powerOff(VmSwitchVO vmSwitchVO) throws ServiceLayerException {
		final String apiVmName = vmSwitchVO.getApiVmName();
		String retVal = "已成功將【" + apiVmName + "】切換至備援模式";

		String errorMsg = null;
		try {
		    logKey = vmSwitchVO.getLogKey();
		    logOrderNo = 0;

		    /*
		     * Step 1. 確認備援機目前狀態是否可使用
		     * >> OK: 接續下步驟
		     * >> NO: 流程結束 (e.g.備援機當下非處於備援狀態，可能仍處在別台host的備援服務中)
		     */
		    writeLog(logKey, Step.CHECK_BACKUP_HOST_STATUS, Status.EXECUTING, null);
		    chkBackupHostStatus();

		    Thread.sleep(500);
		    writeLog(logKey, Step.STEP_RESULT, Status.FINISH, null);

		    /* Step 1. [END] **********************************************************************************/

	        /*
             * Step 2-1. 查詢VM名稱對照表，取得 API 傳入的名稱對應到 CMAP & ESXi 內實際 VMware 設定的名稱
             */
		    writeLog(logKey, Step.GET_VM_MAPPING_TABLE, Status.EXECUTING, null);
		    vmSwitchVO = getEsxiAndVmNameMapping(vmSwitchVO, apiVmName);

		    Thread.sleep(500);
		    writeLog(logKey, Step.STEP_RESULT, Status.FINISH, null);

		    /* Step 2-1. [END] ********************************************************************************/

		    final String deviceListId = vmSwitchVO.getDeviceListId();
		    final Map<Integer, String> esxiIdNameMapping = vmSwitchVO.getEsxiIdNameMapping();

		    /*
             * Step 2-2. 查詢要切換的設備相關資料
             */
		    writeLog(logKey, Step.GET_SWITCH_HOST_INFO, Status.EXECUTING, null);
		    Thread.sleep(500);

            DeviceList deviceList = deviceDAO.findDeviceListByDeviceListId(deviceListId);

            if (deviceList == null) {
                errorMsg = "傳入的VM名稱查詢不到對應的設備資料 (VM名稱: " + apiVmName + ", 設備ID: " + deviceListId + ")";

                writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);
                throw new ServiceLayerException("API傳入的VM名稱查詢不到CMAP DeviceList 資料 >> apiVmName: " + apiVmName + ", deviceListId: " + deviceListId);

            } else {
                writeLog(logKey, Step.STEP_RESULT, Status.FINISH, null);
            }

            /* Step 2-2. [END] ********************************************************************************/

            final String groupId = deviceList.getGroupId();
            final String deviceId = deviceList.getDeviceId();
            final String systemVersion = deviceList.getSystemVersion();
            final String deviceEngName = deviceList.getDeviceEngName();
            final String deviceIp = deviceList.getDeviceIp();

            // 判斷要切換的設備是否為ePDG
            final boolean _IS_EPDG_ = chkSwitchHostIsEpdgOrNot(deviceIp);
            vmSwitchVO.setEPDG(_IS_EPDG_);

            if (!_IS_EPDG_) {
                // 如果要切換的不是ePDG，則取得設備最新的備份檔資料
                getSwitchHostConfigBackupData(vmSwitchVO, deviceList);
            }

            /*
             * Step 3. 確認要切換備援的Host是否可SSH連線
             * >> OK: 接續 Step 4-1 (連進Host關Interface)
             * >> NO: 接續 Step 4-2 (連進ESXi關機VM)
             */
            final boolean _SSH_IS_FINE_ = chkSwitchHostSSHStatus(deviceIp, true);

            if (_SSH_IS_FINE_) {
                /*
                 * Step 4-1. 關設備所有Interface
                 */
                disableSwitchHostInterface(deviceList);

            } else {
                /*
                 * Step 4-2. 從ESXi層將要切換的設備VM關機
                 */
                powerOffSwitchHostVmFromEsxi(vmSwitchVO);
            }

            /*
             * Step 5. 登入備援機，依照要切換的設備類型決定還原作法
             */
            if (!_IS_EPDG_) {
                modifyBackupHostBootInfoAndReload();

            } else {
                insertConfig2BackupHost(vmSwitchVO, deviceList);
            }

            /*
             * Step 6. 將切換紀錄寫入 DB
             */

		} catch (ServiceLayerException sle) {
		    log.error(sle.toString(), sle);

		    throw new ServiceLayerException("VM切換失敗");

		} catch (Exception e) {
		    log.error(e.toString(), e);

            errorMsg = "非預期錯誤!! (" + e.getMessage() + ")";
            writeLog(logKey, Step.STEP_RESULT, Status.ERROR, errorMsg);

			throw new ServiceLayerException("VM切換失敗");

		} finally {
		    writeLog(logKey, Step.PROCESS_END, Status.FINISH, null);
		    sendMail();   // 將切換結果發送mail
		}

		return retVal;
	}

	private void sendMail() {

	}

	/**
	 * 分析ESXi上VM清單，取得異常VM名稱對應的ID值
	 * @param vmInfo
	 * @param targetVmName
	 * @return
	 */
	private String analyzeVmId(String vmInfo, String targetVmName) {
		String vmId = null;
		try {
		    if (vmInfo == null || targetVmName == null) {
		        return null;
		    }

			String[] lines = vmInfo.split("\r\n");

			for (String line : lines) {
				if (StringUtils.contains(line, targetVmName)) {
					String[] tmp = line.split(targetVmName);

					vmId = StringUtils.isNotBlank(tmp[0]) ? tmp[0].replaceAll("\\s+","") : null;
					break;
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			vmId = null;
		}

		return vmId;
	}

	private List<String> transVarKeyList(String scriptVariables) {

		return null;
	}

	private List<List<String>> transOneDeviceVarValueList(String values) {

		return null;
	}

	@Override
	public String powerOn(VmSwitchVO vmSwitchVO) throws ServiceLayerException {
		final String apiVmName = vmSwitchVO.getApiVmName();
		String retVal = "已成功將【" + apiVmName + "】從備援模式恢復成一般模式";

		try {




		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException();
		}
		return retVal;
	}

    @Override
    public ModuleVmProcessLog findFistOneNotPushedLogByLogKey(String logKey) throws ServiceLayerException {
        ModuleVmProcessLog retEntity = null;
        try {
            do {
                System.out.println(Constants.FORMAT_YYYYMMDD_HH24MISS.format(new Date()) + " >>> find process log ..");
                List<ModuleVmProcessLog> retList = vmSwitchDAO.findNotPushedModuleVmProcessLogByLogKey(logKey);

                if (retList != null && !retList.isEmpty()) {
                    retEntity = retList.get(0);

                } else {
                    Thread.sleep(1000);
                }

            } while (retEntity == null);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢VM切換log異常");
        }
        return retEntity;
    }

    @Override
    public void updateLog(ModuleVmProcessLog logEntity) throws ServiceLayerException {
        try {
            vmSwitchDAO.updateProcessLog(logEntity);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @Override
    public VmSwitchVO chkVmStatus(VmSwitchVO vmSwitchVO) throws ServiceLayerException {
        boolean vmNowFailure = false;
        String vmStatus = Constants.VM_STATUS_FINE;
        String vmStatusMsg = "";

        try {
            final String apiVmName = vmSwitchVO.getApiVmName();

            /*
             * Step 1. 查詢VM名稱對照表，取得 API 傳入的名稱對應到 CMAP & ESXi 內實際 VMware 設定的名稱
             */
            vmSwitchVO = getEsxiAndVmNameMapping(vmSwitchVO, apiVmName);

            final String deviceListId = vmSwitchVO.getDeviceListId();
            DeviceList deviceList = deviceDAO.findDeviceListByDeviceListId(deviceListId);

            if (deviceList == null) {
                throw new ServiceLayerException("傳入的VM名稱查找不到對應的設備資料 (apiVmName: " + apiVmName + ", deviceListId: " + deviceListId + ")");
            }

            final String deviceIp = deviceList.getDeviceIp();

            /*
             * Step 2. 確認SSH是否可連通
             */
            boolean sshEnable = chkSwitchHostSSHStatus(deviceIp, false);

            if (!sshEnable) {
                vmStatus = Constants.VM_STATUS_SSH_FAILED;
                throw new ServiceLayerException("SSH不通");

            } else {
                /*
                 * Step 3. SSH可通，確認有無 subscriber
                 */
                boolean hasSubscriber = chkSwitchHostHasSubscriber(deviceList);

                if (!hasSubscriber) {
                    vmStatus = Constants.VM_STATUS_NO_SUBSCRIBER;
                    throw new ServiceLayerException("No Subscribers");

                } else {
                    vmNowFailure = false;
                    vmStatusMsg = "SSH & Subscriber檢核正常";
                    vmStatus = Constants.VM_STATUS_FINE;
                }
            }

        } catch (ServiceLayerException sle) {
            vmNowFailure = true;
            vmStatusMsg = sle.getMessage();

        } catch (Exception e) {
            log.error(e.toString(), e);

            vmNowFailure = true;
            vmStatusMsg = e.getMessage();

        } finally {
            vmSwitchVO.setVmNowFailure(vmNowFailure);
            vmSwitchVO.setVmStatus(vmStatus);
            vmSwitchVO.setVmStatusMsg(vmStatusMsg);
        }

        return vmSwitchVO;
    }

    /**
     * 檢核要切換的VM當下是否有 Subscribers
     * @param deviceList
     * @return
     * @throws ServiceLayerException
     */
    private boolean chkSwitchHostHasSubscriber(DeviceList deviceList) throws ServiceLayerException {
        boolean hasSubscriber = false;
        try {
            /*
             * Step 1. 查詢預設腳本 for VM show subscribers
             */
            ScriptServiceVO vmSubScriberScriptVO = scriptService.findDefaultScriptInfoByScriptTypeAndSystemVersion(
                    ScriptType.VM_SUBSCRIBERS.toString(), Constants.DATA_STAR_SYMBOL);

            if (vmSubScriberScriptVO == null) {
                throw new ServiceLayerException("查詢不到預設腳本 for VM show subscribers >> scriptType: " + ScriptType.VM_SUBSCRIBERS);
            }

            /*
             * Step 2. 準備腳本派送所需參數
             */
            DeliveryParameterVO dpVO = new DeliveryParameterVO();
            dpVO.setScriptInfoId(vmSubScriberScriptVO.getScriptInfoId());
            dpVO.setScriptCode(vmSubScriberScriptVO.getScriptCode());

            Map<String, String> deviceInfo = new HashMap<>();
            deviceInfo.put(Constants.DEVICE_IP, deviceList.getDeviceIp());
            deviceInfo.put(Constants.DEVICE_NAME, deviceList.getDeviceEngName());
            deviceInfo.put(Constants.DEVICE_LOGIN_ACCOUNT, Env.DEFAULT_DEVICE_LOGIN_ACCOUNT);
            deviceInfo.put(Constants.DEVICE_LOGIN_PASSWORD, Env.DEFAULT_DEVICE_LOGIN_PASSWORD);
            dpVO.setDeviceInfo(deviceInfo);

            /*
             * Step 3. 呼叫共用進行腳本派送
             */
            DeliveryServiceVO deliveryVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_VM_SWITCH, dpVO, true, "PRTG", "【VM備援切換】取得當前VM subscriber數", false);

            /*
             * Step 4. 將派送查詢結果進行文字處理，取得 Subscriber 數
             */
            final String subscribersInfo = StringUtils.split(deliveryVO.getCmdOutputList().get(0), Env.COMM_SEPARATE_SYMBOL)[1];
            final String subscribersCount = analyzeVmSubscribersCount(subscribersInfo);

            if (subscribersCount != null && Integer.parseInt(subscribersCount) > 0) {
                hasSubscriber = true;
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("非預期錯誤 (" + e.getMessage() + ")");
        }
        return hasSubscriber;
    }

    /**
     * 解析「show subscribers counters」內容，目標值 >> Total Subscribers: 3
     * @param subscribersInfo
     * @return
     */
    private String analyzeVmSubscribersCount(String subscribersInfo) {
        String retVal = null;
        try {
            if (subscribersInfo == null) {
                return null;
            }

            final String keyWord = "Total Subscribers:";
            String[] lines = subscribersInfo.split("\r\n");

            for (String line : lines) {
                if (StringUtils.contains(line, keyWord)) {
                    String[] tmp = line.split(keyWord);

                    /*
                     * 1、 表示空格  " \\s"， "[ ]"， "[\\s]"
                     *    表示多個空格 "\\s+"， "[ ]+"， "[\\s]+"
                     * 2、 表示數字  "\\d"， "[\\d]"， "[0-9]"
                     *    表示多個數字，同理，在後面加上"+"
                     */
                    retVal = StringUtils.isNotBlank(tmp[1]) ? tmp[1].replaceAll("\\s+","") : null;
                    break;
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            retVal = null;
        }

        return retVal;
    }
}
