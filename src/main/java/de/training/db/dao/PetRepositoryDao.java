package de.training.db.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.training.db.model.PetEntity;

/**
 * The interface extending the {@link JpaRepository} for DAO usage on the {@link PetEntity} objects.
 * 
 * @since 2022-03-14
 * @version 0.5
 * @author Dirk Weissmann
 *
 */
public interface PetRepositoryDao extends JpaRepository<PetEntity, UUID> {
	/* Nothing special (derived queries) needed */
}
