package se.plushogskolan.casemanagement.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.plushogskolan.casemanagement.config.InfrastructureConfig;
import se.plushogskolan.casemanagement.exception.ServiceException;
import se.plushogskolan.casemanagement.model.Issue;
import se.plushogskolan.casemanagement.model.WorkItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { InfrastructureConfig.class,
		CaseService.class }, loader = AnnotationConfigContextLoader.class)
public class CaseServiceIssue {

	@Autowired
	private CaseService service;

	public static boolean dbInit = false;

	@Before
	public void init() {

		if (!dbInit) {

			dbInit = true;
		}

	}

	@Test
	public void saveIssue() {

		WorkItem workItem = new WorkItem("new workitem", WorkItem.Status.DONE);

		service.save(workItem);

		Issue issue = new Issue(workItem, "new issue");

		Issue returnedIssue = service.save(issue);

		assertEquals(returnedIssue, issue);
		assertEquals(WorkItem.Status.UNSTARTED, returnedIssue.getWorkitem().getStatus());
	}

	@Test(expected = ServiceException.class)
	public void saveIssueShouldThrowExceptionIfWorkItemIsNotDone() {

		WorkItem workItem = new WorkItem("not done workitem", WorkItem.Status.STARTED);

		service.save(workItem);

		Issue issue = new Issue(workItem, "work item not done");

		service.save(issue);
	}

	@Test(expected = ServiceException.class)
	public void saveIssueShouldThrowExceptionIfIssueIfPersisted() {

		WorkItem workItem = new WorkItem("persisted issue", WorkItem.Status.DONE);

		service.save(workItem);

		Issue issue = new Issue(workItem, "persisted issue");

		service.save(issue);

		service.save(issue);

	}

	@Test
	public void updateIssueDescription() {

		WorkItem workItem = new WorkItem("persisted issue", WorkItem.Status.DONE);

		service.save(workItem);

		Issue issue = new Issue(workItem, "update this");

		service.save(issue);

		Issue returnedIssue = service.updateIssueDescription(issue.getId(), "it should be updated");

		assertEquals(workItem.getDescription(), returnedIssue.getWorkitem().getDescription());
		assertEquals("it should be updated", returnedIssue.getDescription());
	}

	@Test(expected = ServiceException.class)
	public void updateIssueDescriptionShouldThrowExceptionIfNotExists() {

		service.updateIssueDescription(0l, "will not work");
	}
	
	@Test
	public void getIssue(){
		
		WorkItem workItem = service.save(new WorkItem("workitem", WorkItem.Status.DONE));
		
		Issue issue = new Issue(workItem, "get issue");
		
		service.save(issue);
		
		Issue returnedIssue = service.getIssue(issue.getId());
		
		assertEquals(issue, returnedIssue);
	}
	
	@Test
	public void getAllIssues(){
		
		for(int i = 1; i <= 5; i++){	
			WorkItem workItem = service.save(new WorkItem("workitem " + i, WorkItem.Status.DONE));
			
			Issue issue = new Issue(workItem, "issue" + i);
			service.save(issue);
		}
		
		Slice<Issue> issues = service.getAllIssues(new PageRequest(0, 5));
		
		assertTrue(issues.getNumberOfElements() >= 5);
	}

}
