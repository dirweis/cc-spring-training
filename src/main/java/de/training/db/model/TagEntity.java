package de.training.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The database {@link Entity} representing a tag as {@link String}.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-08
 * @version 1.1
 *
 */
@Entity(name = "tag")
@Table(indexes = @Index(columnList = "tag"), uniqueConstraints = @UniqueConstraint(columnNames = { "pet_id", "tag" }))
@EntityListeners(AuditingEntityListener.class)
public class TagEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Getter
	@Column(nullable = false, length = 20)
	private String tag;

	@Setter
	@ManyToOne
	@JoinColumn(name = "pet_id", nullable = false)
	private PetEntity pet;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private Date createdTime;

	/**
	 * Constructor for the only field that is to be set here by the developer: The name of the tag.
	 * 
	 * @param tag the tag's name, may be {@code null}
	 */
	public TagEntity(final String tag) {
		this.tag = tag;
	}
}
