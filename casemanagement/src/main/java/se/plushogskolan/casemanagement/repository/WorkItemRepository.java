package se.plushogskolan.casemanagement.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.plushogskolan.casemanagement.model.WorkItem;

public interface WorkItemRepository extends PagingAndSortingRepository<WorkItem, Long> {

	Slice<WorkItem> findByDescriptionContaining(String description, Pageable pageable);

	Slice<WorkItem> findByStatus(WorkItem.Status workItemStatus, Pageable pageable);

	Slice<WorkItem> findByUserTeamId(Long teamId, Pageable pageable);

	Slice<WorkItem> findByUserId(Long userId, Pageable pageable);

	@Query("SELECT wi FROM #{#entityName} wi WHERE wi.issue IS NOT NULL")
	Slice<WorkItem> getWorkItemsWithIssue(Pageable pageable);

	@Query("SELECT wi FROM #{#entityName} wi WHERE wi.status = :status AND wi.lastModifiedDate BETWEEN :startDate AND :endDate")
	Slice<WorkItem> getWorkItemsByStatusAndPeriod(@Param("status") WorkItem.Status status, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
			Pageable pageable);

}
