package uz.pdp.task1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.task1.entity.Orders;
import uz.pdp.task1.entity.Product;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDetail {

    private Integer orderId;

    private String productName;

    private Integer quantity;
}
