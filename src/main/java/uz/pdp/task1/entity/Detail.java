package uz.pdp.task1.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Orders order;
    @ManyToOne
    private Product product;
    @Column(nullable = false)
    private Integer quantity;
}
