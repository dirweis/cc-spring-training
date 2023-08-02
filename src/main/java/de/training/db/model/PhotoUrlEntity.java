package de.training.db.model;

import java.net.URL;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The {@link Entity} JPA class for the pet's photo URLs to be stored in the database.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-18
 * @version 1.0
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
