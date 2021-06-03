package uz.pdp.task1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.task1.entity.Orders;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResInvoice {


    private Integer orderId;
    private double amount;

    private Date issued;

    private Date due;
}
