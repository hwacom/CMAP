package com.cmap.plugin.module.firewall;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("firewallDAO")
@Transactional
public class FirewallDAOImpl extends BaseDaoHibernate implements FirewallDAO {

    @Override
    public List<ModuleFirewallLogSetting> getFirewallLogSetting(String settingName) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mfls ")
          .append(" from ModuleFirewallLogSetting mfls ")
          .append(" where 1=1 ")
          .append(" and mfls.settingName = :settingName ")
          .append(" order by mfls.orderNo asc ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("settingName", settingName);

        return (List<ModuleFirewallLogSetting>)q.list();
    }

    @Override
    public long countFirewallLogFromDB(FirewallVO fVO, List<String> searchLikeField,
            String tableName) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mfl.id) ")
          .append(" from ").append(tableName).append(" mfl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
            sb.append(" and mfl.dev_name = :devName ");
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
            sb.append(" and mfl.src_ip = :querySrcIp ");
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
            sb.append(" and mfl.src_port = :querySrcPort ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
            sb.append(" and mfl.dst_ip = :queryDstIp ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
            sb.append(" and mfl.dst_port = :queryDstPort ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryDateBegin())) {
            sb.append(" and (mfl.date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and mfl.date < DATE_ADD(:beginDate, INTERVAL 1 DAY)) ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryTimeBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
            sb.append(" and (mfl.time >= :beginTime and mfl.time < :endTime) ");
        }

        if (StringUtils.isNotBlank(fVO.getSearchValue())) {
            StringBuffer sfb = new StringBuffer();
            sfb.append(" and ( ");

            int i = 0;
            for (String sField : searchLikeField) {
                sfb.append(sField).append(" like :searchValue ");

                if (i < searchLikeField.size() - 1) {
                    sfb.append(" or ");
                }

                i++;
            }

            sfb.append(" ) ");
            sb.append(sfb);
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
            q.setParameter("devName", fVO.getQueryDevName());
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
            q.setParameter("querySrcIp", fVO.getQuerySrcIp());
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
            q.setParameter("querySrcPort", fVO.getQuerySrcPort());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
            q.setParameter("queryDstIp", fVO.getQueryDstIp());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
            q.setParameter("queryDstPort", fVO.getQueryDstPort());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDateBegin())) {
            q.setParameter("beginDate", fVO.getQueryDateBegin());
        }
        if (StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
            q.setParameter("beginTime", fVO.getQueryTimeBegin());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDateEnd())) {
            q.setParameter("endDate", fVO.getQueryDateEnd());
        }
        if (StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
            q.setParameter("endTime", fVO.getQueryTimeEnd());
        }
        if (StringUtils.isNotBlank(fVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(fVO.getSearchValue()).concat("%"));
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findFirewallLogFromDB(FirewallVO fVO, Integer startRow,
            Integer pageLength, List<String> searchLikeField, String tableName, String selectSql) {
        StringBuffer sb = new StringBuffer();

        sb.append(" select ");

        if (StringUtils.isNotBlank(selectSql)) {
            sb.append(selectSql);
        } else {
            sb.append("*");
        }

        sb.append(" from ").append(tableName).append(" mfl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
            sb.append(" and mfl.dev_name = :devName ");
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
            sb.append(" and mfl.src_ip = :querySrcIp ");
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
            sb.append(" and mfl.src_port = :querySrcPort ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
            sb.append(" and mfl.dst_ip = :queryDstIp ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
            sb.append(" and mfl.dst_port = :queryDstPort ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryDateBegin())) {
            sb.append(" and (mfl.date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and mfl.date < DATE_ADD(:beginDate, INTERVAL 1 DAY)) ");
        }
        if (StringUtils.isNotBlank(fVO.getQueryTimeBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
            sb.append(" and (mfl.time >= :beginTime and mfl.time < :endTime) ");
        }

        if (StringUtils.isNotBlank(fVO.getSearchValue())) {
            StringBuffer sfb = new StringBuffer();
            sfb.append(" and ( ");

            int i = 0;
            for (String sField : searchLikeField) {
                sfb.append(sField).append(" like :searchValue ");

                if (i < searchLikeField.size() - 1) {
                    sfb.append(" or ");
                }

                i++;
            }

            sfb.append(" ) ");
            sb.append(sfb);
        }

        if (StringUtils.isNotBlank(fVO.getOrderColumn())) {
            sb.append(" order by mfl.").append(fVO.getOrderColumn()).append(" ").append(fVO.getOrderDirection());

        } else {
            sb.append(" order by mfl.date desc, mfl.time desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
            q.setParameter("devName", fVO.getQueryDevName());
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
            q.setParameter("querySrcIp", fVO.getQuerySrcIp());
        }
        if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
            q.setParameter("querySrcPort", fVO.getQuerySrcPort());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
            q.setParameter("queryDstIp", fVO.getQueryDstIp());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
            q.setParameter("queryDstPort", fVO.getQueryDstPort());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDateBegin())) {
            q.setParameter("beginDate", fVO.getQueryDateBegin());
        }
        if (StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
            q.setParameter("beginTime", fVO.getQueryTimeBegin());
        }
        if (StringUtils.isNotBlank(fVO.getQueryDateEnd())) {
            q.setParameter("endDate", fVO.getQueryDateEnd());
        }
        if (StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
            q.setParameter("endTime", fVO.getQueryTimeEnd());
        }
        if (StringUtils.isNotBlank(fVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(fVO.getSearchValue()).concat("%"));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }

        return (List<Object[]>)q.list();
    }
}
