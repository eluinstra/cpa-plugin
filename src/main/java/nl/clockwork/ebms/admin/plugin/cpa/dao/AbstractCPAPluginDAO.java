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

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPAElement;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.dao.DAOException;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
public abstract class AbstractCPAPluginDAO implements CPAPluginDAO
{
	RowMapper<CPATemplate> cpaTemplateRowMapper = (RowMapper<CPATemplate>)(rs,rowNum) -> new CPATemplate(rs.getLong("id"),rs.getString("name"),rs.getString("content"));
	
	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	JdbcTemplate jdbcTemplate;

	@Override
	public CPATemplate findCPATemplate(long id)
	{
		try
		{
			return jdbcTemplate.queryForObject(
				"select * from cpa_template" +
				" where id = ?",
				cpaTemplateRowMapper,
				id
			);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	@Override
	public int countCPATemplates()
	{
		return jdbcTemplate.queryForObject("select count(id) from cpa_template",Integer.class);
	}
	
	@Override
	public List<CPATemplate> selectCPATemplates()
	{
		return jdbcTemplate.query(
			"select * from cpa_template" +
			" order by name",
			cpaTemplateRowMapper
		);
	}

	public abstract String selectCPATemplatesQuery(long first, long count);
	
	@Override
	public List<CPATemplate> selectCPATemplates(long first, long count)
	{
		return jdbcTemplate.query(
			selectCPATemplatesQuery(first,count),
			cpaTemplateRowMapper
		);
	}

	@Override
	public void insertCPATemplate(String name, String cpa) throws DAOException
	{
		jdbcTemplate.update
		(
			"insert into cpa_template (" +
				"name," +
				"content" +
			") values (?,?)",
			name,
			cpa
		);
	}
	
	@Override
	public int deleteCPATemplate(long id)
	{
		return jdbcTemplate.update
		(
			"delete from cpa_template" +
			" where id = ?",
			id
		);
	}

	@Override
	public List<CPAElement> selectCPAElements(long cpaTemplateId)
	{
		return jdbcTemplate.query(
			"select id, name, xpath_query" +
			" from cpa_element" +
			" where cpa_template_id = ?" +
			" order by order_nr asc",
			(RowMapper<CPAElement>)(rs,rowNum) ->
			{
				return new CPAElement(rs.getLong("id"),rs.getString("name"),rs.getString("xpath_query"));
			}
		);
	}

}