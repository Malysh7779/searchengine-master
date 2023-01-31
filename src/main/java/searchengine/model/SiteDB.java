package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "site")
@Setter
@Getter
public class SiteDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum('LOAD', 'LOADED', 'INDEXING', 'INDEXED', 'FAILED')")
    private IndexStatus status;

    @Column(name = "status_time", nullable = false)
    private Date statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String url;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

}
