package uz.pdp.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.task1.entity.Category;
import uz.pdp.task1.entity.Product;
import uz.pdp.task1.entity.attachment.Attachment;
import uz.pdp.task1.payload.ProductDto;
import uz.pdp.task1.payload.ResProduct;
import uz.pdp.task1.payload.response.HighDemandProductDto;
import uz.pdp.task1.payload.response.ProductInBulkResponse;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.repository.AttachmentRepository;
import uz.pdp.task1.repository.CategoryRepository;
import uz.pdp.task1.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    public Result add(ProductDto productDto) {
        //check category exists or not
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());
        if (!optionalCategory.isPresent()) return new Result("Category not found", false);
        Product product = new Product();
        //check photo exists or not
        if(productDto.getAttachmentId()!=null) {
            Optional<Attachment> optionalAttachment = attachmentRepository.findById(productDto.getAttachmentId());
            product.setAttachment(optionalAttachment.get());
        }


        product.setCategory(optionalCategory.get());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setName(productDto.getName());
        productRepository.save(product);
        return new Result("Product successfully added", true);
    }

    public Page<ResProduct> getAllInPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Product> all = productRepository.findAll();
        List<ResProduct> resProductList = new ArrayList<>();

        for (Product product : all) {
            ResProduct resProduct = new ResProduct(
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategory().getName()
            );
            resProductList.add(resProduct);
        }
        return new PageImpl<>(resProductList, pageable, resProductList.size());
    }


    public ResProduct getOneById(Integer id) {
        //checking product whether exist or not
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent())
            return null;
        Product product = optionalProduct.get();
        ResProduct resProduct = new ResProduct(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getName()
        );

        return resProduct;
    }

    public Result delete(Integer id) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (!optionalProduct.isPresent()) return new Result("Product not found", false);
            productRepository.deleteById(id);
            return new Result("Successfully deleted", true);

        } catch (Exception e) {
            return new Result("Error in deleting", false);
        }

    }

    public Result edit(ProductDto productDto, Integer id) {
        //checking product,attachment,category whether exist or not
        Optional<Product> optionalProduct = productRepository.findById(id);
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(productDto.getAttachmentId());
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());

        if (!optionalAttachment.isPresent() || !optionalCategory.isPresent() || !optionalProduct.isPresent()) {
            return new Result("Error in deleting", false);
        }
        Product editedProduct = optionalProduct.get();
        editedProduct.setName(productDto.getName());
        editedProduct.setAttachment(optionalAttachment.get());
        editedProduct.setPrice(productDto.getPrice());
        editedProduct.setDescription(productDto.getDescription());
        editedProduct.setCategory(optionalCategory.get());
        productRepository.save(editedProduct);
        return new Result("Successfully edited", true);

    }

    public List<HighDemandProductDto> getHighDemandProducts() {
        List<HighDemandProductDto> highDemandProducts = productRepository.getHighDemandProducts();
        return highDemandProducts;
    }

    public List<ProductInBulkResponse> getProductsInBulk() {
        List<ProductInBulkResponse> productsInBulk = productRepository.getProductsInBulk();
        return productsInBulk;
    }
}
