package com.example.cinema_client.controllers.admin;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.cinema_client.constants.Api;
import com.example.cinema_client.models.BillDTO;
import com.example.cinema_client.models.BranchDTO;
import com.example.cinema_client.models.JwtResponseDTO;
import com.example.cinema_client.models.MovieDTO;
import com.example.cinema_client.models.TicketDTO;

@Controller
@RequestMapping("/admin/bills")
public class ManageBillsController {
	@Autowired
	private RestTemplate restTemplate;
	private static final String API_GET_BILLS = Api.baseURL + "/api/admin/bills";
	private static final String API_SCHEDULES = Api.baseURL+"/api/admin/schedules";
	private static final String API_GET_TICKETS = Api.baseURL+"/api/admin/tickets";
	@GetMapping
	public String displayManageBillPage(HttpSession session, Model model) {
		// Gắn access token jwt vào header để gửi kèm request
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<BillDTO[]> response = restTemplate.exchange(API_GET_BILLS, HttpMethod.GET, entity, BillDTO[].class);
		BillDTO[] bills = response.getBody();
		model.addAttribute("bills", bills);
		return "admin/manage-bill";
	}
	@GetMapping("/viewTickets")
    public String displayTicket(@RequestParam Integer billId ,HttpSession session,Model model){
        //Gắn access token jwt vào header để gửi kèm request
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO)session.getAttribute("jwtResponse");
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer "+jwtResponseDTO.getAccessToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(API_GET_TICKETS)
                .queryParam("billId", "{billId}")
                .encode()
                .toUriString();
        Map<String,Integer> listRequestParam = new HashMap<>();
        listRequestParam.put("billId", billId);
        TicketDTO[] tickets = null;
        try {
            ResponseEntity<TicketDTO[]> responseTicket = restTemplate.exchange(urlTemplate,HttpMethod.POST,entity,TicketDTO[].class,
            		listRequestParam);
    	    tickets = responseTicket.getBody();
		} catch (Exception e) {
			System.out.println(e);
		}
        model.addAttribute("tickets", tickets);
	  
        return "admin/view-ticket";
    }
	@PostMapping("/delete")
    public String deleteBill(@RequestParam Integer billId, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        // Get the JWT token from the session
        JwtResponseDTO jwtResponseDTO = (JwtResponseDTO) session.getAttribute("jwtResponse");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken());

        // Create the entity with headers
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            // Send DELETE request to the backend
            ResponseEntity<String> response = restTemplate.exchange(API_GET_BILLS + "?billId=" + billId, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                model.addAttribute("successMessage", "Xóa hóa đơn thành công!");
            } else {
                model.addAttribute("errorMessage", "Error deleting bill");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        // Redirect back to the bills page
        return "redirect:/admin/bills";
    }
	
}
