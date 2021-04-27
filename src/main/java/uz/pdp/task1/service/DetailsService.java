package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.Detail;
import uz.pdp.task1.entity.Orders;
import uz.pdp.task1.entity.Product;
import uz.pdp.task1.payload.DetailsDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.repository.DetailRepository;
import uz.pdp.task1.repository.OrderRepository;
import uz.pdp.task1.repository.ProductRepository;

import java.util.Optional;

@Service
public class DetailsService {

    @Autowired
    DetailRepository detailRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    public Result add(DetailsDto detailsDto) {
        Optional<Orders> optionalOrders = orderRepository.findById(detailsDto.getOrderId());
        if (!optionalOrders.isPresent()) return new Result("Order not found", false);
        Optional<Product> optionalProduct = productRepository.findById(detailsDto.getProductId());
        if (!optionalProduct.isPresent()) return new Result("Product Not found", false);
        Detail detail = new Detail();
        detail.setOrder(optionalOrders.get());
        detail.setProduct(optionalProduct.get());
        detail.setQuantity(detailsDto.getQuantity());
        detailRepository.save(detail);
        return new Result("Detail successfully saved", true);


    }

    public Page<Detail> getAllDetails(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Detail> detailRepositoryAll = detailRepository.findAll(pageable);
        return detailRepositoryAll;
    }

    public Detail getOneById(Integer id) {
        Optional<Detail> optionalDetail = detailRepository.findById(id);
        return optionalDetail.orElseGet(Detail::new);
    }

    public Result delete(Integer id) {
        try {

            detailRepository.deleteById(id);
            return new Result("Detail Successfully deleted", true);

        } catch (Exception e) {
            return new Result("Error in deleting detail", false);
        }
    }

    public Result edit(Integer id, DetailsDto detailsDto) {
        Optional<Product> optionalProduct = productRepository.findById(detailsDto.getProductId());
        Optional<Orders> optionalOrders = orderRepository.findById(detailsDto.getOrderId());
        Optional<Detail> optionalDetail = detailRepository.findById(id);
        if (!optionalOrders.isPresent() || !optionalProduct.isPresent() || !optionalDetail.isPresent())
            return new Result("Error", false);
        Detail detail = optionalDetail.get();
        detail.setQuantity(detailsDto.getQuantity());
        detail.setProduct(optionalProduct.get());
        detail.setOrder(optionalOrders.get());
        detailRepository.save(detail);
        return new Result("Successfully edited", true);


    }

}
