/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fantasyteam.sw2.timing;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author javu
 */
public class Timing {
    protected Calendar time;
    
    public Timing(){
        time = Calendar.getInstance();
    }
    
    public Date getDate(){
        return time.getTime();
    }
    
    public void printTime(){
        System.out.println(time.getTime().toString());
    }
    
    public void resetTime(){
        time = Calendar.getInstance();
    }
    
    public long compareTime(Date date){
        long diff = date.getTime() - time.getTime().getTime();
        return diff;
    }
}
