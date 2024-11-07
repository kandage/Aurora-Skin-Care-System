import java.util.Date;

/**
 * @author K2425199
 */
class Appointment {
    private int appointmentId;
    private Patient patient;
    private Doctor doctor;
    private Date appointmentDate;
    private String timeSlot;

    public Appointment(int appointmentId, Patient patient, Doctor doctor, Date appointmentDate, String timeSlot) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
