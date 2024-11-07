import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author K2425199
 */
public class AuroraSkinCareSystem {

    private static final double REGISTRATION_FEE = 500.00;
    private static final double TAX_RATE = 0.10;
    private static final Map<String, String[]> consultationHours = new HashMap<>();
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private static HashMap<Integer, Appointment> appointments = new HashMap<>();
    private static HashMap<String, Treatment> treatments = new HashMap<>();
    private static int appointmentCounter = 0;

    public static void main(String[] args) {
        initializeConsultationHours();
        Scanner scanner = new Scanner(System.in);
        doctorTreatment();
        boolean running = true;

        while (running) {
            try {
                System.out.println("============================================================");
                System.out.println("                Welcome to Aurora Skin Care                  ");
                System.out.println("============================================================");
                System.out.println();
                System.out.println("        1. Book Appointment");
                System.out.println("        2. Update Appointment");
                System.out.println("        3. View Appointments by Date");
                System.out.println("        4. Search Appointment");
                System.out.println("        5. Pay and Generate Invoice");
                System.out.println("        6. Update Customer Details");
                System.out.println("        7. Exit");
                System.out.println();
                System.out.print("Please Enter Your Choice : ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        bookAppointment(scanner);
                        break;
                    case 2:
                        updateAppointment(scanner);
                        break;
                    case 3:
                        viewAppointmentsByDate(scanner);
                        break;
                    case 4:
                        searchAppointment(scanner);
                        break;
                    case 5:
                        generateInvoice(scanner);
                        break;
                    case 6:
                        updateCustomerDetails(scanner);
                        break;
                    case 7:
                        System.out.println("Thank you for visiting Aurora Skin Care. Goodbye!");
                        running = false; // Ends the loop
                        break;
                    default:
                        System.out.println("Invalid option! Please enter a number between 1 and 7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Clear the invalid input
            }
        }
        scanner.close();
    }

    private static void bookAppointment(Scanner scanner) {
        try {
            String nic;
            while (true) {
                System.out.print("            Enter Patient NIC (format: 9 digits followed by 'V'): ");
                nic = scanner.next();
                if (isValidNIC(nic)) break;
                else System.out.println("            Invalid NIC format. Please enter a valid NIC (e.g., 112233445V).");
            }

            System.out.print("            Enter Patient Name: ");
            String name = scanner.next();

            String email;
            while (true) {
                System.out.print("            Enter Patient Email: ");
                email = scanner.next();
                if (isValidEmail(email)) break;
                else
                    System.out.println("            Invalid email format. Please enter a valid email (e.g., sample@gmail.com).");
            }

            String phone;
            while (true) {
                System.out.print("            Enter Patient Phone: ");
                phone = scanner.next();
                if (isValidPhoneNumber(phone)) break;
                else System.out.println("            Invalid phone number. Please enter a 10-digit phone number.");
            }

            Patient patient = new Patient(nic, name, email, phone);

            System.out.println("            Available Doctors: ");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i));
            }
            System.out.print("            Choose a doctor by number: ");
            int doctorChoice = scanner.nextInt();
            if (doctorChoice < 1 || doctorChoice > doctors.size())
                throw new IndexOutOfBoundsException("Invalid doctor selection.");
            Doctor doctor = doctors.get(doctorChoice - 1);

            boolean slotAvailable = false;
            Date appointmentDate = null;
            String timeSlot = null;

            while (!slotAvailable) {
                System.out.print("            Enter Appointment Date (dd-MM-yyyy): ");
                String dateStr = scanner.next();
                appointmentDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);

                System.out.print("            Enter Time Slot (e.g., 10:00am-10:15am): ");
                timeSlot = scanner.next();

                if (!isValidConsultationTime(appointmentDate, timeSlot)) {
                    System.out.println("            Invalid date or time slot. Please select a correct consultation day and time.");
                    continue;
                }

                if (isDoctorBooked(doctor, appointmentDate, timeSlot)) {
                    System.out.println("            Doctor " + doctor.getName() + " is already booked for " + timeSlot + " on " + dateStr + ".");
                    System.out.println("            Please select another date and time.");
                } else {
                    slotAvailable = true;
                }
            }

