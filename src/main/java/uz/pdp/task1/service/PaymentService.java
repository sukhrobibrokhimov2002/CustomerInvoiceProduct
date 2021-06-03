package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.Invoice;
import uz.pdp.task1.entity.Payment;
import uz.pdp.task1.payload.PaymentDto;
import uz.pdp.task1.payload.ResPayment;
import uz.pdp.task1.payload.response.PaymentOverpaidDto;
import uz.pdp.task1.payload.response.PaymentOverpaidResponse;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.Result3;
import uz.pdp.task1.repository.InvoiceRepository;
import uz.pdp.task1.repository.PaymentRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    InvoiceRepository invoiceRepository;


    public Result3 add(PaymentDto paymentDto) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(paymentDto.getInvoiceId());
        if (!optionalInvoice.isPresent()) return new Result3("Invoice not found", false, null);
        Invoice invoice = optionalInvoice.get();
        Payment payment = new Payment();
        payment.setInvoice(optionalInvoice.get());
        payment.setAmount(invoice.getAmount());
        payment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        Payment save = paymentRepository.save(payment);
        return new Result3("Successfully added", true, save);
    }


    public Page<ResPayment> getAllInPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Payment> all = paymentRepository.findAll();
        List<ResPayment> resPaymentList = new ArrayList<>();
        for (Payment payment : all) {
            ResPayment resPayment = new ResPayment(
                    payment.getTimestamp(),
                    payment.getAmount(),
                    payment.getInvoice().getId()
            );
            resPaymentList.add(resPayment);
        }
        return new PageImpl<>(resPaymentList, pageable, resPaymentList.size());
    }


    public ResPayment getOneById(Integer id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (!optionalPayment.isPresent())
            return null;
        Payment payment = optionalPayment.get();
        ResPayment resPayment = new ResPayment(
                payment.getTimestamp(),
                payment.getAmount(),
                payment.getInvoice().getId()
        );
        return resPayment;
    }

    public Result delete(Integer id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (!optionalPayment.isPresent()) return new Result("Payment not found", false);
        paymentRepository.deleteById(id);
        return new Result("Successfully deleted", true);
    }

    public Result3 edit(Integer id, PaymentDto paymentDto) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (!optionalPayment.isPresent()) return new Result3("Payment not found", false, null);
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(paymentDto.getInvoiceId());
        if (!optionalInvoice.isPresent()) return new Result3("Invoice not found", false, null);
        Payment payment = optionalPayment.get();
        payment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        payment.setInvoice(optionalInvoice.get());
        payment.setAmount(optionalInvoice.get().getAmount());
        Payment save = paymentRepository.save(payment);
        return new Result3("Successfully edited", true, save);

    }


    public List<PaymentOverpaidResponse> getOverpaidInvoices() {
        List<PaymentOverpaidDto> overpaidInvoice = paymentRepository.getOverpaidInvoice();
        List<PaymentOverpaidResponse> response = new ArrayList<>();
        for (PaymentOverpaidDto paymentOverpaidDto : overpaidInvoice) {
            Optional<Invoice> optionalInvoice = invoiceRepository.findById(paymentOverpaidDto.getInvoice_id());
            if (!optionalInvoice.isPresent()) return null;
            Invoice invoice = optionalInvoice.get();

            if (paymentOverpaidDto.getSum() > invoice.getAmount()) {
                PaymentOverpaidResponse overpaidResponse = new PaymentOverpaidResponse();
                overpaidResponse.setReimbursedSumma(paymentOverpaidDto.getSum() - invoice.getAmount());
                overpaidResponse.setInvoiceId(invoice.getId());
                response.add(overpaidResponse);
            } else {
                continue;
            }
        }

        return response;

    }
}
