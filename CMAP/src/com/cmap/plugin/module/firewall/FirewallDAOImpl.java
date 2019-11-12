package com.cmap.plugin.module.firewall;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("firewallDAO")
@Transactional
public class FirewallDAOImpl extends BaseDaoHibernate implements FirewallDAO {
	@Log
    private static Logger log;

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
          .append(" from ( ");

        int beginMonth = fVO.getQueryMonths()[0];
        int endMonth = fVO.getQueryMonths()[1];
        for (int month = beginMonth; month <= endMonth; month++) {
            String tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            String stName = "mfl".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append(" select id from ").append(tName).append(" ").append(stName).append(" ")
              .append(" where 1=1 ");

              if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                  sb.append(" and ").append(stName).append(".dev_name = :devName ");
              }
              if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                  sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
              }
              if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                  sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
              }
              if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                  sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
              }
              if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                  sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
              }
              if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                  if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                      sb.append(" and ").append(stName).append(".action is null ");
                  } else {
                      sb.append(" and ").append(stName).append(".action = :queryAction ");
                  }
              }
              /*
              if (StringUtils.isNotBlank(fVO.getQueryDateBegin())) {
                  sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".date < DATE_ADD(:beginDate, INTERVAL 1 DAY)) ");
              }
              if (StringUtils.isNotBlank(fVO.getQueryTimeBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                  sb.append(" and (").append(stName).append(".time >= :beginTime and ").append(stName).append(".time < :endTime) ");
              }
              */
              if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                  sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
              }
              if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                  sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
              }

              if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                  StringBuffer sfb = new StringBuffer();
                  sfb.append(" and ( ");

                  int i = 0;
                  for (String sField : searchLikeField) {
                      sfb.append(stName).append(".").append(sField).append(" like :searchValue ");

                      if (i < searchLikeField.size() - 1) {
                          sfb.append(" or ");
                      }

                      i++;
                  }

                  sfb.append(" ) ");
                  sb.append(sfb);
              }

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append(" ) mfl ");

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
        if (StringUtils.isNotBlank(fVO.getQueryAction()) && !StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
            q.setParameter("queryAction", fVO.getQueryAction());
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
        sb.append(" select mfl.* from ( ");

        int beginMonth = fVO.getQueryMonths()[0];
        int endMonth = fVO.getQueryMonths()[1];
        for (int month = beginMonth; month <= endMonth; month++) {
            String tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            String stName = "mfl".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append(" select ");

            if (StringUtils.isNotBlank(selectSql)) {
                sb.append(selectSql);
            } else {
                sb.append("*");
            }

            sb.append(" from ").append(tName).append(" ").append(stName).append(" ")
              .append(" where 1=1 ");

                if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                    sb.append(" and ").append(" ").append(stName).append(".dev_name = :devName ");
                }
                if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                    sb.append(" and ").append(" ").append(stName).append(".src_ip = :querySrcIp ");
                }
                if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                    sb.append(" and ").append(" ").append(stName).append(".src_port = :querySrcPort ");
                }
                if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                    sb.append(" and ").append(" ").append(stName).append(".dst_ip = :queryDstIp ");
                }
                if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                    sb.append(" and ").append(" ").append(stName).append(".dst_port = :queryDstPort ");
                }
                if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                    if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                        sb.append(" and ").append(" ").append(stName).append(".action is null ");
                    } else {
                        sb.append(" and ").append(" ").append(stName).append(".action = :queryAction ");
                    }
                }
                /*
                if (StringUtils.isNotBlank(fVO.getQueryDateBegin())) {
                    sb.append(" and (").append(" ").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(" ").append(stName).append(".date < DATE_ADD(:beginDate, INTERVAL 1 DAY)) ");
                }
                if (StringUtils.isNotBlank(fVO.getQueryTimeBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                    sb.append(" and (").append(" ").append(stName).append(".time >= :beginTime and ").append(" ").append(stName).append(".time < :endTime) ");
                }
                */
                if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                    sb.append(" and (").append(" ").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(" ").append(stName).append(".time >= :beginTime) ");
                }
                if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                    sb.append(" and (").append(" ").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(" ").append(stName).append(".time < :endTime) ");
                }

                if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                    StringBuffer sfb = new StringBuffer();
                    sfb.append(" and ( ");

                    int i = 0;
                    for (String sField : searchLikeField) {
                        sfb.append(stName).append(".").append(sField).append(" like :searchValue ");

                        if (i < searchLikeField.size() - 1) {
                            sfb.append(" or ");
                        }

                        i++;
                    }

                    sfb.append(" ) ");
                    sb.append(sfb);
                }

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append(" ) mfl ");

        String orderColumnName = fVO.getOrderColumn();
        String orderDirection = fVO.getOrderDirection();
        if (StringUtils.isNotBlank(orderColumnName) && !StringUtils.equals(orderColumnName, "DATE") && !StringUtils.equals(orderColumnName, "TIME")) {
            sb.append(" order by mfl.").append(orderColumnName).append(" ").append(orderDirection);
        } else {
            sb.append(" order by mfl.date ").append(orderDirection).append(", mfl.time ").append(orderDirection);
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
        if (StringUtils.isNotBlank(fVO.getQueryAction()) && !StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
            q.setParameter("queryAction", fVO.getQueryAction());
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

    @Override
    public long countFirewallLogFromDBbyAll(FirewallVO fVO, Map<String, List<String>> searchLikeFieldMap) {
        int beginMonth = fVO.getQueryMonths()[0];
        int endMonth = fVO.getQueryMonths()[1];
        String tableName = "";
        String tName = "";
        String stName = "";

        StringBuffer sb = new StringBuffer();
        sb.append(" select sum(alltb.cc) ")
          .append(" from ( ");

        tableName = "module_firewall_log_app";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "app".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select count( ").append(stName).append(".id ) cc ")
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_APP);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }
            sb.append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_forwarding";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "forwarding".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select count( ").append(stName).append(".id ) cc ")
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_FORWARDING);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }
            sb.append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_intrusion";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "intrusion".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select count( ").append(stName).append(".id ) cc ")
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_INTRUSION);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }
            sb.append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_system";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "sys".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select count( ").append(stName).append(".id ) cc ")
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_SYSTEM);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }
            sb.append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_webfilter";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "webfilter".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select count( ").append(stName).append(".id ) cc ")
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_WEBFILTER);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }
            sb.append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append(") alltb ");

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
        if (StringUtils.isNotBlank(fVO.getQueryAction()) && !StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
            q.setParameter("queryAction", fVO.getQueryAction());
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
    public List<Object[]> findFirewallLogFromDBbyAll(FirewallVO fVO, Integer startRow, Integer pageLength,
            Map<String, String> selectSqlMap, Map<String, List<String>> searchLikeFieldMap) {
        int beginMonth = fVO.getQueryMonths()[0];
        int endMonth = fVO.getQueryMonths()[1];
        String tableName = "";
        String tName = "";
        String stName = "";

        String orderColumnName = fVO.getOrderColumn();
        String orderDirection = StringUtils.isBlank(fVO.getOrderDirection()) ? "desc" : fVO.getOrderDirection();

        // 計算各別TABLE要LIMIT的筆數，依照使用者滑動查詢分頁的進度決定
        int subTableLimit = 100;
        if (startRow != null && pageLength != null && startRow > 0) {
            subTableLimit = startRow;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(" select alltb.* ")
          .append(" from ( ");

        tableName = "module_firewall_log_app";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "app".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select ").append(selectSqlMap.get(Constants.FIREWALL_LOG_TYPE_APP))
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_APP);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }

            // 各別TABLE先 order by 後限制筆數，避免所有資料JOIN
            if (StringUtils.isNotBlank(orderColumnName)
                    // 排除此查詢類別沒有的欄位
                    && (!StringUtils.equals(orderColumnName, "DATE")
                        && !StringUtils.equals(orderColumnName, "TIME")
                        && !StringUtils.equals(orderColumnName, "SEVERITY")
                        && !StringUtils.equals(orderColumnName, "SRC_COUNTRY")
                        && !StringUtils.equals(orderColumnName, "SERVICE")
                        && !StringUtils.equals(orderColumnName, "URL")
                        && !StringUtils.equals(orderColumnName, "SENT_BYTE")
                        && !StringUtils.equals(orderColumnName, "RCVD_BYTE")
                        && !StringUtils.equals(orderColumnName, "UTM_ACTION")
                        && !StringUtils.equals(orderColumnName, "LEVEL")
                        && !StringUtils.equals(orderColumnName, "USER")
                        && !StringUtils.equals(orderColumnName, "MESSAGE")
                        && !StringUtils.equals(orderColumnName, "ATTACK"))) {
                sb.append(" order by ").append(stName).append(".").append(orderColumnName).append(" ").append(orderDirection);

            } else {
                sb.append(" order by ").append(stName).append(".date ").append(orderDirection).append(", ").append(stName).append(".time ").append(orderDirection);
            }
            sb.append("  limit ").append(subTableLimit)
              .append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_forwarding";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "forwarding".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select ").append(selectSqlMap.get(Constants.FIREWALL_LOG_TYPE_FORWARDING))
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_FORWARDING);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }

            //各別TABLE先 order by 後限制筆數，避免所有資料JOIN
            if (StringUtils.isNotBlank(orderColumnName)
                    // 排除此查詢類別沒有的欄位
                    && (!StringUtils.equals(orderColumnName, "DATE")
                        && !StringUtils.equals(orderColumnName, "TIME")
                        && !StringUtils.equals(orderColumnName, "SEVERITY")
                        && !StringUtils.equals(orderColumnName, "SRC_COUNTRY")
                        && !StringUtils.equals(orderColumnName, "SERVICE")
                        && !StringUtils.equals(orderColumnName, "URL")
                        && !StringUtils.equals(orderColumnName, "LEVEL")
                        && !StringUtils.equals(orderColumnName, "USER")
                        && !StringUtils.equals(orderColumnName, "MESSAGE")
                        && !StringUtils.equals(orderColumnName, "ATTACK"))) {
                sb.append(" order by ").append(stName).append(".").append(orderColumnName).append(" ").append(orderDirection);

            } else {
                sb.append(" order by ").append(stName).append(".date ").append(orderDirection).append(", ").append(stName).append(".time ").append(orderDirection);
            }
            sb.append("  limit ").append(subTableLimit)
              .append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_intrusion";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "intrusion".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select ").append(selectSqlMap.get(Constants.FIREWALL_LOG_TYPE_INTRUSION))
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_INTRUSION);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }

            //各別TABLE先 order by 後限制筆數，避免所有資料JOIN
            if (StringUtils.isNotBlank(orderColumnName)
                    // 排除此查詢類別沒有的欄位
                    && (!StringUtils.equals(orderColumnName, "DATE")
                        && !StringUtils.equals(orderColumnName, "TIME")
                        && !StringUtils.equals(orderColumnName, "SERVICE")
                        && !StringUtils.equals(orderColumnName, "URL")
                        && !StringUtils.equals(orderColumnName, "APP")
                        && !StringUtils.equals(orderColumnName, "SENT_BYTE")
                        && !StringUtils.equals(orderColumnName, "RCVD_BYTE")
                        && !StringUtils.equals(orderColumnName, "UTM_ACTION")
                        && !StringUtils.equals(orderColumnName, "LEVEL")
                        && !StringUtils.equals(orderColumnName, "USER")
                        && !StringUtils.equals(orderColumnName, "MESSAGE"))) {
                sb.append(" order by ").append(stName).append(".").append(orderColumnName).append(" ").append(orderDirection);

            } else {
                sb.append(" order by ").append(stName).append(".date ").append(orderDirection).append(", ").append(stName).append(".time ").append(orderDirection);
            }
            sb.append("  limit ").append(subTableLimit)
              .append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_system";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "sys".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select ").append(selectSqlMap.get(Constants.FIREWALL_LOG_TYPE_SYSTEM))
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_SYSTEM);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }

            //各別TABLE先 order by 後限制筆數，避免所有資料JOIN
            if (StringUtils.isNotBlank(orderColumnName)
                    // 排除此查詢類別沒有的欄位
                    && (!StringUtils.equals(orderColumnName, "DATE")
                        && !StringUtils.equals(orderColumnName, "TIME")
                        && !StringUtils.equals(orderColumnName, "SEVERITY")
                        && !StringUtils.equals(orderColumnName, "SRC_IP")
                        && !StringUtils.equals(orderColumnName, "SRC_PORT")
                        && !StringUtils.equals(orderColumnName, "SRC_COUNTRY")
                        && !StringUtils.equals(orderColumnName, "DST_IP")
                        && !StringUtils.equals(orderColumnName, "DST_PORT")
                        && !StringUtils.equals(orderColumnName, "PROTO")
                        && !StringUtils.equals(orderColumnName, "SERVICE")
                        && !StringUtils.equals(orderColumnName, "URL")
                        && !StringUtils.equals(orderColumnName, "APP")
                        && !StringUtils.equals(orderColumnName, "ACTION")
                        && !StringUtils.equals(orderColumnName, "SENT_BYTE")
                        && !StringUtils.equals(orderColumnName, "RCVD_BYTE")
                        && !StringUtils.equals(orderColumnName, "UTM_ACTION")
                        && !StringUtils.equals(orderColumnName, "ATTACK"))) {
                sb.append(" order by ").append(stName).append(".").append(orderColumnName).append(" ").append(orderDirection);

            } else {
                sb.append(" order by ").append(stName).append(".date ").append(orderDirection).append(", ").append(stName).append(".time ").append(orderDirection);
            }
            sb.append("  limit ").append(subTableLimit)
              .append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append("   union all ");

        tableName = "module_firewall_log_webfilter";
        for (int month = beginMonth; month <= endMonth; month++) {
            tName = tableName.concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));
            stName = "webfilter".concat("_").concat(StringUtils.leftPad(String.valueOf(month), 3, "0"));

            sb.append("  ( ")
              .append("   select ").append(selectSqlMap.get(Constants.FIREWALL_LOG_TYPE_WEBFILTER))
              .append("   from ").append(tName).append(" ").append(stName).append(" ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(fVO.getQueryDevName())) {
                sb.append(" and ").append(stName).append(".dev_name = :devName ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcIp())) {
                sb.append(" and ").append(stName).append(".src_ip = :querySrcIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQuerySrcPort())) {
                sb.append(" and ").append(stName).append(".src_port = :querySrcPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstIp())) {
                sb.append(" and ").append(stName).append(".dst_ip = :queryDstIp ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDstPort())) {
                sb.append(" and ").append(stName).append(".dst_port = :queryDstPort ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryAction())) {
                if (StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
                    sb.append(" and ").append(stName).append(".action is null ");
                } else {
                    sb.append(" and ").append(stName).append(".action = :queryAction ");
                }
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateBegin()) && StringUtils.isNotBlank(fVO.getQueryTimeBegin())) {
                sb.append(" and (").append(stName).append(".date >= DATE_FORMAT(:beginDate, '%Y-%m-%d') and ").append(stName).append(".time >= :beginTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getQueryDateEnd()) && StringUtils.isNotBlank(fVO.getQueryTimeEnd())) {
                sb.append(" and (").append(stName).append(".date <= DATE_FORMAT(:endDate, '%Y-%m-%d') and ").append(stName).append(".time < :endTime) ");
            }
            if (StringUtils.isNotBlank(fVO.getSearchValue())) {
                StringBuffer sfb = new StringBuffer();
                sfb.append(" and ( ");

                List<String> searchLikeField = searchLikeFieldMap.get(Constants.FIREWALL_LOG_TYPE_WEBFILTER);
                int i = 0;
                for (String sField : searchLikeField) {
                    sfb.append("").append(stName).append(".").append(sField).append(" like :searchValue ");

                    if (i < searchLikeField.size() - 1) {
                        sfb.append(" or ");
                    }

                    i++;
                }

                sfb.append(" ) ");
                sb.append(sfb);
            }

            //各別TABLE先 order by 後限制筆數，避免所有資料JOIN
            if (StringUtils.isNotBlank(orderColumnName)
                    // 排除此查詢類別沒有的欄位
                    && (!StringUtils.equals(orderColumnName, "DATE")
                        && !StringUtils.equals(orderColumnName, "TIME")
                        && !StringUtils.equals(orderColumnName, "SEVERITY")
                        && !StringUtils.equals(orderColumnName, "SRC_COUNTRY")
                        && !StringUtils.equals(orderColumnName, "APP")
                        && !StringUtils.equals(orderColumnName, "UTM_ACTION")
                        && !StringUtils.equals(orderColumnName, "LEVEL")
                        && !StringUtils.equals(orderColumnName, "USER")
                        && !StringUtils.equals(orderColumnName, "MESSAGE")
                        && !StringUtils.equals(orderColumnName, "ATTACK"))) {
                sb.append(" order by ").append(stName).append(".").append(orderColumnName).append(" ").append(orderDirection);

            } else {
                sb.append(" order by ").append(stName).append(".date ").append(orderDirection).append(", ").append(stName).append(".time ").append(orderDirection);
            }
            sb.append("  limit ").append(subTableLimit)
              .append(" ) ");

            if (month < endMonth) {
                sb.append(" union all ");
            }
        }

        sb.append(") alltb ");

        if (StringUtils.isNotBlank(orderColumnName) && !StringUtils.equals(orderColumnName, "DATE") && !StringUtils.equals(orderColumnName, "TIME")) {
            sb.append(" order by alltb.").append(orderColumnName).append(" ").append(orderDirection);

        } else {
            sb.append(" order by alltb.date ").append(orderDirection).append(", alltb.time ").append(orderDirection);
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
        if (StringUtils.isNotBlank(fVO.getQueryAction()) && !StringUtils.equals(fVO.getQueryAction(), Constants.EMPTY)) {
            q.setParameter("queryAction", fVO.getQueryAction());
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
