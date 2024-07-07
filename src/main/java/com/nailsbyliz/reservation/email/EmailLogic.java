package com.nailsbyliz.reservation.email;

import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.util.TimeUtil;

public class EmailLogic {

        public static String createReservationEmailBody(ReservationEntity reservation) {
                int durationHours = reservation.getNailService().getDuration() / 60;
                int durationMinutes = reservation.getNailService().getDuration() % 60;

                String durationText;
                if (durationMinutes > 0) {
                        durationText = String.format("%d tuntia %d minuuttia", durationHours, durationMinutes);
                } else {
                        durationText = String.format("%d tuntia", durationHours);
                }

                return String.format(
                                "Varauksen tiedot:\n" +
                                                "Nimi: %s %s\n" +
                                                "Puhelin: %s\n" +
                                                "Sposti: %s\n" +
                                                "Osoite: %s, %s %s\n" +
                                                "Palvelu: %s\n" +
                                                "Hinta: %.2f EUR\n" +
                                                "Ajankohta: %s\n" +
                                                "Arvioitu kesto: %s\n",
                                reservation.getFName(), reservation.getLName(),
                                reservation.getPhone(),
                                reservation.getEmail(),
                                reservation.getAddress(), reservation.getCity(), reservation.getPostalcode(),
                                reservation.getNailService() != null ? reservation.getNailService().getType()
                                                : "Ei määritelty",
                                reservation.getPrice(),
                                TimeUtil.formatToHelsinkiTime(reservation.getStartTime()),
                                durationText);
        }

        public static String createReservationAdminEmailBody(ReservationEntity reservation) {
                int durationHours = reservation.getNailService().getDuration() / 60;
                int durationMinutes = reservation.getNailService().getDuration() % 60;

                String durationText;
                if (durationMinutes > 0) {
                        durationText = String.format("%d tuntia %d minuuttia", durationHours, durationMinutes);
                } else {
                        durationText = String.format("%d tuntia", durationHours);
                }

                return String.format(
                                "Varauksen tiedot:\n" +
                                                "Nimi: %s %s\n" +
                                                "Puhelin: %s\n" +
                                                "Sposti: %s\n" +
                                                "Osoite: %s, %s %s\n" +
                                                "Palvelu: %s\n" +
                                                "Hinta: %.2f EUR\n" +
                                                "Ajankohta: %s\n" +
                                                "Arvioitu kesto: %s\n" +
                                                "Varauksen status: %s\n",
                                reservation.getFName(), reservation.getLName(),
                                reservation.getPhone(),
                                reservation.getEmail(),
                                reservation.getAddress(), reservation.getCity(), reservation.getPostalcode(),
                                reservation.getNailService() != null ? reservation.getNailService().getType()
                                                : "Ei määritelty",
                                reservation.getPrice(),
                                TimeUtil.formatToHelsinkiTime(reservation.getStartTime()),
                                durationText, reservation.getStatus());
        }

        public static String createNewReservationEmail(ReservationEntity reservation) {
                return String.format(
                                "Hei, kiitos varauksestasi!\n\n" +
                                                createReservationEmailBody(reservation) +
                                                "\n"
                                                + "Varauksen paikka on Tikkurilassa Tikkuraitin vieressä. \nTarkka osoite ilmoitetaan teille varausta edeltävänä päivänä!\n\n");
        }

        public static String updatedReservationEmail(ReservationEntity originalReservation,
                        ReservationEntity updatedReservation) {
                return String.format(
                                "Hei, varaustanne (%s) on muutettu.\n\nVanhat tiedot:\n%s\nPäivitetyt tiedot:\n%s\nNähdään pian!",
                                TimeUtil.formatToHelsinkiTime(updatedReservation.getStartTime()),
                                createReservationEmailBody(originalReservation),
                                createReservationEmailBody(updatedReservation));
        }

        public static String updatedReservationAdminEmail(ReservationEntity originalReservation,
                        ReservationEntity updatedReservation) {
                return String.format(
                                "Varausta (%s) on muutettu.\n\nVanhat tiedot:\n%s\nPäivitetyt tiedot:\n%s",
                                TimeUtil.formatToHelsinkiTime(updatedReservation.getStartTime()),
                                createReservationAdminEmailBody(originalReservation),
                                createReservationAdminEmailBody(updatedReservation));
        }

        public static String createCancelledReservationEmail(ReservationEntity reservation) {
                return String.format(
                                "Hei, varauksenne on peruttu.\n\n" +
                                                createReservationEmailBody(reservation) +
                                                "\n"
                                                + "Mikäli teille tulee kysyttävää, olkaa yhteydessä info@nailsbyliz.fi.\n"
                                                + "Kiitos asioinnista, toivottavasti näemme pian!");
        }