            Appointment appointment = new Appointment(++appointmentCounter, patient, doctor, appointmentDate, timeSlot);
            appointments.put(appointment.getAppointmentId(), appointment);
            System.out.printf("            Appointment booked successfully! Registration fee: Rs. %.2f%n", REGISTRATION_FEE);
        } catch (ParseException e) {
            System.out.println("            Invalid date format. Please enter the date in dd-MM-yyyy format.");
        } catch (InputMismatchException e) {
            System.out.println("            Invalid input. Please try again.");
            scanner.next();
        }
    }

    private static void updateAppointment(Scanner scanner) {
        try {
            System.out.print("            Enter Appointment ID to update: ");
            int appointmentId = scanner.nextInt();
            Appointment appointment = appointments.get(appointmentId);

            if (appointment == null) throw new NoSuchElementException("Invalid Appointment ID.");

            System.out.print("            Enter new Appointment Date (dd-MM-yyyy): ");
            String dateStr = scanner.next();
            Date newDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);

            System.out.print("            Enter new Time Slot: ");
            String newTimeSlot = scanner.next();
            if (isDoctorBooked(appointment.getDoctor(), newDate, newTimeSlot)) {
                System.out.println("            Doctor " + appointment.getDoctor().getName() + " is already booked for this slot.");
            } else {
                appointment.setAppointmentDate(newDate);
                appointment.setTimeSlot(newTimeSlot);
                System.out.println("            Appointment updated successfully!");
            }
        } catch (ParseException | NoSuchElementException e) {
            System.out.println("            " + e.getMessage());
            scanner.next();
        }
    }

    private static void viewAppointmentsByDate(Scanner scanner) {
        System.out.print("            Enter Date (dd-MM-yyyy) to view appointments: ");
        String dateStr = scanner.next();
        Date filterDate;
        try {
            filterDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
            boolean hasAppointments = false;
            System.out.println("============================================================");
            System.out.println("                      Appointments on " + dateStr);
            System.out.println("============================================================");
            for (Appointment appointment : appointments.values()) {
                if (appointment.getAppointmentDate().equals(filterDate)) {
                    System.out.println("------------------------------------------------------------");
                    System.out.printf("  ID: %-5d  Patient: %-15s  Doctor: %-15s%n",
                            appointment.getAppointmentId(), appointment.getPatient().getName(), appointment.getDoctor().getName());
                    hasAppointments = true;
                }
            }
            if (!hasAppointments) System.out.println("            No appointments found for this date.");
        } catch (ParseException e) {
            System.out.println("            Invalid date format. Please enter in dd-MM-yyyy format.");
        }
    }

    private static void searchAppointment(Scanner scanner) {
        System.out.print("            Enter Appointment ID or Patient Name to search: ");
        String input = scanner.next();
        boolean found = false;

        for (Appointment appointment : appointments.values()) {
            if (String.valueOf(appointment.getAppointmentId()).equals(input) || appointment.getPatient().getName().equalsIgnoreCase(input)) {
                System.out.println("------------------------------------------------------------");
                System.out.printf("  ID: %-5d  Patient: %-15s  Doctor: %-15s%n",
                        appointment.getAppointmentId(), appointment.getPatient().getName(), appointment.getDoctor().getName());
                found = true;
            }
        }
        if (!found) System.out.println("            No matching appointment found.");
    }

    private static void generateInvoice(Scanner scanner) {
        try {
            System.out.print("            Enter Appointment ID: ");
            int appointmentId = scanner.nextInt();
            Appointment appointment = appointments.get(appointmentId);
            if (appointment == null) throw new NoSuchElementException("Invalid Appointment ID.");

            System.out.println("            Available Treatments: ");
            List<String> treatmentKeys = new ArrayList<>(treatments.keySet());
            for (int i = 0; i < treatmentKeys.size(); i++) {
                System.out.println((i + 1) + ". " + treatments.get(treatmentKeys.get(i)));
            }

            System.out.print("            Choose a treatment by number: ");
            int treatmentChoice = scanner.nextInt();
            if (treatmentChoice < 1 || treatmentChoice > treatmentKeys.size())
                throw new IndexOutOfBoundsException("Invalid treatment selection.");

            Treatment treatment = treatments.get(treatmentKeys.get(treatmentChoice - 1));
            double totalCost = REGISTRATION_FEE + treatment.getCost();
            double taxAmount = totalCost * TAX_RATE;
            double finalAmount = totalCost + taxAmount;

            System.out.println("============================================================");
            System.out.println("                       Invoice");
            System.out.println("============================================================");
            System.out.printf("  Appointment ID: %-5d%n", appointmentId);
            System.out.printf("  Patient Name: %-15s%n", appointment.getPatient().getName());
            System.out.printf("  Doctor: %-15s%n", appointment.getDoctor().getName());
            System.out.printf("  Treatment: %-15s  Cost: Rs. %.2f%n", treatment.getName(), treatment.getCost());
            System.out.printf("  Registration Fee: Rs. %.2f%n", REGISTRATION_FEE);
            System.out.printf("  Tax (%.2f%%): Rs. %.2f%n", TAX_RATE * 100, taxAmount);
            System.out.printf("  Total Amount: Rs. %.2f%n", finalAmount);
        } catch (NoSuchElementException e) {
            System.out.println("            " + e.getMessage());
        }
    }

    private static void updateCustomerDetails(Scanner scanner) {
        System.out.print("            Enter Appointment ID to update details: ");
        int appointmentId = scanner.nextInt();
        Appointment appointment = appointments.get(appointmentId);

        if (appointment == null) {
            System.out.println("            Invalid Appointment ID.");
            return;
        }

        System.out.print("            Enter new Patient Name: ");
        String newName = scanner.next();
        System.out.print("            Enter new Email: ");
        String newEmail = scanner.next();
        System.out.print("            Enter new Phone: ");
        String newPhone = scanner.next();

        appointment.getPatient().setName(newName);
        appointment.getPatient().setEmail(newEmail);
        appointment.getPatient().setPhone(newPhone);

        System.out.println("            Customer details updated successfully!");
    }

    private static boolean isValidNIC(String nic) {
        return nic.matches("\\d{9}V");
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^\\S+@\\S+\\.\\S+$");
    }

    private static boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }

    private static boolean isDoctorBooked(Doctor doctor, Date date, String timeSlot) {
        return appointments.values().stream()
                .anyMatch(a -> a.getDoctor().equals(doctor) && a.getAppointmentDate().equals(date) && a.getTimeSlot().equalsIgnoreCase(timeSlot));
    }

    private static boolean isValidConsultationTime(Date date, String timeSlot) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String day = switch (dayOfWeek) {
            case Calendar.MONDAY -> "Monday";
            case Calendar.WEDNESDAY -> "Wednesday";
            case Calendar.FRIDAY -> "Friday";
            case Calendar.SATURDAY -> "Saturday";
            default -> null;
        };

        if (day == null || !consultationHours.containsKey(day)) {
            return false;
        }

        for (String slot : consultationHours.get(day)) {
            if (slot.equalsIgnoreCase(timeSlot)) {
                return true;
            }
        }
        return false;
    }

    private static void initializeConsultationHours() {
        consultationHours.put("Monday", new String[]{"10:00am-10:15am", "10:15am-10:30am", "10:30am-10:45am",
                "10:45am-11:00am", "11:00am-11:15am", "11:15am-11:30am",
                "11:30am-11:45am", "11:45am-12:00pm", "12:00pm-12:15pm",
                "12:15pm-12:30pm", "12:30pm-12:45pm", "12:45pm-01:00pm"});
        consultationHours.put("Wednesday", new String[]{"02:00pm-02:15pm", "02:15pm-02:30pm", "02:30pm-02:45pm",
                "02:45pm-03:00pm", "03:00pm-03:15pm", "03:15pm-03:30pm",
                "03:30pm-03:45pm", "03:45pm-04:00pm", "04:00pm-04:15pm",
                "04:15pm-04:30pm", "04:30pm-04:45pm", "04:45pm-05:00pm"});
        consultationHours.put("Friday", new String[]{"04:00pm-04:15pm", "04:15pm-04:30pm", "04:30pm-04:45pm",
                "04:45pm-05:00pm", "05:00pm-05:15pm", "05:15pm-05:30pm",
                "05:30pm-05:45pm", "05:45pm-06:00pm", "06:00pm-06:15pm",
                "06:15pm-06:30pm", "06:30pm-06:45pm", "06:45pm-07:00pm",
                "07:00pm-07:15pm", "07:15pm-07:30pm", "07:30pm-07:45pm",
                "07:45pm-08:00pm"});
        consultationHours.put("Saturday", new String[]{"09:00am-09:15am", "09:15am-09:30am", "09:30am-09:45am",
                "09:45am-10:00am", "10:00am-10:15am", "10:15am-10:30am",
                "10:30am-10:45am", "10:45am-11:00am", "11:00am-11:15am",
                "11:15am-11:30am", "11:30am-11:45am", "11:45am-12:00pm",
                "12:00pm-12:15pm", "12:15pm-12:30pm", "12:30pm-12:45pm",
                "12:45pm-01:00pm"});
    }

    private static void doctorTreatment() {
        doctors.add(new Doctor("Dr. Nishadi Gunasekara", "Skin Specialist"));
        doctors.add(new Doctor("Dr. Prasad Devaka", "Dermatologist"));
        treatments.put("Acne Treatment", new Treatment("Acne Treatment", 2750.00));
        treatments.put("Skin Whitening", new Treatment("Skin Whitening", 7650.00));
        treatments.put("Mole Removal", new Treatment("Mole Removal", 3850.00));
        treatments.put("Laser Treatment", new Treatment("Laser Treatment", 12500.00));
    }
}
