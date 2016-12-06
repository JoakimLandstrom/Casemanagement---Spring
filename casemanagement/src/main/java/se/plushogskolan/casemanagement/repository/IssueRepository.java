package se.plushogskolan.casemanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.plushogskolan.casemanagement.model.Issue;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long>{

	@Query("SELECT i FROM #{#entityName} i WHERE i.workItem.id = :workItemId")
    Slice<Issue> getIssuesByWorkItemId(@Param("workItemId")Long workItemId, Pageable pageable);    
}
