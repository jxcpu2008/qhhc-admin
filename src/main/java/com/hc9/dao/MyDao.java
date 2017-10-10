package com.hc9.dao;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MyDao extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public void executeSql(final String sql, final Object... params) {
		getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// TODO Auto-generated method stub
				Query query = session.createQuery(sql);
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
				
				return query.executeUpdate();
			}
			
		});
	}
}
