package se.plushogskolan.casemanagement.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.plushogskolan.casemanagement.service.CaseServiceTeam;
import se.plushogskolan.casemanagement.service.CaseServiceUser;

@RunWith(Suite.class)
@SuiteClasses({CaseServiceUser.class, CaseServiceTeam.class})
public class AllTests {

}
