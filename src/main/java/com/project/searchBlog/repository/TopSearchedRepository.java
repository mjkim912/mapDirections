package com.project.searchBlog.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.searchBlog.domain.TopSearched;

@Repository
public interface TopSearchedRepository extends JpaRepository<TopSearched, Long>{

	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	TopSearched findByKeyword(String keyword);

	List<TopSearched> findTop10ByOrderByCountDesc();
	
}
