/**
 * Copyright 2016 Ordina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
