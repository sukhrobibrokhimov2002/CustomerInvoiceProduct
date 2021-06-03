package uz.pdp.task1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uz.pdp.task1.entity.Orders;
import uz.pdp.task1.payload.OrderDto;
import uz.pdp.task1.payload.ResOrders;
import uz.pdp.task1.payload.response.OrderWithoutInvoiceDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.Result2;
import uz.pdp.task1.service.OrderService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody OrderDto orderDto) {

        Result2 result = orderService.makeOrder(orderDto);
        if (!result.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Getting all orders in pageable format
     *
     * @param page
     * @return Page<Orders>
     */
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam Integer page) {
        Page<ResOrders> all = orderService.getAll(page);
//        if (all.isEmpty()) return null;
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Integer id) {
        ResOrders oneById = orderService.getOneById(id);
        if (oneById == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(oneById);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Result result = orderService.deleteOrder(id);
        if (!result.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Integer id, @Valid @RequestBody OrderDto orderDto) {
        Result2 result = orderService.editOrder(id, orderDto);
        if (!result.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    /**
     * Getting orders that have no details
     *
     * @returnList<Orders>
     */
    @GetMapping("/orders_without_details")
    public ResponseEntity<?> getOrdersWithoutDetails(@RequestParam int page) {
        Page<ResOrders> ordersWithoutDetails = orderService.getOrdersWithoutDetails(page);
        if (ordersWithoutDetails.isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ordersWithoutDetails);
        return ResponseEntity.status(HttpStatus.OK).body(ordersWithoutDetails);

    }

    /**
     * Getting orders without invoice
     *
     * @return List<OrderWithoutInvoice>
     */
    @GetMapping("/orders_without_invoices")
    public ResponseEntity<?> getOrdersWithoutInvoice() {
        List<OrderWithoutInvoiceDto> orderWithoutInvoice = orderService.getOrderWithoutInvoice();
        if (orderWithoutInvoice.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(orderWithoutInvoice);
        return ResponseEntity.status(HttpStatus.OK).body(orderWithoutInvoice);

    }

    //for displaying validation message on console
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
