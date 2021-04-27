package uz.pdp.task1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Integer customerId;
    private Integer productId;
    private Integer quantity;
    private Date date;
    private Double amount;
    private Date issuedDate;
    private Date dueDate;
}
