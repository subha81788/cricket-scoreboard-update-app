package org.subhashis.cricketscoreboardupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.subhashis.cricketscoreboardupdate.service.Play;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CricketScoreboardUpdateApplication extends SpringBootServletInitializer {

	@Autowired
	private Play play;

	@PostConstruct
	public void listen() {
		play.startGame();
	}

	public static void main(String[] args) {
		SpringApplication.run(CricketScoreboardUpdateApplication.class, args);
		System.out.println("Application started...");
	}

}