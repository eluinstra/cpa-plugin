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

import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.admin.web.service.cpa.StringResourceStream;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.val;

public class DownloadCPATemplateLink extends Link<CPATemplate>
{
	private static final long serialVersionUID = 1L;

	public DownloadCPATemplateLink(String id, CPATemplate cpaTemplate)
	{
		super(id,Model.of(cpaTemplate));
	}

	@Override
	public void onClick()
	{
		val cpaTemplate = getModelObject();
		val resourceStream = StringResourceStream.of(cpaTemplate.getContent(),"text/xml");
		getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(cpaTemplate.getName(),resourceStream));
	}

	private ResourceStreamRequestHandler createRequestHandler(String name, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName(name + ".xml")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}
}
