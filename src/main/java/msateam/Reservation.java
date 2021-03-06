package msateam;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;


@Entity
@Table(name="Reservation_table")
public class Reservation  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long rsvId;

    private Long seatId;

    private String status;

    private Long payId;


    @PostPersist
    public void onPostPersist(){
        // ReservationCreated reservationCreated = new ReservationCreated();
        // BeanUtils.copyProperties(this, reservationCreated);
        // reservationCreated.publishAfterCommit();

        // //Following code causes dependency to external APIs
        // // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // msateam.external.Hall hall = new msateam.external.Hall();
        // // mappings goes here
        // ReservationApplication.applicationContext.getBean(msateam.external.HallService.class)
        //     .checkReservation(hall);



        
        ////////////////////////////////
        // RESERVATION에 INSERT 된 경우 
        ////////////////////////////////

        ////////////////////////////////////
        // 예약 요청(reqReserve) 들어온 경우
        ////////////////////////////////////

        // 해당 ROOM이 Available한 상태인지 체크
        // boolean result = ReservationApplication.applicationContext.getBean(msateam.external.HallService.class)
        //                 .checkReservation(this.getSeatId());
        boolean result = true;
        System.out.println("######## Check Result : " + result);

        if(result) { 

            // 예약 가능한 상태인 경우(Available)
            
            //////////////////////////////
            // PAYMENT 결제 진행 (POST방식) - SYNC 호출
            //////////////////////////////
            msateam.external.Payment payment = new msateam.external.Payment();
            payment.setRsvId(this.getRsvId());
            payment.setSeatId(this.getSeatId());
            payment.setStatus("paid");
            ReservationApplication.applicationContext.getBean(msateam.external.PaymentService.class)
                .approvePayment(payment);

            /////////////////////////////////////
            // 이벤트 발행 --> ReservationCreated
            /////////////////////////////////////
            ReservationCreated reservationCreated = new ReservationCreated();
            BeanUtils.copyProperties(this, reservationCreated);
            reservationCreated.publishAfterCommit();
        }

    }
    @PostUpdate
    public void onPostUpdate(){

        ReservationCancelRequested reservationCancelRequested = new ReservationCancelRequested();
        BeanUtils.copyProperties(this, reservationCancelRequested);
        reservationCancelRequested.publishAfterCommit();

        
        if(this.getStatus().equals("reserved")) {

            ////////////////////
            // 예약 확정된 경우
            ////////////////////

            // 이벤트 발생 --> ReservationConfirmed
            ReservationConfirmed reservationConfirmed = new ReservationConfirmed();
            BeanUtils.copyProperties(this, reservationConfirmed);
            reservationConfirmed.publishAfterCommit();
        }

        // ReservationConfirmed reservationConfirmed = new ReservationConfirmed();
        // BeanUtils.copyProperties(this, reservationConfirmed);
        // reservationConfirmed.publishAfterCommit();

        ReservationCancelled reservationCancelled = new ReservationCancelled();
        BeanUtils.copyProperties(this, reservationCancelled);
        reservationCancelled.publishAfterCommit();

    }
    @PrePersist
    public void onPrePersist(){
    }
    @PreUpdate
    public void onPreUpdate(){
    }

    public Long getRsvId() {
        return rsvId;
    }

    public void setRsvId(Long rsvId) {
        this.rsvId = rsvId;
    }
    
    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getPayId() {
        return payId;
    }

    public void setPayId(Long payId) {
        this.payId = payId;
    }
    



}
