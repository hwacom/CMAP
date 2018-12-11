package com.cmap.comm.enums;

public enum Step {

	LOAD_DEFAULT_SCRIPT,
	LOAD_SPECIFIED_SCRIPT,
	FIND_DEVICE_CONNECT_INFO,
	FIND_DEVICE_LOGIN_INFO,
	CONNECT_DEVICE,
	LOGIN_DEVICE,
	SEND_COMMANDS,
	COMPARE_CONTENTS,
	DEFINE_OUTPUT_FILE_NAME,
	COMPOSE_OUTPUT_VO,
	CONNECT_FILE_SERVER_4_DOWNLOAD,
	CONNECT_FILE_SERVER_4_UPLOAD,
	LOGIN_FILE_SERVER_4_DOWNLOAD,
	LOGIN_FILE_SERVER_4_UPLOAD,
	UPLOAD_FILE_SERVER,
	RECORD_DB_OF_CONFIG_VERSION_INFO,
	CLOSE_DEVICE_CONNECTION,
	CLOSE_FILE_SERVER_CONNECTION,
	DOWNLOAD_FILE,
	CHECK_PROVISION_RESULT,
	ANALYZE_CONFIG_INFO

}