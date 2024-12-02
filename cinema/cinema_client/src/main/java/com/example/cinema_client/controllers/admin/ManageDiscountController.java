package com.example.cinema_client.controllers.admin;

import com.example.cinema_client.models.ApiResponse;
import com.example.cinema_client.models.DiscountDTO;
import com.example.cinema_client.models.JwtResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/admin/discounts")
public class ManageDiscountController {

    @Autowired
    private RestTemplate restTemplate;
    private static final String API_GET_DISCOUNTS = "http://localhost:8080/api/admin/discounts";
    private static final String API_DELETE_DISCOUNT = "http://localhost:8080/api/admin/discounts";

    @GetMapping
    public String displayManageDiscountPage(HttpSession session, Model model) {
        System.out.println("---------------------------------- api Disconut");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);

//         Call API to get the list of discounts
        ResponseEntity<ApiResponse<List<DiscountDTO>>> response = restTemplate.exchange(
                API_GET_DISCOUNTS,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<List<DiscountDTO>>>() {}
        );

        // Log list
        System.out.println(response.getBody());
        List<DiscountDTO> discounts = response.getBody().getResult();
        model.addAttribute("discounts", discounts);
        return "admin/manage-discount";
    }

//    @DeleteMapping("/delete")
//    public String deleteDiscount(@RequestParam("id") Integer id, HttpSession session) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
//        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        // Call API to delete the discount
//        restTemplate.exchange(API_DELETE_DISCOUNT + id, HttpMethod.DELETE, entity, Void.class);
//        return "redirect:/admin/discounts";
//    }
    @PostMapping
public ResponseEntity<String> createDiscount(@RequestBody DiscountDTO discountDTO, HttpSession session) {

    	HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        HttpEntity<DiscountDTO> requestEntity = new HttpEntity<>(discountDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(
        	API_GET_DISCOUNTS,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        return response;
    }

    // PUT - Update Discount
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDiscount(@PathVariable("id") Integer id, @RequestBody DiscountDTO discountDTO, HttpSession session) {
    	HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        HttpEntity<DiscountDTO> requestEntity = new HttpEntity<>(discountDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
        		API_GET_DISCOUNTS + "/" + id,
            HttpMethod.PUT,
            requestEntity,
            String.class
        );

        return response;
    }

    // DELETE - Delete Discount
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDiscount(@PathVariable("id") Integer id, HttpSession session) {
    	HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
        		API_GET_DISCOUNTS + "/" + id,
            HttpMethod.DELETE,
            requestEntity,
            String.class
        );
		return response;
    }
}
