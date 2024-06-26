package software.nipunatheekshana.shoe_shop_management_system.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import software.nipunatheekshana.shoe_shop_management_system.dto.CustomerDTO;
import software.nipunatheekshana.shoe_shop_management_system.exception.NotFoundException;
import software.nipunatheekshana.shoe_shop_management_system.service.CustomerService;
import software.nipunatheekshana.shoe_shop_management_system.service.EmailService;


import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final EmailService emailService;

    @GetMapping("/check")
    public String checkTest(){
        logger.info("Customer Checked");
        return "Customer Check Test";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveCustomer(@Validated @RequestBody CustomerDTO customerDTO, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }
        logger.info("Received request for save a customer");
        try {
            customerService.saveCustomer(customerDTO);
            logger.info("Request processed successfully");
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            logger.error("An exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCustomers(){
        logger.info("Received request for get All customers");
        try {
            return ResponseEntity.ok(customerService.getAllCustomers());
        }catch (Exception e){
            logger.error("An exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getSelectedCustomer(@PathVariable("id") String id){
        logger.info("Received request for get a customer");
        try {
            return ResponseEntity.ok(customerService.getSelectedCustomer(id));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            logger.error("An exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") String id){
        logger.info("Received request for delete a customer");
        try {
            customerService.deleteCustomer(id);
            logger.info("Request processed successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            logger.error("An exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCustomer(@PathVariable("id") String id,
                                            @Validated @RequestBody CustomerDTO customerDTO,
                                            BindingResult bindingResult) {
        logger.info("Received request for update a customer");
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }
        try {
            customerService.updateCustomer(id, customerDTO);
            logger.info("Request processed successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            logger.error("An exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendBirthdayWishes() {
        LocalDate today = LocalDate.now();
        List<CustomerDTO> customers = customerService.getAllCustomers();

        for (CustomerDTO customer : customers) {
            if (customer.getDob().equals(today)) {
                String email = customer.getEmail();
                String subject = "Happy Birthday!";
                String text = "Dear " + customer.getName() + ",\n\nWishing you a very happy birthday!\n\nBest regards,\nHello Shoes PVT.LTD";
                emailService.sendEmail(email, subject, text);
            }
        }
    }

}