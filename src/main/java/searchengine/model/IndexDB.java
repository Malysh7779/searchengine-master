package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "indx")
@Getter
@Setter
public class IndexDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)

    private PageDB page;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)

    private LemmaDB lemma;

    @Column(name = "rank1", columnDefinition = "FLOAT", nullable = false)
    private float rank;

}
