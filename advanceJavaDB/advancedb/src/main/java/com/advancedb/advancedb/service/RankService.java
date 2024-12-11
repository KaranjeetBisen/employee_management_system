package com.advancedb.advancedb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.advancedb.advancedb.model.Ranks;
import com.advancedb.advancedb.repository.RankRepository;

@Service
public class RankService {

    @Autowired
    RankRepository rankRepository;

    public ResponseEntity<List<Ranks>> getAllRanks() {
        try {
            return new ResponseEntity<>(rankRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.getMessage();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addRanks(List<Ranks> rank) {
        try {
            rankRepository.saveAll(rank);
            return new ResponseEntity<>("ranks added successfully!!", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getMessage();
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);

    }

}