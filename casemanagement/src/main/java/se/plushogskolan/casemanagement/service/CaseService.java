package se.plushogskolan.casemanagement.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import se.plushogskolan.casemanagement.exception.AlreadyPersistedException;
import se.plushogskolan.casemanagement.exception.IllegalArgumentException;
import se.plushogskolan.casemanagement.exception.InternalErrorException;
import se.plushogskolan.casemanagement.exception.NoSpaceException;
import se.plushogskolan.casemanagement.exception.NotPersistedException;
import se.plushogskolan.casemanagement.exception.StatusConflictException;
import se.plushogskolan.casemanagement.model.AbstractEntity;
import se.plushogskolan.casemanagement.model.Issue;
import se.plushogskolan.casemanagement.model.Team;
import se.plushogskolan.casemanagement.model.User;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.casemanagement.repository.IssueRepository;
import se.plushogskolan.casemanagement.repository.TeamRepository;
import se.plushogskolan.casemanagement.repository.UserRepository;
import se.plushogskolan.casemanagement.repository.WorkItemRepository;

@Service
public class CaseService {

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final WorkItemRepository workItemRepository;
	private final IssueRepository issueRepository;

	@Autowired
	public CaseService(UserRepository userRepository, TeamRepository teamRepository,
			WorkItemRepository workItemRepository, IssueRepository issueRepository) {
		this.userRepository = userRepository;
		this.teamRepository = teamRepository;
		this.workItemRepository = workItemRepository;
		this.issueRepository = issueRepository;
	}

	// USER

	public User save(User user) {

		if (!userFillsRequirements(user) || isPersistedObject(user)) {
			throw new AlreadyPersistedException(String.format("User with id: %d already exists", user.getId()));
		}

		try {
			return userRepository.save(user);
		} catch (DataAccessException e) {
			throw new InternalErrorException("User could not be saved : " + user.getUsername(), e);
		}
	}

	public User updateUserFirstName(Long userId, String firstName) {
		if (userRepository.exists(userId)) {
			try {
				User user = userRepository.findOne(userId);

				user.setFirstName(firstName);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new InternalErrorException("User could not be updated", e);
			}
		} else {
			throw new NotPersistedException("User does not exist :" + userId);
		}
	}

	public User updateUserLastName(Long userId, String lastName) {

		if (userRepository.exists(userId)) {
			try {
				User user = userRepository.findOne(userId);

				user.setLastName(lastName);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new InternalErrorException("User could not be updated", e);
			}
		} else {
			throw new NotPersistedException("User does not exist :" + userId);
		}
	}

	public User updateUserUsername(Long userId, String username) {

		if (!usernameLongEnough(username)) {
			throw new IllegalArgumentException("Username not long enough!");
		}
		if (searchUsersByUsername(username, 0, 1).size() != 0) {
			throw new AlreadyPersistedException("Username already exists");
		}

		if (userRepository.exists(userId)) {
			try {

				User user = userRepository.findOne(userId);

				user.setUsername(username);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new InternalErrorException("User could not be updated", e);
			}
		} else
			throw new NotPersistedException("User doesnt exist");
	}

	public User inactivateUser(Long userId) {

		if (userRepository.exists(userId)) {

			try {

				User user = userRepository.findOne(userId);

				user.setActive(false);

				setStatusOfAllWorkItemsOfUserToUnstarted(userId);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new InternalErrorException("User could not be updated", e);
			}
		} else {
			throw new NotPersistedException("User does not exists :" + userId);
		}
	}

	public User activateUser(Long userId) {

		if (userRepository.exists(userId)) {

			try {

				User user = userRepository.findOne(userId);

				user.setActive(true);

				return userRepository.save(user);
			} catch (DataAccessException e) {
				throw new InternalErrorException("User could not be updated", e);
			}
		} else {
			throw new NotPersistedException("User does not exists :" + userId);
		}
	}

	public User getUser(Long userId) {

		if (userRepository.exists(userId)) {
			return userRepository.findOne(userId);
		} else {
			throw new NotPersistedException("User does not exists :" + userId);
		}

	}

