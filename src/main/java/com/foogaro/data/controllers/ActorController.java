package com.foogaro.data.controllers;

import com.foogaro.data.models.Actor;
import com.foogaro.data.services.ActorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/actors")
public class ActorController {

	private final ActorService service;

	public ActorController(ActorService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Actor>> getAll() {
		try {
			List<Actor> list = service.findAll();

			if (list.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Actor> getById(@PathVariable("id") long id) {
		Optional<Actor> data = service.findById(id);
		return data.map(actor -> new ResponseEntity<>(actor, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/")
	public ResponseEntity<Actor> create(@RequestBody Actor actor) {
		try {
			Actor _actor = service.save(new Actor(actor.getFirstname(),actor.getLastname(), actor.getYearOfBirth()));
			return new ResponseEntity<>(_actor, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Actor> update(@PathVariable("id") long id, @RequestBody Actor actor) {
		Optional<Actor> data = service.findById(id);

		if (data.isPresent()) {
			Actor _actor = data.get();
			_actor.setFirstname(actor.getFirstname());
			_actor.setLastname(actor.getLastname());
			_actor.setYearOfBirth(actor.getYearOfBirth());
			return new ResponseEntity<>(service.save(_actor), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
		try {
			service.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/")
	public ResponseEntity<HttpStatus> deleteAll() {
		try {
			service.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
