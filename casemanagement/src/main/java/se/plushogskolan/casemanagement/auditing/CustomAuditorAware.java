package se.plushogskolan.casemanagement.auditing;

import org.springframework.data.domain.AuditorAware;

public class CustomAuditorAware implements AuditorAware<String> {

	@Override
	public String getCurrentAuditor() {

		String username = "";

		username = System.getProperty("user.name");

		return username;
	}

}
