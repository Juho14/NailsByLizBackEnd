package com.nailsbyliz.reservation.email;

import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.util.TimeUtil;

public class EmailBodyLogic {

        public static String createReservationEmailBody(ReservationEntity reservation) {
                return String.format(
                                "Varauksen tiedot:\n" +
                                                "Nimi: %s %s\n" +
                                                "Puhelin: %s\n" +
                                                "Osoite: %s, %s %s\n" +
                                                "Palvelu: %s\n" +
                                                "Hinta: %.2f EUR\n" +
                                                "Aloitusaika: %s\n" +
                                                "Lopetusaika: %s\n" +
                                                "Tila: %s\n",
                                reservation.getFName(), reservation.getLName(),
                                reservation.getPhone(),
                                reservation.getAddress(), reservation.getCity(), reservation.getPostalcode(),
                                reservation.getNailService() != null ? reservation.getNailService().getType()
                                                : "Ei määritelty",
                                reservation.getPrice(),
                                TimeUtil.formatToHelsinkiTime(reservation.getStartTime()),
                                TimeUtil.formatToHelsinkiTime(reservation.getEndTime()),
                                reservation.getStatus());
        }

        public static String createNewReservationEmail(ReservationEntity reservation) {
                return String.format(
                                "Hei, kiitos varauksestasi!\n\n%s\nNähdään pian!",
                                createReservationEmailBody(reservation));
        }

        public static String updatedReservationEmail(ReservationEntity originalReservation,
                        ReservationEntity updatedReservation) {
                return String.format(
                                "Hei, varaustanne (%s) on muutettu.\n\nVanhat tiedot:\n%s\nPäivitetyt tiedot:\n%s\nNähdään pian!",
                                TimeUtil.formatToHelsinkiTime(updatedReservation.getStartTime()),
                                createReservationEmailBody(originalReservation),
                                createReservationEmailBody(updatedReservation));
        }
}