package de.training.db.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
import jakarta.persistence.FetchType;
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
@Getter
@NoArgsConstructor
@Entity(name = "pet")
@EntityListeners(AuditingEntityListener.class)
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
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TagEntity> tags;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PhotoUrlEntity> photoUrls;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedTime;
}
