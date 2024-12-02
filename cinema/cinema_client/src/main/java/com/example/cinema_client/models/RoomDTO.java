package com.example.cinema_client.models;

import java.util.List;
import lombok.Data;

@Data
public class RoomDTO {
    private int id;
    private String name;
    private int capacity;
    private double totalArea;
    private String imgURL;
    private BranchDTO branch;
    private List<SeatDTO> seats;
}
