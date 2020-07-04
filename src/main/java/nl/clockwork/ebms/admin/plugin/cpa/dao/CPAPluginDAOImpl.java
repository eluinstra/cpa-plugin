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

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPAElement;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.admin.plugin.cpa.querydsl.model.QCpaElement;
import nl.clockwork.ebms.admin.plugin.cpa.querydsl.model.QCpaTemplate;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
public class CPAPluginDAOImpl implements CPAPluginDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QCpaTemplate table = QCpaTemplate.cpaTemplate;
	ConstructorExpression<CPATemplate> cpaTemplateProjection = Projections.constructor(CPATemplate.class,table.id,table.name,table.content);
	QCpaElement cpaElementTable = QCpaElement.cpaElement;
	ConstructorExpression<CPAElement> cpaElementProjection = Projections.constructor(CPAElement.class,cpaElementTable.id,cpaElementTable.name,cpaElementTable.xpathQuery);

	@Override
	public CPATemplate findCPATemplate(long id)
	{
		return queryFactory.select(cpaTemplateProjection)
				.from(table)
				.where(table.id.eq(id))
				.fetchOne();
	}

	@Override
	public long countCPATemplates()
	{
		return queryFactory.select(table.id.count())
				.from(table)
				.fetchOne();
	}
	
	@Override
	public List<CPATemplate> selectCPATemplates()
	{
		return queryFactory.select(cpaTemplateProjection)
				.from(table)
				.orderBy(table.name.asc())
				.fetch();
	}

	@Override
	public List<CPATemplate> selectCPATemplates(long first, long count)
	{
		return queryFactory.select(cpaTemplateProjection)
				.from(table)
				.orderBy(table.name.asc())
				.limit(count)
				.offset(first)
				.fetch();
	}

	@Override
	public long insertCPATemplate(String name, String cpa)
	{
		return queryFactory.insert(table)
				.set(table.name,name)
				.set(table.content,cpa)
				.execute();
	}
	
	@Override
	public long deleteCPATemplate(long id)
	{
		return queryFactory.delete(table)
				.where(table.id.eq(id))
				.execute();
	}

	@Override
	public List<CPAElement> selectCPAElements(long cpaTemplateId)
	{
		return queryFactory.select(cpaElementProjection)
				.from(cpaElementTable)
				.where(cpaElementTable.cpaTemplateId.eq(cpaTemplateId))
				.orderBy(cpaElementTable.orderNr.asc())
				.fetch();
	}
}