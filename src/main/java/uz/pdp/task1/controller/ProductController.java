package uz.pdp.task1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.task1.entity.Product;
import uz.pdp.task1.payload.ProductDto;
import uz.pdp.task1.payload.ResProduct;
import uz.pdp.task1.payload.response.HighDemandProductDto;
import uz.pdp.task1.payload.response.ProductInBulkResponse;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {


    @Autowired
    ProductService productService;


    @PostMapping
    public ResponseEntity<?> add(@RequestBody ProductDto productDto) {
        Result add = productService.add(productDto);
        if (!add.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(add);
        return ResponseEntity.status(HttpStatus.CREATED).body(add);
    }

    /**
     * Method for getting all product in page format
     *
     * @param page
     * @return Page
     */
    @GetMapping
    public ResponseEntity<?> getAllInPage(@RequestParam Integer page) {
        Page<ResProduct> all = productService.getAllInPage(page);
        if (all.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(all);

        return ResponseEntity.status(HttpStatus.OK).body(all);
    }





    @GetMapping("/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Integer id) {
        ResProduct  oneById = productService.getOneById(id);
        if (oneById == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(oneById);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Result delete = productService.delete(id);
        if (!delete.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(delete);
        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Integer id, @RequestBody ProductDto productDto) {
        Result edit = productService.edit(productDto, id);
        if (!edit.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(edit);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(edit);
    }

    /**
     * Getting products that ordered more than 10times
     *
     * @return List<HighDemandProductDto>
     */
    @GetMapping("/high_demand_products")
    public ResponseEntity<?> getHighDemandProducts() {
        List<HighDemandProductDto> highDemandProducts = productService.getHighDemandProducts();
        if (!highDemandProducts.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(highDemandProducts);
        return ResponseEntity.status(HttpStatus.OK).body(highDemandProducts);


    }

    /**
     * Getting products that ordered in quantity more than 8
     *
     * @return
     */
    @GetMapping("/bulk_products")
    public ResponseEntity<?> getBulkProducts() {
        List<ProductInBulkResponse> productsInBulk = productService.getProductsInBulk();
        if (productsInBulk.isEmpty()) return ResponseEntity.status(409).body(productsInBulk);
        return ResponseEntity.status(200).body(productsInBulk);

    }

}
