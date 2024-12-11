package com.advancedb.advancedb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.advancedb.advancedb.model.Ranks;
import com.advancedb.advancedb.service.RankService;

@RestController  // Marks this class as a RESTful controller
@RequestMapping("/")  // Maps requests to the root path
public class RankController {

    @Autowired  // Automatically injects the RankService bean into the controller
    RankService rankService;  // RankService is used to handle business logic for ranks

    @GetMapping("rank")  // Endpoint to retrieve all ranks
    public ResponseEntity<List<Ranks>> getAllRanks() {
        return rankService.getAllRanks();  // Delegates the call to the RankService to get all ranks
    }

    @PostMapping("/rank/add")  // Endpoint to add new ranks
    public ResponseEntity<String> addRanks(@RequestBody List<Ranks> rank) {
        return rankService.addRanks(rank);  // Delegates the call to the RankService to add ranks
    }

}
