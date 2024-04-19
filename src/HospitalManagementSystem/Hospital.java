package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Hospital {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="Chirag@125055";
    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor= new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter Your Choice: ");
                int choice= scanner.nextInt();
                switch (choice){
                    case 1:
                        // Add Patient
                        patient.addPatient();
                    case 2:
                        // View Patients
                        patient.viewPatient();
                    case 3:
                        //View Doctors
                        doctor.viewDoctors();
                    case 4:
                        // Book Appointment
                        bookappointment(patient,doctor,connection,scanner);
                    case 5:
                        return;

                    default:
                        System.out.println("Enter Valid Choice!!!");


                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
   public static void bookappointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
       System.out.println("Enter Patient ID");
       int patientId= scanner.nextInt();
       System.out.println("Enter Doctor ID");
       int doctorId = scanner.nextInt();
       System.out.println("Enter Appointment Date (YYYY-MM-DD)");
       String appointmentdate = scanner.next();
       if(patient.getPatientById(patientId) && doctor.getDoctorsById(doctorId)){
       if(checkDoctorAvailability(doctorId,appointmentdate,connection)){
           String appointmentQuery="Insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
           try{
               PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
               preparedStatement.setInt(1,patientId);
               preparedStatement.setInt(2,doctorId);
               preparedStatement.setString(3,appointmentdate);
               int rowsAffected= preparedStatement.executeUpdate();
               if(rowsAffected>0){
                   System.out.println("Appointment Booked");
               }else{
                   System.out.println("Failed to Book Appointment");
               }
           }catch (SQLException e){
               e.printStackTrace();
           }


       }
       }else {
           System.out.println("Doctor Not Availaible on this Date");

           System.out.println("Either Doctor or Patient doesn't Exist!!!");
       }
   }
   public  static boolean checkDoctorAvailability(int doctorId,String appointmentDate, Connection connection){
   String  query= "Select count(*) from appointments where doctor_id =? AND appointment_date=?";
   try{
       PreparedStatement preparedStatement = connection.prepareStatement(query);
       preparedStatement.setInt(1,doctorId);
       preparedStatement.setString(2,appointmentDate);
       ResultSet resultSet = preparedStatement.executeQuery();
       if(resultSet.next()){
           int count = resultSet.getInt(1);
           if(count==0){
               return true;
           }else{
               return false;
           }
       }
   } catch (SQLException e){
       e.printStackTrace();
   }
   return false;
   }
}