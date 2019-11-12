package com.cmap.comm.enums;

public enum Step {

    /**
     * 讀取預設腳本
     */
	LOAD_DEFAULT_SCRIPT,

	/**
	 * 讀取指定腳本(Action Script)
	 */
	LOAD_SPECIFIED_SCRIPT,

	/**
	 * 讀取指定檢核腳本(Check Script)
	 */
	LOAD_SPECIFIED_CHECK_SCRIPT,

	/**
	 * 查詢設備連線資訊
	 */
	FIND_DEVICE_CONNECT_INFO,

	/**
	 * 查詢設備登入資訊
	 */
	FIND_DEVICE_LOGIN_INFO,

	/**
	 * 連線設備
	 */
	CONNECT_DEVICE,

	/**
	 * 登入設備
	 */
	LOGIN_DEVICE,

	/**
	 * 發送命令
	 */
	SEND_COMMANDS,

	/**
	 * 比對內容
	 */
	COMPARE_CONTENTS,

	/**
	 * 定義輸出檔案名稱
	 */
	DEFINE_OUTPUT_FILE_NAME,

	/**
	 * 組合輸出檔案所需資訊VO
	 */
	COMPOSE_OUTPUT_VO,

	/**
	 * 連線 File server for 下載
	 */
	CONNECT_FILE_SERVER_4_DOWNLOAD,

	/**
	 * 連線 File server for 上傳
	 */
	CONNECT_FILE_SERVER_4_UPLOAD,

	/**
	 * 登入 File server for 下載
	 */
	LOGIN_FILE_SERVER_4_DOWNLOAD,

	/**
	 * 登入 File server for 上傳
	 */
	LOGIN_FILE_SERVER_4_UPLOAD,

	/**
	 * 上傳檔案到 File server
	 */
	UPLOAD_FILE_SERVER,

	/**
	 * 將組態檔版本資訊寫入DB
	 */
	RECORD_DB_OF_CONFIG_VERSION_INFO,

	/**
	 * 關閉設備連線
	 */
	CLOSE_DEVICE_CONNECTION,

	/**
	 * 關閉 File server 連線
	 */
	CLOSE_FILE_SERVER_CONNECTION,

	/**
	 * 下載檔案
	 */
	DOWNLOAD_FILE,

	/**
	 * 檢查供裝結果
	 * 目前for[IP封鎖]使用，當IP封鎖腳本執行完後，再執行檢核腳本(ping IP)確認是否有正常封鎖，沒有的話則接續進行第二種封鎖方式
	 * Case:
	 *   [苗栗教網]無法透過三灣國中的L3 switch封鎖IP，已查明是Gateway不如預期，但是否有其他方式從學校端封鎖?
	 *            解法: 系統自動檢查，鎖完之後如果還Ping的通，則判定為失敗，自動封鎖MAC(從ARP_TABLE查詢)
	 */
	CHECK_PROVISION_RESULT,

	/**
	 * 分析組態內容 (若有設定要擷取特定內容時，在此部分執行)
	 * 設定表: config_content_setting
	 * 擷取的內容寫入資料表: device_detail_info
	 */
	ANALYZE_CONFIG_INFO,

	/**
	 * for [組態還原]流程使用
	 * 針對設定表(config_content_setting)決定在執行組態還原時，要還原哪些部分內容
	 * ([亞太VM切換]功能，針對VM的組態還原不能用檔案覆蓋的方式，只能還原部分設定檔內容
	 *  因此透過此設定，先將要還原的內容擷取出來，後面再針對擷取的內容還原目標設備的組態內容)
	 */
	PROCESS_CONFIG_CONTENT_SETTING,

	/**
	 * 取得要還原的版本號相關資訊
	 */
	GET_VERSION_INFO,

	/**
	 * 依照前面傳入的資訊設定要還原的版本號
	 */
	SET_LOCAL_VERSION_INFO,

	/**
	 * 有啟用版本異常通知時才執行 (Env.ENABLE_CONFIG_DIFF_NOTIFY)
	 * 在執行組態備份流程時，若組態內容版本有差異的話則(1)寫入config_version_diff_log、(2)發信通知<尚未開發完成>
	 */
	VERSION_DIFF_NOTIFY,

	/**
	 * 針對特定腳本(ex: IP封鎖、PORT封鎖、MAC封鎖)
	 * 在執行後須寫入特定紀錄TABLE
	 */
	WRITE_SPECIFY_LOG,

	/**
	 * 當一般腳本執行結果失敗時，再執行特定的替代方案流程
	 * (目前for[苗栗教網]IP封鎖失敗時，再改以MAC封鎖)
	 */
	DO_SPECIFIED_ALTERNATIVE_ACTION
}
