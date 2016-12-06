package se.plushogskolan.casemanagement.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.plushogskolan.casemanagement.service.CaseServiceIssue;
import se.plushogskolan.casemanagement.service.CaseServiceTeam;
import se.plushogskolan.casemanagement.service.CaseServiceUser;
import se.plushogskolan.casemanagement.service.CaseServiceWorkItem;

@RunWith(Suite.class)
@SuiteClasses({ CaseServiceUser.class, CaseServiceTeam.class, CaseServiceIssue.class, CaseServiceWorkItem.class })
public class AllTests {
}
