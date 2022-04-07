package de.training.db.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.training.db.model.PetEntity;

/**
 * The interface extending the {@link JpaRepository} for DAO usage on the {@link PetEntity} objects.
 * 
 * @since 2022-03-14
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
public interface PetRepositoryDao extends JpaRepository<PetEntity, UUID>, JpaSpecificationExecutor<PetEntity> {
	/* No derived queries (allowed here) */
}
