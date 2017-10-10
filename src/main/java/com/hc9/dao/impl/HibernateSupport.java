package com.hc9.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.rowset.CachedRowSet;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.hc9.commons.log.LOG;
import com.hc9.commons.normal.Validate;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.model.PageModel;
import com.sun.rowset.CachedRowSetImpl;

/**
 * HibernateHelper
 * 
 * @author frank
 * 
 */
@SuppressWarnings("all")
public class HibernateSupport extends HibernateTemplate implements HibernateSupportTemplate {

    /**
     * 注入LocalSessionFactoryBean
     */
    @Resource
    private LocalSessionFactoryBean bean;

    /**
     * 配置信息
     */
    private Configuration configuration;

    /**
     * sessionFactory
     */
    private SessionFactory sessionFactory;

    /**
     * 批量保存
     * 
     * @param collection
     *            集合
     * @param clearTimePer
     *            清空批次
     * 
     * @return 受影响行数
     */
    public int saveAll(Collection<?> collection, int clearTimePer) {

        int index = 0;

        Session session = getSession();

        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {

            session.save(iterator.next());

            index++;

            if (index % clearTimePer == 0) {
                session.flush();
                session.clear();
            }
        }

        return index;

    }
    /**
     * 映射查询集合
     * 
     * @param name
     *            名称
     * @param objs
     *            参数
     * @return 集合
     */
    public List mappingQuery(String name, Object... objs) {

        return fillQuery(getSession().getNamedQuery(name), objs).list();

    }
    
    /**
     * 映射修改
     * 
     * @param name
     *            名称
     * @param objs
     *            参数
     * @return 集合
     */
    public int mappingUpdate(String name, Object... objs) {

        return fillQuery(getSession().getNamedQuery(name), objs).executeUpdate();

    }

    /**
     * 映射查询对象
     * 
     * @param name
     *            名称
     * @param objs
     *            参数
     * @return 对象
     */
    public Object mappingQueryObject(String name, Object... objs) {

        return fillQuery(getSession().getNamedQuery(name), objs).uniqueResult();

    }

    /**
     * 映射查询行数
     * 
     * @param name
     *            名称
     * @param objs
     *            参数
     * @return 对象
     */
    public int mappingQueryCount(String name, Object... objs) {

        return ((Long) fillQuery(getSession().getNamedQuery(name), objs)
                .uniqueResult()).intValue();

    }

    @Override
    public List<?> query(String hql, boolean cache, Object... params) {
        return fillQuery(getSession().createQuery(hql), params).setCacheable(
                cache).list();
    }

    
    @Override
    public List<?> findCache(String hql, Object... params) {
        return query(hql, true, params);
    }

    @Override
    public void delete(Serializable id, Class<?> clazz) {
        delete(get(clazz, id));
    }
    
    /**
     * 删除选中
     * @param clazz 实体类
     * @param ids   主键s
     * @return      受影响行数
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public int deleteSelected(Class<?> clazz,Class<?> type,Serializable... ids) throws InstantiationException, IllegalAccessException {
        
        Serializable[] params = null;
        
        String id = configuration.getClassMapping(clazz.getName()).getTable().getPrimaryKey().getColumn(0).getName();
        
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(clazz.getName()).append(" WHERE ").append(id).append(" IN(:ids)");
        
        if(!ids[0].getClass().equals(type.getClass())){
            
             if(type.equals(Long.class)){
                
                params = new Long[ids.length];
                for(int i=0;i<ids.length;i++){
                    params[i] = Long.parseLong(ids[i].toString());
                }
                
            }else if(type.equals(Double.class)){
                
                params = new Double[ids.length];
                for(int i=0;i<ids.length;i++){
                    params[i] = Double.parseDouble(ids[i].toString());
                }
                
            }else if(type.equals(Integer.class)){
                
                params = new Integer[ids.length];
                for(int i=0;i<ids.length;i++){
                    params[i] = Integer.parseInt(ids[i].toString());
                }
                
            }
            
        }else{
            
            params = ids;
            
        }
        
        
        
        return fillQueryByName(getSession().createQuery(sb.toString()),"ids",params).executeUpdate();
        
    }
    
    /**
     * 删除选中
     * @param entitys   实体对象s
     * @return          受影响行数
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws InstantiationException
     */
    public int deleteSelected(Serializable... entitys) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException {
        
        
        Field field = entitys[0].getClass().getDeclaredField(configuration.getClassMapping(entitys[0].getClass().getName())
                .getTable().getPrimaryKey().getColumn(0).getName());
        field.setAccessible(true);
        
        Serializable[] ids = new Serializable[entitys.length];
        
        for(int i=0;i<entitys.length;i++){
            ids[i] = (Serializable)field.get(entitys[i]);
        }
        
        return deleteSelected(entitys[0].getClass(), ids);
        
    }
    
