package uz.pdp.task1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import uz.pdp.task1.entity.Detail;
import uz.pdp.task1.payload.DetailsDto;
import uz.pdp.task1.payload.response.Result;
import uz.pdp.task1.service.DetailsService;

@RestController
@RequestMapping("/details")
public class DetailsController {

    @Autowired
    DetailsService detailsService;


    @PostMapping
    public ResponseEntity<?> add(@RequestBody DetailsDto detailsDto) {
        Result add = detailsService.add(detailsDto);
        if (!add.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(add);
        return ResponseEntity.status(HttpStatus.CREATED).body(add);
    }

    /**
     * Get all details in pageable format
     *
     * @param page
     * @return Page<Details>
     */
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam Integer page) {
        Page<Detail> allDetails = detailsService.getAllDetails(page);
        if (allDetails.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(allDetails);
        return ResponseEntity.status(HttpStatus.OK).body(allDetails);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Integer id) {
        Detail oneById = detailsService.getOneById(id);
        if (oneById == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(oneById);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Result delete = detailsService.delete(id);
        if (!delete.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(delete);
        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Integer id, @RequestBody DetailsDto detailsDto) {
        Result edit = detailsService.edit(id, detailsDto);
        if (!edit.isStatus()) return ResponseEntity.status(HttpStatus.CONFLICT).body(edit);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(edit);
    }

}
