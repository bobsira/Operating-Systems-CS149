import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;

/*******************************************************************
 * Extends Scheduler as a First Come First Served algorithm
 * Reads a PriorityQueue<Process>, schedules it, and returns a new Queue<Process>
 * @author Michael Riha
 * @data 06/19/13
 * @version FINAL
 * *****************************************************************/

public class FirstComeFirstServed extends Scheduler
{
    @Override
    public Queue<Process> schedule(PriorityQueue<Process> q) 
    {
        int startTime = 0; // the current time slice
        int queueSize = q.size();
        int finishTime = 0;
        Process p;
        Process scheduled;
        Stats stats = this.getStats();
        Queue<Process> scheduledQueue = new LinkedList<>();
        
        for (int i = 0; i < queueSize; ++i)
        {
            p = q.poll(); // MUST BE POLLED SEPARATELY for PriorityQueue to update
                          // for (Process p : q) will give the wrong order!
            
            startTime = Math.max((int) Math.ceil(p.getArrivalTime()), finishTime);            
            
            // Record the statistics for this process
            stats.addWaitTime(startTime - p.getArrivalTime());
            stats.addTurnaroundTime(startTime - p.getArrivalTime() + p.getBurstTime());
            stats.addResponseTime(startTime - p.getArrivalTime() + p.getBurstTime());
            stats.addProcess();            
            finishTime = startTime + p.getBurstTime();
            
            // Don't start any processes after 100 time slices
            if (startTime > 100) 
                break;

            // Create a new process with the calculated start time and add to a new queue
            scheduled = new Process();
            scheduled.setBurstTime(p.getBurstTime());
            scheduled.setStartTime(startTime);
            scheduled.setName(p.getName());
            scheduledQueue.add(scheduled);            
        }
        stats.addQuanta(finishTime); // Add the total quanta to finish all jobs
        printTimeChart(scheduledQueue);
        printRoundAvgStats();
        stats.nextRound();
        
        return scheduledQueue;
    }
}
