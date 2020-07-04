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

import lombok.val;
import nl.clockwork.ebms.admin.plugin.cpa.MainConfig;
import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;

public class ExtensionProviderImpl extends ExtensionProvider
{
	@Override
	public Class<?> getSpringConfigurationClass()
	{
		return MainConfig.class;
	}

	@Override
	public String getDbMigrationLocation()
	{
		return "classpath:/nl/clockwork/ebms/admin/plugin/cpa/db/migration";
	}

	@Override
	public String getName()
	{
		return "CPA Plugin";
	}

	@Override
	public MenuItem createSubMenu(MenuItem parent, int index, String name)
	{
		val result = new MenuItem(parent,String.valueOf(index),name);
		result.getChildren().add(new MenuLinkItem("cpa_0","View CPA Templates",ViewCPATemplatesPage.class));
		result.getChildren().add(new MenuLinkItem("cpa_1","Create CPA",CreateCPAPage.class));
		return result;
	}
}
