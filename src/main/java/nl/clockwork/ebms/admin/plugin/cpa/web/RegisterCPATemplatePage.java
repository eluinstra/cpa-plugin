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

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.service.cpa.CPAService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterCPATemplatePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="cpaPluginDAO")
	CPAPluginDAO cpaPluginDAO;

	public RegisterCPATemplatePage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new RegisterCPATemplateForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("registerCPATemplate",this);
	}

	public class RegisterCPATemplateForm extends Form<RegisterCPATemplateFormData>
	{
		private static final long serialVersionUID = 1L;

		public RegisterCPATemplateForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new RegisterCPATemplateFormData()));
			setMultiPart(true);
			add(new TextField<String>("name").setLabel(new ResourceModel("lbl.name")));
			add(new BootstrapFormComponentFeedbackBorder("cpaFeedback",createCPAFileField("cpaFile")));
			add(createValidateButton("validate"));
			add(createUploadButton("upload"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),RegisterCPATemplatePage.class));
		}

		private FileUploadField createCPAFileField(String id)
		{
			val result = new FileUploadField(id);
			result.setLabel(new ResourceModel("lbl.cpa"));
			result.setRequired(true);
			return result;
		}

		private Button createValidateButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val files = RegisterCPATemplateForm.this.getModelObject().cpaFile;
					if (files != null && files.size() == 1)
					{
						val file = files.get(0);
						//val contentType = file.getContentType();
						//FIXME char encoding
						cpaService.validateCPA(new String(file.getBytes()));
					}
					info(getString("cpa.valid"));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.validate"),onSubmit);
		}

		private Button createUploadButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val files = RegisterCPATemplateForm.this.getModelObject().cpaFile;
					if (files != null && files.size() == 1)
					{
						val file = files.get(0);
						//val contentType = file.getContentType();
						//FIXME char encoding
						cpaPluginDAO.insertCPATemplate(RegisterCPATemplateForm.this.getModelObject().getName(),new String(file.getBytes()));
					}
					setResponsePage(new ViewCPATemplatesPage());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.upload"),onSubmit);
			setDefaultButton(result);
			return result;
		}
	}
	
	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class RegisterCPATemplateFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		String name;
		List<FileUpload> cpaFile;
	}
}
