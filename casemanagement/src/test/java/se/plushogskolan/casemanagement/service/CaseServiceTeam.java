//package se.plushogskolan.casemanagement.service;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.Before;
//import org.junit.Test;
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
//import se.plushogskolan.casemanagement.model.Team;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { InfrastructureConfig.class,
//		CaseService.class }, loader = AnnotationConfigContextLoader.class)
//public class CaseServiceTeam {
//
//	@Autowired
//	private CaseService service;
//
//	public static boolean dbInit = false;
//
//	@Before
//	public void setUp() {
//		if (!dbInit) {
//			for (int i = 1; i <= 5; i++) {
//				Team team = new Team("new team" + i);
//				service.save(team); 
//			}
//		}
//		dbInit = true;
//	}
//
//	@Test
//	public void saveTeam() {
//		Team team = new Team("saveteam");
//
//		Team returnedTeam = service.save(team);
//
//		assertEquals(team, returnedTeam);
//	}
//
//	@Test(expected = ServiceException.class)
//	public void saveTeamShouldThrowExceptionIfTeamIsPersisted() {
//
//		Team team = new Team("persistedteam");
//
//		service.save(team);
//
//		service.save(team);
//	}
//
//	@Test
//	public void updateTeam() {
//
//		Team team = new Team("updateteam");
//
//		service.save(team);
//
//		Team returnedTeam = service.updateTeam(team.getId(), new Team("its updated"));
//
//		assertEquals("its updated", returnedTeam.getName());
//		assertEquals(returnedTeam.getId(), team.getId());
//	}
//
//	@Test(expected = ServiceException.class)
//	public void updateTeamShouldThrowExceptionWhenNotExists() {
//
//		service.updateTeam(0l, new Team("not working"));
//	}
//
//	@Test
//	public void inactivateTeam() {
//
//		Team team = new Team("activated team").setActive(true);
//
//		service.save(team);
//
//		Team returnedTeam = service.inactivateTeam(team.getId());
//
//		assertFalse(returnedTeam.isActive());
//	}
//
//	@Test
//	public void activateTeam() {
//		Team team = new Team("inactivated team").setActive(false);
//
//		service.save(team);
//
//		Team returnedTeam = service.activateTeam(team.getId());
//
//		assertTrue(returnedTeam.isActive());
//	}
//
//	@Test
//	public void getTeam() {
//
//		Team team = new Team("get team");
//
//		service.save(team);
//
//		Team returnedTeam = service.getTeam(team.getId());
//
//		assertEquals(team, returnedTeam);
//	}
//
//	@Test
//	public void searchTeamByName() {
//		
//		Slice<Team> teams = service.searchTeamByName("new team", new PageRequest(0, 10));
//		
//		assertTrue(teams.getNumberOfElements() >= 5);
//	}
//	
//	@Test
//	public void getAllTeams(){
//		
//		Slice<Team> teams = service.getAllTeams(new PageRequest(0, 20));
//		
//		assertTrue(teams.getNumberOfElements() >= 5);
//	}
//	
//	
//}
