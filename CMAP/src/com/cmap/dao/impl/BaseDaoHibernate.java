package com.cmap.dao.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.BaseDAO;
import com.cmap.dao.vo.CommonDAOVO;
import com.cmap.model.ConfigVersionInfo;
import com.cmap.model.DeviceList;
import com.cmap.plugin.module.clustermigrate.ModuleClusterMigrateLog;
import com.cmap.plugin.module.ip.maintain.ModuleIpDataSetting;
import com.cmap.plugin.module.netflow.statistics.ModuleIpTrafficStatistics;
public class BaseDaoHibernate extends HibernateDaoSupport implements BaseDAO {
	@Log
	private static Logger log;

	@Resource(name = "primarySessionFactory")
    private SessionFactory primarySessionFactory;

	@Resource(name = "secondSessionFactory")
    private SessionFactory secondSessionFactory;
	
	@Resource(name = "quartzSessionFactory")
    private SessionFactory quartzSessionFactory;
	
	protected static final String MARK_AS_DELETE = "Y";

	@Autowired
	public void setSessionFactoryOverride(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	protected String composeSelectStr(String[] fields, String alias) {
		StringBuffer sb = new StringBuffer();

		int idx = 0;
		for (String field : fields) {
			sb.append(" ").append(alias).append(".").append(field);

			if (idx != fields.length-1) {
				sb.append(",");
			}

			idx++;
		}

		return sb.toString();
	}

	/**
	 * 因部分查詢SQL語法HQL不支援，採用Native SQL寫法，查詢結果為Object陣列List
	 * 透過此方法將List<Object[]>轉換為List<ConfigVersionInfo>
	 */
	protected List<Object[]> transObjList2ModelList4Version(List<Object[]> objList) {
		List<Object[]> retList = new ArrayList<>();

		try {
			if (objList != null && !objList.isEmpty()) {

				int versionFieldLength = Constants.HQL_FIELD_NAME_FOR_VERSION.length;
				int deviceFieldLength = Constants.HQL_FIELD_NAME_FOR_DEVICE.length;
				ConfigVersionInfo cviModel;
				DeviceList dlModel;
				for (Object[] objArray : objList) {
					if (objArray != null) {
						cviModel = new ConfigVersionInfo();
						dlModel = new DeviceList();

						for (int i = 0; i<versionFieldLength; i++) {
							new PropertyDescriptor(Constants.HQL_FIELD_NAME_FOR_VERSION[i], cviModel.getClass()).getWriteMethod().invoke(cviModel, objArray[i]);

							try {
								new PropertyDescriptor(Constants.HQL_FIELD_NAME_FOR_VERSION[i], dlModel.getClass()).getWriteMethod().invoke(dlModel, objArray[i]);
							} catch (IntrospectionException ie) {
								//僅塞入Group_Id、Group_Name、Device_Id、Device_Name至DeviceList model，其餘欄位不處理
							}
						}

						for (int j = versionFieldLength; j<(versionFieldLength+deviceFieldLength); j++) {
							new PropertyDescriptor(Constants.HQL_FIELD_NAME_FOR_DEVICE[j-versionFieldLength], dlModel.getClass()).getWriteMethod().invoke(dlModel, objArray[j]);
						}

						retList.add(new Object[] {cviModel, dlModel});
					}
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return retList;
	}

	/**
	 * 因部分查詢SQL語法HQL不支援，採用Native SQL寫法，查詢結果為Object陣列List
	 * 透過此方法將List<Object[]>轉換為List<ConfigVersionInfo>
	 */
	protected List<Object[]> transObjList2ModelList4Device(List<Object[]> objList) {
		List<Object[]> retList = new ArrayList<>();

		try {
			if (objList != null && !objList.isEmpty()) {

				int deviceFieldLength = Constants.HQL_FIELD_NAME_FOR_DEVICE_2.length;
				int versionFieldLength = Constants.HQL_FIELD_NAME_FOR_VERSION_2.length;
				ConfigVersionInfo cviModel;
				DeviceList dlModel;
				for (Object[] objArray : objList) {
					if (objArray != null) {
						cviModel = new ConfigVersionInfo();
						dlModel = new DeviceList();

						for (int i = 0; i<deviceFieldLength; i++) {
							new PropertyDescriptor(Constants.HQL_FIELD_NAME_FOR_DEVICE_2[i], dlModel.getClass()).getWriteMethod().invoke(dlModel, objArray[i]);

							try {
								new PropertyDescriptor(Constants.HQL_FIELD_NAME_FOR_DEVICE_2[i], cviModel.getClass()).getWriteMethod().invoke(cviModel, objArray[i]);
							} catch (IntrospectionException ie) {
								//僅塞入Group_Id、Group_Name、Device_Id、Device_Name至ConfigVersionInfo model，其餘欄位不處理
							}
						}

						for (int j = deviceFieldLength; j<(deviceFieldLength+versionFieldLength); j++) {
							new PropertyDescriptor(Constants.HQL_FIELD_NAME_FOR_VERSION_2[j-deviceFieldLength], cviModel.getClass()).getWriteMethod().invoke(cviModel, objArray[j]);
						}

						retList.add(new Object[] {cviModel, dlModel});
					}
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return retList;
	}

	@Override
    public Object insertEntityAndGetReturnIdValue(Object entity) {
        return getHibernateTemplate().save(entity);
    }

	@Override
	public boolean insertEntities(String targetDB, List<? extends Object> entities) {
		return processEntities(targetDB, entities, Constants.DAO_ACTION_INSERT);
	}

	@Override
    public boolean updateEntities(String targetDB, List<? extends Object> entities) {
	    return processEntities(targetDB, entities, Constants.DAO_ACTION_UPDATE);
    }

	@Override
    public boolean deleteEntities(String targetDB, List<? extends Object> entities) {
        return processEntities(targetDB, entities, Constants.DAO_ACTION_DELETE);
    }

	private boolean processEntities(String targetDB, List<? extends Object> entities, String processType) {
        boolean success = true;

        /*
         * 複製一份LIST給如果要寫入多個DB使用，避免在寫入第二個DB時發生Key修改問題
         * (identifier of an instance of com.cmap.plugin.module.ip.maintain.ModuleIpDataSetting
         *  was altered from 40283a816d70a673016d70a8572e0000 to 40283a816d70a673016d70a8712e0004)
         */
        List<Object> copyEntities = new ArrayList<>();

        if (StringUtils.equals(targetDB, TARGET_ALL_DB)) {
        	/*
        	 * 若有需要同時寫兩邊DB的資料，需在此定義Object instance
        	 */
        	Object copyEntity = null;
            for (Object obj : entities) {
                if (obj instanceof ModuleIpDataSetting) {
                    copyEntity = new ModuleIpDataSetting();

                } else if (obj instanceof ModuleIpTrafficStatistics) {
                    copyEntity = new ModuleIpTrafficStatistics();

                } else if (obj instanceof ModuleClusterMigrateLog) {
                    copyEntity = new ModuleClusterMigrateLog();

                } else {
                    log.error("Entity object can't mapping to specify instance class!!");
                    return false;
                }

                if (copyEntity != null) {
                    BeanUtils.copyProperties(obj, copyEntity);
                    copyEntities.add(copyEntity);
                }
            }
        }

        if (StringUtils.equals(targetDB, TARGET_ALL_DB) || StringUtils.equals(targetDB, TARGET_PRIMARY_DB)) {
            /*
            int count = 1;
            for (Object entity : entities) {
                if (processType.equals(Constants.DAO_ACTION_INSERT)) {
                    getHibernateTemplate().save(entity);

                } else if (processType.equals(Constants.DAO_ACTION_DELETE)) {
                    getHibernateTemplate().delete(entity);

                } else if (processType.equals(Constants.DAO_ACTION_UPDATE)) {
                    getHibernateTemplate().update(entity);
                }

                count++;

                if (count >= Env.DEFAULT_BATCH_INSERT_FLUSH_COUNT) {
                    getHibernateTemplate().flush();
                }
            }
            */

            Session session = null;
            Transaction tx = null;

            try {
                session = primarySessionFactory.openSession();
            } catch (HibernateException e) {
                session = primarySessionFactory.openSession();
            } finally {
                if (session != null) {
                    if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
                        tx = session.beginTransaction();
                    } else {
                        tx = session.getTransaction();
                    }
                }
            }

            try {
                if (session != null && tx != null) {
                    int count = 1;
                    for (Object entity : entities) {
                        if (processType.equals(Constants.DAO_ACTION_INSERT)) {
                            session.save(entity);

                        } else if (processType.equals(Constants.DAO_ACTION_DELETE)) {
                            session.delete(entity);

                        } else if (processType.equals(Constants.DAO_ACTION_UPDATE)) {
                            session.update(entity);
                        }

                        count++;

                        if (count >= Env.DEFAULT_BATCH_INSERT_FLUSH_COUNT) {
                            session.flush();
                        }
                    }
                }

            } catch (TransactionRequiredException tre) {
                log.error(tre.toString());

            } catch (Exception e) {
                log.error(e.toString(), e);

                if (tx != null) {
                    tx.rollback();
                    session.close();
                }
                return false;

            } finally {
                if (tx != null) {
                    tx.commit();
                }
                if (session != null) {
                    session.close();
                }
            }
        }

        if (StringUtils.equals(targetDB, TARGET_ALL_DB) || StringUtils.equals(targetDB, TARGET_SECONDARY_DB)) {
            Session session = null;
            Transaction tx = null;

            try {
                session = secondSessionFactory.openSession();
            } catch (HibernateException e) {
                session = secondSessionFactory.openSession();
            } finally {
                if (session != null) {
                    if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
                        tx = session.beginTransaction();
                    } else {
                        tx = session.getTransaction();
                    }
                }
            }

            try {
                if (session != null && tx != null) {
                    int count = 1;

                    if (StringUtils.equals(targetDB, TARGET_ALL_DB)) {
                    	/*
                    	 * 如果是要同時寫入兩個DB時，則寫入Secondary DB採用copyEntities
                    	 */
                    	for (Object entity : copyEntities) {
                            if (processType.equals(Constants.DAO_ACTION_INSERT)) {
                                session.save(entity);

                            } else if (processType.equals(Constants.DAO_ACTION_DELETE)) {
                                session.delete(entity);

                            } else if (processType.equals(Constants.DAO_ACTION_UPDATE)) {
                                session.update(entity);
                            }

                            count++;

                            if (count >= Env.DEFAULT_BATCH_INSERT_FLUSH_COUNT) {
                                session.flush();
                            }
                        }
                    } else if (StringUtils.equals(targetDB, TARGET_SECONDARY_DB)) {
                    	/*
                    	 * 如果是單純只要寫入Secondary DB，則採用一開始傳入的entities即可
                    	 */
                    	for (Object entity : entities) {
                            if (processType.equals(Constants.DAO_ACTION_INSERT)) {
                                session.save(entity);

                            } else if (processType.equals(Constants.DAO_ACTION_DELETE)) {
                                session.delete(entity);

                            } else if (processType.equals(Constants.DAO_ACTION_UPDATE)) {
                                session.update(entity);
                            }

                            count++;

                            if (count >= Env.DEFAULT_BATCH_INSERT_FLUSH_COUNT) {
                                session.flush();
                            }
                        }
                    }
                }

            } catch (TransactionRequiredException tre) {
                log.error(tre.toString());

            } catch (Exception e) {
                log.error(e.toString(), e);

                if (tx != null) {
                    tx.rollback();
                    session.close();
                }
                return false;

            } finally {
                if (tx != null) {
                    tx.commit();
                }
                if (session != null) {
                    session.close();
                }
            }
        }

        return success;
    }

	@Override
    public boolean insertEntity(String targetDB, Object entity) {
        return processEntity(targetDB, entity, Constants.DAO_ACTION_INSERT);
    }

    @Override
    public boolean updateEntity(String targetDB, Object entity) {
        return processEntity(targetDB, entity, Constants.DAO_ACTION_UPDATE);
    }

	@Override
	public boolean deleteEntity(String targetDB, Object entity) {
	    return processEntity(targetDB, entity, Constants.DAO_ACTION_DELETE);
	}

	private boolean processEntity(String targetDB, Object entity, String processType) {
        boolean success = true;

        if (StringUtils.equals(targetDB, TARGET_ALL_DB) || StringUtils.equals(targetDB, TARGET_PRIMARY_DB)) {
            if (processType.equals(Constants.DAO_ACTION_INSERT)) {
                getHibernateTemplate().save(entity);

            } else if (processType.equals(Constants.DAO_ACTION_DELETE)) {
                getHibernateTemplate().delete(entity);

            } else if (processType.equals(Constants.DAO_ACTION_UPDATE)) {
                getHibernateTemplate().update(entity);
            }

        }

        if (StringUtils.equals(targetDB, TARGET_ALL_DB) || StringUtils.equals(targetDB, TARGET_SECONDARY_DB)) {
            Session session = null;
            Transaction tx = null;

            try {
                session = secondSessionFactory.getCurrentSession();
            } catch (HibernateException e) {
                session = secondSessionFactory.openSession();
            } finally {
                if (session != null) {
                    if (!session.getTransaction().isActive()) {
                        tx = session.beginTransaction();
                    } else {
                        tx = session.getTransaction();
                        tx.begin();
                    }
                }
            }

            try {
                if (session != null && tx != null) {
                    if (processType.equals(Constants.DAO_ACTION_INSERT)) {
                        session.save(entity);

                    } else if (processType.equals(Constants.DAO_ACTION_DELETE)) {
                        session.delete(entity);

                    } else if (processType.equals(Constants.DAO_ACTION_UPDATE)) {
                        session.update(entity);
                    }
                }

            } catch (Exception e) {
                log.error(e.toString(), e);

                if (tx != null) {
                    tx.rollback();
                    session.close();
                }
                return false;

            } finally {
                if (tx != null) {
                    tx.commit();
                }
                if (session != null) {
                    session.close();
                }
            }
        }
        return success;
    }

	@Override
	public boolean insertEntitiesByNativeSQL(List<String> nativeSQLs) {
		boolean success = true;

		int count = 1;
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

		for (String sql : nativeSQLs) {
			session.createNativeQuery(sql).executeUpdate();
			count++;

			if (count >= Env.DEFAULT_BATCH_INSERT_FLUSH_COUNT) {
				getHibernateTemplate().flush();
			}
		}

		return success;
	}

	@Override
	public boolean deleteEntitiesByNativeSQL(List<String> nativeSQLs) {
		boolean success = true;

		int count = 1;
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

		for (String sql : nativeSQLs) {
			session.createNativeQuery(sql).executeUpdate();
			count++;

			if (count >= Env.DEFAULT_BATCH_INSERT_FLUSH_COUNT) {
				getHibernateTemplate().flush();
			}
		}

		return success;
	}

	@Override
	@Deprecated
	public Integer loadDataInFile(String targetDB, String tableName, String filePath, String charset, String fieldsTerminatedBy,
			String linesTerminatedBy, String extraSetStr) {
	    Session session = null;
	    Transaction tx = null;
	    try {
	     // LOAD DATA LOCAL INFILE 'D:\\Net_Flow_Log\\Streams Sensor_20181220164000_20181220164011.csv' INTO TABLE cmap.net_flow_raw_data_3 CHARACTER SET big5 FIELDS TERMINATED BY ','  LINES TERMINATED BY '\r\n';
	        StringBuffer sql = new StringBuffer();
	        sql.append(" LOAD DATA LOCAL INFILE ")
	           //.append(" :filePath ")
	           .append(" '").append(filePath).append("' ");       
	           // 2020-02-20 edit by  Alvin for duplicate data updating rows
	           //.append(" REPLACE INTO TABLE :tableName ");	 
	        if ( ! tableName.contains("NET_FLOW")) {
	        	//TODO data_poller_setting增加REPLACE欄位
		        String replace =" REPLACE ";
	            sql.append(replace).append(" INTO TABLE ").append(tableName).append(" ");
	        }else {
	        	// 2020-02-20 edit by  Alvin. The format  of  Netflow insertCSV doesn't support REPLACE syntax
	        	sql.append(" INTO TABLE ").append(tableName).append(" ");
	        }
	        if (StringUtils.isNotBlank(charset)) {
	            //sql.append(" CHARACTER SET :charset ");
	            sql.append(" CHARACTER SET ").append(charset).append(" ");
	        }
	        if (StringUtils.isNotBlank(fieldsTerminatedBy)) {
	            //sql.append(" FIELDS TERMINATED BY :fieldsTerminatedBy ");
	            sql.append(" FIELDS TERMINATED BY '").append(fieldsTerminatedBy).append("' ");
	        }
	        // 2020-02-18 add by Alvin for WIFI_TRACE data_poller using ENCLOSED BY '"'
	        if ( ! tableName.contains("NET_FLOW")) {
	        	//TODO data_poller_setting增加ENCLOSED_BY欄位
	        	String enclosedBy="\"";
	            //sql.append(" ENCLOSED BY :enclosedBy ");
	            sql.append(" ENCLOSED BY '").append(enclosedBy).append("' ");
	        }
	        if (StringUtils.isNotBlank(linesTerminatedBy)) {
	            //sql.append(" LINES TERMINATED BY :linesTerminatedBy ");
	            sql.append(" LINES TERMINATED BY '").append(linesTerminatedBy).append("' ");
	        }
	        if (StringUtils.isNotBlank(extraSetStr)) {
	            sql.append(extraSetStr);
	        }

	        if (StringUtils.equals(targetDB, TARGET_PRIMARY_DB)) {
	            try {
	                session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	            } catch (HibernateException e) {
	                session = getHibernateTemplate().getSessionFactory().openSession();
	            }

	        } else if (StringUtils.equals(targetDB, TARGET_SECONDARY_DB)) {
	            try {
	                session = secondSessionFactory.getCurrentSession();
	            } catch (HibernateException e) {
	                session = secondSessionFactory.openSession();
	            }
	        }
	        logger.info("Creating native query to load csv file with SQL : "+sql.toString());
	        if (session != null) {
	            tx = session.beginTransaction();
	            Query<?> q = session.createNativeQuery(sql.toString());
	            /*
	            q.setParameter("filePath", filePath);
	            q.setParameter("tableName", tableName);
	            if (StringUtils.isNotBlank(charset)) {
	                q.setParameter("charset", charset);
	            }
	            if (StringUtils.isNotBlank(fieldsTerminatedBy)) {
	                q.setParameter("fieldsTerminatedBy", fieldsTerminatedBy);
	            }
	            if (StringUtils.isNotBlank(linesTerminatedBy)) {
	                q.setParameter("linesTerminatedBy", linesTerminatedBy);
	            }
	            if (StringUtils.isNotBlank(extraSetStr)) {
	                q.setParameter("extraSetStr", extraSetStr);
	            }
	            */

	            return q.executeUpdate();

	        } else {
	            return null;
	        }

	    } catch (Exception e) {
	        log.error(e.toString(), e);

	        if (tx != null) {
	            tx.rollback();
	            session.close();
	        }
	        return null;

	    } finally {
	        if (tx != null) {
	            tx.commit();
	        }
	        if (session != null) {
	            session.close();
	        }
	    }
	}

	@Override
	public Integer loadDataInFile(
	        String targetDB, String tableName, String filePath, String charset,
	        String fieldsTerminatedBy, String linesTerminatedBy, String enclosedBy,
	        String replaceInto, String extraSetStr) {
	    Session session = null;
	    Transaction tx = null;
	    try {
	     // LOAD DATA LOCAL INFILE 'D:\\Net_Flow_Log\\Streams Sensor_20181220164000_20181220164011.csv' INTO TABLE cmap.net_flow_raw_data_3 CHARACTER SET big5 FIELDS TERMINATED BY ','  LINES TERMINATED BY '\r\n';
	        StringBuffer sql = new StringBuffer();
	        sql.append(" LOAD DATA LOCAL INFILE ")
	           //.append(" :filePath ")
	           .append(" '").append(filePath).append("' ");       
	           // 2020-02-20 edit by  Alvin for duplicate data updating rows
	           //.append(" REPLACE INTO TABLE :tableName ");	 
	        if (replaceInto.contains("Y")) {
	            sql.append(" REPLACE ").append(" INTO TABLE ").append(tableName).append(" ");
	        }else {
	        	// 2020-02-20 edit by  Alvin. The format  of  Netflow insertCSV doesn't support REPLACE syntax
	        	sql.append(" INTO TABLE ").append(tableName).append(" ");
	        }
	        if (StringUtils.isNotBlank(charset)) {
	            //sql.append(" CHARACTER SET :charset ");
	            sql.append(" CHARACTER SET ").append(charset).append(" ");
	        }
	        if (StringUtils.isNotBlank(fieldsTerminatedBy)) {
	            //sql.append(" FIELDS TERMINATED BY :fieldsTerminatedBy ");
	            sql.append(" FIELDS TERMINATED BY '").append(fieldsTerminatedBy).append("' ");
	        }
	        // 2020-02-18 add by Alvin for WIFI_TRACE data_poller using ENCLOSED BY '"'
	        if (StringUtils.isNotBlank(enclosedBy)) {
	        	//sql.append(" ENCLOSED BY :enclosedBy ");
	            sql.append(" ENCLOSED BY '").append(enclosedBy).append("' ");
	        }
	        if (StringUtils.isNotBlank(linesTerminatedBy)) {
	            //sql.append(" LINES TERMINATED BY :linesTerminatedBy ");
	            sql.append(" LINES TERMINATED BY '").append(linesTerminatedBy).append("' ");
	        }
	        if (StringUtils.isNotBlank(extraSetStr)) {
	            sql.append(extraSetStr);
	        }

	        if (StringUtils.equals(targetDB, TARGET_PRIMARY_DB)) {
	            try {
	                session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	            } catch (HibernateException e) {
	                session = getHibernateTemplate().getSessionFactory().openSession();
	            }

	        } else if (StringUtils.equals(targetDB, TARGET_SECONDARY_DB)) {
	            try {
	                session = secondSessionFactory.getCurrentSession();
	            } catch (HibernateException e) {
	                session = secondSessionFactory.openSession();
	            }
	        }
	        logger.debug("Creating native query to load csv file with SQL : "+sql.toString());
	        if (session != null) {
	            tx = session.beginTransaction();
	            Query<?> q = session.createNativeQuery(sql.toString());
	            
	            return q.executeUpdate();

	        } else {
	            return null;
	        }

	    } catch (Exception e) {
	        log.error(e.toString(), e);

	        if (tx != null) {
	            tx.rollback();
	            session.close();
	        }
	        return null;

	    } finally {
	        if (tx != null) {
	            tx.commit();
	        }
	        if (session != null) {
	            session.close();
	        }
	    }		
	}
	@Override
	public boolean insertEntities2File(Path filePath, List<String> recordList, boolean appendFile) {
		long begin = System.currentTimeMillis();

		FileWriter fw = null;
		try {
			Path fileFolderPath = filePath.getParent();
			if (!Files.exists(fileFolderPath)) {
				Files.createDirectories(fileFolderPath);
			}

			fw = new FileWriter(filePath.toString(), appendFile);

			for (String content : recordList) {
				fw.write(content + "\r\n");
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;

		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}

			long end = System.currentTimeMillis();
			log.info("readTxt1方法,使用記憶體="+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+",使用時間毫秒="+(end-begin));

		}
		return true;
	}

    @Override
    public boolean insertEntity2Secondary(Object entity) {
        return processEntity(TARGET_SECONDARY_DB, entity, Constants.DAO_ACTION_INSERT);
    }

    @Override
    public boolean insertEntities2Secondary(List<? extends Object> entities) {
        return processEntities(TARGET_SECONDARY_DB, entities, Constants.DAO_ACTION_INSERT);
    }

    @Override
    public boolean updateEntity2Secondary(Object entity) {
        return processEntity(TARGET_SECONDARY_DB, entity, Constants.DAO_ACTION_UPDATE);
    }

    @Override
    public boolean updateEntities2Secondary(List<? extends Object> entities) {
        return processEntities(TARGET_SECONDARY_DB, entities, Constants.DAO_ACTION_UPDATE);
    }

    @Override
    public boolean deleteEntity2Secondary(Object entity) {
        return processEntity(TARGET_SECONDARY_DB, entity, Constants.DAO_ACTION_DELETE);
    }

    @Override
    public boolean deleteEntities2Secondary(List<? extends Object> entities) {
        return processEntities(TARGET_SECONDARY_DB, entities, Constants.DAO_ACTION_DELETE);
    }

    @Override
    public CommonDAOVO getTableInformation(String targetDB, String tableName) {
        CommonDAOVO retVO = new CommonDAOVO();
        Session session = null;
        Transaction tx = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(" select ")
              .append("   table_name ")
              .append("  ,table_rows ")
              .append("  ,row_format ")
              .append("  ,data_length / 1024 AS 'data(K)' ")
              .append("  ,index_length / 1024 AS 'index(K)' ")
              .append(" from INFORMATION_SCHEMA.TABLES ")
              .append(" where 1=1 ")
              .append(" and table_name = :tableName ");

            if (StringUtils.equals(targetDB, TARGET_PRIMARY_DB)) {
                try {
                    session = getHibernateTemplate().getSessionFactory().getCurrentSession();
                } catch (HibernateException e) {
                    session = getHibernateTemplate().getSessionFactory().openSession();
                }

            } else if (StringUtils.equals(targetDB, TARGET_SECONDARY_DB)) {
                try {
                    session = secondSessionFactory.getCurrentSession();
                } catch (HibernateException e) {
                    session = secondSessionFactory.openSession();
                }
            }

            if (session != null) {

                if (session.getTransaction().isActive()) {
                    tx = session.getTransaction();
                } else {
                    tx = session.beginTransaction();
                }

                Query<?> q = session.createNativeQuery(sb.toString());
                q.setParameter("tableName", tableName);

                List<Object[]> retList = (List<Object[]>)q.list();
                if (retList != null && !retList.isEmpty()) {
                    Object[] tableInfo = retList.get(0);
                    retVO.setTableInfoOfTableName(Objects.toString(tableInfo[0], "N/A"));
                    retVO.setTableInfoOfRows(Long.valueOf(Objects.toString(tableInfo[1], "0")));
                    retVO.setTableInfoOfRowFormat(Objects.toString(tableInfo[2], "N/A"));
                    retVO.setTableInfoOfDataSizeInKBytes(Objects.toString(tableInfo[3], "N/A"));
                    retVO.setTableInfoOfIndexSizeInKBytes(Objects.toString(tableInfo[4], "N/A"));
                }

            } else {
                return null;
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }
}