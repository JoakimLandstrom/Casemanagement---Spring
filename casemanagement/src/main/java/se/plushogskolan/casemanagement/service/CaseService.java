package se.plushogskolan.casemanagement.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import se.plushogskolan.casemanagement.exception.ServiceException;
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

		if (!userFillsRequirements(user)) {
			throw new ServiceException("Username is too short or team is full");
		}
		if (isPersistedObject(user)) {
			throw new ServiceException(String.format("User with id: %d already exists", user.getId()));
		}

		try {
			return userRepository.save(user);
		} catch (DataAccessException e) {
			throw new ServiceException("User could not be saved : " + user.getUsername(), e);
		}
	}

	public User updateUserFirstName(Long userId, String firstName) {
		if (userRepository.exists(userId)) {
			try {
				User user = userRepository.findOne(userId);

				user.setFirstName(firstName);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new ServiceException("User could not be updated", e);
			}
		} else {
			throw new ServiceException("User does not exist :" + userId);
		}
	}

	public User updateUserLastName(Long userId, String lastName) {

		if (userRepository.exists(userId)) {
			try {
				User user = userRepository.findOne(userId);

				user.setLastName(lastName);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new ServiceException("User could not be updated", e);
			}
		} else {
			throw new ServiceException("User does not exist :" + userId);
		}
	}

	public User updateUserUsername(Long userId, String username) {

		if (!usernameLongEnough(username)) {
			throw new ServiceException("Username not long enough!");
		}

		if (userRepository.exists(userId)) {
			try {

				User user = userRepository.findOne(userId);

				user.setUsername(username);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new ServiceException("User could not be updated", e);
			}
		} else
			throw new ServiceException("User could not be updated");
	}

	public User inactivateUser(Long userId) {

		if (userRepository.exists(userId)) {

			try {

				User user = userRepository.findOne(userId);

				user.setActive(false);

				setStatusOfAllWorkItemsOfUserToUnstarted(userId);

				return userRepository.save(user);

			} catch (DataAccessException e) {
				throw new ServiceException("User could not be updated", e);
			}
		} else {
			throw new ServiceException("User does not exists :" + userId);
		}
	}

	public User activateUser(Long userId) {

		if (userRepository.exists(userId)) {

			try {

				User user = userRepository.findOne(userId);

				user.setActive(true);

				return userRepository.save(user);
			} catch (DataAccessException e) {
				throw new ServiceException("User could not be updated", e);
			}
		} else {
			throw new ServiceException("User does not exists :" + userId);
		}
	}

	public User getUser(Long userId) {

		if (userRepository.exists(userId)) {
			return userRepository.findOne(userId);
		} else {
			throw new ServiceException("User does not exists :" + userId);
		}

	}

	public Slice<User> searchUsersByFirstName(String firstName, Pageable page) {
		try {
			return userRepository.findByFirstNameContaining(firstName, page);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not search users", e);
		}
	}

	public Slice<User> searchUsersByLastName(String lastName, Pageable page) {
		try {
			return userRepository.findByLastNameContaining(lastName, page);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not search users", e);
		}
	}

	public Slice<User> searchUsersByUsername(String username, Pageable page) {
		try {
			return userRepository.findByUsernameContaining(username, page);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not search users", e);
		}
	}

	public Slice<User> getUsersByTeam(Long teamId, Pageable page) {
		try {
			return userRepository.findByTeamId(teamId, page);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not search users", e);
		}
	}

	public Slice<User> getAllUsers(Pageable pageable) {
		try {
			return userRepository.findAll(pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Couldnt get all users", e);
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
				throw new ServiceException("No space in team for user. userId = " + userId + "teamId = " + teamId);
			}
		} catch (DataAccessException e) {
			throw new ServiceException("User could not be added to Team");
		}
	}

	// // TEAM

	public Team save(Team team) {
		if (!isPersistedObject(team)) {
			try {
				return teamRepository.save(team);
			} catch (DataAccessException e) {
				throw new ServiceException("Team could not be saved");
			}
		} else {
			throw new ServiceException("Team already exists");
		}
	}

	public Team updateTeam(Long teamId, Team newValues) {
		if (teamRepository.exists(teamId)) {
			try {
				Team team = teamRepository.findOne(teamId);
				team.setActive(newValues.isActive()).setName(newValues.getName());
				return teamRepository.save(team);
			} catch (DataAccessException e) {
				throw new ServiceException("Could not update Team");
			}
		} else {
			throw new ServiceException("Team does not exist");
		}
	}

	public Team inactivateTeam(Long teamId) {
		Team team = teamRepository.findOne(teamId);
		if (team.isActive() == true) {
			try {
				team.setActive(false);
				return teamRepository.save(team);
			} catch (DataAccessException e) {
				throw new ServiceException("Team could not be inactivated");
			}
		} else {
			throw new ServiceException("Team is already inactive");
		}
	}

	public Team activateTeam(Long teamId) {
		try {
			Team team = teamRepository.findOne(teamId);
			if (team.isActive() == false) {
				team.setActive(true);
				return teamRepository.save(team);
			} else {
				throw new ServiceException("Team could not be activated");
			}
		} catch (DataAccessException e) {
			throw new ServiceException("Team could not be activated");
		}
	}

	public Team getTeam(Long teamId) {
		try {
			return teamRepository.findOne(teamId);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get team with id: " + teamId);
		}
	}

	public Slice<Team> searchTeamByName(String name, Pageable page) {
		try {
			return teamRepository.findByNameContaining(name, page);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get team with name: " + name);
		}
	}

	public Slice<Team> getAllTeams(Pageable pageable) {
		try {
			return teamRepository.findAll(pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get teams");
		}
	}

	// WORKITEM

	public WorkItem save(WorkItem workItem) {
		if (isPersistedObject(workItem))
			throw new ServiceException("WorkItem already exists");
		try {
			return workItemRepository.save(workItem);
		} catch (DataAccessException e) {
			throw new ServiceException("WorkItem could not be saved", e);
		}
	}

	public WorkItem updateStatusById(Long workItemId, WorkItem.Status workItemStatus) {
		if (workItemRepository.exists(workItemId)) {
			try {
				WorkItem workItem = workItemRepository.findOne(workItemId);
				workItem.setStatus(workItemStatus);
				return workItemRepository.save(workItem);
			} catch (DataAccessException e) {
				throw new ServiceException("This WorkItem could not be updated", e);
			}
		} else
			throw new ServiceException("This WorkItem does not exist");
	}

	public void deleteWorkItem(Long workItemId) {
		if (workItemRepository.exists(workItemId)) {
			try {
				workItemRepository.delete(workItemId);
			} catch (DataAccessException e) {
				throw new ServiceException("WorkItem could not be deleted", e);
			}
		} else
			throw new ServiceException("This WorkItem does not exist");
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
				throw new ServiceException("Could not add WorkItem to User", e);
			}
		} else
			throw new ServiceException("User is either inactive or has no space for additional WorkItems");
	}

	public Slice<WorkItem> searchWorkItemByDescription(String description, Pageable pageable) {
		try {
			return workItemRepository.findByDescriptionContaining(description, pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not find any WorkItem with description: " + description, e);
		}
	}

	public Slice<WorkItem> getWorkItemsByStatus(WorkItem.Status workItemStatus, Pageable pageable) {
		try {
			return workItemRepository.findByStatus(workItemStatus, pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get WorkItems with status " + workItemStatus, e);
		}
	}

	public Slice<WorkItem> getWorkItemsByTeamId(Long teamId, Pageable pageable) {
		try {
			return workItemRepository.findByUserTeamId(teamId, pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get WorkItem connected to Team id " + teamId, e);
		}
	}

	public Slice<WorkItem> getWorkItemsByUserId(Long userId, Pageable pageable) {
		try {
			return workItemRepository.findByUserId(userId, pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get WorkItem connected to User id " + userId, e);
		}
	}

	public Slice<WorkItem> getWorkItemsWithIssue(Pageable pageable) {
		try {
			return workItemRepository.getWorkItemsWithIssue(pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get WorkItems with Issues", e);
		}
	}

	public Slice<WorkItem> getAllWorkItems(Pageable pageable) {

		try {
			return workItemRepository.findAll(pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Couldnt get all workitems", e);
		}
	}

	public Slice<WorkItem> getWorkItemsByPeriodAndStatus(WorkItem.Status status, Date start, Date end,
			Pageable pageable) {
		try {
			return workItemRepository.getWorkItemsByStatusAndPeriod(status, start, end, pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("", e);
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
				throw new ServiceException("Issue could not be saved");
			}
		} else {
			throw new ServiceException("WorkItem does not have status done or the Issue already exists");
		}
	}

	public Issue updateIssueDescription(Long issueId, String description) {
		if (issueRepository.exists(issueId)) {
			try {
				Issue issue = issueRepository.findOne(issueId);
				issue.setDescription(description);
				return issueRepository.save(issue);
			} catch (DataAccessException e) {
				throw new ServiceException("Issue could not be updated");
			}
		} else {
			throw new ServiceException("Could not change description of issue with id: " + issueId);
		}
	}

	public Issue getIssue(Long id) {
		try {
			return issueRepository.findOne(id);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get Issue with id: " + id);
		}
	}

	public Slice<Issue> getAllIssues(Pageable pageable) {
		try {
			return issueRepository.findAll(pageable);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not get issues");
		}
	}

	private boolean userFillsRequirements(User user) {
		if (!usernameLongEnough(user.getUsername())) {
			return false;
		}
		if (user.getTeam() != null && !teamHasSpaceForUser(user.getTeam().getId())) {
			return false;
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
			return false;
	}

	private boolean workItemIsDone(Long workItemId) {
		WorkItem workItem = workItemRepository.findOne(workItemId);
		return WorkItem.Status.DONE.equals(workItem.getStatus());
	}

	private boolean isPersistedObject(AbstractEntity entity) {
		return entity.getId() != null;
	}

}
