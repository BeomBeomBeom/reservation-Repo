package msateam.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="blueSquare", url="http://team01-blueSqure:8080")
public interface HallService {
    @RequestMapping(method= RequestMethod.GET, path="/checkReservation")
    public boolean checkReservation(@RequestBody Hall hall);
    // public boolean  checkReservation(@RequestParam("seatId") long seatId);
    

}

