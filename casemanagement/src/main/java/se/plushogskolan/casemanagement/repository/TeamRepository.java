package se.plushogskolan.casemanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;

import se.plushogskolan.casemanagement.model.Team;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long>{
	
	Slice<Team> findByNameContaining(String name, Pageable pageable);
	
}
