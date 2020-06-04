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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageClassLink;
import nl.clockwork.ebms.admin.web.WicketApplication;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewCPATemplatesPage extends BasePage
{
	private class CPATemplateDataView extends DataView<CPATemplate>
	{
		private static final long serialVersionUID = 1L;

		protected CPATemplateDataView(String id, IDataProvider<CPATemplate> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		@Override
		public long getItemsPerPage()
		{
			return maxItemsPerPage;
		}

		@Override
		protected void populateItem(final Item<CPATemplate> item)
		{
			val o = item.getModelObject();
			item.add(createViewLink(o));
			item.add(new DownloadCPATemplateLink("downloadCPATemplate",o));
			item.add(createDeleteButton("delete"));
			item.add(AttributeModifier.replace("class",OddOrEvenIndexStringModel.of(item.getIndex())));
		}

		private Link<Void> createViewLink(final CPATemplate cpaTemplate)
		{
			val result = Link.<Void>builder()
					.id("view")
					.onClick(() -> setResponsePage(new ViewCPATemplatePage(cpaTemplate,ViewCPATemplatesPage.this)))
					.build();
			result.add(new Label("name",cpaTemplate.getName()));
			return result;
		}

		private Button createDeleteButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val cpaTemplate = (CPATemplate)getParent().getDefaultModelObject();
					cpaPluginDAO.deleteCPATemplate(cpaTemplate.getId());
					setResponsePage(new ViewCPATemplatesPage());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.delete"),onSubmit);
			result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
			return result;
		}

	}
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaPluginDAO")
	CPAPluginDAO cpaPluginDAO;
	@NonNull
	final Integer maxItemsPerPage;

	public ViewCPATemplatesPage()
	{
		this.maxItemsPerPage = WicketApplication.get().getMaxItemsPerPage();
		add(new BootstrapFeedbackPanel("feedback"));
		add(new ViewCPATemplatesForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("viewCPATemplates",this);
	}

	public class ViewCPATemplatesForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;

		public ViewCPATemplatesForm(String id)
		{
			super(id);
			val container = new WebMarkupContainer("container");
			add(container);
			container.add(new CPATemplateDataView("cpaTemplates",CPATemplateDataProvider.of(cpaPluginDAO)));
			add(new PageClassLink("new",RegisterCPATemplatePage.class));
		}
	}
}
