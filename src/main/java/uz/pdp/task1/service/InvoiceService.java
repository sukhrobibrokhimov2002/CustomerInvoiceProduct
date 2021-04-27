package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.Invoice;
import uz.pdp.task1.entity.Orders;
import uz.pdp.task1.payload.InvoiceDto;
import uz.pdp.task1.payload.response.ExpiredInvoiceDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.WrongInvoiceDto;
import uz.pdp.task1.repository.InvoiceRepository;
import uz.pdp.task1.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    OrderRepository orderRepository;

    public Result addInvoice(InvoiceDto invoiceDto) {
        Optional<Orders> optionalOrders = orderRepository.findById(invoiceDto.getOrderId());
        if (!optionalOrders.isPresent()) return new Result("Order not found", false);
        Invoice invoice = new Invoice();
        invoice.setDue(invoiceDto.getDueDate());
        invoice.setOrder(optionalOrders.get());
        invoice.setAmount(invoiceDto.getAmount());
        invoice.setIssued(invoiceDto.getIssuedDate());
        invoiceRepository.save(invoice);
        return new Result("Invoice Successfully saved", true);
    }

    public Page<Invoice> getAll(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Invoice> all = invoiceRepository.findAll(pageable);
        return all;
    }

    public Invoice getByOrderId(Integer orderId) {
        Optional<Orders> optionalOrders = orderRepository.findById(orderId);
        if (!optionalOrders.isPresent()) return new Invoice();
        Invoice byOrder_id = invoiceRepository.findByOrder_Id(orderId);
        return byOrder_id;
    }

    public Invoice byId(Integer id) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
        return optionalInvoice.orElse(null);


    }

    public Result delete(Integer id) {
        try {
            invoiceRepository.deleteById(id);
            return new Result("Successfully deleted", true);

        } catch (Exception e) {
            return new Result("Error in deleting invoice", false);
        }
    }

    public Result edit(Integer id, InvoiceDto invoiceDto) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
        if (!optionalInvoice.isPresent()) return new Result("Invoice not found", false);
        Invoice invoice = optionalInvoice.get();

        //checking orderId is coming null or not
        if (invoiceDto.getOrderId() != null) {
            Optional<Orders> optionalOrders = orderRepository.findById(invoiceDto.getOrderId());
            if (!optionalOrders.isPresent()) return new Result("Order not found", false);
            invoice.setOrder(optionalOrders.get());
        } else {
            //order id remain same
            invoice.setOrder(invoice.getOrder());
        }

        invoice.setIssued(invoiceDto.getIssuedDate());
        invoice.setAmount(invoiceDto.getAmount());
        invoice.setDue(invoiceDto.getDueDate());
        invoiceRepository.save(invoice);
        return new Result("Successfully edited", true);
    }

    public List<ExpiredInvoiceDto> getExpiredInvoice() {
        List<ExpiredInvoiceDto> expiredInvoice = invoiceRepository.getExpiredInvoice();
        return expiredInvoice;
    }

    public List<WrongInvoiceDto> getWrongInvoiceDto() {
        List<WrongInvoiceDto> wrongInvoice = invoiceRepository.getWrongInvoice();
        return wrongInvoice;

    }
}
