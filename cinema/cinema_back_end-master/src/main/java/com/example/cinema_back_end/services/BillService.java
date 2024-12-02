package com.example.cinema_back_end.services;

import com.example.cinema_back_end.dtos.BillDTO;
import com.example.cinema_back_end.dtos.BookingRequestDTO;

import com.example.cinema_back_end.entities.Bill;
import com.example.cinema_back_end.entities.Schedule;
import com.example.cinema_back_end.entities.Seat;
import com.example.cinema_back_end.entities.Ticket;
import com.example.cinema_back_end.entities.User;
import com.example.cinema_back_end.repositories.IBillRepository;
import com.example.cinema_back_end.repositories.IScheduleRepository;
import com.example.cinema_back_end.repositories.ISeatRepository;
import com.example.cinema_back_end.repositories.ITicketRepository;
import com.example.cinema_back_end.security.repo.IUserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService implements IBillService{
    @Autowired
    private IScheduleRepository scheduleRepository;
    @Autowired
    private ITicketRepository ticketRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ISeatRepository seatRepository;
    @Autowired
    private IBillRepository billRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    @Transactional
    public void createNewBill(BookingRequestDTO bookingRequestDTO) throws RuntimeException {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        // Lấy ra lịch
        Schedule schedule = scheduleRepository.findById(bookingRequestDTO.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Lịch không tồn tại"));

        if (schedule.getStartDate().isAfter(date) ||
            (schedule.getStartDate().isEqual(date) && schedule.getStartTime().isAfter(time))) {

            // Lấy ra người dùng
            User user = userRepository.findById(bookingRequestDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            // Khởi tạo tổng giá
            double totalPrice = 0.0;

            // Tạo Bill
            Bill billToCreate = new Bill();
            billToCreate.setUser(user);
            billToCreate.setCreatedTime(LocalDateTime.now());

            // Đặt ghế và tính giá
            for (Integer seatId : bookingRequestDTO.getListSeatIds()) {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Ghế không tồn tại"));

                double seatPrice = schedule.getPrice();

                // Nếu ghế là VIP, tăng giá thêm 10,000
                if (seat.isVip()) {
                    seatPrice += 10000;
                }

                // Thêm giá của ghế vào tổng giá
                totalPrice += seatPrice;

                // Tạo Ticket và lưu vào DB
                Ticket ticket = new Ticket();
                ticket.setSchedule(schedule);
                ticket.setSeat(seat);
                ticket.setBill(billToCreate);
                ticket.setQrImageURL("https://example.com/qr-image"); // Replace with actual QR URL
                ticketRepository.save(ticket);
            }

            // Cập nhật tổng giá vào Bill
            billToCreate.setPrice(totalPrice);

            // Lưu Bill vào DB
            billRepository.save(billToCreate);

        } else {
            throw new RuntimeException("Lịch chiếu đã kết thúc không thể đặt chỗ ngồi!");
        }
    }

	@Override
	public List<BillDTO> findAll() {
		// TODO Auto-generated method stub
		return billRepository.findAll().stream().map(bill->modelMapper.map(bill, BillDTO.class)).collect(Collectors.toList());
	}

	@Override
	public BillDTO getById(Integer billId) {
		// TODO Auto-generated method stub
		 return modelMapper.map(billRepository.getById(billId),BillDTO.class);
	}	
	

	@Override
	public void remove(Integer id) {
		// TODO Auto-generated method stub
		billRepository.deleteById(id);
	}

	@Override
	public void update(BillDTO t) {
		// TODO Auto-generated method stub
		
	}
}
