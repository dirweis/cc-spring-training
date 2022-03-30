package de.infoteam.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import de.infoteam.db.model.PhotoUrlEntity;

/**
 * The interface extending the {@link JpaRepository} for DAO usage on the {@link PhotoUrlEntity} objects.
 * 
 * @since 2022-03-25
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
public interface PhotoUrlRepositoryDao extends JpaRepository<PhotoUrlEntity, Long> {
	/* Nothing special needed so far */
}
