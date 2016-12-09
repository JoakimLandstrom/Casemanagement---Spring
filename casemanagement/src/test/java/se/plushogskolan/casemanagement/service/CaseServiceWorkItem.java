//package se.plushogskolan.casemanagement.service;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Date;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Slice;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.support.AnnotationConfigContextLoader;
//
//import se.plushogskolan.casemanagement.config.InfrastructureConfig;
//import se.plushogskolan.casemanagement.exception.ServiceException;
//import se.plushogskolan.casemanagement.model.Issue;
//import se.plushogskolan.casemanagement.model.Team;
//import se.plushogskolan.casemanagement.model.User;
//import se.plushogskolan.casemanagement.model.WorkItem;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { InfrastructureConfig.class,
//		CaseService.class }, loader = AnnotationConfigContextLoader.class)
//public class CaseServiceWorkItem {
//
//	@Autowired
//	private CaseService service;
//
//	@Rule
//	public final ExpectedException exception = ExpectedException.none();
//
//	private static boolean dbInit = false;
//
//	@Before
//	public void init() {
//
//		if (!dbInit) {
//
//			for (int i = 1; i <= 10; i++) {
//				WorkItem workItem = new WorkItem("searchworkitem" + 1, WorkItem.Status.DONE);
//				service.save(workItem);
//			}
//
//		}
//
//		dbInit = true;
//	}
//
//	@Test
//	public void saveWorkItem() {
//
//		WorkItem workItem = new WorkItem("new workItem", WorkItem.Status.UNSTARTED);
//
//		WorkItem returnedWorkItem = service.save(workItem);
//
//		assertEquals(workItem, returnedWorkItem);
//	}
//
//	@Test(expected = ServiceException.class)
//	public void saveWorkItemShouldThrowExceptionIfPersisted() {
//
//		WorkItem workItem = new WorkItem("another workitem", WorkItem.Status.UNSTARTED);
//
//		service.save(workItem);
//
//		service.save(workItem);
//	}
//
//	@Test
//	public void updateStatusById() {
//
//		WorkItem workItem = new WorkItem("new workItem", WorkItem.Status.STARTED);
//
//		service.save(workItem);
//
//		WorkItem returnedWorkItem = service.updateStatusById(workItem.getId(), WorkItem.Status.DONE);
//
//		assertEquals(WorkItem.Status.DONE, returnedWorkItem.getStatus());
//		assertEquals(workItem.getDescription(), returnedWorkItem.getDescription());
//		assertEquals(workItem.getCreatedBy(), returnedWorkItem.getCreatedBy());
//	}
//
//	@Test(expected = ServiceException.class)
//	public void updateStatusShouldThrowExceptionIfNotExists() {
//
//		service.updateStatusById(0l, WorkItem.Status.DONE);
//	}
//
//	@Test
//	public void deleteWorkItem() {
//
//		WorkItem workItem = new WorkItem("new workitem", WorkItem.Status.UNSTARTED);
//
//		service.save(workItem);
//
//		service.deleteWorkItem(workItem.getId());
//
//		exception.expect(ServiceException.class);
//
//		service.deleteWorkItem(workItem.getId());
//	}
//
//	@Test
//	public void addWorkItemToUser() {
//
//		User user = new User("workitem user").setActive(true);
//
//		WorkItem workItem = new WorkItem("new workitem", WorkItem.Status.UNSTARTED);
//
//		service.save(user);
//
//		service.save(workItem);
//
//		WorkItem returnedWorkItem = service.addWorkItemToUser(workItem.getId(), user.getId());
//
//		assertEquals(user, returnedWorkItem.getUser());
//		assertEquals(workItem, returnedWorkItem);
//	}
//
//	@Test(expected = ServiceException.class)
//	public void addWorkItemToUserShouldThrowExceptionIfUserIsInactive() {
//
//		User user = new User("inactive user").setActive(false);
//
//		service.save(user);
//
//		WorkItem workItem = new WorkItem("new workitem", WorkItem.Status.UNSTARTED);
//
//		service.save(workItem);
//
//		service.addWorkItemToUser(workItem.getId(), user.getId());
//	}
//
//	@Test(expected = ServiceException.class)
//	public void addWorkItemToUserShouldThrowExceptionIfUserDontHaveSpaceForMoreWorkItems() {
//
//		User user = new User("gåttiniväggen").setActive(true);
//
//		service.save(user);
//
//		for (int i = 1; i <= 6; i++) {
//
//			WorkItem workItem = new WorkItem("new workitem" + i, WorkItem.Status.UNSTARTED);
//
//			service.addWorkItemToUser(workItem.getId(), user.getId());
//		}
//	}
//
//	@Test
//	public void searchWorkItemByDescription() {
//
//		Slice<WorkItem> workItems = service.searchWorkItemByDescription("searchworkitem", new PageRequest(0, 10));
//
//		assertTrue(workItems.getNumberOfElements() >= 10);
//	}
//
//	@Test
//	public void getWorkItemByStatus() {
//
//		Slice<WorkItem> workItems = service.getWorkItemsByStatus(WorkItem.Status.DONE, new PageRequest(0, 10));
//
//		assertTrue(workItems.getNumberOfElements() >= 10);
//	}
//
//	@Test
//	public void getWorkItemByTeamId() {
//
//		User user = new User("getworkitembyteamid").setActive(true);
//		Team team = new Team("workitem team");
//		WorkItem workItem = new WorkItem("userworkitem", WorkItem.Status.DONE);
//
//		service.save(user);
//		service.save(team);
//		service.save(workItem);
//
//		user = service.addUserToTeam(user.getId(), team.getId());
//
//		service.addWorkItemToUser(workItem.getId(), user.getId());
//
//		Slice<WorkItem> workItems = service.getWorkItemsByTeamId(team.getId(), new PageRequest(0, 10));
//
//		assertTrue(workItems.getNumberOfElements() == 1);
//		assertEquals(user, workItems.getContent().get(0).getUser());
//		assertEquals(user.getTeam(), workItems.getContent().get(0).getUser().getTeam());
//	}
//
//	@Test
//	public void getWorkItemsByUserId() {
//
//		User user = new User("getworkitembyuserid").setActive(true);
//
//		service.save(user);
//
//		for (int i = 1; i <= 4; i++) {
//			WorkItem workItem = new WorkItem("userworkitem", WorkItem.Status.STARTED);
//
//			service.save(workItem);
//
//			service.addWorkItemToUser(workItem.getId(), user.getId());
//		}
//
//		Slice<WorkItem> workItems = service.getWorkItemsByUserId(user.getId(), new PageRequest(0, 10));
//
//		for (WorkItem wi : workItems.getContent()) {
//			assertEquals(user, wi.getUser());
//		}
//	}
//
//	@Test
//	public void getWorkItemsWithIssues() {
//
//		WorkItem workItem = new WorkItem("workitem with issue", WorkItem.Status.DONE);
//
//		service.save(workItem);
//
//		Issue issue = new Issue(workItem, "issue to workitem");
//
//		service.save(issue);
//
//		Slice<WorkItem> workItems = service.getWorkItemsWithIssue(new PageRequest(0, 5));
//
//		for (WorkItem wi : workItems.getContent()) {
//			assertTrue(wi.getIssue() != null);
//		}
//	}
//
//	
//	@Test
//	public void getAllWorkItems(){
//		
//		Slice<WorkItem> workItems = service.getAllWorkItems(new PageRequest(0,20));
//		
//		assertTrue(workItems.getNumberOfElements() >= 10);
//	}
//	
//	@Test
//	public void getWorkItemByStatusAndPeriod(){
//		
//		Date start = new Date(System.currentTimeMillis()-24*60*60*1000);
//		
//		Date end = new Date(System.currentTimeMillis());
//		
//		
//		Slice<WorkItem> workItems = service.getWorkItemsByPeriodAndStatus(WorkItem.Status.DONE, start, end, new PageRequest(0, 20));
//		
//		for(WorkItem wi: workItems.getContent()){
//			assertEquals(WorkItem.Status.DONE, wi.getStatus());
//			assertTrue(wi.getLastModifiedDate().getTime() >= start.getTime());
//			assertTrue(wi.getLastModifiedDate().getTime() <= end.getTime());
//		}
//		assertTrue(workItems.getNumberOfElements()>=10);
//		
//	}
//}
