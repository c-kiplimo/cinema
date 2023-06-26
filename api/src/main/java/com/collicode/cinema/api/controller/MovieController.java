package com.collicode.cinema.api.controller;


import com.collicode.cinema.api.dto.MovieDTO;
import com.collicode.cinema.api.model.Movie;
import com.collicode.cinema.api.service.MovieService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "api/movie")
public class MovieController {
	
	@Autowired
	MovieService movieService;
	
	
	@RequestMapping(value = "/createMovie", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createMeal(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
		
		System.out.println(request.getParameter("movie"));
		Gson g = new Gson();  
		Movie movie = g.fromJson(request.getParameter("movie"), Movie.class);
		
		String responseToClient;
		responseToClient = movieService.isValidInput(movie);
		if (responseToClient.equals("valid")) {
			
			try {
				movie.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
				movie.setImageName(image.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
			//meal.setImage(Base64.getEncoder().encodeToString(file.getBytes()))
			movieService.save(movie);
			return new ResponseEntity<String>(responseToClient, HttpStatus.OK);

		} else {
			responseToClient = "invalid";
			return new ResponseEntity<String>(responseToClient, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/updateMovie", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editMeal(@RequestBody Movie movie){
		String response = movieService.editMovie(movie);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getAllMovies", method = RequestMethod.GET)
		public ResponseEntity<List<MovieDTO>> getAllMovies(){
			List<MovieDTO> moviesDTO = movieService.findAll();
			return new ResponseEntity<List<MovieDTO>>(moviesDTO, HttpStatus.OK);
		}
	
	@RequestMapping(value = "/deleteMovie/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable Long id) {
		String responseToClient;
		Movie movie = movieService.findOne(id);
		if (movie == null) {
			responseToClient = "fail";
			return new ResponseEntity<String>(responseToClient, HttpStatus.OK);
		}
		movieService.delete(movie);
		responseToClient = "success";
		return new ResponseEntity<String>(responseToClient, HttpStatus.OK);
	}


}
