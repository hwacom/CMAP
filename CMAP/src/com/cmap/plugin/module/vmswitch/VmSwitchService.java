package com.cmap.plugin.module.vmswitch;

import com.cmap.exception.ServiceLayerException;

public interface VmSwitchService {

    public enum Step {
        CHECK_BACKUP_HOST_STATUS,
        GET_VM_MAPPING_TABLE,
        GET_SWITCH_HOST_INFO,
        GET_CONFIG_BACKUP_RECORD,
        CHECK_SSH_STATUS,
        DISABLE_SWITCH_HOST_INTERFACE,
        POWER_OFF_FROM_ESXI,
        INSERT_CONFIG_TO_BACKUP_HOST,
        MODIFY_BOOT_SETTING_AND_RELOAD,
        STEP_RESULT,
        PROCESS_END,
        NONE
    }

    public enum Status {
        EXECUTING,
        FINISH,
        ERROR,
        END
    };

	/**
	 * 將指定 VM Name 切換成備援機
	 * @param vmSwitchVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public String powerOff(VmSwitchVO vmSwitchVO) throws ServiceLayerException;

	/**
	 * 將指定 VM Name 從備援復原
	 * @param vmSwitchVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public String powerOn(VmSwitchVO vmSwitchVO) throws ServiceLayerException;

	public ModuleVmProcessLog findFistOneNotPushedLogByLogKey(String logKey) throws ServiceLayerException;

	public void updateLog(ModuleVmProcessLog logEntity) throws ServiceLayerException;
}
