package se.plushogskolan.casemanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.plushogskolan.casemanagement.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	Slice<User> findByFirstNameContaining(String firstName, Pageable page);
	
	Slice<User> findByLastNameContaining(String lastName, Pageable page);
	
	Slice<User> findByUsernameContaining(String username, Pageable page);
	
	Slice<User> findByTeamId(Long id, Pageable page);
	
	@Query("SELECT u FROM #{#entityName} u WHERE u.firstName LIKE %:firstName% AND u.lastName LIKE %:lastName% AND u.username LIKE %:username%")
	Slice<User> findUsersBy(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("username") String username, Pageable page);
	
	Long countByTeamId(Long id);
}
