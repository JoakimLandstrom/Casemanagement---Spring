package se.plushogskolan.casemanagement.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import se.plushogskolan.casemanagement.model.Team;
import se.plushogskolan.casemanagement.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { InfrastructureConfig.class,
		CaseService.class }, loader = AnnotationConfigContextLoader.class)
public class CaseServiceUser {

	@Autowired
	private CaseService service;

	public static boolean dbInit = false;

	@Before
	public void setUp() {
		if (!dbInit) {
			for (int i = 1; i <= 10; i++) {
				User user = new User("searchusername" + i).setFirstName("searchfirstname" + i)
						.setLastName("searchlastname" + i);

				service.save(user);
			}
			dbInit = true;
		}
	}

	@Test
	public void saveUser() {

		User user = new User("joakimlandstrom").setActive(false).setFirstName("joakim").setLastName("landstrom");

		User returnedUser = service.save(user);

		assertEquals(user, returnedUser);

	}
	
	@Test(expected = ServiceException.class)
	public void saveUserShouldThrowExceptionIfTeamIsFull(){
		
		User user = new User("userjoinfullteam");
		
		Team team = new Team("fullteam");
		
		service.save(team);
		
		for(int i = 1; i <= 10; i++){
			User newUser = service.save(new User("joinedfullteam" + i));
			
			service.addUserToTeam(newUser.getId(), team.getId());
		}
		
		user.setTeam(team);
		
		service.save(user);
	}

	@Test(expected = ServiceException.class)
	public void saveUserShouldThrowExceptionWhenUsernameNotLongEnough() {

		User user = new User("joakim");

		service.save(user);
	}

	@Test(expected = ServiceException.class)
	public void saveUserShouldThrowExceptionIfPersisted() {

		User user = new User("joakimlandstrom1");

		service.save(user);

		service.save(user);
	}

	@Test
	public void updateUsername() {

		User user = new User("joakimlandstrom2");

		service.save(user);

		User returnedUser = service.updateUserUsername(user.getId(), "Anakinskywalker");

		assertEquals("Anakinskywalker", returnedUser.getUsername());
	}

	@Test(expected = ServiceException.class)
	public void updateUsernameShouldThrowExceptionIfNotExists() {

		service.updateUserUsername(0l, "Anakinskywalker");

	}

	@Test(expected = ServiceException.class)
	public void shouldThrowExceptionIfUsernameNotLongEnough() {
		service.updateUserUsername(1l, "short");
	}

	@Test
	public void updateFirstname() {

		User user = new User("joakimlandstrom3");

		user = service.save(user);

		User returnedUser = service.updateUserFirstName(user.getId(), "joakim");

		assertEquals("joakim", returnedUser.getFirstName());
	}

	@Test(expected = ServiceException.class)
	public void updateFirstNameShouldThrowExceptionIfNotExists() {

		service.updateUserFirstName(0l, "joakimlandstrom");
	}

	@Test
	public void updateUserLastName() {

		User user = new User("joakimlandstrom4").setLastName("landstrom");

		service.save(user);

		User returnedUser = service.updateUserLastName(user.getId(), "anakin");

		assertEquals("anakin", returnedUser.getLastName());
	}

	@Test(expected = ServiceException.class)
	public void updateLastnameShouldThrowExceptionIfNotExists() {

		service.updateUserLastName(0l, "asdsadasd");
	}

	@Test
	public void inactivateUser() {
		User user = new User("joakimlandstrom5").setActive(true);

		service.save(user);

		User returnedUser = service.inactivateUser(user.getId());

		assertEquals(false, returnedUser.isActive());

	}

	@Test(expected = ServiceException.class)
	public void inactiveUserShouldThrowExceptionIfNotExists() {
		service.inactivateUser(0l);
	}

	@Test
	public void activateUser() {
		User user = new User("joakimlandstrom6").setActive(false);

		service.save(user);

		User returnedUser = service.activateUser(user.getId());

		assertEquals(true, returnedUser.isActive());

	}

	@Test(expected = ServiceException.class)
	public void activateUserShouldThrowExceptionIfNotExists() {
		service.activateUser(0l);
	}

	@Test
	public void getUser() {
		User user = new User("joakimlandstrom7");

		service.save(user);

		User returnedUser = service.getUser(user.getId());

		assertEquals(user, returnedUser);
	}

	@Test(expected = ServiceException.class)
	public void getUserShouldThrowExceptionIfNotExists() {
		service.getUser(0l);
	}

	@Test
	public void searchUserByFirstName() {

		Slice<User> users = service.searchUsersByFirstName("searchfirstname", new PageRequest(0, 15));

		assertEquals(10, users.getNumberOfElements());
	}

	@Test
	public void searchUsersByLastName() {
		Slice<User> users = service.searchUsersByLastName("searchlastname", new PageRequest(0, 15));

		assertEquals(10, users.getNumberOfElements());
	}

	@Test
	public void searchUsersByUsername() {
		Slice<User> users = service.searchUsersByUsername("searchusername", new PageRequest(0, 15));

		assertEquals(10, users.getNumberOfElements());
	}

	@Test
	public void getUsersByTeam() {
		Team team = new Team("userteam");
		User savedUser = new User("joakimlandstrom8");

		service.save(team);
		service.save(savedUser);

		service.addUserToTeam(savedUser.getId(), team.getId());
		
		Slice<User> users = service.getUsersByTeam(team.getId(), new PageRequest(0, 5));
		
		assertEquals(savedUser, users.getContent().get(0));
	}
	
	@Test
	public void getAllUsers(){
		
		Slice<User> users = service.getAllUsers(new PageRequest(0, 20));
		
		assertTrue(users.getContent().size()>10);		
	}
	
	@Test(expected = ServiceException.class)
	public void addUserToTeamShouldThrowExceptionWhenTeamIsFull(){
		
		Team team = new Team("userteam2");
		
		service.save(team);
		
		for(int i = 1; i <= 10; i++){
			
			User user = service.save(new User("teamfilleruser" + i));
			
			service.addUserToTeam(user.getId(),team.getId());
		}
		
		User testUser = new User("maybeteamisfull");
		
		service.save(testUser);
		
		service.addUserToTeam(testUser.getId(), team.getId());
	}
}
