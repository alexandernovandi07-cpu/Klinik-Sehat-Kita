package klinik;

import java.io.Serializable;

// WEEK 11: Custom Checked Exception
class InvalidPatientDataException extends Exception {
    public InvalidPatientDataException(String message) {
        super(message);
    }
}

// WEEK 7: Interface
interface Billable {
    double ADMIN_FEE = 15000.0;

    double calculateTotalBill();

    default String getCurrencyFormat(double amount) {
        return String.format("Rp %,.0f", amount);
    }
}