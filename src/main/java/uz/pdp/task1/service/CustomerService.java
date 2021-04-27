package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.Customer;
import uz.pdp.task1.payload.CustomerDto;
import uz.pdp.task1.payload.response.OrdersByCountry;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    public Result registerCustomer(CustomerDto customer) {
        boolean existsByPhone = customerRepository.existsByPhone(customer.getPhone());
        if (existsByPhone) return new Result("This phone number already exist", false);
        Customer customer1 = new Customer();
        customer1.setAddress(customer.getAddress());
        customer1.setCountry(customer.getCountry());
        customer1.setName(customer.getName());
        customer1.setPhone(customer.getPhone());
        customerRepository.save(customer1);
        return new Result("User successfully registered", true);
    }

    //get all customers
    public Page<?> getAll(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Customer> all = customerRepository.findAll(pageable);
        return all;
    }

    //getting one customer by id
    public Customer getOneById(Integer id) {
        Optional<Customer> customerRepositoryById = customerRepository.findById(id);
        if (!customerRepositoryById.isPresent()) return null;
        return customerRepositoryById.get();
    }

    public Result delete(Integer id) {
        try {
            Optional<Customer> customerRepositoryById = customerRepository.findById(id);
            if (!customerRepositoryById.isPresent()) return new Result("Customer not found", false);
            customerRepository.deleteById(id);
            return new Result("Successfully deleted", true);
        } catch (Exception r) {
            return new Result("Error in deleting", false);
        }
    }

    //edit customers
    public Result edit(Integer id, CustomerDto customer) {
        //checking customer by id
        Optional<Customer> customerRepositoryById = customerRepository.findById(id);
        if (!customerRepositoryById.isPresent()) return new Result("Customer not found", false);

        //checking phone number exist in database or not
        boolean byPhoneAndIdNot = customerRepository.existsByPhoneAndIdNot(customer.getPhone(), id);
        if (byPhoneAndIdNot) return new Result("Phone already exists", false);

        Customer editedCustomer = customerRepositoryById.get();
        editedCustomer.setCountry(customer.getCountry());
        editedCustomer.setPhone(customer.getPhone());
        editedCustomer.setName(customer.getName());
        editedCustomer.setAddress(customer.getAddress());
        customerRepository.save(editedCustomer);
        return new Result("Successfully edited", true);
    }

    //Customers who didn't make any order in 2016
    public List<Customer> getCustomerWhoNotMakeOrder() {
        List<Customer> customerWhoNotMakeOrder = customerRepository.getCustomerWhoNotMakeOrder();
        return customerWhoNotMakeOrder;

    }

    //customer with the latest order
    public List<?> getCustomerWithLatestOrder() {
        List<?> customersWithLatestOrder = customerRepository.getCustomersWithLatestOrder();
        return customersWithLatestOrder;

    }

    //getting numberOfOrders by Country
    public List<OrdersByCountry> getOrdersByCountry() {
        List<OrdersByCountry> ordersByCountry = customerRepository.getOrdersByCountry();
        return ordersByCountry;

    }

}
