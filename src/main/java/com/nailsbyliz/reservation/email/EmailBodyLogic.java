package com.nailsbyliz.reservation.email;

import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.util.TimeUtil;

public class EmailBodyLogic {

        public static String createReservationEmailBody(ReservationEntity reservation) {
                return String.format(
                                "Varauksen tiedot:\n" +
                                                "Nimi: %s %s\n" +
                                                "Puhelin: %s\n" +
                                                "Sposti: %s\n" +
                                                "Osoite: %s, %s %s\n" +
                                                "Palvelu: %s\n" +
                                                "Hinta: %.2f EUR\n" +
                                                "Ajankohta: %s\n" +
                                                "Arvioitu kesto: %d tuntia %d minuuttia\n",
                                reservation.getFName(), reservation.getLName(),
                                reservation.getPhone(),
                                reservation.getEmail(),
                                reservation.getAddress(), reservation.getCity(), reservation.getPostalcode(),
                                reservation.getNailService() != null ? reservation.getNailService().getType()
                                                : "Ei määritelty",
                                reservation.getPrice(),
                                TimeUtil.formatToHelsinkiTime(reservation.getStartTime()),
                                reservation.getNailService().getDuration() / 60,
                                reservation.getNailService().getDuration() % 60) + "\n\n";
        }

        public static String createNewReservationEmail(ReservationEntity reservation) {
                return String.format(
                                "Hei, kiitos varauksestasi!\n\n" +
                                                createReservationEmailBody(reservation) +
                                                "\n\n"
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

        public static String getEmailEnd() {
                return "Yhteyden otot ja kynsiehdotukset/ideat sähköpostitse info@nailsbyliz.fi tai <a href='https://www.instagram.com/nailsbyliz.fi'>Instagramissa @nailsbyliz.fi</a>";
        }
}
