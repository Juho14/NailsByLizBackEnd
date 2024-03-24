package com.nailsbyliz.reservation;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.domain.ReservationSettings;
import com.nailsbyliz.reservation.repositories.AppUserRepository;
import com.nailsbyliz.reservation.repositories.NailServiceRepository;
import com.nailsbyliz.reservation.repositories.ReservationRepository;
import com.nailsbyliz.reservation.repositories.ReservationSettingsRepository;
import com.nailsbyliz.reservation.service.AppUserService;
import com.nailsbyliz.reservation.service.ReservationService;

@SpringBootApplication
public class ReservationServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(ReservationServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(AppUserRepository userRepo, AppUserService appUserService,
			NailServiceRepository nailRepo,
			ReservationRepository reservationRepo, ReservationService rService,
			ReservationSettingsRepository rSettingsRepository) {
		return (args) -> {
			log.info("Setting the data for reservations.");

			AppUserEntity admin = new AppUserEntity("Liz", "Meowee", "Renate", "0450987654",
					"liz.meowee@hotmail.com", "Testi",
					"ROLE_ADMIN");
			AppUserEntity dev = new AppUserEntity("Test", "Dev", "TestDev", "0451234567", "TestDev@gmail.com", "Test",
					"ROLE_ADMIN");

			AppUserEntity testCustomer = new AppUserEntity("Customer", "Caleb", "Customer", "12345678790",
					"Customer@gmail.com", "Test",
					"USER");
			appUserService.createUser(admin);
			appUserService.createUser(dev);
			appUserService.createUser(testCustomer);
			NailServiceEntity gelPolish = new NailServiceEntity("GelPolish", 180, 35, false);
			NailServiceEntity gelMaintenance = new NailServiceEntity("GelMaintenance", 360, 80, false);
			NailServiceEntity gelRemoval = new NailServiceEntity("GelRemoval", 120, 20, false);
			NailServiceEntity dayOff = new NailServiceEntity("DayOff", 420, 0, true);
			nailRepo.save(gelPolish);
			nailRepo.save(gelMaintenance);
			nailRepo.save(gelRemoval);
			nailRepo.save(dayOff);

			LocalTime openingHour = LocalTime.of(11, 0);
			LocalTime closingHour = LocalTime.of(18, 0);
			ReservationSettings rSettings = new ReservationSettings("OpeningHours", openingHour, closingHour, true);
			rSettingsRepository.save(rSettings);

			LocalDateTime firstAppointment = LocalDateTime.of(2024, 3, 21, 11, 0);
			LocalDateTime secondAppointment = LocalDateTime.of(2024, 3, 23, 13, 30);
			LocalDateTime thirdAppointment = LocalDateTime.of(2024, 3, 25, 10, 15);

			// Create reservations for each type of maintenance
			ReservationEntity reservation1 = new ReservationEntity();
			reservation1.setFName("John");
			reservation1.setLName("Doe");
			reservation1.setEmail("john.doe@example.com");
			reservation1.setPhone("1239874658");
			reservation1.setStartTime(firstAppointment);
			reservation1.setNailService(gelPolish);
			reservation1.setStatus("OK");

			ReservationEntity reservation2 = new ReservationEntity();
			reservation2.setFName("Jane");
			reservation2.setLName("Smith");
			reservation2.setEmail("jane.smith@example.com");
			reservation2.setPhone("9348877193");
			reservation2.setStartTime(secondAppointment);
			reservation2.setNailService(gelMaintenance);
			reservation2.setStatus("OK");

			ReservationEntity reservation3 = new ReservationEntity();
			reservation3.setFName("Alice");
			reservation3.setLName("Johnson");
			reservation3.setEmail("alice.johnson@example.com");
			reservation3.setPhone("9812983722");
			reservation3.setStartTime(thirdAppointment);
			reservation3.setNailService(gelRemoval);
			reservation3.setStatus("OK");

			// Save reservations
			rService.saveReservation(reservation1);
			rService.saveReservation(reservation2);
			rService.saveReservation(reservation3);

			log.info("Saving all data.");

			for (AppUserEntity appUser : userRepo.findAll()) {
				log.info(appUser.toString());
			}

			for (NailServiceEntity nailService : nailRepo.findAll()) {
				log.info(nailService.toString());
			}

			for (ReservationSettings reservationSettings : rSettingsRepository.findAll()) {
				log.info(reservationSettings.toString());
			}

			for (ReservationEntity reservation : reservationRepo.findAll()) {
				log.info(reservation.toString());
			}
		};
	}

}
