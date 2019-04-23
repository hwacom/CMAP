package com.cmap.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.dao.SysMailDAO;
import com.cmap.dao.vo.SysMailDAOVO;
import com.cmap.model.SysMailListSetting;

@Repository("sysMailDAO")
@Transactional
public class SysMailDAOImpl extends BaseDaoHibernate implements SysMailDAO {

    @Override
    public SysMailDAOVO getMailListSettingBySettingCode(String settingCode) {
        SysMailDAOVO retDAOVO = null;

        StringBuffer sb = new StringBuffer();
        sb.append(" from SysMailListSetting smls ")
        .append(" where 1=1 ")
        .append(" and scs.settingCode = :settingCode ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("settingCode", settingCode);

        SysMailListSetting setting = (SysMailListSetting)q.uniqueResult();

        if (setting != null) {
            retDAOVO = new SysMailDAOVO();
            retDAOVO.setSubject(setting.getMailSubject());
            retDAOVO.setMailTo(setting.getMailTo().split(","));

            if (StringUtils.isNotBlank(setting.getMailCc())) {
                retDAOVO.setMailCc(setting.getMailCc().split(","));
            }
            if (StringUtils.isNotBlank(setting.getMailBcc())) {
                retDAOVO.setMailBcc(setting.getMailBcc().split(","));
            }
            retDAOVO.setRemark(setting.getRemark());
        }

        return retDAOVO;
    }
}