    @Override
    public int deleteSelected(Collection<?> collection) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {

        Iterator<?> iterator = collection.iterator();
        Object t = iterator.next();

        Field field = t.getClass().getDeclaredField(configuration.getClassMapping(t.getClass().getName())
                .getTable().getPrimaryKey().getColumn(0).getName());
        field.setAccessible(true);
        
        Serializable[] ids = new Serializable[collection.size()];
        int i=0;
        for (; iterator.hasNext();) {
            ids[i] = (Serializable)field.get(t);
            
            t = iterator.next();
            i++;
        }


        return deleteSelected(t.getClass(), ids);
    }

    @Override
    public Object findObject(String hql, Object... params) {
        List<Object> list = find(hql, params);
        if (list != null && list.size() == 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public <T> T findObjectBySql(String sql, Class<T> clazz, Object... params) {
        return (T) ((SQLQuery) fillQuery(getSession().createSQLQuery(sql),
                params)).addEntity(clazz).uniqueResult();
    }

    @Override
    public Object findObjectBySql(String sql, Object... params) {
        return fillQuery(getSession().createSQLQuery(sql), params)
                .uniqueResult();
    }

    @Override
    public <T> List<T> findBySql(String sql, Class<T> clazz, Object... params) {
        return ((SQLQuery) fillQuery(getSession().createSQLQuery(sql), params))
                .addEntity(clazz).list();
    }
    

    @Override
    public List findBySql(String sql, Object... params) {
        return fillQuery(getSession().createSQLQuery(sql), params).list();
    }

    @Override
    public int executeHql(String hql, Object... params) {
        return fillQuery(getSession().createSQLQuery(hql), params)
                .executeUpdate();
    }

    @Override
    public int executeSql(String sql, Object... params) {
        return fillQuery(getSession().createSQLQuery(sql), params)
                .executeUpdate();
    }

    /**
     * 分页查询
     * 
     * @param hqlCount
     *            总条数hql
     * @param hqlInfo
     *            查询hql
     * @param page
     *            分页实体对象
     * @param cache
     *            是否缓存
     * @param params
     *            参数
     * @return 实体对象集合
     */
    public List pageListByHql(org.pomo.commons.model.page.PageModel page,
            String hqlCount, String hqlInfo, boolean cache, Object... params) {

        Session session = getSession();

        page.setDataCount(queryNumberHql(hqlCount, cache, params).intValue());

        if (page.getDataCount() == 0){
            return null;
        }
            

        return fillQuery(session.createQuery(hqlInfo), params)
                .setMaxResults(page.getPagePerSize())
                .setFirstResult((page.getPageNo() - 1) * page.getPagePerSize())
                .setCacheable(cache).list();

    }

    /**
     * 分页查询
     * 
     * @param hql
     *            查询hql
     * @param page
     *            分页实体对象
     * @param cache
     *            是否缓存
     * @param params
     *            参数
     * @return 实体对象集合
     */
    public List pageListByHql(org.pomo.commons.model.page.PageModel page,
            String hql, boolean cache, Object... params) {

        return pageListByHql(page, getCount(hql), hql, cache, params);

    }

    /**
     * 分页查询
     * 
     * @param sqlCount
     *            总条数sql
     * @param sqlInfo
     *            查询sql
     * @param page
     *            分页实体对象
     * @param clazz
     *            实体类(可以为null)
     * @param params
     *            参数
     * @return 实体对象集合(若clazz为null,则为数组集合)
     */
    public List pageListBySql(org.pomo.commons.model.page.PageModel page,
            String sqlCount, String sqlInfo, Class<?> clazz, Object... params) {

        Session session = getSession();

        page.setDataCount(queryNumberSql(sqlCount, params).intValue());

        if (page.getDataCount() == 0){
            return null;
        }
        SQLQuery sqlQuery = session.createSQLQuery(sqlInfo);
        if (clazz != null){
            sqlQuery.addEntity(clazz);
        }
        return fillQuery(sqlQuery, params).setMaxResults(page.getPagePerSize())
                .setFirstResult((page.getPageNo() - 1) * page.getPagePerSize())
                .list();
    }

    /**
     * 分页查询
     * 
     * @param sql
     *            查询sql
     * @param page
     *            分页实体对象
     * @param clazz
     *            实体类(可以为null)
     * @param params
     *            参数
     * @return 实体对象集合(若clazz为null,则为数组集合)
     */
    public List pageListBySql(org.pomo.commons.model.page.PageModel page,
            String sql, Class<?> clazz, Object... params) {

        return pageListBySql(page, getCount(sql), sql, clazz, params);

    }

    @Override
    public List<?> pageListByHql(PageModel page, String hqlCount,
            String hqlInfo, boolean cache, Object... params) {

        Session session = getSession();

        page.setTotalCount(queryNumberHql(hqlCount, cache, params).intValue());

        if (page.getTotalCount() == 0) {
            return null;
        }
        return fillQuery(session.createQuery(hqlInfo), params)
                .setMaxResults(page.getNumPerPage())
                .setFirstResult((page.getPageNum() - 1) * page.getNumPerPage())
                .setCacheable(cache).list();

    }

    @Override
    public List<?> pageListByHql(PageModel page, String hql, boolean cache,
            Object... params) {

    	String getCountSql = getCount(hql);
        return pageListByHql(page, getCountSql, hql, cache, params);

    }

    @Override
    public List<?> pageListBySql(PageModel page, String sqlCount,
            String sqlInfo, Class<?> clazz, Object... params) {

        Session session = getSession();

        page.setTotalCount(queryNumberSql(sqlCount, params).intValue());

        if (page.getTotalCount() == 0) {
            return null;
        }
        SQLQuery sqlQuery = session.createSQLQuery(sqlInfo);
        if (clazz != null) {
            sqlQuery.addEntity(clazz);
        }
        return fillQuery(sqlQuery, params).setMaxResults(page.getNumPerPage())
                .setFirstResult((page.getPageNum() - 1) * page.getNumPerPage())
                .list();
    }

    @Override
    public List<?> pageListBySql(PageModel page, String sql, Class<?> clazz,
            Object... params) {

        return pageListBySql(page, getCount(sql), sql, clazz, params);

    }

    @Override
    public void clearFirstCache(Object entity) {
        if (entity == null) {
            getSession().clear();
        } else {
            getSession().evict(entity);
        }
    }

    @Override
    public void clearSecondCache(String hql) {
        if (hql == null) {
            getSessionFactory().evictQueries();
        } else {
            getSessionFactory().evictQueries(hql);
        }
    }

    @Override
    public ResultSet callProcedureResultSet(String procedureName,
            Object... params) {

        CallableStatement cs = null;
        ResultSet rs = null;

        try {

            cs = getConnection().prepareCall(
                    bornExecuteProcedureStr(procedureName, params));
            rs = installParams(cs, params).executeQuery();

        } catch (SQLException e) {
            LOG.error("执行存储过程发生错误！", e);
        }

        return rs;
    }

    @Override
    public CachedRowSet callProcedureCachedRowSet(String procedureName,
            Object... params) {

        CachedRowSet rowSet = null;

        ResultSet rs = callProcedureResultSet(procedureName, params);

        try {

            rowSet = new CachedRowSetImpl();
            rowSet.populate(rs);

        } catch (SQLException e) {
            LOG.error("转换为断开式结果集发生错误！", e);
        } finally {

            closeResultSetAndStatement(rs);

        }

        return rowSet;
    }

    @Override
    public Object callProcedureOutput(String procedureName, Object... params) {

        CallableStatement cs = null;
        try {

            cs = getConnection().prepareCall(
                    bornExecuteProcedureStr(procedureName, params));
            cs.registerOutParameter(params.length,
                    Integer.parseInt(params[params.length - 1].toString()));
            Object[] realParams = new Object[params.length - 1];
            for (int i = 0; i < realParams.length; i++) {
                realParams[i] = params[i];
            }
            installParams(cs, realParams).execute();
            return cs.getObject(params.length);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean callProcedureVoid(String procedureName, Object... params) {
        try {
            return !installParams(getConnection().prepareCall(bornExecuteProcedureStr(procedureName, params)),
                    params).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void closeResultSetAndStatement(ResultSet rs) {
        try {
            rs.close();
            rs.getStatement().close();
        } catch (SQLException e) {
            LOG.error("关闭 ResultSet 或 Statement 失败！", e);
        }
    }

    /**
     * 填充参数
     * 
     * @param cs
     *            CallableStatement
     * @param params
     *            参数
     * @return CallableStatement
     * @throws SQLException
     *             SQL异常
     */
    private CallableStatement installParams(CallableStatement cs,
            Object[] params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if (null == params[i]) {
                    cs.setString(i + 1, null);
                } else {
                    cs.setString(i + 1, params[i].toString());
                }
            }
        }
        return cs;
    }

    /**
     * 产生一个存储过程命令
     * 
     * @param procedureName
     *            命令名称
     * @param params
     *            参数
     * @return 命令
     */
    private String bornExecuteProcedureStr(String procedureName, Object[] params) {

        StringBuilder procedureStr = new StringBuilder("{CALL ")
                .append(procedureName);
        if (params != null && params.length > 0) {
            procedureStr.append("(?");
            for (int i = 1; i < params.length; i++) {
                procedureStr.append(",?");
            }
            procedureStr.append(")");
        }
        procedureStr.append("}");

        return procedureStr.toString();
    }

    @Override
    public Query fillQuery(Query query, Object... params) {

        if (!Validate.emptyArrayValidate(params)) {
            return query;
        }

        boolean isPosition = true;

        for (Object obj : params) {

            if (obj instanceof Object[] || obj instanceof Collection) {

                isPosition = false;
                break;

            }

        }

        if (isPosition) {

            return fillQueryByPosition(query, params);

        } else {

            return fillQueryByName(query, params);

        }

    }

    /**
     * 根据下标填充query
     * @param query query
     * @param params    参数
     * @return  query
     */
    public Query fillQueryByPosition(Query query, Object... params) {

        for (int i = 0; i < params.length; i++) {

            if (null != params[i]) {
                query.setString(i, params[i].toString());

            } else {
                query.setString(i, null);
            }
        }

        return query;

    }

    /**
     * 根据名称填充query
     * @param query query
     * @param params    参数
     * @return  query
     */
    public Query fillQueryByName(Query query, Object... params) {

        for (int i = 1; i < params.length; i+=2) {
            if (null != params[i]) {

                if (params[i] instanceof Object[]) {

                    query.setParameterList((String) params[i - 1],
                            (Object[]) params[i]);

                } else if (params[i] instanceof Collection) {

                    query.setParameterList((String) params[i - 1],
                            (Collection) params[i]);

                } else {

                    query.setParameter((String) params[i - 1], params[i]);

                }

            } else {

                query.setParameter((String) params[i - 1], null);

            }
        }

        return query;
    }

    /**
     * 设置参数
     * 
     * @param preparedStatement
     *            preparedStatement
     * @param params
     *            参数
     * @return preparedStatement
     * @throws SQLException
     *             SQL异常
     */
    public PreparedStatement fillPreparedStatement(
            PreparedStatement preparedStatement, Object... params)
            throws SQLException {

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if (null != params[i]) {
                    preparedStatement.setString(i + 1, params[i].toString());
                }
            }
        }

        return preparedStatement;
    }

    private Double queryNumber(List list){
        String str = null;
        if(list!=null && list.size()==1 && list.get(0)!=null && Validate.emptyStringValidate((str=list.get(0).toString()))){
            return Double.parseDouble(str);
        }
        return 0d;
    }
    
    @Override
    public Double queryNumberHql(String hql, boolean cache, Object... params) {
        return queryNumber(query(hql, cache, params));
    }

    @Override
    public Double queryNumberSql(String sql, Object... params) {
        return queryNumber(findBySql(sql, params));
    }

    /**
     * 得到查询行数语句
     * 
     * @param sql
     *            原语句
     * @return 语句
     */
    private String getCount(String sql) {
        return "SELECT COUNT(*) "
                + sql.substring(sql.toUpperCase().indexOf("FROM"));
    }

    @Override
    public Session getSession() {
        return super.getSession();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 得到数据库连接
     * 
     * @return 数据库连接
     */
    @SuppressWarnings("deprecation")
    public Connection getConnection() {
        return getSession().connection();
    }

    /**
     * 初始化
     * 
     * @param configuration
     *            配置信息
     */
    public void init(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        this.configuration = bean.getConfiguration();
    }


    
}