        public static String getReservationEmailEnd() {
                return "Yhteyden otot ja kynsiehdotukset/ideat sähköpostitse info@nailsbyliz.fi tai <a href='https://www.instagram.com/nailsbyliz.fi'>Instagramissa @nailsbyliz.fi</a>";
        }

        public static String getEmailEnd() {
                return "Yhteyden otot sähköpostitse info@nailsbyliz.fi tai <a href='https://www.instagram.com/nailsbyliz.fi'>Instagramissa @nailsbyliz.fi</a>";
        }

        public static void sendNewReservationEmails(ReservationEntity reservation) {
                try {
                        String adminEmail = System.getenv("EMAIL_ADMIN");
                        // Sending email to customer if not admin
                        if (!reservation.getEmail().equals(adminEmail)) {
                                EmailSender.sendEmail(reservation.getEmail(),
                                                "Nailzbyliz varausvavhistus, " + reservation.getLName() + " "
                                                                + TimeUtil.formatToHelsinkiTime(
                                                                                reservation.getStartTime()),
                                                EmailLogic.createNewReservationEmail(reservation),
                                                EmailLogic.getReservationEmailEnd());
                        }
                        // Always send email to admin
                        EmailSender.sendEmail(adminEmail,
                                        "Uusi varaus, " + reservation.getLName() + " "
                                                        + TimeUtil.formatToHelsinkiTime(reservation.getStartTime()),
                                        EmailLogic.createReservationAdminEmailBody(reservation), "");
                } catch (Exception ex) {
                        System.out.println("Email wasn't sent");
                }
        }

        public static void sendCancelledReservationEmail(ReservationEntity reservation) {
                try {
                        String adminEmail = System.getenv("EMAIL_ADMIN");

                        if (!reservation.getEmail().equalsIgnoreCase(adminEmail)) {
                                EmailSender.sendEmail(reservation.getEmail(), "Varauksenne "
                                                + TimeUtil.formatToHelsinkiTime(reservation.getStartTime())
                                                + " on peruttu.",
                                                createCancelledReservationEmail(
                                                                reservation),
                                                getEmailEnd());
                        }
                        // Send an update to ADMIN
                        EmailSender.sendEmail(adminEmail,
                                        "Varaus peruttu, " + reservation.getLName()
                                                        + TimeUtil.formatToHelsinkiTime(
                                                                        reservation.getStartTime()),
                                        createReservationAdminEmailBody(reservation),
                                        null);
                } catch (Exception ex) {
                        System.out.println("Email wasnt sent");
                }

        }

        public static void sendEditedReservationEmail(ReservationEntity originalReservation,
                        ReservationEntity editedReservation) {
                if (editedReservation != null) {
                        String adminEmail = System.getenv("EMAIL_ADMIN");
                        try {
                                if (!editedReservation.getEmail().equalsIgnoreCase(adminEmail)) {
                                        EmailSender.sendEmail(editedReservation.getEmail(),
                                                        "Varuksenne tietoja muutettu, " + editedReservation.getLName()
                                                                        + ", "
                                                                        + TimeUtil.formatToHelsinkiTime(
                                                                                        originalReservation
                                                                                                        .getStartTime()),
                                                        updatedReservationEmail(originalReservation,
                                                                        editedReservation),
                                                        getReservationEmailEnd());
                                }
                                // Send an update to ADMIN
                                EmailSender.sendEmail(adminEmail,
                                                "Varausta muutettu, " + editedReservation.getLName()
                                                                + TimeUtil.formatToHelsinkiTime(
                                                                                originalReservation.getStartTime()),
                                                updatedReservationAdminEmail(originalReservation,
                                                                editedReservation),
                                                null);
                        } catch (Exception ex) {
                                System.out.println("Email wasnt sent");
                        }
                }
        }

        public static void sendRegistrationEmail(AppUserEntity newAppUser) {
                try {
                        EmailSender.sendEmail(newAppUser.getEmail(),
                                        "Nailzbyliz.fi rekisteröinti, " + newAppUser.getUsername(),
                                        "Hei, käyttäjänne on nyt rekisteröity. Jos et itse luonut tätä käyttäjää, laita sähköpostia osoitteeseen info@nailsbyliz.fi, ja poistamme tilin.",
                                        null);
                } catch (Exception ex) {
                        System.out.println("Email wasnt sent");
                }
        }

        public static void sendDeleteUserEmail(AppUserEntity deletedUser) {
                try {
                        EmailSender.sendEmail(deletedUser.getEmail(),
                                        "Nailzbyliz.fi käyttäjänne " + deletedUser.getUsername() + " on poistettu.",
                                        "Käyttäjänne on nyt poistettu. Harmi ettette halua enää asioida kanssamme, kiitos yhteisistä hetkistä!",
                                        null);
                } catch (Exception ex) {
                        System.out.println("Email wasnt sent");
                }
        }
}
