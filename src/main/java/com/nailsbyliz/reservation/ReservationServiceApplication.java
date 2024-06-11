package com.nailsbyliz.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReservationServiceApplication {

	// private static final Logger log =
	// LoggerFactory.getLogger(ReservationServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
	/*
	 * @Bean
	 * public CommandLineRunner dataLoader(AppUserRepository userRepo,
	 * AppUserService appUserService,
	 * NailServiceRepository nailRepo,
	 * ReservationRepository reservationRepo, ReservationService rService,
	 * ReservationSettingsRepository rSettingsRepository) {
	 * return (args) -> {
	 * log.info("Setting the data for reservations.");
	 * 
	 * AppUserEntity admin = new AppUserEntity("Liz", "Meowee", "Renate",
	 * "0450987654",
	 * "liz.meowee@hotmail.com", "Kissala 12A 42", "45741", "Vantaa", "Testi",
	 * "ROLE_ADMIN");
	 * AppUserEntity dev = new AppUserEntity("Test", "Dev", "TestDev", "0451234567",
	 * "TestDev@gmail.com", "TestStreet 12A 2", "21039", "Testtown", "TestDev",
	 * "ROLE_ADMIN");
	 * 
	 * AppUserEntity testCustomer = new AppUserEntity("Customer", "Caleb",
	 * "Customer", "12345678790",
	 * "Customer@gmail.com", "Customer hills", "84848", "Customeria", "Customer",
	 * "ROLE_USER");
	 * 
	 * // public AppUserEntity(String fName, String lName, String username,
	 * // String phone, String email,
	 * // String address, String postalcode, String city, String passwordHash,
	 * String
	 * // role)
	 * appUserService.createUser(admin);
	 * appUserService.createUser(dev);
	 * appUserService.createUser(testCustomer);
	 * NailServiceEntity gelPolish = new NailServiceEntity("Geelilakkaus", 180, 35,
	 * false);
	 * NailServiceEntity gelMaintenance = new NailServiceEntity("Geelihuolto",
	 * 360, 80, false);
	 * NailServiceEntity gelRemoval = new NailServiceEntity("Geelilakan poisto",
	 * 120, 20,
	 * false);
	 * NailServiceEntity dayOff = new NailServiceEntity("DayOff", 420, 0, true);
	 * nailRepo.save(gelPolish);
	 * nailRepo.save(gelMaintenance);
	 * nailRepo.save(gelRemoval);
	 * nailRepo.save(dayOff);
	 * 
	 * LocalTime openingHour = LocalTime.of(8, 0);
	 * LocalTime closingHour = LocalTime.of(15, 0);
	 * ReservationSettings rSettings = new ReservationSettings("OpeningHours",
	 * openingHour, closingHour, true);
	 * rSettingsRepository.save(rSettings);
	 * 
	 * LocalDateTime firstAppointment = LocalDateTime.of(2024, 6, 21, 11, 0);
	 * LocalDateTime secondAppointment = LocalDateTime.of(2024, 6, 23, 13, 30);
	 * LocalDateTime thirdAppointment = LocalDateTime.of(2024, 6, 25, 10, 30);
	 * 
	 * // Create reservations for each type of maintenance
	 * ReservationEntity reservation1 = new ReservationEntity();
	 * reservation1.setFName("John");
	 * reservation1.setLName("Doe");
	 * reservation1.setEmail("john.doe@example.com");
	 * reservation1.setPhone("1239874658");
	 * reservation1.setAddress("Mikonkatu 2 A 34");
	 * reservation1.setCity("Helsinki");
	 * reservation1.setPostalcode("00100");
	 * reservation1.setStartTime(firstAppointment);
	 * reservation1.setNailService(gelPolish);
	 * reservation1.setPrice(gelPolish.getPrice());
	 * reservation1.setCustomerId(null);
	 * reservation1.setStatus("OK");
	 * 
	 * ReservationEntity reservation2 = new ReservationEntity();
	 * reservation2.setFName("Jane");
	 * reservation2.setLName("Smith");
	 * reservation2.setEmail("jane.smith@example.com");
	 * reservation2.setPhone("9348877193");
	 * reservation2.setAddress("Tiistiläntie 7 D 85");
	 * reservation2.setCity("Espoo");
	 * reservation2.setPostalcode("02210");
	 * reservation2.setStartTime(secondAppointment);
	 * reservation2.setNailService(gelMaintenance);
	 * reservation2.setPrice(gelMaintenance.getPrice());
	 * reservation2.setCustomerId(null);
	 * reservation2.setStatus("OK");
	 * 
	 * ReservationEntity reservation3 = new ReservationEntity();
	 * reservation3.setFName("Alice");
	 * reservation3.setLName("Johnson");
	 * reservation3.setEmail("alice.johnson@example.com");
	 * reservation3.setPhone("9812983722");
	 * reservation3.setAddress("Ratapihantie 2 C 42");
	 * reservation3.setCity("Vantaa");
	 * reservation3.setPostalcode("01300");
	 * reservation3.setStartTime(thirdAppointment);
	 * reservation3.setNailService(gelRemoval);
	 * reservation3.setPrice(gelRemoval.getPrice());
	 * reservation3.setCustomerId(null);
	 * reservation3.setStatus("OK");
	 * 
	 * // Save reservations
	 * rService.saveReservation(reservation1);
	 * rService.saveReservation(reservation2);
	 * rService.saveReservation(reservation3);
	 * 
	 * log.info("Saving all data.");
	 * 
	 * for (AppUserEntity appUser : userRepo.findAll()) {
	 * log.info(appUser.toString());
	 * }
	 * 
	 * for (NailServiceEntity nailService : nailRepo.findAll()) {
	 * log.info(nailService.toString());
	 * }
	 * 
	 * for (ReservationSettings reservationSettings : rSettingsRepository.findAll())
	 * {
	 * log.info(reservationSettings.toString());
	 * }
	 * 
	 * for (ReservationEntity reservation : reservationRepo.findAll()) {
	 * log.info(reservation.toString());
	 * }
	 * };
	 * }
	 */

}
