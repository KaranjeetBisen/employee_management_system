package com.advancedb.advancedb.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data  // Lombok annotation that generates getters, setters, toString, equals, and hashCode methods.
@Entity  // Marks this class as a JPA entity to be mapped to a database table.
@Table(name = "ranks")  // Specifies the table name in the database.
@Component  // Indicates that this class is a Spring Component and can be injected as a bean in other classes.
public class Ranks {

    @Id  // Marks this field as the primary key of the entity.
    private Long id;

    @Column(name = "rankdesc", nullable = false)  // Specifies the column in the database and makes it non-nullable.
    private String rankdesc;  // Field for the rank description (e.g., "Manager", "Junior", etc.)

    @ManyToOne  // Indicates a many-to-one relationship (many ranks can refer to one parent rank).
    @JoinColumn(name = "parentrankid", referencedColumnName = "id")  // Specifies the foreign key relationship, linking to the "id" of the parent rank.
    private Ranks parentRank;  // A reference to the parent rank (could be null if the rank does not have a parent).
}
