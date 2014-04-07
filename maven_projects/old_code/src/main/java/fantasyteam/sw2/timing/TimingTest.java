/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fantasyteam.sw2.timing;

/**
 *
 * @author javu
 */
public class TimingTest {
    protected Timing timer;
    
    public TimingTest(){
        timer = new Timing();
    }
    
     public static void main(String args[]) {
         int run = 1;
         TimingTest timer_test = new TimingTest();
         while(run==1){
             /*
             timer_test.getTiming().resetTime();
             timer_test.getTiming().printTime();
             int waiting = 0;
             while(waiting < 10000)
             {
                 waiting++;
             }
             */
             long five = 0;
             System.out.println("Test started");
             while(five < 5000){
                 Timing timer2 = new Timing();
                 five = timer_test.getTiming().compareTime(timer2.getDate());
             }
             System.out.println("Test finished");
             run=0;
         }
     }
     
     public Timing getTiming(){
         return timer;
     }
}
