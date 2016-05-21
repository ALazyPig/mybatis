package com.zhaojun.mybatis.page;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

//Ҫ���ص�Ŀ��������StatementHandler��ע�⣺typeֻ�����óɽӿ����ͣ������صķ���������Ϊprepare,����ΪConnection���͵ķ�����
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PaginationInterceptor implements Interceptor {

	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static String DEFAULT_PAGE_SQL_ID = ".*Page$"; // ��Ҫ���ص�ID(����ƥ��)
	/*Object intercept(Invocation invocation)��ʵ�������߼��ĵط����ڲ�Ҫͨ��invocation.proceed()��ʽ���ƽ�������ǰ����
	Ҳ���ǵ�����һ������������Ŀ�귽����*/
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		
		/*MetaObject��Mybatis�ṩ��һ���Ĺ����࣬ͨ������װһ���������Ի�ȡ�����øö����ԭ�����ɷ��ʵ����ԣ�������Щ˽�����ԣ����и�������Ҫ���������õ���

		MetaObject forObject(Object object,ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory)

		Object getValue(String name)

		void setValue(String name, Object value)

		����1���ڰ�װ���� ; ����2���ڻ�ȡ���Ե�ֵ��֧��OGNL�ķ�����; ����3�����������Ե�ֵ��֧��OGNL�ķ�����.*/
		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,DEFAULT_OBJECT_WRAPPER_FACTORY);
		
		RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
		
		// ������������(����Ŀ������ܱ�������������أ��Ӷ��γɶ�δ���ͨ�����������ѭ�����Է������ԭʼ�ĵ�Ŀ����)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
        }
        // �������һ����������Ŀ����
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
        }
        
        // property��mybatis settings�ļ�������
        Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
        // ����pageSqlId
        String pageSqlId = configuration.getVariables().getProperty("pageSqlId");
        if (null == pageSqlId || "".equals(pageSqlId)) {
            pageSqlId = DEFAULT_PAGE_SQL_ID;
        }

        MappedStatement mappedStatement = (MappedStatement)
                metaStatementHandler.getValue("delegate.mappedStatement");
        // ֻ��д��Ҫ��ҳ��sql��䡣ͨ��MappedStatement��IDƥ�䣬Ĭ����д��Page��β��MappedStatement��sql
        if (mappedStatement.getId().matches(pageSqlId)) {
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            Object parameterObject = boundSql.getParameterObject();
            if (parameterObject == null) {
                throw new NullPointerException("parameterObject is null!");
            } else {
                String sql = boundSql.getSql();
                // ��дsql
                String pageSql = sql + " LIMIT " + rowBounds.getOffset() + "," + rowBounds.getLimit();
                metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
                // ���������ҳ�󣬾Ͳ���Ҫmybatis���ڴ��ҳ�ˣ����������������������
                metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
                metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            }
        }
        // ��ִ��Ȩ������һ��������
        return invocation.proceed();
    }
	
	
	/*Object plugin(Object target) �����õ�ǰ������������ɶ�Ŀ��target�Ĵ���ʵ����ͨ��Plugin.wrap(target,this) ����ɵģ�
	 * ��Ŀ��target��������this�����˰�װ������*/
	@Override
	public Object plugin(Object target) {
		 // ��Ŀ������StatementHandler����ʱ���Ű�װĿ���࣬����ֱ�ӷ���Ŀ�걾��,����Ŀ�걻����Ĵ���
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
	}

	/* setProperties(Properties properties)�������ö���Ĳ�����������������������Properties�ڵ��
	 	ע������������ָ�����ط�����ǩ��  [type,method,args] ���������ֶ�������ַ����������أ�����������ǰ���ھ��ϡ�*/
	@Override
	public void setProperties(Properties properties) {
		
	}

}
