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

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.crypto.MarshalException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.Eithers;
import nl.clockwork.ebms.admin.plugin.cpa.common.Utils;
import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.admin.plugin.cpa.model.Certificate;
import nl.clockwork.ebms.admin.plugin.cpa.model.PartyInfo;
import nl.clockwork.ebms.admin.plugin.cpa.model.Url;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.service.cpa.CPAsPage;
import nl.clockwork.ebms.service.cpa.CPAService;
import nl.clockwork.ebms.util.DOMUtils;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCPAPage extends BasePage
{
	private class LoadableDetachableCPATemplatesModel extends LoadableDetachableModel<List<CPATemplate>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<CPATemplate> load()
		{
			return cpaPluginDAO.selectCPATemplates();
		}
	}
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="cpaPluginDAO")
	CPAPluginDAO cpaPluginDAO;

	public CreateCPAPage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new CreateCPAForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("createCPA",this);
	}

	public class CreateCPAForm extends Form<CreateCPAFormData>
	{
		private static final long serialVersionUID = 1L;

		public CreateCPAForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new CreateCPAFormData()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaTemplateFeedback",createCPATemplateChoice("cpaTemplate")));
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",new TextField<String>("cpaId").setLabel(new ResourceModel("lbl.cpaId")).setRequired(true)).setOutputMarkupId(true));
			add(new BootstrapFormComponentFeedbackBorder("startDateFeedback",new BootstrapDateTimePicker("startDate","dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE).setLabel(new ResourceModel("lbl.startDate")).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("endDateFeedback",new BootstrapDateTimePicker("endDate","dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE).setLabel(new ResourceModel("lbl.endDate")).setRequired(true)));
			add(createPartyInfosContainer("partyInfosContainer"));
			add(createGenerateButton("generate"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),CreateCPAPage.class));
		}

		private DropDownChoice<CPATemplate> createCPATemplateChoice(String id)
		{
			val result = new DropDownChoice<CPATemplate>(id,new LoadableDetachableCPATemplatesModel(),new ChoiceRenderer<>("name","id"));
			result.setLabel(new ResourceModel("lbl.cpaTemplate"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = CreateCPAForm.this.getModelObject();
					if (model.getCpaTemplate() != null)
					{
						val document = DOMUtils.read(model.getCpaTemplate().getContent());
						val xpath = Utils.createXPath();
						model.getPartyInfos().addAll(getPartyInfos(document,xpath));
						model.setCpaId(generateCPAId(model,document,xpath));
					}
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private WebMarkupContainer createPartyInfosContainer(String id)
		{
			val result = new WebMarkupContainer(id);
			val partyInfos = new ListView<PartyInfo>("partyInfos",CreateCPAForm.this.getModelObject().getPartyInfos())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final ListItem<PartyInfo> item)
				{
					item.setModel(new CompoundPropertyModel<>(item.getModelObject()));
					item.add(createEnabledCheckBox("enabled"));
					item.add(new BootstrapFormComponentFeedbackBorder("partyNameFeedback",new TextField<String>("partyName").setLabel(new ResourceModel("lbl.partyName")).setRequired(true))
					{
						private static final long serialVersionUID = 1L;

						@Override
						public boolean isEnabled()
						{
							return item.getModelObject().isEnabled();
						}
					});
					item.add(new BootstrapFormComponentFeedbackBorder("partyIdFeedback",createPartyIdTextField("partyId"))
					{
						private static final long serialVersionUID = 1L;

						@Override
						public boolean isVisible()
						{
							return item.getModelObject().isEnabled();
						}
					});
					item.add(createUrlsContainer("urlsContainer",item));
					item.add(createCertificatesContainer("certificatesContainer",item));
				}
			};
			result.add(partyInfos);
			result.setOutputMarkupId(true);
			return result;
		}

		private CheckBox createEnabledCheckBox(String id)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.enabled"));
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private FormComponent<String> createPartyIdTextField(String id)
		{
			val result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.partyId"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					if (o.getCpaTemplate() != null)
					{
						val document = DOMUtils.read(o.getCpaTemplate().getContent());
						val xpath = Utils.createXPath();
						generateCPAId(o,document,xpath);
					}
					t.add(getPage().get("form:cpaIdFeedback"));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private WebMarkupContainer createUrlsContainer(String id, final ListItem<PartyInfo> item)
		{
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(() -> item.getModelObject().isEnabled())
					.build();
			val certificates = new ListView<Url>("urls",item.getModelObject().getUrls())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Url> item)
				{
					item.setModel(new CompoundPropertyModel<>(item.getModelObject()));
					item.add(new Label("transportId"));
					item.add(new BootstrapFormComponentFeedbackBorder("urlFeedback",createUrlTextField("url",item.getModelObject().getTransportId())));
				}

			};
			result.add(certificates);
			result.setOutputMarkupId(true);
			return result;
		}

		private FormComponent<String> createUrlTextField(String id, String label)
		{
			return new TextField<String>(id).setLabel(new ResourceModel(label,label)).setRequired(true);
		}

		private WebMarkupContainer createCertificatesContainer(String id, final ListItem<PartyInfo> item)
		{
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(() -> item.getModelObject().isEnabled())
					.build();
			val certificates = new ListView<Certificate>("certificates",item.getModelObject().getCertificates())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Certificate> item)
				{
					item.setModel(new CompoundPropertyModel<>(item.getModelObject()));
					item.add(new Label("id"));
					item.add(new BootstrapFormComponentFeedbackBorder("fileFeedback",createCertificateFileUploadField("file",item.getModelObject().getId())));
				}
			};
			result.add(certificates);
			result.setOutputMarkupId(true);
			return result;
		}

		private FileUploadField createCertificateFileUploadField(String id, final String label)
		{
			val result = new FileUploadField(id);
			result.setLabel(new ResourceModel(label,label));
			result.setRequired(true);
			return result;
		}

		private Button createGenerateButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val cpaTemplate = getModelObject().cpaTemplate;
					val document = DOMUtils.read(cpaTemplate.getContent());
					processDocument(document,getModelObject());
					cpaService.insertCPA(DOMUtils.toString(document),false);
					setResponsePage(new CPAsPage());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.generate"),onSubmit);
			setDefaultButton(result);
			return result;
		}

		private void processDocument(Document document, CreateCPAFormData modelObject) throws XPathExpressionException, CertificateException, KeyException, MarshalException, IOException
		{
			val xpath = Utils.createXPath();
			getCpaId(document,xpath).setNodeValue(modelObject.cpaId);
			getStartDate(document,xpath).setNodeValue(Utils.toXSDDate(modelObject.startDate));
			getEndDate(document,xpath).setNodeValue(Utils.toXSDDate(modelObject.endDate));
			for (PartyInfo partyInfo : modelObject.getPartyInfos())
			{
				if (partyInfo.isEnabled())
				{
					getPartyName(document,xpath,partyInfo.getId()).setNodeValue(partyInfo.getPartyName());
					getPartyId(document,xpath,partyInfo.getId()).setNodeValue(partyInfo.getPartyId());
					for (Url url: partyInfo.getUrls())
						getUrl(document,xpath,partyInfo.getId(),url.getTransportId()).setNodeValue(url.getUrl());
					for (Certificate certificate : partyInfo.getCertificates())
					{
						val files = certificate.getFile();
						if (files != null && files.size() == 1)
							replaceCertificate(document,xpath,partyInfo.getId(),certificate.getId(),files.get(0).getInputStream());
					}
				}
			}
		}

		private Node getCpaId(Document document, XPath xpath) throws XPathExpressionException
		{
			return (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/@cpa:cpaid",document,XPathConstants.NODE);
		}

		private Node getStartDate(Document document, XPath xpath) throws XPathExpressionException
		{
			return (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:Start/text()",document,XPathConstants.NODE);
		}

		private Node getEndDate(Document document, XPath xpath) throws XPathExpressionException
		{
			return (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:End/text()",document,XPathConstants.NODE);
		}

		private Node getPartyName(Document document, XPath xpath, Integer partyInfoId) throws XPathExpressionException
		{
			return (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfoId + "]/@cpa:partyName",document,XPathConstants.NODE);
		}

		private Node getPartyId(Document document, XPath xpath, Integer partyInfoId) throws XPathExpressionException
		{
			return (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfoId + "]/cpa:PartyId/text()",document,XPathConstants.NODE);
		}

		private Node getUrl(Document document, XPath xpath, Integer partyInfoId, String transportId) throws XPathExpressionException
		{
			return (Node)xpath.evaluate(
					"/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfoId + "]/cpa:Transport[@cpa:transportId = '" + transportId + "']/cpa:TransportReceiver/cpa:Endpoint[1]/@cpa:uri",
					document,
					XPathConstants.NODE);
		}

		private void replaceCertificate(Document document, XPath xpath, Integer partyInfoId, String certificateId, InputStream certificate) throws XPathExpressionException, CertificateException, KeyException, MarshalException, IOException
		{
			val certificateNode = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfoId + "]//cpa:Certificate[@cpa:certId = '" + certificateId + "']",document,XPathConstants.NODE);
			val keyInfo = (Node)xpath.evaluate("//cpa:Certificate[@cpa:certId = '" + certificateId + "']/xmldsig:KeyInfo",document,XPathConstants.NODE);
			certificateNode.removeChild(keyInfo);
			Utils.generateKeyInfo(certificateNode,certificate);
		}

		private List<PartyInfo> getPartyInfos(Document document, XPath xpath) throws XPathExpressionException
		{
			Function3<Integer,Document,XPath,Either<XPathExpressionException,PartyInfo>> createPartyInfo = (i,d,x) ->
					Try.<Either<XPathExpressionException,PartyInfo>>of(() -> Either.<XPathExpressionException,PartyInfo>right(createPartyInfo(i,d,x)))
					.getOrElseGet(e -> Either.<XPathExpressionException,PartyInfo>left((XPathExpressionException)e));
			val nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement//cpa:PartyInfo",document,XPathConstants.NODESET);
			return Eithers.sequenceRight(
					IntStream.range(1,nodeList.getLength() + 1).boxed()
					.map(i -> createPartyInfo.apply(i,document,xpath)))
					.getOrElseThrow(e -> e);
		}

		private PartyInfo createPartyInfo(int id, Document document, XPath xpath) throws XPathExpressionException
		{
			val result = new PartyInfo();
			result.setId(id);
			result.setPartyName(getPartyName(id,document,xpath));
			result.setPartyId(getPartyId(id,document,xpath));
			result.setUrls(getURLs(id,document,xpath));
			result.setCertificates(getCertificateFiles(id,document,xpath));
			return result;
		}

		private String generateCPAId(CreateCPAFormData model, Document document, XPath xpath) throws XPathExpressionException
		{
			val cpaId = (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/@cpa:cpaid",document,XPathConstants.STRING);
			return cpaId + "_" + toString(model.getPartyInfos()) + "_" + UUID.randomUUID();
		}

		private String toString(List<PartyInfo> partyInfos)
		{
			return partyInfos.stream().filter(p -> !StringUtils.isEmpty(p.getPartyId())).map(p -> p.getPartyId()).collect(Collectors.joining("_"));
		}

		private String getPartyName(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			return (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[[[" + id + "]/@cpa:partyName",document,XPathConstants.STRING);
		}

		private String getPartyId(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			return (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]/cpa:PartyId/text()",document,XPathConstants.STRING);
		}

		private List<Url> getURLs(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			Function4<Integer,String,Document,XPath,Either<XPathExpressionException,Url>> createUrl = (i,t,d,x) ->
					Try.<Either<XPathExpressionException,Url>>of(() -> Either.<XPathExpressionException,Url>right(createUrl(i,t,d,x)))
					.getOrElseGet(e -> Either.<XPathExpressionException,Url>left((XPathExpressionException)e));
			val nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]//cpa:Transport/@cpa:transportId",document,XPathConstants.NODESET);
			return Eithers.sequenceRight(IntStream.range(0,nodeList.getLength()).boxed()
					.map(i -> createUrl.apply(id,nodeList.item(i).getNodeValue(),document,xpath)))
					.getOrElseThrow(e -> e);
		}

		private Url createUrl(Integer id, String transportId, Document document, XPath xpath) throws XPathExpressionException
		{
			val url = (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]/cpa:Transport[@cpa:transportId = '" + transportId + "']/cpa:TransportReceiver/cpa:Endpoint[1]/@cpa:uri",document,XPathConstants.STRING);
			return new Url(transportId,url);
		}

		private ArrayList<Certificate> getCertificateFiles(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			val result = new ArrayList<Certificate>();
			val nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]//cpa:Certificate/@cpa:certId",document,XPathConstants.NODESET);
			val certificates = IntStream.range(0,nodeList.getLength()).boxed()
					.map(i -> new Certificate(nodeList.item(i).getNodeValue()))
					.collect(Collectors.toList());
			result.addAll(certificates);
			return result;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class CreateCPAFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		CPATemplate cpaTemplate;
		String cpaId;
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDate = LocalDateTime.now().plusYears(1);
		List<PartyInfo> partyInfos = new ArrayList<>();
	}
}
