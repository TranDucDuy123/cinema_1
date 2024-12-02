package com.example.cinema_client.controllers.admin;

import com.example.cinema_client.models.ApiResponse;
import com.example.cinema_client.models.FeedbackDTO;
import com.example.cinema_client.models.FeedbackRequest;
import com.example.cinema_client.models.JwtResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/feedbacks")
public class ManageFeedbackController {

    @Autowired
    private RestTemplate restTemplate;
    private static final String API_GET_FEEDBACKS = "http://localhost:8080/api/feedbacks/getall";
    private static final String API_FEEDBACK = "http://localhost:8080/api/feedbacks";

    @GetMapping
    public String displayManageFeedbackPage(HttpSession session, Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<ApiResponse<List<FeedbackDTO>>> response = restTemplate.exchange(
                API_GET_FEEDBACKS,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<List<FeedbackDTO>>>() {}
        );
        System.out.println(response.getBody());
        List<FeedbackDTO> feedbacks = response.getBody().getResult();
        model.addAttribute("feedbacks", feedbacks);

        return "admin/manage-feedback";
    }

//    @DeleteMapping("/delete")
//    public String deleteFeedback(@RequestParam("id") Integer id, HttpSession session) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
//        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//        restTemplate.exchange(API_DELETE_FEEDBACK + id, HttpMethod.DELETE, entity, Void.class);
//        return "redirect:/admin/feedbacks";
//    }
    @PostMapping("/add/{type}")
    public ResponseEntity<?> addFeedback(@PathVariable String type, @RequestBody FeedbackRequest feedbackRequest,HttpSession session) {
        String url = API_FEEDBACK + "/add/" + type;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(feedbackRequest),
            ApiResponse.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Integer id,HttpSession session) {
        String url = API_FEEDBACK + "/delete/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            ApiResponse.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getFeedbacksForMovie(@PathVariable Integer movieId,HttpSession session) {
        String url = API_FEEDBACK + "/movie/" + movieId;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
        ResponseEntity<ApiResponse> response;
        response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                ApiResponse.class
        );

        // Kiểm tra phản hồi và trả về kết quả
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }
}
