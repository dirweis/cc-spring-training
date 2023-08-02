package de.training.db.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The {@link Entity} JPA class for the pet's information to be stored in the database.
 * 
 * @author Dirk Weissmann
 * @since 2021-02-18
 * @version 1.0
 *
 */
@Entity(name = "pet")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class PetEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Setter
    @Column(nullable = false, length = 30)
    private String name;

    @Setter
    @Column(nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    private PetStatus status;

    @Setter
    @Column(nullable = false, length = 1_000)
    private String description;

    @Setter
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TagEntity> tags;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PhotoUrlEntity> photoUrls;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Date createdTime;

    @Column(nullable = false)
    @LastModifiedDate
    private Date modifiedTime;
}
