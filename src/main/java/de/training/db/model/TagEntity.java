package de.training.db.model;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The database {@link Entity} representing a tag as {@link String}.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-08
 * @version 1.0
 *
 */
@NoArgsConstructor
@Entity(name = "tag")
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = @Index(columnList = "tag"), uniqueConstraints = @UniqueConstraint(columnNames = { "pet_id", "tag" }))
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
