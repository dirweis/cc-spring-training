package de.training.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import de.training.db.model.TagEntity;

/**
 * The interface extending the {@link JpaRepository} for DAO usage on the {@link TagEntity} objects.
 * 
 * @since 2022-03-16
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
public interface TagRepositoryDao extends JpaRepository<TagEntity, Long> {
	/* Nothing special needed so far */
}
