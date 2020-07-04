package nl.clockwork.ebms.admin.plugin.cpa;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import nl.clockwork.ebms.admin.plugin.cpa.dao.DAOConfig;

@Configuration
@Import({
	DAOConfig.class
})
@PropertySource(value = {"classpath:nl/clockwork/ebms/admin/plugin/cpa/default.properties"}, ignoreResourceNotFound = true)
public class MainConfig
{
	public static void main(String[] args)
	{
		try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class))
		{
			
		}
	}
}
