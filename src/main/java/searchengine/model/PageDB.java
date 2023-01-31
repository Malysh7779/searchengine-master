package searchengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "page") //, uniqueConstraints = {@UniqueConstraint(columnNames = {"path"}) }) //, indexes = @Index(columnList = "path")) //, uniqueConstraints = {@UniqueConstraint(columnNames = {"path"}) })

@Getter
@Setter
public class PageDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SiteDB site;

    @Column(columnDefinition = "VARCHAR(600)", nullable = false, unique = true)
    private String path;

    @Column(nullable = false)
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

}
