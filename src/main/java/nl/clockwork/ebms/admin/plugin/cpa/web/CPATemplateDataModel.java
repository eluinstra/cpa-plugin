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
package nl.clockwork.ebms.admin.plugin.cpa.web;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

import org.apache.wicket.model.LoadableDetachableModel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class CPATemplateDataModel extends LoadableDetachableModel<CPATemplate>
{
	private static final long serialVersionUID = 1L;
	CPAPluginDAO cpaPluginDAO;
	long id;

	public CPATemplateDataModel(CPAPluginDAO ebMSDAO, CPATemplate cpaTemplate)
	{
		this(ebMSDAO,cpaTemplate.getId());
	}

	@Override
	protected CPATemplate load()
	{
		return cpaPluginDAO.findCPATemplate(id);
	}

	@Override
	public int hashCode()
	{
		return new Long(id).hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return Match(obj).of(
				Case($(this),true),
				Case($(null),false),
				Case($(instanceOf(CPATemplateDataModel.class)),o -> id == o.id),
				Case($(),false));
	}
}
