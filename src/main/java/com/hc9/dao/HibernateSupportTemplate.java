package com.hc9.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateOperations;

import com.hc9.model.PageModel;

/**
 * Hibernate通用dao
 * 
 * @author Frank
 * 
 */
public interface HibernateSupportTemplate extends HibernateOperations {

    /**
     * 得到当前session
     * 
     * @return  session
     */
    Session getSession();

    /**
     * 填充查询参数
     * 
     * @param query     Query
     * @param params    参数
     * @return          Query
     */
    Query fillQuery(Query query, Object... params);

    /**
     * 批量添加
     * 
     * @param collection
     *            待添加对象集合
     * @param clearTimePer
     *            清空缓存批次
     * @return 受影响行数
     */
    int saveAll(Collection<?> collection, int clearTimePer);

    /**
     * 根据hql语句查询
     * 
     * @param hql   语句
     * @param cache
     *            是否缓存结果
     * @param params    参数
     * @return          集合
     */
    List<?> query(String hql, boolean cache, Object... params);

    /**
     * 根据hql语句查询，并缓存查询数据
     * 
     * @param hql       语句
     * @param params    参数
     * @return          集合
     */
    List<?> findCache(String hql, Object... params);

    /**
     * 根据id和实体类删除数据
     * 
     * @param id
     *            主键
     * @param clazz
     *            实体类
     */
    void delete(Serializable id, Class<?> clazz);

    /**
     * 删除勾选
     * 
     * @param collection
     *            需要被删除的实体类集合
     * @return 受影响行数
     * @throws Exception
     *             可能有外键关联导致的删除失败
     */
    int deleteSelected(Collection<?> collection) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException ;

    /**
     * 通过hql查找一个对象
     * 
     * @param hql
     *            hql语句
     * @param params
     *            参数
     * @return 注意：当查询不到，或查询结果不为唯一时返回null
     */
    Object findObject(String hql, Object... params);

    /**
     * 
     * @param sql       语句
     * @param <T>       实体类
     * @param clazz     类
     * @param params    参数
     * @return          对象
     */
    <T> T findObjectBySql(String sql, Class<T> clazz, Object... params);

    /**
     * 
     * @param sql       语句
     * @param params    参数
     * @return          对象
     */
    Object findObjectBySql(String sql, Object... params);

    /**
     * 
     * @param sql       语句
     * @param clazz     类
     * @param <T>       实体类
     * @param params    参数
     * @return          集合
     */
    <T> List<T> findBySql(String sql, Class<T> clazz, Object... params);

    /**
     * 
     * @param sql       语句
     * @param params    参数
     * @return          集合
     */
    List<?> findBySql(String sql, Object... params);

    /**
     * 
     * @param hql       语句
     * @param params    参数
     * @return          集合
     */
    int executeHql(String hql, Object... params);

    /**
     * 
     * @param sql       语句
     * @param params    参数
     * @return          受影响行数
     */
    int executeSql(String sql, Object... params);

    /**
     * 调用存储过程[无显示输出参数](需要手动关闭结果集和CallableStatement对象,
     * 可调用closeResultSetAndStatement关闭)
     * 
     * @param procedureName
     *            存储过程名称
     * @param params
     *            需要传入的参数
     * @return 结果集
     */
    ResultSet callProcedureResultSet(String procedureName, Object... params);

    /**
     * 调用存储过程[无显示输出参数](需要手动关闭,推荐使用)
     * 
     * @param procedureName
     *            存储过程名称
     * @param params
     *            需要传入的参数
     * @return 离线结果集
     */
    CachedRowSet callProcedureCachedRowSet(String procedureName,
            Object... params);

    /**
     * 调用存储过程[有显示输出参数]
     * 
     * @param procedureName
     *            存储过程名称
     * @param params
     *            需要传入的参数
     * @return 输出参数
     */
    Object callProcedureOutput(String procedureName, Object... params);

    /**
     * 调用存储过程[注意:若修改的数据启用了缓存，必须手动清除缓存]
     * 
     * @param procedureName
     *            存储过程名称
     * @param params
     *            需要传入的参数
     * @return 是否执行成功
     */
    boolean callProcedureVoid(String procedureName, Object... params);

    /**
     * 关闭ResultSet及Statement
     * 
     * @param rs
     *            结果集
     */
    void closeResultSetAndStatement(ResultSet rs);

    /**
     * 分页查询
     * @param page      分页实体对象
     * @param hqlCount  查询总条数
     * @param hqlInfo   查询hql
     * @param cache     是否缓存
     * @param params    参数
     * @return          实体对象集合
     */
    List<?> pageListByHql(PageModel page, String hqlCount, String hqlInfo,
            boolean cache, Object... params);

    /**
     * 分页查询
     * @param page      分页实体对象
     * @param hql       查询hql
     * @param cache     是否缓存
     * @param params    参数
     * @return          实体对象集合
     */
    List<?> pageListByHql(PageModel page, String hql, boolean cache,
            Object... params);

    /**
     * 分页查询
     * @param page      分页实体对象
     * @param sqlCount  查询总条数
     * @param sqlInfo   查询sql
     * @param clazz     对象类
     * @param params    参数
     * @return          实体对象集合
     */
    List<?> pageListBySql(PageModel page, String sqlCount, String sqlInfo,
            Class<?> clazz, Object... params);
    
    


    /**
     * 分页查询
     * @param page      分页实体对象
     * @param sql       查询sql
     * @param clazz     实体类(可以为null)
     * @param params    参数
     * @return          实体对象集合
     */
    List<?> pageListBySql(PageModel page, String sql, Class<?> clazz,
            Object... params);


    /**
     * 查询总条数
     * @param cache 是否缓存
     * @param hql
     *            hql语句
     * @param params
     *            参数
     * @return     查询条数
     */
    Double queryNumberHql(String hql, boolean cache, Object... params);

    /**
     * 查询总条数
     * 
     * @param sql
     *            sql语句
     * @param params
     *            参数
     * @return  查询条数
     */
    Double queryNumberSql(String sql, Object... params);

    /**
     * 清除Hibernate一级缓存，当传入对象为null时，清空所有一级缓存
     * 
     * @param entity    实体对象
     */
    void clearFirstCache(Object entity);

    /**
     * 清除Hibernate二级缓存，当传入hql为null时，清空所有hql查询语句的缓存
     * 
     * @param hql   缓存语句
     */
    void clearSecondCache(String hql);
}
