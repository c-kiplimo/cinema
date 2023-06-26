package com.collicode.cinema.api.service;


import com.collicode.cinema.api.dto.MovieDTO;
import com.collicode.cinema.api.model.Movie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MovieService {
	
	String isValidInput(Movie movie);
	List<MovieDTO> findAll();
	Movie save(Movie movie);
	String editMovie(Movie movie);
	Movie delete(Movie movie);
    Movie findOne(Long id);
//	

}
