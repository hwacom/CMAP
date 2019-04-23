package com.cmap.dao;

import com.cmap.dao.vo.SysMailDAOVO;

public interface SysMailDAO {

    /**
     * 查找 SYS_MAIL_LIST_SETTING 資料 BY SETTING_CODE
     * @param settingCode
     * @return
     */
    public SysMailDAOVO getMailListSettingBySettingCode(String settingCode);
}
