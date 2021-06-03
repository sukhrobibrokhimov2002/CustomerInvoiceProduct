package uz.pdp.task1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uz.pdp.task1.entity.Customer;
import uz.pdp.task1.payload.CustomerDto;
import uz.pdp.task1.payload.ResCustomerDto;
import uz.pdp.task1.payload.response.OrdersByCountry;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.service.CustomerService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;


    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody CustomerDto customer) {
        Result result = customerService.registerCustomer(customer);
        if (!result.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Getting all customer in pageable format
     *
     * @param page
     * @return Page<Customer>
     */
    @GetMapping
    public ResponseEntity<Page<?>> getAll(@RequestParam Integer page) {
        Page<?> all = customerService.getAll(page);
        if (all.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(all);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Integer id) {
        ResCustomerDto oneById = customerService.getOneById(id);
        if (oneById == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(oneById);
        return ResponseEntity.status(HttpStatus.OK).body(oneById);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Result delete = customerService.delete(id);
        if (!delete.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(delete);
        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Integer id, @Valid @RequestBody CustomerDto customer) {
        Result edit = customerService.edit(id, customer);
        if (!edit.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(edit);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(edit);

    }

    /**
     * Getting customers without orders in 2016
     *
     * @return List<Customer>
     */
    @GetMapping("/customers_without_orders")
    public ResponseEntity<?> getCusWithoutOrder(@RequestParam int page) {
        Page<?> customerWhoNotMakeOrder = customerService.getCustomerWhoNotMakeOrder(page);

        if (customerWhoNotMakeOrder.isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(customerWhoNotMakeOrder);
        return ResponseEntity.status(HttpStatus.OK).body(customerWhoNotMakeOrder);
    }

    /**
     * getting customer with latest order
     *
     * @return List<Order>
     */
    @GetMapping("/customers_last_orders")
    public ResponseEntity<?> getCustomerWithLatestOrder(@RequestParam int page) {
        Page<?> customerWithLatestOrder = customerService.getCustomerWithLatestOrder(page);
        if (customerWithLatestOrder.isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(customerWithLatestOrder);
        return ResponseEntity.status(HttpStatus.OK).body(customerWithLatestOrder);
    }

    /**
     * Getting number of orders by country in 2016
     *
     * @return List<OrdersByCountry>
     */
    @GetMapping("/number_of_products_in_year")
    public ResponseEntity<?> getNumberOfOrdersByCountry() {
        List<OrdersByCountry> ordersByCountry = customerService.getOrdersByCountry();
        if (ordersByCountry.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(ordersByCountry);
        return ResponseEntity.status(HttpStatus.OK).body(ordersByCountry);

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
