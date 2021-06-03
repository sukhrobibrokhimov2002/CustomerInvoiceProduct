package uz.pdp.task1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.task1.entity.Payment;
import uz.pdp.task1.payload.PaymentDto;
import uz.pdp.task1.payload.ResPayment;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.Result3;
import uz.pdp.task1.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    /**
     * method for addding payment
     *
     * @param paymentDto
     * @return Result(message, status)
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody PaymentDto paymentDto) {
        Result3 add = paymentService.add(paymentDto);
        if (!add.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(add);
        return ResponseEntity.status(HttpStatus.CREATED).body(add);
    }

    /**
     * method for getting all in pageable format
     *
     * @param page
     * @return Page<Payment>
     */
    @GetMapping
    public ResponseEntity<?> getAllInPage(@RequestParam Integer page) {
        Page<ResPayment> all = paymentService.getAllInPage(page);
        if (all.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(all);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }





    @GetMapping("/details/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Integer id) {
        ResPayment oneById = paymentService.getOneById(id);
        if (oneById == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(oneById);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Result delete = paymentService.delete(id);
        if (!delete.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(delete);
        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Integer id, @RequestBody PaymentDto paymentDto) {
        Result3 edit = paymentService.edit(id, paymentDto);
        if (!edit.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(edit);
        return ResponseEntity.status(HttpStatus.OK).body(edit);
    }

    /**
     * Payments that are overpaid and return sum that should be reimbursed
     *
     * @return List<>
     */
    @GetMapping("/overpaid_invoices")
    public ResponseEntity<?> getOverPaid() {
        List<?> overpaidInvoices = paymentService.getOverpaidInvoices();
        if (overpaidInvoices.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(overpaidInvoices);
        return ResponseEntity.status(HttpStatus.OK).body(overpaidInvoices);
    }

}

