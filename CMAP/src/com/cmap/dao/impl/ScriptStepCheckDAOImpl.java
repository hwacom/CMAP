package com.cmap.dao.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.ScriptStepDAO;
import com.cmap.dao.vo.ScriptStepDAOVO;
import com.cmap.model.ScriptStepAction;

@Repository("scriptStepCheckDAOImpl")
@Transactional
public class ScriptStepCheckDAOImpl extends ScriptStepDAOImpl implements ScriptStepDAO {
	@Log
    private static Logger log;

	@Override
	public List<ScriptStepDAOVO> findScriptStepByScriptInfoIdOrScriptCode(String scriptInfoId, String scriptCode) {
	    if (StringUtils.isBlank(scriptInfoId) && StringUtils.isBlank(scriptCode)) {
            return null;
        }

		StringBuffer sb = new StringBuffer();
		sb.append(" from ScriptStepCheck ssa ")
		  .append(" where 1=1 ")
		  .append(" and ssa.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (StringUtils.isNotBlank(scriptInfoId)) {
			sb.append(" and ssa.scriptInfo.scriptInfoId = :scriptInfoId ");
		}
		if (StringUtils.isNotBlank(scriptCode)) {
			sb.append(" and ssa.scriptInfo.scriptCode = :scriptCode ");
		}

		sb.append(" order by ssa.stepOrder ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (StringUtils.isNotBlank(scriptInfoId)) {
			q.setParameter("scriptInfoId", scriptInfoId);
		}
		if (StringUtils.isNotBlank(scriptCode)) {
			q.setParameter("scriptCode", scriptCode);
		}

		return transModel2DAOVO(q.list());
	}

	@Override
	public void delete(ScriptStepAction action) {
		getHibernateTemplate().delete(action);
	}
}
