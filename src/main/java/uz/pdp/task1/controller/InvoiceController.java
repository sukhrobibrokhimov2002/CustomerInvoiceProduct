package uz.pdp.task1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uz.pdp.task1.entity.Invoice;
import uz.pdp.task1.payload.InvoiceDto;
import uz.pdp.task1.payload.response.ExpiredInvoiceDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.WrongInvoiceDto;
import uz.pdp.task1.service.InvoiceService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    InvoiceService invoiceService;


    @PostMapping
    public ResponseEntity<?> add(@RequestBody InvoiceDto invoiceDto) {
        Result result = invoiceService.addInvoice(invoiceDto);
        if (!result.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    /**
     * Get all in pageable format
     *
     * @param page
     * @return Page<Invoice>
     */
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam Integer page) {
        Page<Invoice> all = invoiceService.getAll(page);
        if (all.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(all);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Integer id) {
        Invoice invoice = invoiceService.byId(id);
        if (invoice == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(invoice);
    }

    /**
     * Getting invoice by order id
     *
     * @param orderId
     * @return Invoice
     */
    @GetMapping("/getByOrderId/{orderId}")
    public ResponseEntity<?> getByOrderId(@PathVariable Integer orderId) {
        Invoice byOrderId = invoiceService.getByOrderId(orderId);
        if (byOrderId == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(byOrderId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Result delete = invoiceService.delete(id);
        if (!delete.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(delete);
        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Integer id, @Valid @RequestBody InvoiceDto invoiceDto) {
        Result edit = invoiceService.edit(id, invoiceDto);
        if (!edit.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(edit);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(edit);
    }

    /**
     * getting expired invoice
     *
     * @return List<ExpiredInvoiceDto>
     */
    @GetMapping("/expired_invoices")
    public ResponseEntity<?> getExpiredInvoice() {
        List<ExpiredInvoiceDto> expiredInvoice = invoiceService.getExpiredInvoice();
        if (expiredInvoice.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(expiredInvoice);
        return ResponseEntity.status(HttpStatus.OK).body(expiredInvoice);

    }

    /**
     * Getting wrong Invoices
     *
     * @return
     */
    @GetMapping("/wrong_date_invoices")
    public ResponseEntity<?> getWrongInvoice() {
        List<WrongInvoiceDto> wrongInvoiceDto = invoiceService.getWrongInvoiceDto();
        if (wrongInvoiceDto.isEmpty()) return ResponseEntity.status(409).body(wrongInvoiceDto);
        return ResponseEntity.status(200).body(wrongInvoiceDto);
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
