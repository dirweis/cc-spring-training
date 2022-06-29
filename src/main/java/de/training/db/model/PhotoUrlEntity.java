package de.training.db.model;

import java.net.URL;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The {@link Entity} JPA class for the pet's photo URLs to be stored in the database.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-18
 * @version 0.9
 *
 */
@Entity(name = "photo_url")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "pet_id", "url" }))
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PhotoUrlEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Getter
	@Column(nullable = false)
	private URL url;

	@ManyToOne
	@JoinColumn(name = "pet_id", nullable = false)
	private PetEntity pet;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private Date createdTime;
}
