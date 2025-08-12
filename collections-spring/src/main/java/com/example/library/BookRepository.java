package com.example.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    
    Optional<BookEntity> findByTitleIgnoreCase(String title);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BookEntity b WHERE b.title = :title")
    Optional<BookEntity> findByTitleWithPessimisticLock(@Param("title") String title);
    
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b FROM BookEntity b WHERE b.title = :title")
    Optional<BookEntity> findByTitleWithOptimisticLock(@Param("title") String title);
}
