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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;

import lombok.val;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.Link;

public class ViewCPATemplatePage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public ViewCPATemplatePage(CPATemplate cpaTemplate, final ViewCPATemplatesPage responsePage)
	{
		add(new Label("name",cpaTemplate.getName()));
		val cpaTemplate_ = new TextArea<String>("cpaTemplate",PropertyModel.of(cpaTemplate,"content"));
		cpaTemplate_.setEnabled(false);
		add(cpaTemplate_);
		add(Link.<Void>builder()
				.id("back")
				.onClick(() -> setResponsePage(responsePage))
				.build());
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("viewCPATemplate",this);
	}

}
