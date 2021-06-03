package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.*;
import uz.pdp.task1.payload.OrderDto;
import uz.pdp.task1.payload.ResOrders;
import uz.pdp.task1.payload.response.OrderWithoutInvoiceDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.payload.response.Result2;
import uz.pdp.task1.repository.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    DetailRepository detailRepository;
    @Autowired
    InvoiceRepository invoiceRepository;


    public Result2 makeOrder(OrderDto orderDto) {
        Optional<Customer> optionalCustomer = customerRepository.findById(orderDto.getCustomerId());
        if (!optionalCustomer.isPresent()) return new Result2("Customer not found", false, null);


        //Only for adding order without any details or invoice
        if (orderDto.getProductId() == null && orderDto.getQuantity() == null && orderDto.getAmount() == null && orderDto.getIssuedDate() == null) {
            Orders orders = new Orders();
            orders.setCustomer(optionalCustomer.get());

            //if Client doesn't give a date, it will get system(current) date
            if (orderDto.getDate() == null) {
                orders.setDate(new Date());
            } else {
                orders.setDate(orderDto.getDate());
            }

            orderRepository.save(orders);
            return new Result2("Order Successfully added", true, null);
        }
        //adding order with only details
        else if (orderDto.getQuantity() != null && orderDto.getAmount() == null) {
            Optional<Product> optionalProduct = productRepository.findById(orderDto.getProductId());
            if (!optionalProduct.isPresent())
                return new Result2("product not found", false, null);
            //saving info into order table
            Orders orders = new Orders();
            //if Client doesn't give a date, it will get system(current) date
            if (orderDto.getDate() == null) {
                orders.setDate(new Date());
            } else {
                orders.setDate(orderDto.getDate());
            }
            orders.setCustomer(optionalCustomer.get());
            Orders savedOrder = orderRepository.save(orders);
            //saving info into detail table
            Detail detail = new Detail();
            detail.setOrder(savedOrder);
            detail.setProduct(optionalProduct.get());
            detail.setQuantity(orderDto.getQuantity());
            detailRepository.save(detail);
            return new Result2("Order with detail Successfully added", true, null);

        }
        //adding order with only invoice
        else if (orderDto.getQuantity() == null && orderDto.getAmount() != null) {
            Orders orders = new Orders();
            //checking invoice values are null or not
            if (orderDto.getAmount() == null || orderDto.getDueDate() == null || orderDto.getIssuedDate() == null)
                return new Result2("Sth went wrong", false, null);
            //if Client doesn't give a date, it will get system(current) date
            if (orderDto.getDate() == null) {
                orders.setDate(new Date());
            } else {
                orders.setDate(orderDto.getDate());
            }
            orders.setCustomer(optionalCustomer.get());
            Orders savedOrder = orderRepository.save(orders);
            Invoice invoice = new Invoice();
            invoice.setAmount(orderDto.getAmount());
            invoice.setDue(orderDto.getDueDate());
            invoice.setIssued(orderDto.getIssuedDate());
            invoice.setOrder(savedOrder);
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return new Result2("Order with invoice successfully saved", true, savedInvoice.getId());
        }
        //adding order with both detail and invoice
        else {
            Orders orders = new Orders();
            orders.setCustomer(optionalCustomer.get());
            //if Client doesn't give a date, it will get system(current) date
            if (orderDto.getDate() == null) {
                orders.setDate(new Date());
            } else {
                orders.setDate(orderDto.getDate());
            }
            Orders savedOrder = orderRepository.save(orders);

            Optional<Product> optionalProduct = productRepository.findById(orderDto.getProductId());
            if (!optionalProduct.isPresent()) return new Result2("Product not found", false, null);
            Detail detail = new Detail();
            detail.setProduct(optionalProduct.get());
            detail.setOrder(savedOrder);
            detail.setQuantity(orderDto.getQuantity());
            detailRepository.save(detail);

            Invoice invoice = new Invoice();
            invoice.setOrder(savedOrder);
            invoice.setDue(orderDto.getDueDate());
            invoice.setAmount(orderDto.getAmount());
            invoice.setIssued(orderDto.getIssuedDate());
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return new Result2("Order with detail and invoice successfully saved", true, savedInvoice.getId());
        }

    }

    public Page<ResOrders> getAll(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<ResOrders> resOrderList = new ArrayList<>();
        List<Orders> ordersList = orderRepository.findAll();
        for (Orders orders : ordersList) {
            ResOrders resOrders = new ResOrders(
                    orders.getDate(),
                    orders.getCustomer().getName()
            );
            resOrderList.add(resOrders);
        }
        return new PageImpl<>(resOrderList, pageable, resOrderList.size());
    }


    public ResOrders getOneById(Integer id) {
        Optional<Orders> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent())
            return null;
        Orders orders = orderOptional.get();
        ResOrders resOrders = new ResOrders(
                orders.getDate(),
                orders.getCustomer().getName()
        );
        return resOrders;
    }

    public Result deleteOrder(Integer id) {
        try {
            Optional<Orders> optionalOrders = orderRepository.findById(id);
            boolean invoiceExistsByOrderId = invoiceRepository.existsByOrder_Id(id);
            boolean detailExistsByOrderId1 = detailRepository.existsByOrder_Id(id);
            //if order has detail, it automatically will be deleted
            if (detailExistsByOrderId1) {
                Detail byOrder_id = detailRepository.findByOrder_Id(id);
                detailRepository.deleteById(byOrder_id.getId());
            }
            //if invoice has detail, it automatically will be deleted
            if (invoiceExistsByOrderId) {
                Invoice optionalInvoice = invoiceRepository.findByOrder_Id(id);
                invoiceRepository.deleteById(optionalInvoice.getId());
            }

            orderRepository.deleteById(id);
            return new Result("Order successfully deleted", true);


        } catch (Exception e) {
            return new Result("Error in deleting order", false);
        }
    }

    public Result2 editOrder(Integer id, OrderDto orderDto) {

        Optional<Customer> optionalCustomer = customerRepository.findById(orderDto.getCustomerId());
        if (!optionalCustomer.isPresent()) return new Result2("Customer not found", false, null);
        Optional<Orders> optionalOrders = orderRepository.findById(id);
        if (!optionalOrders.isPresent()) return new Result2("Order not found", false, null);


        //Only for adding order without any details or invoice
        if (orderDto.getProductId() == null && orderDto.getQuantity() == null && orderDto.getAmount() == null && orderDto.getIssuedDate() == null) {
            Orders orders = optionalOrders.get();
            orders.setCustomer(optionalCustomer.get());
            orders.setDate(new Date());
            orderRepository.save(orders);
            return new Result2("Order Successfully added", true, null);
        }
        //adding order with only details
        else if (orderDto.getQuantity() != null && orderDto.getAmount() == null) {
            Optional<Product> optionalProduct = productRepository.findById(orderDto.getProductId());
            if (!optionalProduct.isPresent())
                return new Result2("product not found", false, null);
            //saving info into order table
            Orders orders = optionalOrders.get();
            orders.setDate(new Date());
            orders.setCustomer(optionalCustomer.get());
            Orders savedOrder = orderRepository.save(orders);

            //saving info into detail table
            Detail detail = detailRepository.findByOrder_Id(id);
            detail.setOrder(savedOrder);
            detail.setProduct(optionalProduct.get());
            detail.setQuantity(orderDto.getQuantity());
            detailRepository.save(detail);
            return new Result2("Order with detail Successfully added", true, null);

        }
        //adding order with only invoice
        else if (orderDto.getQuantity() == null && orderDto.getAmount() != null) {
            Orders orders = optionalOrders.get();
            //checking invoice values are null or not
            if (orderDto.getAmount() == null || orderDto.getDueDate() == null || orderDto.getIssuedDate() == null)
                return new Result2("Sth went wrong", false, null);
            orders.setDate(new Date());
            orders.setCustomer(optionalCustomer.get());
            Orders savedOrder = orderRepository.save(orders);

            //inserting data into invoice

            Invoice invoice = invoiceRepository.findByOrder_Id(id);
            invoice.setAmount(orderDto.getAmount());
            invoice.setDue(orderDto.getDueDate());
            invoice.setIssued(orderDto.getIssuedDate());
            invoice.setOrder(savedOrder);
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return new Result2("Order with invoice successfully saved", true, savedInvoice.getId());
        }
        //adding order with both detail and invoice
        else {
            Orders orders = optionalOrders.get();
            orders.setCustomer(optionalCustomer.get());
            orders.setDate(new Date());
            Orders savedOrder = orderRepository.save(orders);

            //inserting data info detail
            Optional<Product> optionalProduct = productRepository.findById(orderDto.getProductId());
            if (!optionalProduct.isPresent()) return new Result2("Product not found", false, null);
            Detail detail = detailRepository.findByOrder_Id(id);
            detail.setProduct(optionalProduct.get());
            detail.setOrder(savedOrder);
            detail.setQuantity(orderDto.getQuantity());
            detailRepository.save(detail);

            //inserting data into invoice

            Invoice invoice = invoiceRepository.findByOrder_Id(id);
            invoice.setOrder(savedOrder);
            invoice.setDue(orderDto.getDueDate());
            invoice.setAmount(orderDto.getAmount());
            invoice.setIssued(orderDto.getIssuedDate());
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return new Result2("Order with detail and invoice successfully saved", true, savedInvoice.getId());
        }


    }

    /**
     * Get orders without details and placed before 2016-09-06
     *
     * @return List<Orders>
     */
    public Page<ResOrders> getOrdersWithoutDetails(int page) {
        List<ResOrders> resOrdersList = new ArrayList<>();
        List<Orders> ordersWithoutDetails = orderRepository.findOrdersWithoutDetails();
        for (Orders ordersWithoutDetail : ordersWithoutDetails) {
            ResOrders resOrders = new ResOrders(
                    ordersWithoutDetail.getDate(),
                    ordersWithoutDetail.getCustomer().getName()
            );
            resOrdersList.add(resOrders);
        }
       return new PageImpl<>(resOrdersList, PageRequest.of(page, 15), resOrdersList.size());
    }

    /**
     * For getting orders without invoice and its total price
     *
     * @return
     */
    public List<OrderWithoutInvoiceDto> getOrderWithoutInvoice() {
        List<OrderWithoutInvoiceDto> ordersWithoutInvoice = orderRepository.getOrdersWithoutInvoice();
        return ordersWithoutInvoice;

    }

}
