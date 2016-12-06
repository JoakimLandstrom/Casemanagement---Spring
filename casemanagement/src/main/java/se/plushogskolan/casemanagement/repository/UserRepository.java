package se.plushogskolan.casemanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;

import se.plushogskolan.casemanagement.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	Slice<User> findByFirstNameContaining(String firstName, Pageable page);
	
	Slice<User> findByLastNameContaining(String lastName, Pageable page);
	
	Slice<User> findByUsernameContaining(String username, Pageable page);
	
	Slice<User> findByTeamId(Long id, Pageable page);
	
	Long countByTeamId(Long id);
}
