package nl.clockwork.ebms.admin.plugin.cpa.dao;

import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.plugin.cpa.querydsl.StringType;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DAOConfig
{
	@Autowired
	DataSource dataSource;
	@Autowired
	SQLTemplates sqlTemplates;

	@Bean
	public CPAPluginDAO cpaPluginDAO()
	{
		return new CPAPluginDAOImpl(queryFactory());
	}

	private SQLQueryFactory queryFactory()
	{
		val provider = new SpringConnectionProvider(dataSource);
		return new SQLQueryFactory(querydslConfiguration(),provider);
	}

	private com.querydsl.sql.Configuration querydslConfiguration()
	{
		val result = new com.querydsl.sql.Configuration(sqlTemplates);
		result.setExceptionTranslator(new SpringExceptionTranslator());
		result.register("cpa_template","content",new StringType(Types.CLOB));
		return result;
	}
}
