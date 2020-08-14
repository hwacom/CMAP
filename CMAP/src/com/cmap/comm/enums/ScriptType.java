package com.cmap.comm.enums;

public enum ScriptType {
	//script_default_mapping用
	BACKUP,
	RESTORE_WITH_COPY_CONFIG,
	RESTORE_WITHOUT_COPY_CONFIG,
	VM_INFO,
	VM_SUBSCRIBERS,
	VM_POWER_OFF,
	VM_POWER_ON,
	VM_SHUTDOWN_PORT,
	CLUSTER_STATUS,
	CLUSTER_MIGRATE,
	SERVICE_RESTART,
	IP_BLOCK,
	PORT_BLOCK,
	MAC_BLOCK,
	MAC_OPEN,
	VLAN_SWITCH,
	//Script Info 用
	BAK_,//備份
	RES_,//還原
	IP_,//IP封鎖/開通
	IP_CTR_,//IP控制 (中心端)封鎖/開通
	PORT_,//PORT封鎖/開通
	MAC_,//MAC封鎖開通
	BIND_//IP MAC 綁定/開通
}
