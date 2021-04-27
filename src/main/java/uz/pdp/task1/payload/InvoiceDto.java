package uz.pdp.task1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.criterion.Order;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {

    private Integer orderId;
    @NotNull(message = "Amount must not be null")
    private Double amount;
    @NotNull(message = "issuedDate must not be null")
    private Date issuedDate;
    @NotNull(message = "dueDate must not be null")
    private Date dueDate;


}
