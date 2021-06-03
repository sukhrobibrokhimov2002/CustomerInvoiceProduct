package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.Invoice;
import uz.pdp.task1.entity.Orders;
import uz.pdp.task1.payload.InvoiceDto;
import uz.pdp.task1.payload.ResInvoice;
import uz.pdp.task1.payload.response.ExpiredInvoiceDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.WrongInvoiceDto;
import uz.pdp.task1.repository.InvoiceRepository;
import uz.pdp.task1.repository.OrderRepository;

import java.util.ArrayList;
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

    public Page<ResInvoice> getAll(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<ResInvoice> resInvoiceList = new ArrayList<>();
        List<Invoice> all = invoiceRepository.findAll();
        for (Invoice invoice : all) {
            ResInvoice resInvoice = new ResInvoice(
                    invoice.getOrder().getId(),
                    invoice.getAmount(),
                    invoice.getIssued(),
                    invoice.getDue()
            );
            resInvoiceList.add(resInvoice);
        }
        return new PageImpl<>(resInvoiceList, pageable, resInvoiceList.size());
    }

    public ResInvoice getByOrderId(Integer orderId) {
        Optional<Orders> optionalOrders = orderRepository.findById(orderId);
        if (!optionalOrders.isPresent()) return null;
        Invoice byOrderId = invoiceRepository.findByOrder_Id(orderId);
        ResInvoice resInvoice = new ResInvoice(
                byOrderId.getOrder().getId(),
                byOrderId.getAmount(),
                byOrderId.getIssued(),
                byOrderId.getDue()
        );
        return resInvoice;
    }

    public ResInvoice byId(Integer id) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
        if (!optionalInvoice.isPresent())
            return null;
        Invoice invoice = optionalInvoice.get();
        ResInvoice resInvoice = new ResInvoice(
                invoice.getOrder().getId(),
                invoice.getAmount(),
                invoice.getIssued(),
                invoice.getDue()
        );

        return resInvoice;

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
