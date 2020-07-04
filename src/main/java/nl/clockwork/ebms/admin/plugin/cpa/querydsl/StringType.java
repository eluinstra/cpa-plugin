/**
 * Copyright 2011 Clockwork
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
package nl.clockwork.ebms.admin.plugin.cpa.querydsl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.querydsl.sql.types.AbstractType;

public class StringType extends AbstractType<String>
{
	public StringType()
	{
		this(Types.BLOB);
	}
	public StringType(int type)
	{
		super(type);
	}

	@Override
	public Class<String> getReturnedClass()
	{
		return String.class;
	}

	@Override
	public String getValue(ResultSet rs, int startIndex) throws SQLException
	{
		return rs.getString(startIndex);
	}

	@Override
	public void setValue(PreparedStatement st, int startIndex, String value) throws SQLException
	{
		st.setString(startIndex,value);
	}
}