	public List<User> searchUsersByFirstName(String firstName, int page, int size) {
		try {

			return userRepository.findByFirstNameContaining(firstName, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not search users", e);
		}
	}

	public List<User> searchUsersByLastName(String lastName, int page, int size) {
		try {
			return userRepository.findByLastNameContaining(lastName, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not search users", e);
		}
	}

	public List<User> searchUsersByUsername(String username, int page, int size) {
		try {
			return userRepository.findByUsernameContaining(username, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not search users", e);
		}
	}

	public List<User> searchUsersByFirstNameLastNameUsername(String firstName, String lastName, String username,
			int page, int size) {
		try {
			return userRepository.findUsersBy(firstName, lastName, username, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new InternalErrorException("Could not search users", e);
		}
	}

	public List<User> getUsersByTeam(Long teamId, int page, int size) {
		try {
			return userRepository.findByTeamId(teamId, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not search users", e);
		}
	}

	public List<User> getAllUsers(int page, int size) {
		try {
			return userRepository.findAll(new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Couldnt get all users", e);
		}
	}

	@Transactional
	public User addUserToTeam(Long userId, Long teamId) {
		try {
			if (teamHasSpaceForUser(teamId)) {
				Team team = teamRepository.findOne(teamId);
				User user = userRepository.findOne(userId);
				user.setTeam(team);
				return userRepository.save(user);

			} else {
				throw new NoSpaceException("No space in team for user. userId = " + userId + "teamId = " + teamId);
			}
		} catch (DataAccessException e) {
			throw new InternalErrorException("User could not be added to Team");
		}
	}

	// // TEAM

	public Team save(Team team) {
		if (!isPersistedObject(team)) {
			try {
				return teamRepository.save(team);
			} catch (DataAccessException e) {
				throw new InternalErrorException("Team could not be saved");
			}
		} else {
			throw new AlreadyPersistedException("Team already exists");
		}
	}

	public Team updateTeam(Long teamId, Team newValues) {
		if (teamRepository.exists(teamId)) {
			try {
				Team team = teamRepository.findOne(teamId);
				team.setActive(newValues.isActive()).setName(newValues.getName());
				return teamRepository.save(team);
			} catch (DataAccessException e) {
				throw new InternalErrorException("Could not update Team");
			}
		} else {
			throw new NotPersistedException("Team does not exist");
		}
	}

	public Team inactivateTeam(Long teamId) {
		Team team = teamRepository.findOne(teamId);
		if (team.isActive() == true) {
			try {
				team.setActive(false);
				return teamRepository.save(team);
			} catch (DataAccessException e) {
				throw new InternalErrorException("Team could not be inactivated");
			}
		} else {
			throw new StatusConflictException("Team is already inactive");
		}
	}

	public Team activateTeam(Long teamId) {
		try {
			Team team = teamRepository.findOne(teamId);
			if (team.isActive() == false) {
				team.setActive(true);
				return teamRepository.save(team);
			} else {
				throw new StatusConflictException("Team could not be activated");
			}
		} catch (DataAccessException e) {
			throw new InternalErrorException("Team could not be activated");
		}
	}

	public Team getTeam(Long teamId) {
		try {
			return teamRepository.findOne(teamId);
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get team with id: " + teamId);
		}
	}

	public List<Team> searchTeamByName(String name, int page, int size) {
		try {
			return teamRepository.findByNameContaining(name, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get team with name: " + name);
		}
	}

	public List<Team> getAllTeams(int page, int size) {
		try {
			return teamRepository.findAll(new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get teams");
		}
	}

	// WORKITEM

	public WorkItem save(WorkItem workItem) {
		if (isPersistedObject(workItem))
			throw new AlreadyPersistedException("WorkItem already exists");
		try {
			return workItemRepository.save(workItem);
		} catch (DataAccessException e) {
			throw new InternalErrorException("WorkItem could not be saved", e);
		}
	}

	public WorkItem getWorkItemById(Long id) {

		if (workItemRepository.exists(id)) {
			return workItemRepository.findOne(id);
		} else {
			throw new NotPersistedException("WorkItem does not exists : " + id);
		}
	}

	public WorkItem updateStatusById(Long workItemId, WorkItem.Status workItemStatus) {
		if (workItemRepository.exists(workItemId)) {
			try {
				WorkItem workItem = workItemRepository.findOne(workItemId);
				workItem.setStatus(workItemStatus);
				return workItemRepository.save(workItem);
			} catch (DataAccessException e) {
				throw new InternalErrorException("This WorkItem could not be updated", e);
			}
		} else
			throw new NotPersistedException("This WorkItem does not exist");
	}

	public void deleteWorkItem(Long workItemId) {
		if (workItemRepository.exists(workItemId)) {
			try {
				workItemRepository.delete(workItemId);
			} catch (DataAccessException e) {
				throw new InternalErrorException("WorkItem could not be deleted", e);
			}
		} else
			throw new NotPersistedException("This WorkItem does not exist");
	}

	@Transactional
	public WorkItem addWorkItemToUser(Long workItemId, Long userId) {
		// PageRequest(0, 5) because if page 0 has 5 entries the method will

		if (userIsActive(userId) && userHasSpaceForAdditionalWorkItem(workItemId, userId, new PageRequest(0, 5))) {
			try {
				WorkItem workItem = workItemRepository.findOne(workItemId);
				User user = userRepository.findOne(userId);
				workItem.setUser(user);
				return workItemRepository.save(workItem);
			} catch (DataAccessException e) {
				throw new InternalErrorException("Could not add WorkItem to User", e);
			}
		} else
			throw new StatusConflictException("User is inactive");
	}

	public List<WorkItem> searchWorkItemByDescription(String description, int page, int size) {
		try {
			return workItemRepository.findByDescriptionContaining(description, new PageRequest(page, size))
					.getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not find any WorkItem with description: " + description, e);
		}
	}

	public List<WorkItem> getWorkItemsByStatus(WorkItem.Status workItemStatus, int page, int size) {
		try {
			return workItemRepository.findByStatus(workItemStatus, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get WorkItems with status " + workItemStatus, e);
		}
	}

	public List<WorkItem> getWorkItemsByTeamId(Long teamId, int page, int size) {
		try {
			return workItemRepository.findByUserTeamId(teamId, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get WorkItem connected to Team id " + teamId, e);
		}
	}

	public List<WorkItem> getWorkItemsByUserId(Long userId, int page, int size) {
		try {
			return workItemRepository.findByUserId(userId, new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get WorkItem connected to User id " + userId, e);
		}
	}

	public List<WorkItem> getWorkItemsWithIssue(int page, int size) {
		try {
			return workItemRepository.getWorkItemsWithIssue(new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get WorkItems with Issues", e);
		}
	}

	public List<WorkItem> getAllWorkItems(int page, int size) {

		try {
			return workItemRepository.findAll(new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Couldnt get all workitems", e);
		}
	}

	public List<WorkItem> getWorkItemsByPeriodAndStatus(WorkItem.Status status, Date start, Date end, int page,
			int size) {
		try {
			return workItemRepository.getWorkItemsByStatusAndPeriod(status, start, end, new PageRequest(page, size))
					.getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("", e);
		}
	}

	// ISSUE

	@Transactional
	public Issue save(Issue issue) {
		if (workItemIsDone(issue.getWorkitem().getId()) && !isPersistedObject(issue)) {
			try {
				issueRepository.save(issue);
				WorkItem workItem = workItemRepository.findOne(issue.getWorkitem().getId());
				workItem.setIssue(issue).setStatus(WorkItem.Status.UNSTARTED);
				issue.setWorkItem(workItem);
				workItemRepository.save(workItem);
				return issue;
			} catch (DataAccessException e) {
				throw new InternalErrorException("Issue could not be saved");
			}
		} else {
			throw new AlreadyPersistedException("Issue already exists");
		}
	}

	public Issue updateIssueDescription(Long issueId, String description) {
		if (issueRepository.exists(issueId)) {
			try {
				Issue issue = issueRepository.findOne(issueId);
				issue.setDescription(description);
				return issueRepository.save(issue);
			} catch (DataAccessException e) {
				throw new InternalErrorException("Issue could not be updated");
			}
		} else {
			throw new NotPersistedException("Issue doesnt exist: " + issueId);
		}
	}

	public Issue getIssue(Long id) {
		try {
			return issueRepository.findOne(id);
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get Issue with id: " + id);
		}
	}

	public List<Issue> getAllIssues(int page, int size) {
		try {
			return issueRepository.findAll(new PageRequest(page, size)).getContent();
		} catch (DataAccessException e) {
			throw new InternalErrorException("Could not get issues");
		}
	}

	private boolean userFillsRequirements(User user) {
		if (!usernameLongEnough(user.getUsername())) {
			throw new IllegalArgumentException("Username to short");
		}
		if (user.getTeam() != null && !teamHasSpaceForUser(user.getTeam().getId())) {
			throw new NoSpaceException("Team has no space");
		}
		if (searchUsersByUsername(user.getUsername(), 0, 1).size() != 0) {
			throw new AlreadyPersistedException("Username already exists");
		}
		return true;
	}

	private boolean usernameLongEnough(String username) {

		return username.length() >= 10;
	}

	private boolean teamHasSpaceForUser(Long teamId) {

		return userRepository.countByTeamId(teamId) < 10;
	}

	private void setStatusOfAllWorkItemsOfUserToUnstarted(Long userId) {

		Slice<WorkItem> workItems = workItemRepository.findByUserId(userId, new PageRequest(0, 5));
		for (WorkItem workItem : workItems) {
			updateStatusById(workItem.getId(), WorkItem.Status.UNSTARTED);
		}
	}

	private boolean userIsActive(Long userId) {

		User user = userRepository.findOne(userId);
		if (user.isActive())
			return true;
		else
			return false;
	}

	private boolean userHasSpaceForAdditionalWorkItem(Long workItemId, Long userId, Pageable pageable) {
		Slice<WorkItem> workItems = workItemRepository.findByUserId(userId, pageable);

		if (workItems == null) {
			return true;
		}
		for (WorkItem workItem : workItems) {
			if (workItem.getId() == workItemId) {
				return true;
			}
		}
		if (workItems.getNumberOfElements() < 5)
			return true;
		else
			throw new NoSpaceException("User has no space for additional workitems");
	}

	private boolean workItemIsDone(Long workItemId) {
		WorkItem workItem = workItemRepository.findOne(workItemId);

		if (workItem.getStatus().equals(WorkItem.Status.DONE)) {
			return true;
		} else {
			throw new StatusConflictException("WorkItem status is not done");
		}
	}

	private boolean isPersistedObject(AbstractEntity entity) {
		return entity.getId() != null;
	}

}
