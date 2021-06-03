package uz.pdp.task1.entity;

import lombok.*;
import uz.pdp.task1.entity.attachment.Attachment;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private double price;
    @ManyToOne
    private Category category;
    @OneToOne
    private Attachment attachment;
}
